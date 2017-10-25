package org.tonky.mserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView timeView = (TextView) findViewById(R.id.TimeView);
        final TextView dateView = (TextView) findViewById(R.id.DateView);
        final TextView connectView = (TextView) findViewById(R.id.ConnectView);
        handler.postDelayed(Time, 1); //每隔1s执行
        handler.postDelayed(webConnect,1);
    }



    public static void updateView(TextView time,TextView date){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date=new Date();
        time.setText(timeFormat.format(current_date));
        date.setText(dateFormat.format(current_date));
    }

    public static String isConnected(String ipAddress) {
        try {

                    Runtime runtime = Runtime.getRuntime();
                    Process p = runtime.exec("su");//执行多个命令
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    os.writeBytes("ping -c 1 " + ipAddress + "\n");
                    os.writeBytes("exit\n");
                    os.flush();

                    InputStream is = p.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i;
                    while ((i = is.read()) != -1) {

                        baos.write(i);
                    }
                    String str = baos.toString();
                    //Log.v("test", str);
                    if (str.contains("0% packet loss")) {
                        return "Connected";
                    }
                    else
                        return "Disconnected";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }


    final Handler handler = new Handler();//实时刷新
    Runnable Time = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 1000);
                final TextView timeView = (TextView) findViewById(R.id.TimeView);
                final TextView dateView = (TextView) findViewById(R.id.DateView);
                updateView(timeView,dateView);//刷新日期时间
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable webConnect = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, 3000);
                final TextView connectView = (TextView) findViewById(R.id.ConnectView);

                new Thread(new Runnable() {//在子线程完成操作，在UI线程替换msg
                    public void run() {
                        final String msg = isConnected("123.125.114.144");//baidu.com ip地址
                        connectView.post(new Runnable() {
                            @Override
                            public void run() {
                                connectView.setText("Net:"+msg);
                            }
                        });
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
