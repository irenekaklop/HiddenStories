package com.example.eirini.hiddenstories;


import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;


public class QuestionFragment extends DialogFragment {
    Button bt_exit;
    Button bt_continue;

    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        TextView textView = view.findViewById(R.id.info_message);
        textView.setText(R.string.nfc_info_message);

        Toolbar toolbar = view.findViewById(R.id.toolbarId);
        toolbar.setTitle("EXIT");

        bt_exit = view.findViewById(R.id.bt_exit);
        bt_continue = view.findViewById(R.id.bt_continue);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FinalActivity.class);
                //Add data
                startActivity(intent);
                getActivity().finish();
            }
        });

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

}
