package org.tensorflow.lite.examples.detection.tracking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.database.DbOpenHelper;
import org.tensorflow.lite.examples.detection.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.os.SystemClock.sleep;
import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main";
    private final AppCompatActivity activity = MainActivity.this;

    Button btn_Insert;
    ImageButton imgbtn_voice;

    EditText edit_ID;
    EditText edit_PW;
    EditText edit_NAME;
    EditText edit_AGE;
    EditText edit_GENDER;
    EditText edit_ADDRESS;
    EditText edit_EMAIL;
    EditText edit_PHONE;
    final int PERMISSION = 1;
    int count=0;
    /*VOICE DETECT*/
    Intent intent;
    SpeechRecognizer mRecognizer;

    long nowIndex;
    String ID;
    String name;
    long age;
    String gender = "";
    String sort = "userid";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;

    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        btn_Insert = (Button) findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);

        imgbtn_voice = (ImageButton) findViewById(R.id.imgbtn_voice);
        imgbtn_voice.setOnClickListener(this);

        edit_ID = (EditText) findViewById(R.id.edit_id);
        edit_PW = (EditText) findViewById(R.id.edit_pw);
        edit_NAME = (EditText) findViewById(R.id.edit_name);
        edit_AGE = (EditText) findViewById(R.id.edit_age);
        edit_GENDER = (EditText) findViewById(R.id.edit_gender);
        edit_EMAIL = (EditText) findViewById(R.id.edit_email);
        edit_PHONE = (EditText) findViewById(R.id.edit_phone);
        edit_ADDRESS = (EditText) findViewById(R.id.edit_address);


        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

        btn_Insert.setEnabled(true);



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

        imgbtn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count==0) {
                    tts.speak("아이디를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);
                }
                else if(count==1) {
                    tts.speak("페스워드를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);
                }
                else if(count==2) {
                    tts.speak("이름을 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);

                }
                else if(count==3) {
                    tts.speak("나이를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);

                }
                else if(count==4) {
                    tts.speak("성별을 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);

                }
                else if(count==5) {
                    tts.speak("이메일을 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);

                }
                else if(count==6) {
                    tts.speak("휴대폰 번호를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(3000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);

                }
                else if(count==7) {
                    tts.speak("주소를 말하세요", TextToSpeech.QUEUE_FLUSH, null);
                    sleep(2000);
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(intent);

                }
                else {
                    tts.speak("다시 입력받습니다.", TextToSpeech.QUEUE_FLUSH, null);
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
                    edit_ID.setText(matches.get(i));
                }
                count++;
            }

            else if (count==1)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_PW.setText(matches.get(i));
                }
                count++;
            }
            else if (count==2)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_NAME.setText(matches.get(i));
                }
                count++;
            }
            else if (count==3)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_AGE.setText(matches.get(i));
                }
                count++;
            }
            else if (count==4)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_GENDER.setText(matches.get(i));
                }
                count++;
            }
            else if (count==5)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_EMAIL.setText(matches.get(i));
                }
                count++;
            }
            else if (count==6)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_PHONE.setText(matches.get(i));
                }
                count++;
            }
            else if (count==7)
            {
                for (int i = 0; i < matches.size(); i++) {
                    edit_ADDRESS.setText(matches.get(i));
                }
                count++;
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
    public void setInsertMode(){
        edit_ID.setText("test");
        edit_PW.setText("test");
        edit_AGE.setText("222");
        edit_NAME.setText("test");
        edit_GENDER.setText("test");
        edit_EMAIL.setText("test");
        edit_PHONE.setText("test");
        edit_ADDRESS.setText("test");

        btn_Insert.setEnabled(true);

    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_insert:

                mDbOpenHelper.getToDoCount();
                String tempID = edit_ID.getText().toString();
                String tempPW = edit_PW.getText().toString();
                String tempNAME = edit_NAME.getText().toString();
                String tempAGE = edit_AGE.getText().toString();
                String tempGENDER = edit_GENDER.getText().toString();
                String tempADDRESS = edit_ADDRESS.getText().toString();
                String tempEMAIL = edit_EMAIL.getText().toString();
                String tempPHNNE = edit_PHONE.getText().toString();

                USER new_USER = new USER(11111, tempID,tempPW,tempNAME,Integer.parseInt(tempAGE),tempGENDER,tempEMAIL,tempPHNNE,tempADDRESS);
                mDbOpenHelper.createUSER(new_USER);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

                tts.speak("회원가입이 완료되었습니다. 다시 로그인해주세요.", TextToSpeech.QUEUE_FLUSH, null);


        }
    }

}