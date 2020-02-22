
        /*
         * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         *       http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */
        package org.tensorflow.lite.examples.detection;

        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.Bitmap.Config;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.graphics.Paint;
        import android.graphics.Paint.Style;
        import android.graphics.RectF;
        import android.graphics.Typeface;
        import android.media.ImageReader.OnImageAvailableListener;
        import android.os.Bundle;
        import android.os.SystemClock;
        import android.speech.SpeechRecognizer;
        import android.speech.tts.TextToSpeech;
        import android.util.Log;
        import android.util.Size;
        import android.util.TypedValue;
        import android.widget.Toast;
        import java.io.IOException;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Locale;
        import java.util.Timer;
        import java.util.TimerTask;

        import org.tensorflow.lite.examples.detection.customview.OverlayView;
        import org.tensorflow.lite.examples.detection.customview.OverlayView.DrawCallback;
        import org.tensorflow.lite.examples.detection.database.DbOpenHelper;
        import org.tensorflow.lite.examples.detection.env.BorderedText;
        import org.tensorflow.lite.examples.detection.env.ImageUtils;
        import org.tensorflow.lite.examples.detection.env.Logger;
        import org.tensorflow.lite.examples.detection.model.OBJECT;
        import org.tensorflow.lite.examples.detection.tflite.Classifier;
        import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;
        import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

        import static android.speech.tts.TextToSpeech.ERROR;

        /**
         * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
         * objects.
         */
        public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
            private static final Logger LOGGER = new Logger();

            // Configuration values for the prepackaged SSD model.
            private static final int TF_OD_API_INPUT_SIZE = 300;
            private static final boolean TF_OD_API_IS_QUANTIZED = true;
            private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
            private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
            private static final DetectorMode MODE = DetectorMode.TF_OD_API;
            // Minimum detection confidence to track a detection.
            private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
            private static final boolean MAINTAIN_ASPECT = false;
            private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
            private static final boolean SAVE_PREVIEW_BITMAP = false;
            private static final float TEXT_SIZE_DIP = 10;
            OverlayView trackingOverlay;
            private Integer sensorOrientation;

            private Classifier detector;

            private long lastProcessingTimeMs;
            private Bitmap rgbFrameBitmap = null;
            private Bitmap croppedBitmap = null;
            private Bitmap cropCopyBitmap = null;

            private boolean computingDetection = false;

            private long timestamp = 0;

            private Matrix frameToCropTransform;
            private Matrix cropToFrameTransform;

            private MultiBoxTracker tracker;

            private BorderedText borderedText;

            public double personocc;
            public double carocc;
            public double bicycleocc;

            private DbOpenHelper mDbOpenHelper;

            /*VOICE DETECT*/
            Intent intent;
            SpeechRecognizer mRecognizer;
            final int PERMISSION = 1;

            //보이스  출력
            private TextToSpeech tts;

            private float testrisk;
            private String testString;


            private TimerTask mTask;
            private Timer mTimer;

            @Override
            public void onCreate(final Bundle savedInstanceState) {


                mTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (testString != null) {
                            tts.speak(testString, TextToSpeech.QUEUE_FLUSH, null);
                        }

                        testString = null;
                        testrisk=0;
                    }
                };

                mTimer = new Timer();

                //mTimer.schedule(mTask, 5000);
                mTimer.schedule(mTask, 3000, 3000);


                super.onCreate(savedInstanceState);
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != ERROR) {
                            // 언어를 선택한다.
                            tts.setLanguage(Locale.KOREAN);
                        }
                    }
                });

            }

            @Override
            public void onPreviewSizeChosen(final Size size, final int rotation) {
                final float textSizePx =
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
                borderedText = new BorderedText(textSizePx);
                borderedText.setTypeface(Typeface.MONOSPACE);

                tracker = new MultiBoxTracker(this);


                int cropSize = TF_OD_API_INPUT_SIZE;

                try {
                    detector =
                            TFLiteObjectDetectionAPIModel.create(
                                    getAssets(),
                                    TF_OD_API_MODEL_FILE,
                                    TF_OD_API_LABELS_FILE,
                                    TF_OD_API_INPUT_SIZE,
                                    TF_OD_API_IS_QUANTIZED);
                    cropSize = TF_OD_API_INPUT_SIZE;
                } catch (final IOException e) {
                    e.printStackTrace();
                    LOGGER.e(e, "Exception initializing classifier!");
                    Toast toast =
                            Toast.makeText(
                                    getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }

                previewWidth = size.getWidth();
                previewHeight = size.getHeight();

                sensorOrientation = rotation - getScreenOrientation();
                LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

                LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
                rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
                croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

                frameToCropTransform =
                        ImageUtils.getTransformationMatrix(
                                previewWidth, previewHeight,
                                cropSize, cropSize,
                                sensorOrientation, MAINTAIN_ASPECT);

                cropToFrameTransform = new Matrix();
                frameToCropTransform.invert(cropToFrameTransform);

                trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
                trackingOverlay.addCallback(
                        new DrawCallback() {
                            @Override
                            public void drawCallback(final Canvas canvas) {
                                tracker.draw(canvas);
                                if (isDebug()) {
                                    tracker.drawDebug(canvas);
                                }
                            }
                        });

                tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
            }

            @Override
            protected void processImage() {
                ++timestamp;
                final long currTimestamp = timestamp;
                trackingOverlay.postInvalidate();

                // No mutex needed as this method is not reentrant.
                if (computingDetection) {
                    readyForNextImage();
                    return;
                }
                computingDetection = true;
                LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

                rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

                readyForNextImage();

                final Canvas canvas = new Canvas(croppedBitmap);
                canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
                // For examining the actual TF input.
                if (SAVE_PREVIEW_BITMAP) {
                    ImageUtils.saveBitmap(croppedBitmap);
                }

                runInBackground(
                        new Runnable() {
                            @Override
                            public void run() {
                                LOGGER.i("Running detection on image " + currTimestamp);
                                final long startTime = SystemClock.uptimeMillis();
                                final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                                cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                                final Canvas canvas = new Canvas(cropCopyBitmap);
                                final Paint paint = new Paint();
                                paint.setColor(Color.RED);
                                paint.setStyle(Style.STROKE);
                                paint.setStrokeWidth(2.0f);

                                float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                switch (MODE) {
                                    case TF_OD_API:
                                        minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                        break;
                                }

                                final List<Classifier.Recognition> mappedRecognitions =
                                        new LinkedList<Classifier.Recognition>();

                                for (final Classifier.Recognition result : results) {
                                    final RectF location = result.getLocation();
                                    if (location != null && result.getConfidence() >= minimumConfidence) {
                                        canvas.drawRect(location, paint);

                                        cropToFrameTransform.mapRect(location);

                                        result.setLocation(location);
                                        mappedRecognitions.add(result);
                                    }
                                }

                                mDbOpenHelper = new DbOpenHelper(getApplicationContext());

                                if(tracker.returnThisTile().equals("person")){
                                    personocc = (personocc + tracker.returnThisOCC())/2;
                                    OBJECT realOB1 = new OBJECT(001, "person",3, Double.toString(personocc));
                                    mDbOpenHelper.updateOBJECT(realOB1);
                                }

                                LOGGER.d("ggg" + Double.toString(personocc) +Double.toString(carocc) +Double.toString(bicycleocc)  );

                                tracker.trackResults(mappedRecognitions, currTimestamp);



                                trackingOverlay.postInvalidate();

                                if(tracker.returnriskStatus() == true){

                                    if(tracker.returnDirection().equals("전방")) {
                                        if (tracker.returnobjectSize() > 60000) {
                                            LOGGER.d("위험!위험!");
                                            // Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            // vibrator.vibrate(500);
                                            if (tracker.returnobjectSize() > testrisk) {
                                                testrisk = tracker.returnobjectSize();
                                                testString = "세보 " +tracker.returnResultString();
                                            }
                                        }
                                        else if (tracker.returnobjectSize() > 35000) {
                                            LOGGER.d("위험!위험!");
                                            // Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            // vibrator.vibrate(500);
                                            if (tracker.returnobjectSize() > testrisk) {
                                                testrisk = tracker.returnobjectSize();
                                                testString = "다섯보 " +tracker.returnResultString();
                                            }
                                        }
                                    }
                                    else{
                                        if (tracker.returnobjectSize() > 50000) {
                                            LOGGER.d("위험!위험!");
                                            // Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            // vibrator.vibrate(500);
                                            if (tracker.returnobjectSize() > testrisk) {
                                                testrisk = tracker.returnobjectSize();
                                                testString = "세보 " +tracker.returnResultString();
                                            }
                                        }
                                        else if (tracker.returnobjectSize() > 25000) {
                                            LOGGER.d("위험!위험!");
                                            // Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            // vibrator.vibrate(500);
                                            if (tracker.returnobjectSize() > testrisk) {
                                                testrisk = tracker.returnobjectSize();
                                                testString = "다섯보 " +tracker.returnResultString();
                                            }
                                        }
                                    }
                                }

                                computingDetection = false;

                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                //showFrameInfo(previewWidth + "x" + previewHeight);
                                                //showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                                                //showInference(lastProcessingTimeMs + "ms");
                                            }
                                        });
                            }
                        });
            }

            @Override
            protected int getLayoutId() {
                return R.layout.camera_connection_fragment_tracking;
            }

            @Override
            protected Size getDesiredPreviewFrameSize() {
                return DESIRED_PREVIEW_SIZE;
            }

            // Which detection model to use: by default uses Tensorflow Object Detection API frozen
            // checkpoints.
            private enum DetectorMode {
                TF_OD_API;
            }

            @Override
            protected void setUseNNAPI(final boolean isChecked) {
                runInBackground(() -> detector.setUseNNAPI(isChecked));
            }

            @Override
            protected void setNumThreads(final int numThreads) {
                runInBackground(() -> detector.setNumThreads(numThreads));
            }

            private void testtts() {
                if (testString != null) {
                    tts.speak(testString, TextToSpeech.QUEUE_FLUSH, null);
                }

                testString = null;
                testrisk=0;
            }
        }