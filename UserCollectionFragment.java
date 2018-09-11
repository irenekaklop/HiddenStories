package com.example.eirini.hiddenstories;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eirini.hiddenstories.Utils.CheckedBoxListener;
import com.example.eirini.hiddenstories.Utils.CustomArrayAdapter;
import com.example.eirini.hiddenstories.Utils.DataSharedViewModel;
import com.example.eirini.hiddenstories.Utils.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserCollectionFragment extends Fragment implements CheckedBoxListener{
    public static final String TAG = UserCollectionFragment.class.getSimpleName();

    TextView textView;
    TextView n_chapters;
    View view;
    GridView gridView ;
    CustomArrayAdapter arrayAdapter;

   String story_name;

    public static UserCollectionFragment newInstance(String name) {
        UserCollectionFragment fragment = new UserCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Story name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "UserCollection on create");
        story_name = getArguments().getString("Story name");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setup the listener for the fragment A
        ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).getUserdata().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(@Nullable List<UserData> userData) {
                arrayAdapter.setContent(userData, "onCollection");
                arrayAdapter.notifyDataSetChanged();
            }
        });

        ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).getStories_collected().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                getActivity().setTitle("Hidden Stories");
                String rem = Integer.toString(integer);
                rem = rem+"/6";
                n_chapters.setText(rem);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Hidden Stories");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_collection, container, false);
        gridView = view.findViewById(R.id.gridView);
        textView = view.findViewById(R.id.title);
        textView.setText(story_name);
        n_chapters = view.findViewById(R.id.n_chapters);
        arrayAdapter = new CustomArrayAdapter(getActivity(), this);
        gridView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "UserCollection on pause");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach user collection" );
    }

    @Override
    public void onCheckedBoxClicked(String story_name) {
        //Change data for this character
        Log.d(TAG, "onCheckedBox is clicked"+story_name);
        ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).setUserData_checked(story_name);
    }

    @Override
    public void onCheckedBoxUnClicked(String story_name) {
        ViewModelProviders.of(getActivity()).get(DataSharedViewModel.class).setUserData_unchecked(story_name);
    }
}