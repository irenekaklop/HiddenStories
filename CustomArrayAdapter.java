package com.example.eirini.hiddenstories.Utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.eirini.hiddenstories.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomArrayAdapter extends BaseAdapter {

    private Context context;
    private ValueAnimator buttonColorAnim = null;
    //this will create object of listener
    private CheckedBoxListener checkBoxCleckListener;

    public CustomArrayAdapter(Context context, CheckedBoxListener checkedBoxListener) {
        this.context = context;
        this.checkBoxCleckListener = checkedBoxListener;
    }

    public CustomArrayAdapter(Context context) {
        this.context = context;
        this.checkBoxCleckListener = null;
    }

    private String activity;
    private List<UserData> user_data = new ArrayList<>();

    public void setContent(List<UserData> user_data, String activity) {
        this.user_data = user_data;
        this.activity = activity;
    }

    public List<UserData> getUser_data() {
        return user_data;
    }

    @Override
    public int getCount() {
        return user_data.size();
    }

    @Override
    public Object getItem(int position) {
        return user_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;

            convertView = inflater.inflate(R.layout.content_view, parent, false);
        }

        final UserData content = user_data.get(position);
        final CheckBox bt_favorite = convertView.findViewById(R.id.button_favorite);
        Log.d("Adapter", "boolean content favorite"+content.isFavorite());
        if(content.isFavorite()){
            //Make button checked
            bt_favorite.setChecked(true);
            Log.d("custom", "is favorite"+content.getCharacter());
        }
        else{
            Log.d("custom", "is not favorite"+content.getCharacter());
            bt_favorite.setChecked(false);
        }

        bt_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bt_favorite.isChecked()) {
                    checkBoxCleckListener.onCheckedBoxClicked(content.getCharacter());
                }
                else {
                    checkBoxCleckListener.onCheckedBoxUnClicked(content.getCharacter());
                }
            }
        });

        if("onProfile".equals(activity)){
            //List view on Profile user
            //Display only characters
            TextView title = convertView.findViewById(R.id.emotion);
            TextView subtitle = convertView.findViewById(R.id.character);
            title.setText(content.getCharacter());
            subtitle.setVisibility(View.GONE);
        }
        else{
            TextView title = convertView.findViewById(R.id.emotion);
            TextView subtitle = convertView.findViewById(R.id.character);
            title.setText(content.getEmotion());
            subtitle.setText(content.getCharacter());
        }

        String emotion = content.getEmotion();
        convertView.findViewById(R.id.content_layout).setBackgroundColor(convertView.getResources().getColor(R.color.white));
        Log.d("Custom Array", "emotion is"+ emotion);
        if("anger".equals(emotion)){
            convertView.findViewById(R.id.content_layout).setBackgroundColor(convertView.getResources().getColor(R.color.colorAnger));
        }
        else if("anticipation".equals(emotion)){
                convertView.findViewById(R.id.content_layout).setBackgroundColor(convertView.getResources().getColor(R.color.colorAnticipation));
        }

        return convertView;
    }
}
