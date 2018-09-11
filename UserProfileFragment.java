package com.example.eirini.hiddenstories;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eirini.hiddenstories.Utils.CheckedBoxListener;
import com.example.eirini.hiddenstories.Utils.CustomArrayAdapter;
import com.example.eirini.hiddenstories.Utils.TotalSharedDataViewModel;
import com.example.eirini.hiddenstories.Utils.UserData;
import com.example.eirini.hiddenstories.Utils.UserProfile;

import java.io.Serializable;
import java.util.List;

public class UserProfileFragment extends Fragment implements CheckedBoxListener{

    private static final String DESCRIBABLE_KEY = "User Profile";
    public static final String TAG = UserCollectionFragment.class.getSimpleName();

    TextView textView;
    View view;
    GridView gridView ;
    CustomArrayAdapter arrayAdapter;

    public static UserProfileFragment newInstance(UserProfile userProfile) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, (Serializable) userProfile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        gridView = view.findViewById(R.id.gridView);
        textView = view.findViewById(R.id.title);
        textView.setText("Stories collection");
        arrayAdapter = new CustomArrayAdapter(getActivity(), this);
        gridView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setup the listener for the fragment A
        ViewModelProviders.of(getActivity()).get(TotalSharedDataViewModel.class).getUserData().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(@Nullable List<UserData> userDataList) {
                Log.d("View model of UserProf", "string list! "+userDataList);
                arrayAdapter.setContent(userDataList, "onProfile");
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCheckedBoxClicked(String story) {
        //Change data for this character
        ViewModelProviders.of(getActivity()).get(TotalSharedDataViewModel.class).setUserData_checked(story);
    }

    @Override
    public void onCheckedBoxUnClicked(String story_name) {
        ViewModelProviders.of(getActivity()).get(TotalSharedDataViewModel.class).setUserData_unchecked(story_name);
    }
}
