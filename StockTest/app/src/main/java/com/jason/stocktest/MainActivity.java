package com.jason.stocktest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("response");
            switch (msg.what) {
                case 400:
                    break;
                case 200:
                    try {
                        String json = null;
                        if (result != null) {
                            String token[] = result.split("//");
                            json = token[1];
                            Log.d(TAG,json);
                        }
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("t");
                            String price = jsonObject.getString("l");
                            String change = jsonObject.getString("c");
                            String ratio = jsonObject.getString("cp");
                            String time = jsonObject.getString("lt");

                            Log.d(TAG, String.format("ID:%s, Price:%s, Change:%s, Ratio:%s%%, Time:%s",
                                    id, price, change, ratio, time));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String url="https://www.google.com/finance/info?q=TPE:TAIEX,00632R";
        OkHttpThread okHttpThread = new OkHttpThread(url);
        okHttpThread.start();
    }

    class OkHttpThread extends Thread {
        private String _url;

        OkHttpThread(String u){
            _url = u;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            Log.d(TAG, "HttpThread");
            String response = "";
            Log.d(TAG, _url);
            try {
                Message msg = new Message();
                GetExample example = new GetExample();
                response = example.run(_url);
                msg.what = 200;
                response = Html.fromHtml(response).toString();
                Log.d(TAG, response);
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Bundle bundle = new Bundle();
                bundle.putString("response", e.toString());
                Message msg = new Message();
                msg.what = 400;
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    }
}
