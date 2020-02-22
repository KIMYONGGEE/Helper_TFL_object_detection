package org.tensorflow.lite.examples.detection.tracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.database.DbOpenHelper;
import org.tensorflow.lite.examples.detection.model.USER;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Main";
    public static final int sub = 1001;

    TextView text_id;
    TextView text_pw;
    EditText edit_id;
    EditText edit_pw;

    ImageButton imgbtn_idfind;
    ImageButton imgbtn_voice;

    String ID;
    String PW;

    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imgbtn_idfind = (ImageButton) findViewById(R.id.imgbtn_idfind);
        imgbtn_idfind.setOnClickListener(this);
        imgbtn_voice = (ImageButton) findViewById(R.id.imgbtn_voice);
        imgbtn_voice.setOnClickListener(this);

        edit_id = (EditText) findViewById(R.id.edit_id);
        edit_pw = (EditText) findViewById(R.id.edit_pw);

        text_id = (TextView) findViewById(R.id.text_id);
        text_pw = (TextView) findViewById(R.id.text_pw);

        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_idfind:
                ID = edit_id.getText().toString();
                PW = edit_pw.getText().toString();

                String seaid = mDbOpenHelper.searchPW(ID,PW).getUSERID();

                Toast.makeText(getApplicationContext(), seaid , Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SearchActivity.this,LoginActivity.class);
                startActivity(intent);

            default:
        }
    }
}