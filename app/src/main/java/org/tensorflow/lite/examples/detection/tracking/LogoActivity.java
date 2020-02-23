package org.tensorflow.lite.examples.detection.tracking;

 import android.Manifest;
import android.content.Intent;
import android.os.Build;
 import android.os.Handler;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.database.DbOpenHelper;
import org.tensorflow.lite.examples.detection.model.BACKGROUND;
import org.tensorflow.lite.examples.detection.model.GPS;
import org.tensorflow.lite.examples.detection.model.GPSLOC;
import org.tensorflow.lite.examples.detection.model.OBJECT;
import org.tensorflow.lite.examples.detection.model.USER;
import org.tensorflow.lite.examples.detection.model.VOICEDATA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.os.SystemClock.sleep;
import static android.speech.tts.TextToSpeech.ERROR;


public class LogoActivity extends AppCompatActivity implements View.OnClickListener {
    final int PERMISSION = 1;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogo);

        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KO");

         tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent1 = new Intent(LogoActivity.this,MenuActivity.class);
                startActivity(intent1);

                tts.speak(" 디바이스 하단을 터치한 후 원하는 메뉴, 안내시작, 사물인식 중 하나를 말하세요.", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 3000);


    }

    @Override
    public void onClick(View view) {

    }
}