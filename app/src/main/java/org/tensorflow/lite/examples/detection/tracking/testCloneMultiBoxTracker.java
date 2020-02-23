
/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.lite.examples.detection.tracking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Classifier.Recognition;

/** A tracker that handles non-max suppression and matches existing objects to new detections. */
public class testCloneMultiBoxTracker {
    private static final float TEXT_SIZE_DIP = 18;
    private static final float MIN_SIZE = 16.0f;
    private static final int[] COLORS = {
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.WHITE,
            Color.parseColor("#55FF55"),
            Color.parseColor("#FFA500"),
            Color.parseColor("#FF8888"),
            Color.parseColor("#AAAAFF"),
            Color.parseColor("#FFFFAA"),
            Color.parseColor("#55AAAA"),
            Color.parseColor("#AA33AA"),
            Color.parseColor("#0D0068")
    };
    final List<Pair<Float, RectF>> screenRects = new LinkedList<Pair<Float, RectF>>();
    private final Logger logger = new Logger();
    private final Queue<Integer> availableColors = new LinkedList<Integer>();
    private final List<TrackedRecognition> trackedObjects = new LinkedList<TrackedRecognition>();
    private final Paint boxPaint = new Paint();
    private final float textSizePx;
    private final BorderedText borderedText;
    private Matrix frameToCanvasMatrix;
    private int frameWidth;
    private int frameHeight;
    private int sensorOrientation;

    private double traOCC =0;

    private String traTITLE = "";
    private boolean riskSTATUS=false;
    private String direction="";
    private float odjectSize=0;

    /*VOICE DETECT*/
    Intent intent;
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;

    //보이스  출력
    private TextToSpeech tts;



