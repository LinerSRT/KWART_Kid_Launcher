package com.sgtc.launcher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.sgtc.launcher.R;
import com.sgtc.launcher.applications.ApplicationLoader;
import com.sgtc.launcher.applications.ApplicationModel;
import com.sgtc.launcher.applications.AppsAdapter;
import com.sgtc.launcher.util.Broadcast;
import com.sgtc.launcher.util.Callback;

import java.util.List;

public class AppsFragment extends Fragment {
    private RecyclerView recyclerView;
    private AppsAdapter appsAdapter;
    private Broadcast broadcast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.apps_fragment, container, false);
        recyclerView = view.findViewById(R.id.appsRecycler);
        recyclerView.setWillNotDraw(false);
        broadcast = new Broadcast(getContext(), new String[]{
                getContext().getPackageName()+".SCROLL_APPS_TO_TOP",
                getContext().getPackageName()+".UPDATE_APPS"

        }) {
            @Override
            public void handleChanged(Intent intent) {
                if(intent.getAction().equalsIgnoreCase(getContext().getPackageName()+".SCROLL_APPS_TO_TOP")){
                    recyclerView.scrollToPosition(0);
                } else {
                    loadApps();
                }
            }
        };
        broadcast.setListening(true);
        loadApps();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcast.setListening(false);
    }

    private void loadApps(){
        new ApplicationLoader(new Callback<ApplicationModel>() {
            @Override
            public void onDataLoaded(List<ApplicationModel> data) {
                appsAdapter = new AppsAdapter(data);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(appsAdapter);
            }
        });
    }
}
