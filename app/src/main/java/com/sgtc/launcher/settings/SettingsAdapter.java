package com.sgtc.launcher.settings;

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

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<SettingsItem> settingsItemList;
    private int TYPE_HEADER = 0;

    public SettingsAdapter(Context context, List<SettingsItem> settingsItemList) {
        this.context = context;
        this.settingsItemList = settingsItemList;
        this.settingsItemList.add(0, new SettingsItem(null, context.getString(R.string.settings_title), null));
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
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settings_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == TYPE_HEADER) {
            ((SettingsAdapter.TitleHolder) viewHolder).title.setText(settingsItemList.get(0).title);
        } else {
            final SettingsItem settingsItem = settingsItemList.get(i);
            ((ViewHolder) viewHolder).title.setSelected(true);
            ((SettingsAdapter.ViewHolder) viewHolder).holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (settingsItem.title.equals(context.getString(R.string.settings_control))) {
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
                                    context.startActivity(settingsItem.action);
                                } else if (kwartDialog.editText.getText().toString().isEmpty()) {
                                    warnDialog.show(context.getString(R.string.warning), context.getString(R.string.empty_password), "", "Ок");
                                } else {
                                    warnDialog.show(context.getString(R.string.warning), context.getString(R.string.wrong_password), "", "Ок");
                                }
                            }
                        });
                        kwartDialog.showWithEditText(
                                context.getString(R.string.warning),
                                context.getString(R.string.enter_password_require),
                                context.getString(R.string.cancel),
                                context.getString(R.string.next),
                                true,
                                "",
                                context.getString(R.string.enter_password)
                        );
                    } else {
                        context.startActivity(settingsItem.action);
                    }
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

    static class ViewHolder extends RecyclerView.ViewHolder {
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

    static class TitleHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleHeader);
        }
    }
}
