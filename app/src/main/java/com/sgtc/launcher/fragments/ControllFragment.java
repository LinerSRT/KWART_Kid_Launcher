package com.sgtc.launcher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class ControllFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_fragment, container, false);
        Button shutDown = view.findViewById(R.id.shutDown);
        shutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent("android.intent.action.sgtc_shutdown"));
            }
        });
        Button reboot = view.findViewById(R.id.reboot);
        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().sendBroadcast(new Intent("android.intent.action.sgtc_reboot"));
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
