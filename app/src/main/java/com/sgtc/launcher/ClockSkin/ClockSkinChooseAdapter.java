package com.sgtc.launcher.ClockSkin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.launcher.ClockSkin.Model.ClockSkin;
import com.sgtc.launcher.Config;
import com.sgtc.launcher.LauncherApplication;
import com.sgtc.launcher.R;
import com.sgtc.launcher.util.Image;
import com.sgtc.launcher.util.PM;

import java.io.IOException;
import java.util.List;


public class ClockSkinChooseAdapter extends RecyclerView.Adapter<ClockSkinChooseAdapter.ViewHolder> {
    private Context context;
    private List<ClockSkin> clockItemsList;
    protected LauncherApplication launcherApplication;
    private Activity activity;

    private int selectedPos = -1;

    public ClockSkinChooseAdapter(Context context, List<ClockSkin> clockItems, Activity activity) {
        super();
        this.context = context;
        if (clockItems == null) {
            this.clockItemsList = LauncherApplication.getClockSkinList();
        } else {
            this.clockItemsList = clockItems;
        }
        this.activity = activity;
        this.launcherApplication = (LauncherApplication) activity.getApplication();
    }

    @Override
    public int getItemCount() {
        return clockItemsList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clock_skin_choose, parent, false);
        return new ViewHolder(v);
    }


    RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recycler) {
        super.onAttachedToRecyclerView(recycler);
        recyclerView = recycler;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ClockSkin skin = clockItemsList.get(position);
        //Picasso.get().load("file:///android_asset/" + skin.getPreviewPath()).into(holder.clockModel);

		try {
		    Bitmap bitmap = Image.loadBitmap(skin.getPreviewPath(), false);
			//Bitmap bitmap = MediaStore.Images.Media.getBitmap(Launcher.getContext().getContentResolver() , Uri.parse("file:///android_asset/" + skin.getPreviewPath()));
			holder.clockModel.setImageBitmap(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		holder.clockModel.setPadding(0, 0, 0, 0);
        holder.clockModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PM.put(Config.WATCHFACE_SELECTED_INDEX, holder.getAdapterPosition());
                Intent intent = new Intent(LauncherApplication.getContext().getPackageName() + ".WATCHFACE_SELECTED");
                LauncherApplication.getContext().sendBroadcast(intent);
                activity.finish();
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView clockModel;
        ViewHolder(final View view) {
            super(view);
            clockModel = view.findViewById(R.id.clock_skin_choose);
        }
    }
}
