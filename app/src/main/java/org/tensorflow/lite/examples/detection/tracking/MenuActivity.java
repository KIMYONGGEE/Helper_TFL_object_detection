package org.tensorflow.lite.examples.detection.tracking;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.examples.detection.DetectorActivity;
import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main";
    private final AppCompatActivity activity = MenuActivity.this;

    ImageButton imgbtn_Pathstd;
    ImageButton imgbtn_Pathsel;
    ImageButton imgbtn_Pathsave;
    ImageButton imgbtn_Voice;
    EditText Menu_Select;

    /*VOICE DETECT*/
    Intent intent;
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;

    //보이스  출력
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        imgbtn_Pathstd = (ImageButton) findViewById(R.id.imgbtn_pathstd);
        imgbtn_Pathstd.setOnClickListener(this);
        imgbtn_Pathsel = (ImageButton) findViewById(R.id.imgbtn_pathsel);
        imgbtn_Pathsel.setOnClickListener(this);
        imgbtn_Pathsave = (ImageButton) findViewById(R.id.imgbtn_pathsave);
        imgbtn_Pathsave.setOnClickListener(this);
        imgbtn_Voice = (ImageButton) findViewById(R.id.imgbtn_voice);
        imgbtn_Voice.setOnClickListener(this);
        Menu_Select = (EditText) findViewById(R.id.Menu_select);


        /*보이스 디텍션 */
        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KO");
        //정렬버튼을 누루면 보이스 입력

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        tts.speak("메인 메뉴 페이지입니다. 하단에 있는 녹음버튼을 누른 후 원하시는 메뉴 경로선택, 경로저장, 안내시작 중 하나를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);

        imgbtn_Pathstd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MenuActivity.this, DetectorActivity.class);
                startActivity(intent1);
                tts.speak("안내시작 페이지입니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        imgbtn_Pathsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(MenuActivity.this, MapsActivity.class);
                startActivity(intent2);
                tts.speak("경로를 선택합니다.", TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        imgbtn_Pathsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3=new Intent(MenuActivity.this, DetectorActivity.class);
                startActivity(intent3);
                tts.speak("경로저장 페이지입니다.", TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        imgbtn_Voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mRecognizer = SpeechRecognizer.createSpeechRecognizer(MenuActivity.this);
                 mRecognizer.setRecognitionListener(listener);
                 mRecognizer.startListening(intent);


            }
        });

        Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();



    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }



        @Override
        public void onResults(Bundle results) {

            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //여기에  출력됨!
                for (int i = 0; i < matches.size(); i++) {
                    Menu_Select.setText(matches.get(i));
                }

            if(Menu_Select.getText().toString().equals("경로선택") || Menu_Select.getText().toString().equals("경로 선택")|| Menu_Select.getText().toString().equals("경노 선택"))
            {
                Intent intent1=new Intent(MenuActivity.this, MapsActivity.class);
                startActivity(intent1);
                tts.speak("경로 선택 페이지입니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else if (Menu_Select.getText().toString().equals( "안내시작") || Menu_Select.getText().toString().equals("안내 시작"))
            {
                Intent intent2=new Intent(MenuActivity.this, DetectorActivity.class);
                startActivity(intent2);
                tts.speak("안내시작 페이지입니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else if (Menu_Select.getText().toString().equals("경로저장") || Menu_Select.getText().toString().equals("경로 저장")|| Menu_Select.getText().toString().equals("경노 저장"))
            {
                Intent intent3=new Intent(MenuActivity.this, DetectorActivity.class);
                startActivity(intent3);
                tts.speak("경로저장 페이지입니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else
                tts.speak("인식 실패. 다시한번 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);


        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    @Override
    public void onClick(View view) {

    }
}
