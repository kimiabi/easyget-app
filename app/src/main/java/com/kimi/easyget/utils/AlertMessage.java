package com.kimi.easyget.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kimi.easyget.R;

public class AlertMessage {

    public static AlertMessage alertMessage = null;
    private Dialog dialog;
    private ProgressBar mProgressBar;


    public static AlertMessage getInstance() {
        if (alertMessage == null) {
            alertMessage = new AlertMessage();
        }
        return alertMessage;

    }


    public void show(Context context, String title, String message) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_template);
        TextView messageTitle = dialog.findViewById(R.id.message_title);
        TextView messageBody = dialog.findViewById(R.id.body_message);
        Button btnClose = dialog.findViewById(R.id.btn_close);

        messageTitle.setText(title);
        messageBody.setText(message);
        btnClose.setOnClickListener(view -> dialog.dismiss());

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
