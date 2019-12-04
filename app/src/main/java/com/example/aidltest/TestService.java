package com.example.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestService extends Service {

    ArrayList<Integer> card_list = new ArrayList<Integer>();

    public void onCreate(){
        DataReset();//カードデータの作成
    }

    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mService;
    }

    /* 山札データの初期化 */
    public void DataReset(){
        card_list.clear();
        for(int i = 0; i < 52; i++){
            card_list.add(i/4 + 1);
        }
    }

    /* インタフェースの実装 */
    private final IMyAidlInterface.Stub mService = new IMyAidlInterface.Stub() {
        /* プレイヤーがカードを引く */
        public int DrawCard() throws RemoteException {
            if(card_list.size() < 1) DataReset();

            int random = new Random().nextInt(card_list.size());
            int card = card_list.get(random);
            card_list.remove(random);

            return card;
        }

        /* CPUの行動 */
        public int CPUDrawCard() throws RemoteException {
            int sum = 0;

            int border_value = new Random().nextInt(10) + 16;//cpuが目指す目標値
            while(true){
                if(card_list.size() < 1) DataReset();

                int random = new Random().nextInt(card_list.size());
                int card_value = card_list.get(random);
                if(card_value > 10)card_value = 10;
                sum += card_value;
                card_list.remove(random);

                if(sum > border_value)break;
            }
            return sum;
        }

        /* 勝敗を判定する */
        public String JudgResult(int goal_value, int my_sum, int cpu_sum) throws RemoteException {
            if(my_sum > goal_value && cpu_sum > goal_value){return "DRAW";}
            else if(my_sum > goal_value && cpu_sum < goal_value){return "You Lose";}
            else if(my_sum < goal_value && cpu_sum > goal_value){return "You Win";}
            else {
                if(my_sum == cpu_sum){return "DRAW";}
                else if(my_sum < cpu_sum){return "You Lose";}
                else {return "You Win";}
            }
        }
    };
}
