package com.repon.first;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import static com.repon.first.FirstObj.shorttoast;
import static com.repon.first.FirstObj.TAG;
import static com.repon.first.providers.firstDb.insert;

public class FirstActivity extends AppCompatActivity {

    Context context;
    TextView m_first_T_show;
    EditText m_first_E_input;
    Button m_first_B_click;
    private static  class StaticHandler extends Handler{
        private final WeakReference<FirstActivity> mActivity;

        public StaticHandler(FirstActivity activity){
            mActivity = new WeakReference<FirstActivity>(activity);
        }
    }

    public final StaticHandler mHandler = new StaticHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        DoLengthyWork work = new DoLengthyWork();
        work.setmHandler(mHandler);
        work.setmProgressBar(progressBar);
        work.start();

        context = this;
        setviewCompoment(); //sub-Function


    }

    //sub-Function
    @SuppressLint("SetTextI18n")
    private void setviewCompoment() {
        m_first_B_click = findViewById(R.id.first_B_click); //Click it once.
        m_first_E_input = findViewById(R.id.first_E_input); //Input data.
        m_first_T_show = findViewById(R.id.first_T_show); //store

        m_first_B_click.setOnClickListener(click); //Jump to the 'Second' of Page.
    }

    //On button Click Listener. Jump to the 'Second' of Page.
    private Button.OnClickListener click = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            shorttoast(context,"Click the Button once, it will change the title, than goto 'Second'.");

            //Prepared before goto 'Second'.
            Intent it = new Intent();
            it.setClass(FirstActivity.this, SecondActivity.class);
            startActivity(it);
            FirstActivity.this.finish(); //Finished the 'First' activity
            Log.d(TAG,"Finished the First Activity");
        }
    };

    //儲存按鈕
    public void clicksave(View view){
        String data = m_first_E_input.getText().toString().trim();
        int i = 0; //sqlite status:default number is 0 = failure

        if (data.length() > 0){
            i = (int) insert(context, data); //Add those data to the SQLite.
        }

        String msg;

        if (i == 1)
            msg = "Success.";
        else
            msg = "Miss a data.";

        m_first_T_show.setText(msg);
        shorttoast(context, msg);
    }
}
