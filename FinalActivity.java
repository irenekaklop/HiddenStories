package com.example.eirini.hiddenstories;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.eirini.hiddenstories.Utils.UserData;

import java.util.List;

public class FinalActivity extends AppCompatActivity{
    private List<UserData> userDataList; //the choices of user from MainActivity
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Intent intent = getIntent();
        userDataList = (List<UserData>) intent.getSerializableExtra("user data list");
        textView = findViewById(R.id.tv_result);
        if(userDataList!=null){
            carculate_result();
        }
        else
            textView.setText("Not enough data to make you a profile");
    }

    private double carculate_result(){
        int positive =0;
        int negative = 0;
        for(int i = 0; i < userDataList.size(); i++){
            switch (userDataList.get(i).getEmotion()){
                case "anticipation":
                    if(userDataList.get(i).isFavorite())
                        positive = positive + 2;
                    else
                        positive++;
                case "anger":
                    if(userDataList.get(i).isFavorite())
                        negative = negative + 2;
                    else {
                        negative++;
                    }
            }
        }

        int answers = userDataList.size();
        double result_neg = negative / answers;
        double result_pos = positive / answers;
        if(result_neg>result_pos)
            return result_neg;

        return  result_pos;
    }

}
