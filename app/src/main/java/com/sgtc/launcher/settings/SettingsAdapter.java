package com.sgtc.launcher.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SettingsItem> settingsItemList;
    private int TYPE_HEADER = 0;

    public SettingsAdapter(List<SettingsItem> settingsItemList) {
        this.settingsItemList = settingsItemList;
        this.settingsItemList.add(0, new SettingsItem(null, "Настройки", null));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_HEADER)
            return new SettingsAdapter.TitleHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.title_header, viewGroup, false));
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settings_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == TYPE_HEADER) {
            ((SettingsAdapter.TitleHolder) viewHolder).title.setText(settingsItemList.get(0).title);
        } else {
            final SettingsItem settingsItem = settingsItemList.get(i);
            ((SettingsAdapter.ViewHolder) viewHolder).holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LauncherApplication.getContext().startActivity(settingsItem.action);
                }
            });
            ((SettingsAdapter.ViewHolder) viewHolder).icon.setImageDrawable(settingsItem.icon);
            ((SettingsAdapter.ViewHolder) viewHolder).title.setText(settingsItem.title);
        }
    }

    @Override
    public int getItemCount() {
        return settingsItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;
        private ConstraintLayout holder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.settingsIcon);
            title = itemView.findViewById(R.id.settingsTitle);
            holder = itemView.findViewById(R.id.settingsHolder);
        }
    }

    class TitleHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleHeader);
        }
    }
}
