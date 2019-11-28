package com.example.aidltest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button btn_1 = null;
    private Button btn_2 = null;
    private Button btn_3 = null;
    int my_sum = 0;
    int goal_value = 0;
    String my_allDrawCard = "";
    private IMyAidlInterface mService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_1 = (Button)findViewById(R.id.draw_btn);
        btn_1.setOnClickListener(this);
        btn_2 = (Button)findViewById(R.id.stop_btn);
        btn_2.setOnClickListener(this);
        btn_2 = (Button)findViewById(R.id.restart_btn);
        btn_2.setOnClickListener(this);
        my_sum = 0;
        my_allDrawCard = "";

        DefinGoalValue();//ゲームの目標値を決定する

        /* サービスの起動 */
        Intent intent = new Intent(this, TestService.class);
        bindService(intent, connect, BIND_AUTO_CREATE);
    }

    /* プレイヤーの目標値を決定する */
    public void DefinGoalValue(){
        goal_value = new Random().nextInt(10)+20;
        TextView goal_txt = (TextView)findViewById(R.id.goal_id);
        goal_txt.setText(String.valueOf(2 + "?"));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.draw_btn) {
            try {
                /* サービス側でカードを引く処理を行う */
                int card_value = mService.DrawCard();
                my_allDrawCard += " " + card_value;
                if(card_value > 10)card_value = 10;//11,12,13のカードを10に統一する
                my_sum += card_value;

                /* 画面に反映する */
                TextView sum_txt = (TextView)findViewById(R.id.sum_id);
                TextView drawlist_txt = (TextView)findViewById(R.id.drawlist_id);
                sum_txt.setText(String.valueOf(my_sum));
                drawlist_txt.setText(String.valueOf(my_allDrawCard));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else if (v.getId() == R.id.stop_btn) {
            try {
                /* CPUの行動 */
                int cpu_sum = mService.CPUDrawCard();

                /* 2つの値を比較して勝敗を決定する */
                String result = mService.JudgResult(goal_value, my_sum, cpu_sum);
                Log.e("msg", my_sum+"");
                Log.e("msg", cpu_sum+"");

                /* 画面に反映する */
                TextView sum_txt = (TextView)findViewById(R.id.cpcard_id);
                sum_txt.setText("CPU : " + String.valueOf(cpu_sum));
                TextView goal_txt = (TextView)findViewById(R.id.goal_id);
                goal_txt.setText(String.valueOf(goal_value));
                TextView result_txt = (TextView)findViewById(R.id.result_id);
                result_txt.setText(result);

            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else if (v.getId() == R.id.restart_btn){
            finish();
            startActivity(getIntent());
        }
    }

    private ServiceConnection connect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mService = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mService = null;
        }
    };
}
