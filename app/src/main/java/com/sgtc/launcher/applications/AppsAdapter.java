package com.sgtc.launcher.applications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.launcher.Config;
import com.sgtc.launcher.R;
import com.sgtc.launcher.util.PM;
import com.sgtc.launcher.util.dialog.KWARTDialog;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ApplicationModel> applicationModels;
    private int TYPE_HEADER = 0;

    public AppsAdapter(Context context, List<ApplicationModel> applicationModels) {
        this.context = context;
        this.applicationModels = applicationModels;
        this.applicationModels.add(0, new ApplicationModel(null, null, context.getString(R.string.apps_title), null, false));
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
            ((ViewHolder) viewHolder).name.setSelected(true);
            ((ViewHolder) viewHolder).appHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (applicationModel.requirePassword && ((boolean) PM.get(Config.KEY_PROTECT_APPS, true))) {
                        final KWARTDialog kwartDialog = new KWARTDialog(context);
                        kwartDialog.setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                kwartDialog.close();
                            }
                        });
                        kwartDialog.setDoneListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final KWARTDialog warnDialog = new KWARTDialog(context);
                                warnDialog.setDoneListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        warnDialog.close();
                                    }
                                });
                                if (kwartDialog.editText.getText().toString().equals(Config.SECRET_CODE) || kwartDialog.editText.getText().toString().equalsIgnoreCase((String) PM.get(Config.KEY_CONTROL_PASSWORD, Config.DEFAULT_CODE))) {
                                    kwartDialog.close();
                                    context.startActivity(applicationModel.intent);
                                } else if (kwartDialog.editText.getText().toString().isEmpty()) {
                                    warnDialog.show(context.getString(R.string.warning), context.getString(R.string.empty_password), "", "Ок");
                                } else {
                                    warnDialog.show(context.getString(R.string.warning), context.getString(R.string.wrong_password), "", "Ок");
                                }
                            }
                        });
                        kwartDialog.showWithEditText(
                                context.getString(R.string.warning),
                                context.getString(R.string.enter_password_to_start_app),
                                context.getString(R.string.cancel),
                                context.getString(R.string.next),
                                true,
                                "",
                                context.getString(R.string.enter_password)
                        );
                    } else {
                        context.startActivity(applicationModel.intent);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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

    static class TitleHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleHeader);
        }
    }
}
