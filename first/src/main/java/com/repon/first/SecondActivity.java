package com.repon.first;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.repon.first.FirstObj.exitTime;
import static com.repon.first.FirstObj.shorttoast;
import static com.repon.first.FirstObj.uselog;
import static com.repon.first.FirstObj.TAG;
import static com.repon.first.providers.firstDb.DB_TABLE;
import static com.repon.first.providers.firstDb.delete;
import static com.repon.first.providers.firstDb.rawquery;
import static com.repon.first.providers.firstDb.update;

public class SecondActivity extends AppCompatActivity {

    Context context = this;
    RelativeLayout m_second_R_main, m_second_R_item;
    TextView m_second_T_title, m_second_item_T_getid, m_second_item_T_insert_getdate,
             m_second_item_T_modify_getdate;
    EditText m_second_item_E_showdata;
    ListView m_second_L_data;
    int datanum = 0; //記錄資料筆數
    private ArrayList<String> mData = new ArrayList<>(); //This SQLite data will be Temporary storage in the mData.
    private ArrayList<Map<String, Object>> mList = new ArrayList<>(); //Temporary data.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        setupViewComponent(); //sub-Function
    }

    //sub-Function
    private void setupViewComponent() {
        shorttoast(context,"Hello~");

        m_second_R_main = findViewById(R.id.second_R_main); //主畫面Rel
        m_second_R_item = findViewById(R.id.second_R_item); //項目畫面Rel
        m_second_T_title = findViewById(R.id.second_T_title); //列表標題
        m_second_L_data = findViewById(R.id.second_L_data); //列表清單
        m_second_item_T_getid = findViewById(R.id.second_item_T_getid); //讀取id
        m_second_item_T_insert_getdate = findViewById(R.id.second_item_T_insert_getdate); //讀取儲存時間
        m_second_item_T_modify_getdate = findViewById(R.id.second_item_T_modify_getdate); //讀取修改時間
        m_second_item_E_showdata = findViewById(R.id.second_item_E_showdata); //讀取儲存的資料

        //起始控制Rel顯示，預防xml設定忘了改------------------------
        m_second_R_main.setVisibility(View.VISIBLE); //顯示
        m_second_R_item.setVisibility(View.GONE); //殺掉
        //-------------------------------------------------------

        data_list(); //讀取資料列表
    }

    //讀取資料列表
    @SuppressLint("SetTextI18n")
    private void data_list() {
        String sql = "SELECT * FROM " + DB_TABLE + " ORDER BY id DESC";

        try{
            Cursor cur = rawquery(context, sql);

            if (cur != null){
                cur.moveToFirst();
                datanum = cur.getCount();
                mData = new ArrayList<>(); //使用前復歸/重設
                mList = new ArrayList<>();

                //設定listview內容
                while (!cur.isAfterLast()){
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0;i < cur.getColumnCount();i++){
                        sb.append(cur.getString(i));

                        if (i < cur.getColumnCount() - 1)
                            sb.append("#");
                    }

                    mData.add(String.valueOf(sb)); //轉換為String存起來

                    Map<String, Object> item = new HashMap<>();
                    String str = "id:" + cur.getString(0) + "\n資料內容:" +
                                  cur.getString(1) + "\n儲存時間:" +
                                  cur.getString(2) + "\n修改時間:" +
                                  cur.getString(3);
                    item.put("textview", str);
                    mList.add(item);
                    cur.moveToNext();
                }

                cur.close(); //使用完關掉
            }

        }catch (Exception e){
            if (uselog) Log.d(TAG,"data_list() error:" + e.toString());
        }

        //===========設定Listview==========//
        SimpleAdapter adapter = new SimpleAdapter(
                context,
                mList,
                R.layout.second_item,
                new String[]{"textview"},
                new int[]{R.id.item_T_data}
        );
        adapter.notifyDataSetChanged(); //通知UI更新數據
        m_second_L_data.setAdapter(adapter);
        m_second_L_data.setEnabled(true);
        m_second_L_data.setOnItemClickListener(itemclick); //選項監聽

        m_second_T_title.setText("共找到 " + datanum + " 筆資料");
    }

    //選項監聽
    private ListView.OnItemClickListener itemclick = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] fld = mData.get(position).split("#");

            //設定選項字串
            m_second_item_T_getid.setText(fld[0]);
            m_second_item_T_insert_getdate.setText(fld[2]);
            m_second_item_T_modify_getdate.setText(fld[3]);
            m_second_item_E_showdata.setText(fld[1]);

            m_second_item_E_showdata.setTextColor(ContextCompat.getColor(context, R.color.blue)); //字體改為藍色

            m_second_R_main.setVisibility(View.GONE); //殺掉主畫面
            m_second_R_item.setVisibility(View.VISIBLE); //顯示項目
        }
    };

    //標題按鈕監聽
    public void click(View view){
        SecondActivity.this.finish(); //關掉本頁

        //開啟第一頁
        Intent it = new Intent();
        it.setClass(this, FirstActivity.class);
        startActivity(it);
    }

    //修改按鈕
    public void modify_btn(View view){
        String getdata = m_second_item_E_showdata.getText().toString().trim();
        String dataId = m_second_item_T_getid.getText().toString().trim();
        String msg; //訊息

        if (getdata.length() > 0){
            int i = update(context, getdata, dataId);

            if (i > 0){
                msg = "修改資料成功!!";
                data_list(); //重新讀取SQLite資料
                m_second_R_main.setVisibility(View.VISIBLE);
                m_second_R_item.setVisibility(View.GONE);

            } else{
                m_second_item_E_showdata.setTextColor(ContextCompat.getColor(context, R.color.red)); //字體改為紅色
                msg = "無法修改資料";
            }

        }else
            msg = "請輸入資料!";

        shorttoast(context, msg);
    }

    //刪除按鈕
    public void delete_btn(View view){

        //設定刪除確認視窗
        MyAlertDialog alertDialog = new MyAlertDialog(context);
        alertDialog.setTitle("刪除資料確認視窗"); //設定標題
        alertDialog.setMessage("您確定要刪除本筆資料嗎?"); //設定訊息內容
        alertDialog.setIcon(android.R.drawable.star_big_on); //使用系統Icon
        alertDialog.setButton(BUTTON_POSITIVE,"取消", dialogbtn);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"確定", dialogbtn);
        alertDialog.show();
    }

    //實做訊息視窗button
    private DialogInterface.OnClickListener dialogbtn = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){
                case BUTTON_POSITIVE: //取消
                    shorttoast(context,"取消刪除!");
                    break;

                case BUTTON_NEGATIVE: //確定
                    String getId = m_second_item_T_getid.getText().toString().trim(); //獲得id
                    String where = "id = '" + getId + "'";
                    int i = delete(context, where);
                    String msg;

                    if (i != -1){
                        msg = "資料已刪除";
                        data_list(); //重新讀取SQLite資料
                        m_second_R_main.setVisibility(View.VISIBLE); //顯示
                        m_second_R_item.setVisibility(View.GONE); //殺掉

                    } else
                        msg = "資料刪除失敗";

                    shorttoast(context, msg);
                    break;
            }
        }
    };

    //控制手機功能鍵動作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //使用返回鍵時
        if (keyCode == KeyEvent.KEYCODE_BACK){

            //使用者點擊需間隔兩秒以上
            if (System.currentTimeMillis() - exitTime > 2000){
                exitTime = System.currentTimeMillis();

                if (m_second_R_item.getVisibility() == View.VISIBLE){ //項目畫面顯示時
                    m_second_R_main.setVisibility(View.VISIBLE);
                    m_second_R_item.setVisibility(View.GONE);

                }else {
                    String msg = "再點一次跳出本頁";
                    shorttoast(context, msg);
                }

            }else if (m_second_R_main.getVisibility() == View.VISIBLE){ //主畫面顯示時
                Intent it = new Intent();
                it.setClass(context, FirstActivity.class);
                startActivity(it); //執行跳頁
                this.finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}