    public testCloneMultiBoxTracker(final Context context) {
        for (final int color : COLORS) {
            availableColors.add(color);
        }

        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Style.STROKE);
        boxPaint.setStrokeWidth(10.0f);
        boxPaint.setStrokeCap(Cap.ROUND);
        boxPaint.setStrokeJoin(Join.ROUND);
        boxPaint.setStrokeMiter(100);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
    }

    public synchronized void setFrameConfiguration(
            final int width, final int height, final int sensorOrientation) {
        frameWidth = width;
        frameHeight = height;
        this.sensorOrientation = sensorOrientation;
    }

    public synchronized void drawDebug(final Canvas canvas) {
        final Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60.0f);

        final Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setAlpha(200);
        boxPaint.setStyle(Style.STROKE);

        for (final Pair<Float, RectF> detection : screenRects) {
            final RectF rect = detection.second;
            canvas.drawRect(rect, boxPaint);
            canvas.drawText("" + detection.first, rect.left, rect.top, textPaint);
            borderedText.drawText(canvas, rect.centerX(), rect.centerY(), "" + detection.first);
        }
    }

    public synchronized void trackResults(final List<Recognition> results, final long timestamp) {
        logger.i("Processing %d results from %d", results.size(), timestamp);
        processResults(results);
    }

    private Matrix getFrameToCanvasMatrix() {
        return frameToCanvasMatrix;
    }

    public synchronized void draw(final Canvas canvas) {

        final boolean rotated = sensorOrientation % 180 == 90;
        final float multiplier =
                Math.min(
                        canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
                        canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));
        frameToCanvasMatrix =
                ImageUtils.getTransformationMatrix(
                        frameWidth,
                        frameHeight,
                        (int) (multiplier * (rotated ? frameHeight : frameWidth)),
                        (int) (multiplier * (rotated ? frameWidth : frameHeight)),
                        sensorOrientation,
                        false);

        for (final TrackedRecognition recognition : trackedObjects) {

            if((
                    recognition.title.equals("backpack") ||
                            recognition.title.equals("umbrella") ||
                            recognition.title.equals("handbag") ||
                            recognition.title.equals("suitcase") ||
                            recognition.title.equals("bottle") ||
                            recognition.title.equals("wine glass") ||
                            recognition.title.equals("cup") ||
                            recognition.title.equals("fork") ||
                            recognition.title.equals("knife") ||
                            recognition.title.equals("spoon") ||
                            recognition.title.equals("bowl") ||
                            recognition.title.equals("banana") ||
                            recognition.title.equals("apple") ||
                            recognition.title.equals("sandwich") ||
                            recognition.title.equals("orange") ||
                            recognition.title.equals("broccoli") ||
                            recognition.title.equals("carrot") ||
                            recognition.title.equals("hot dog") ||
                            recognition.title.equals("pizza") ||
                            recognition.title.equals("cake") ||
                            recognition.title.equals("chair") ||
                            recognition.title.equals("couch") ||
                            recognition.title.equals("pottend plant") ||
                            recognition.title.equals("bed") ||
                            recognition.title.equals("dining table") ||
                            recognition.title.equals("toilet") ||
                            recognition.title.equals("tv") ||
                            recognition.title.equals("laptop") ||
                            recognition.title.equals("mouse") ||
                            recognition.title.equals("remote") ||
                            recognition.title.equals("keyboard") ||
                            recognition.title.equals("cell phone") ||
                            recognition.title.equals("microwave") ||
                            recognition.title.equals("oven") ||
                            recognition.title.equals("toaster") ||
                            recognition.title.equals("sink") ||
                            recognition.title.equals("refrigerator") ||
                            recognition.title.equals("book") ||
                            recognition.title.equals("clock") ||
                            recognition.title.equals("scissors") ||
                            recognition.title.equals("hair drier") ||
                            recognition.title.equals("toothbrush")
            )
                    && (100 * recognition.detectionConfidence) >= 70 ) {

                riskSTATUS = true;

                final RectF trackedPos = new RectF(recognition.location);

                odjectSize = (trackedPos.top-trackedPos.bottom) * (trackedPos.right-trackedPos.left);

                getFrameToCanvasMatrix().mapRect(trackedPos);
                boxPaint.setColor(recognition.color);
                float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;

                canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);


                final String labelString =
                        String.format("%s %.2f %.0f", recognition.title, (100 * recognition.detectionConfidence), Math.abs(odjectSize));
                //            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top,
                // labelString);

                traTITLE = recognition.title;
                traOCC = (100 * recognition.detectionConfidence);
                borderedText.drawText(
                        canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);

            }
        }
    }

    private void processResults(final List<Recognition> results) {
        final List<Pair<Float, Recognition>> rectsToTrack = new LinkedList<Pair<Float, Recognition>>();

        screenRects.clear();
        final Matrix rgbFrameToScreen = new Matrix(getFrameToCanvasMatrix());

        for (final Recognition result : results) {
            if (result.getLocation() == null) {
                continue;
            }
            final RectF detectionFrameRect = new RectF(result.getLocation());

            final RectF detectionScreenRect = new RectF();
            rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect);

            logger.v(
                    "Result! Frame: " + result.getLocation() + " mapped to screen:" + detectionScreenRect);

            screenRects.add(new Pair<Float, RectF>(result.getConfidence(), detectionScreenRect));

            if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
                logger.w("Degenerate rectangle! " + detectionFrameRect);
                continue;
            }

            rectsToTrack.add(new Pair<Float, Recognition>(result.getConfidence(), result));
        }

        trackedObjects.clear();
        if (rectsToTrack.isEmpty()) {
            logger.v("Nothing to track, aborting.");
            return;
        }

        for (final Pair<Float, Recognition> potential : rectsToTrack) {
            final TrackedRecognition trackedRecognition = new TrackedRecognition();
            trackedRecognition.detectionConfidence = potential.first;
            trackedRecognition.location = new RectF(potential.second.getLocation());
            trackedRecognition.title = potential.second.getTitle();
            trackedRecognition.color = COLORS[trackedObjects.size()];
            trackedObjects.add(trackedRecognition);

            if (trackedObjects.size() >= COLORS.length) {
                break;
            }
        }
    }

    private static class TrackedRecognition {
        RectF location;
        float detectionConfidence;
        int color;
        String title;
    }

    public String returnThisTile(){
        return traTITLE;
    }

    public Double returnThisOCC(){
        return traOCC;
    }

    public boolean returnriskStatus(){
        return riskSTATUS;
    }

    public String returnResultString(){


        String object = "";
        if (traTITLE.equals("backpack")) {
            object = "가방";
        } else if (traTITLE.equals("umbrella")) {
            object = "우산";
        } else if (traTITLE.equals("handbag")) {
            object = "핸드백";
        } else if (traTITLE.equals("suitcase")) {
            object = "캐리어";
        } else if (traTITLE.equals("bottle")) {
            object = "물병";
        } else if (traTITLE.equals("wine glass")) {
            object = "와인잔";
        } else if (traTITLE.equals("cup")) {
            object = "컵";
        } else if (traTITLE.equals("fork")) {
            object = "포크";
        } else if (traTITLE.equals("knife")) {
            object = "나이프";
        } else if (traTITLE.equals("spoon")) {
            object = "수저";
        } else if (traTITLE.equals("bowl")) {
            object = "그릇";
        } else if (traTITLE.equals("banana")) {
            object = "바나나";
        } else if (traTITLE.equals("apple")) {
            object = "사과";
        } else if (traTITLE.equals("sandwich")) {
            object = "샌드위치";
        } else if (traTITLE.equals("orange")) {
            object = "오랜지";
        } else if (traTITLE.equals("broccoli")) {
            object = "브로콜리";
        } else if (traTITLE.equals("carrot")) {
            object = "당근";
        } else if (traTITLE.equals("hot dog")) {
            object = "핫도그";
        } else if (traTITLE.equals("pizza")) {
            object = "피자";
        } else if (traTITLE.equals("cake")) {
            object = "케이크";
        } else if (traTITLE.equals("chair")) {
            object = "의자";
        } else if (traTITLE.equals("couch")) {
            object = "의자";
        } else if (traTITLE.equals("pottend plant")) {
            object = "화초";
        } else if (traTITLE.equals("bed")) {
            object = "침대";
        } else if (traTITLE.equals("dining table")) {
            object = "식탁";
        } else if (traTITLE.equals("toilet")) {
            object = "변기";
        } else if (traTITLE.equals("tv")) {
            object = "티비";
        } else if (traTITLE.equals("laptop")) {
            object = "랩탑";
        } else if (traTITLE.equals("mouse")) {
            object = "마우스";
        } else if (traTITLE.equals("remote")) {
            object = "리모콘";
        } else if (traTITLE.equals("keyboard")) {
            object = "키보드";
        } else if (traTITLE.equals("cell phone")) {
            object = "핸드폰";
        } else if (traTITLE.equals("microwave")) {
            object = "전자렌지";
        } else if (traTITLE.equals("oven")) {
            object = "오븐";
        } else if (traTITLE.equals("toaster")) {
            object = "토스터기";
        } else if (traTITLE.equals("sink")) {
            object = "세면대";
        } else if (traTITLE.equals("refrigerator")) {
            object = "냉장고";
        } else if (traTITLE.equals("book")) {
            object = "책";
        } else if (traTITLE.equals("clock")) {
            object = "시계";
        } else if (traTITLE.equals("scissors")) {
            object = "가위";
        } else if (traTITLE.equals("hair drier")) {
            object = "드라이기";
        } else if (traTITLE.equals("toothbrush")) {
            object = "칫솔";
        }

        return object;
    }

    public float returnobjectSize(){
        return Math.abs(odjectSize);
    }

}