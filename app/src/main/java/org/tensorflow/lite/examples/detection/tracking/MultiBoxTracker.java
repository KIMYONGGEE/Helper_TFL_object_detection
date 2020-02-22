
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
public class MultiBoxTracker {
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



  public MultiBoxTracker(final Context context) {
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

      if((recognition.title.equals("person") || recognition.title.equals("car") || recognition.title.equals("bicycle") || recognition.title.equals("motorcycle")|| recognition.title.equals("bus"))
              && (100 * recognition.detectionConfidence) >= 60 ) {

        recognition.detectionConfidence = 1-((1-recognition.detectionConfidence)/2);

        riskSTATUS = true;

        final RectF trackedPos = new RectF(recognition.location);

        odjectSize = (trackedPos.top-trackedPos.bottom) * (trackedPos.right-trackedPos.left);

        String obj_direction;


        if((trackedPos.bottom+trackedPos.top)/2 < 480/3 )
          obj_direction = "오른쪽";
        else if ((trackedPos.bottom+trackedPos.top)/2 >= 480/3  && (trackedPos.bottom+trackedPos.top)/2 < 480/3*2 )
          obj_direction = "전방";
        else
          obj_direction = "왼쪽";

        direction = obj_direction;

        getFrameToCanvasMatrix().mapRect(trackedPos);
        boxPaint.setColor(recognition.color);
        float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;

        canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);


        final String labelString = obj_direction +
                String.format("%s %.2f %.0f", recognition.title, (100 * recognition.detectionConfidence), Math.abs(odjectSize));
        //            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top,
        // labelString);

        traTITLE = recognition.title;
        traOCC = (100 * recognition.detectionConfidence);
        borderedText.drawText(
                canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);

      }
      else if((recognition.title.equals("bench")||recognition.title.equals("bollard"))
              && (100 * recognition.detectionConfidence) >= 60 ){
        // 장애물 식별시

        recognition.detectionConfidence = 1-((1-recognition.detectionConfidence)/2);

        riskSTATUS = true;

        final RectF trackedPos = new RectF(recognition.location);

        odjectSize = (trackedPos.top-trackedPos.bottom) * (trackedPos.right-trackedPos.left);

        String obj_direction;

        if((trackedPos.bottom+trackedPos.top)/2 < 480/3 )
          obj_direction = "오른쪽";
        else if ((trackedPos.bottom+trackedPos.top)/2 >= 480/3  && (trackedPos.bottom+trackedPos.top)/2 < 480/3*2 )
          obj_direction = "전방";
        else
          obj_direction = "왼쪽";

        direction = obj_direction;

        getFrameToCanvasMatrix().mapRect(trackedPos);
        boxPaint.setColor(recognition.color);
        float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;

        canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

        final String labelString = obj_direction +
                String.format("%s %.2f %.0f", recognition.title, (100 * recognition.detectionConfidence), Math.abs(odjectSize));

        traTITLE = recognition.title;
        traOCC = (100 * recognition.detectionConfidence);
        borderedText.drawText(
                canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);

      }
      else if((recognition.title.equals("traffic light"))
              && (100 * recognition.detectionConfidence) >= 60 ){
        // 횡단보도 식별시

        recognition.detectionConfidence = 1-((1-recognition.detectionConfidence)/2);

        riskSTATUS = false;

        final RectF trackedPos = new RectF(recognition.location);

        odjectSize = (trackedPos.top-trackedPos.bottom) * (trackedPos.right-trackedPos.left);

        getFrameToCanvasMatrix().mapRect(trackedPos);
        boxPaint.setColor(recognition.color);
        float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;

        canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

        final String labelString = String.format("%s %.2f %.0f", recognition.title, (100 * recognition.detectionConfidence), Math.abs(odjectSize));
        //            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top,
        // labelString);

        traTITLE = recognition.title;
        traOCC = (100 * recognition.detectionConfidence);
        borderedText.drawText(
                canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);
      }
      else{
        riskSTATUS = false;
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

    String object="";
    if (traTITLE.equals("person")) {
      object="사람";
    }
    else if (traTITLE.equals("car")){
      object="자동차";
    }
    else if(traTITLE.equals("bicycle")){
      object="자전거";
    }
    else if(traTITLE.equals("motorcycle")){
      object="오토바이";
    }

    else if(traTITLE.equals("bus")){
      object="버스";
    }
    else if(traTITLE.equals("bench")||traTITLE.equals("bollad")){
      object="장애물";
    }
    else if(traTITLE.equals("traffic light")){
      object="신호등";
    }

    return direction + "에" + object;
  }

  public String returnDirection(){
    return this.direction;
  }

  public float returnobjectSize(){
    return Math.abs(odjectSize);
  }

}