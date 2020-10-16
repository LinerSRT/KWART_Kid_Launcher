package com.sgtc.launcher.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sgtc.launcher.R;

import java.util.Objects;

public class KWARTDialog {
    private Context context;
    private Dialog dialog;
    public TextView title;
    public TextView message;
    public EditText editText;
    public Button cancel;
    public Button done;

    public KWARTDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 240;
        lp.height = 240;
        dialog.getWindow().setAttributes(lp);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        title = dialog.findViewById(R.id.dialogTitle);
        message = dialog.findViewById(R.id.dialogMessage);
        editText = dialog.findViewById(R.id.dialogEditText);
        cancel = dialog.findViewById(R.id.dialogCancel);
        done = dialog.findViewById(R.id.dialogDone);
        cancel.setVisibility(View.GONE);
    }


    public void showWithEditText(String titleText, String text, String cancelText, String doneText, boolean showEditText, String editTextText, String editHint) {
        title.setText(titleText);
        message.setText(text);
        cancel.setText(cancelText);
        done.setText(doneText);
        editText.setText(editTextText);
        editText.setHint(editHint);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.show();
    }

    public void show(String titleText, String text, String cancelText, String doneText) {
        title.setText(titleText);
        message.setText(text);
        cancel.setText(cancelText);
        done.setText(doneText);
        editText.setVisibility(View.GONE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.show();
    }

    public void close(){
        dialog.cancel();
    }

    public void setCancelListener(View.OnClickListener listener){
        cancel.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(listener);
    }
    public void setDoneListener(View.OnClickListener listener){
        done.setOnClickListener(listener);
    }
}
