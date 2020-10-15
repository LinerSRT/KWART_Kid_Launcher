package com.sgtc.launcher.applications;

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

public class AppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ApplicationModel> applicationModels;
    private int TYPE_HEADER = 0;

    public AppsAdapter(List<ApplicationModel> applicationModels) {
        this.applicationModels = applicationModels;
        this.applicationModels.add(0, new ApplicationModel(null, null, "Приложения", null));
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return TYPE_HEADER;
        else
            return 1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == TYPE_HEADER)
            return new TitleHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.title_header, viewGroup, false));
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.apps_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == TYPE_HEADER) {
            ((TitleHolder) viewHolder).title.setText(applicationModels.get(0).title);
        } else {
            final ApplicationModel applicationModel = applicationModels.get(i);
            ((ViewHolder) viewHolder).icon.setImageDrawable(applicationModel.icon);
            ((ViewHolder) viewHolder).name.setText(applicationModel.title);
            ((ViewHolder) viewHolder).appHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LauncherApplication.getContext().startActivity(applicationModel.intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView name;
        private ConstraintLayout appHolder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.applicationIcon);
            name = itemView.findViewById(R.id.applicationName);
            appHolder = itemView.findViewById(R.id.applicationHolder);
        }
    }

    class TitleHolder extends RecyclerView.ViewHolder{
        private TextView title;
        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleHeader);
        }
    }
}
