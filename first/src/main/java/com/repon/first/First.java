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

public class First extends AppCompatActivity {

    Context context;
    TextView m_first_T_show;
    EditText m_first_E_input;
    Button m_first_B_click;
    private static  class StaticHandler extends Handler{
        private final WeakReference<First> mActivity;

        public StaticHandler(First activity){
            mActivity = new WeakReference<First>(activity);
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
        setviewCompoment(); //自定義


    }

    //自定義
    @SuppressLint("SetTextI18n")
    private void setviewCompoment() {
        m_first_B_click = findViewById(R.id.first_B_click); //按一下
        m_first_E_input = findViewById(R.id.first_E_input); //輸入資料
        m_first_T_show = findViewById(R.id.first_T_show); //儲存按鈕

        m_first_B_click.setOnClickListener(click); //跳頁按鈕監聽
    }

    //跳頁按鈕監聽
    private Button.OnClickListener click = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            shorttoast(context,"按了一下按鈕，並更改了標題，然後跳頁了");

            //準備跳到第二頁
            Intent it = new Intent();
            it.setClass(First.this, Second.class);
            startActivity(it);
            First.this.finish(); //砍掉自己
            Log.d(TAG,"結束第一頁");
        }
    };

    //儲存按鈕
    public void clicksave(View view){
        String data = m_first_E_input.getText().toString().trim();
        int i = 0; //sqlite儲存狀態:預設為0 = 不成功

        if (data.length() > 0){
            i = (int) insert(context, data); //寫入資料進SQLite
        }

        String msg;

        if (i == 1)
            msg = "儲存成功";
        else
            msg = "資料沒有存入";

        m_first_T_show.setText(msg);
        shorttoast(context, msg);
    }
}
