package com.repon.first;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//共用method及全域變數
public class FirstObj {
    public static final String TAG = "Element Inc=>"; //Log用
    public static boolean uselog = false; //是否啟用log
    public static long exitTime = 0; //記綠離開時間


    //toast短訊息
    public static void shorttoast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //取得現在時間，此寫法網路傳遞無須轉碼
    /*傳入時間格式抓取手機時間：傳入的簡易字串格式 yyyy/M/d HH:mm:ss
      SimpleDateFormat函数语法：
      G 年代標記
      y 年
      M 月
      d 日
      h 時 在上午或下午 (1~12)
      H 時 在一天中 (0~23)
      m 分
      s 秒
      S 毫秒
      E 星期
      D 一年中的第幾天
      F 一月中第幾个星期幾
      w 一年中第幾个星期
      W 一月中第幾个星期
      a 上午 / 下午 標記
      k 時 在一天中 (1~24)
      K 時 在上午或下午 (0~11)
      z 時區
    */
    public static String nowtime(String time_format){ //傳入時間格式,ex."M/d HH:mm"
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat(time_format);
        date.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        return date.format(currentLocalTime);
    }
}
