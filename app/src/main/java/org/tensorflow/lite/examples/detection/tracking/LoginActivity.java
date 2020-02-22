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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main";
    public static final int sub = 1001;
    private DbOpenHelper mDbOpenHelper;

    int count=0;
    final int PERMISSION = 1;
    private TextToSpeech tts;
    SpeechRecognizer mRecognizer;
    TextView text_id;
    TextView text_pw;
    EditText edit_id;
    EditText edit_pw;
    ImageButton imgbtn_login;
    ImageButton imgbtn_join;
    ImageButton imgbtn_idfind;
    ImageButton imgbtn_voice;

    String ID;
    String PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

        imgbtn_login = (ImageButton) findViewById(R.id.imgbtn_login);
        imgbtn_login.setOnClickListener(this);
        imgbtn_join = (ImageButton) findViewById(R.id.imgbtn_join);
        imgbtn_join.setOnClickListener(this);
        imgbtn_idfind = (ImageButton) findViewById(R.id.imgbtn_idfind);
        imgbtn_idfind.setOnClickListener(this);
        imgbtn_voice = (ImageButton) findViewById(R.id.imgbtn_voice);
        imgbtn_voice.setOnClickListener(this);

        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pw = (EditText) findViewById(R.id.edit_pw);

        text_id = (TextView) findViewById(R.id.text_id);
        text_pw = (TextView) findViewById(R.id.text_pw);
        /*보이스 디텍션 */
        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KO");
        //정렬버튼을 누루면 보이스 입력

        imgbtn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count==0) {
                    tts.speak("아이디를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(LoginActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);
                }
                else if(count==1) {
                    tts.speak("페스워드를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(LoginActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);
                }
                else {
                    tts.speak("다시 입력받습니다.", TextToSpeech.QUEUE_ADD, null);
                }
            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        /*voice  detec*/
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
            if(count==0) {
                for (int i = 0; i < matches.size(); i++) {
                    edit_id.setText(matches.get(i));
                }
                count++;
            }

            else if (count==1)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_pw.setText(matches.get(i));
                }
                count++;
                ID = edit_id.getText().toString();
                PW = edit_pw.getText().toString();

                if (mDbOpenHelper.loginUSER(ID,PW) != 0)
                {

                    Intent intent1 = new Intent(LoginActivity.this,MenuActivity.class);
                    startActivity(intent1);
                    tts.speak("로그인 성공. 디바이스 하단을 터치한 후 원하는 메뉴, 경로선택, 경로저장, 안내시작 중 하나를 말하세요.", TextToSpeech.QUEUE_FLUSH, null);

                }


            }

            else {

                count = 0;
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_login:
                ID = edit_id.getText().toString();
                PW = edit_pw.getText().toString();

                setupDB();
                mDbOpenHelper.indexcingGPSLOC();

                if (mDbOpenHelper.loginUSER(ID,PW) != 0)
                {
                    Intent intent1 = new Intent(LoginActivity.this,MenuActivity.class);
                    startActivity(intent1);
                    tts.speak("로그인 성공. 디바이스 하단을 터치한 후 원하는 메뉴, 경로선택, 경로저장, 안내시작 중 하나를 말하세요.", TextToSpeech.QUEUE_FLUSH, null);
                }
                break;

            case R.id.imgbtn_join:
                Intent intent2 = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent2);
                break;

            case R.id.imgbtn_idfind:
                Intent intent3 = new Intent(LoginActivity.this,SearchActivity.class);
                startActivity(intent3);
                break;

            case R.id.imgbtn_voice:
                break;
            default:
        }
    }
    public void setupDB(){

        //gps path setup
        GPS realGPSdata = new GPS(10000, "2019.12.15", "탈마당", "36.6255629 ","127.45463", "36.6266987","127.4542247");
        mDbOpenHelper.createGPS(realGPSdata);

        //gps location setup
        GPSLOC inputdata0 = new GPSLOC(10000, "36.6255629", "127.45463");
        mDbOpenHelper.createGPSLOC(inputdata0);
        GPSLOC inputdata1= new GPSLOC(10000, "36.6257656", "127.454348");
        mDbOpenHelper.createGPSLOC(inputdata1);
        GPSLOC inputdata2 = new GPSLOC(10000, "36.6257656", "127.454348");
        mDbOpenHelper.createGPSLOC(inputdata2);
        GPSLOC inputdata3 = new GPSLOC(10000, "36.6261009", "127.454135");
        mDbOpenHelper.createGPSLOC(inputdata3);
        GPSLOC inputdata4 = new GPSLOC(10000, "36.6261009", "127.454135");
        mDbOpenHelper.createGPSLOC(inputdata4);
        GPSLOC inputdata5 = new GPSLOC(10000, "36.6261009", "127.454135");
        mDbOpenHelper.createGPSLOC(inputdata5);
        GPSLOC inputdata6 = new GPSLOC(10000, "36.6261009", "127.454135");
        mDbOpenHelper.createGPSLOC(inputdata6);
        GPSLOC inputdata7 = new GPSLOC(10000, "36.6263093", "127.4541386");
        mDbOpenHelper.createGPSLOC(inputdata7);
        GPSLOC inputdata8 = new GPSLOC(10000, "36.6263093", "127.4541386");
        mDbOpenHelper.createGPSLOC(inputdata8);
        GPSLOC inputdata9 = new GPSLOC(10000, "36.6266987", "127.4542247");
        mDbOpenHelper.createGPSLOC(inputdata9);
        GPSLOC inputdata10 = new GPSLOC(10000, "36.6266987", "127.4542247");
        mDbOpenHelper.createGPSLOC(inputdata10);
        GPSLOC inputdata11 = new GPSLOC(10000, "36.6266987", "127.4542247");
        mDbOpenHelper.createGPSLOC(inputdata11);
        GPSLOC inputdata12 = new GPSLOC(20000, "36.6266987", "127.4542247");
        mDbOpenHelper.createGPSLOC(inputdata12);
        GPSLOC inputdata13 = new GPSLOC(30000, "36.6266987", "127.4542247");
        mDbOpenHelper.createGPSLOC(inputdata13);
        GPSLOC inputdata14 = new GPSLOC(40000, "36.6266987", "127.4542247");
        mDbOpenHelper.createGPSLOC(inputdata14);

        //object setup
        OBJECT realOB1 = new OBJECT(001, "person",3, "80.0");
        OBJECT realOB2 = new OBJECT(002, "car", 1, "80.0");
        OBJECT realOB3 = new OBJECT(003, "bicycle",2, "80.0");
        mDbOpenHelper.createOBJECT(realOB1);
        mDbOpenHelper.createOBJECT(realOB2);
        mDbOpenHelper.createOBJECT(realOB3);

        //background setup
        BACKGROUND realBG1 = new BACKGROUND(001,"인도",  "80.0");
        BACKGROUND realBG2 = new BACKGROUND(002,"횡단보도", "80.0");
        BACKGROUND realBG3 = new BACKGROUND(003, "차도","80.0");
        mDbOpenHelper.createBACKGROUND(realBG1);
        mDbOpenHelper.createBACKGROUND(realBG2);
        mDbOpenHelper.createBACKGROUND(realBG3);

        //USER SETUP
        USER testUSER001 = new USER(00001,"aaa","aaa","강연수",24,"MALE","rkddustn96@naver.com","01087654524","복대동1759");
        USER testUSER002 = new USER(00002,"bbb","bbb","김용기",18,"FEMALE","rladydrl@naver.com","01012345678","복대동2100");
        USER testUSER003 = new USER(00003,"ccc","ccc","이완석",60,"MALE","dldhkstjr@naver.com","01044445555","복대동2200");
        mDbOpenHelper.createUSER(testUSER001);
        mDbOpenHelper.createUSER(testUSER002);
        mDbOpenHelper.createUSER(testUSER003);

        //VOICDATA SETUP
        VOICEDATA testVOICEDATA1 = new VOICEDATA(001,1,"로그인");
        VOICEDATA testVOICEDATA2= new VOICEDATA(002,1,"회원가입");
        VOICEDATA testVOICEDATA3 = new VOICEDATA(003,1,"ID찾기");

        VOICEDATA testVOICEDATA4 = new VOICEDATA(004,2,"회원가입완료");

        VOICEDATA testVOICEDATA5 = new VOICEDATA(005,3,"경로선택");
        VOICEDATA testVOICEDATA6 = new VOICEDATA(006,3,"경로저장");
        VOICEDATA testVOICEDATA7 = new VOICEDATA(007,3,"안내시작");

        mDbOpenHelper.createVOICEDATA(testVOICEDATA1);
        mDbOpenHelper.createVOICEDATA(testVOICEDATA2);
        mDbOpenHelper.createVOICEDATA(testVOICEDATA3);
        mDbOpenHelper.createVOICEDATA(testVOICEDATA4);
        mDbOpenHelper.createVOICEDATA(testVOICEDATA5);
        mDbOpenHelper.createVOICEDATA(testVOICEDATA6);
        mDbOpenHelper.createVOICEDATA(testVOICEDATA7);
    }
}