package com.lowhot.cody.movement.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

/**
 * Created by cody on 2016/3/9.
 * 用于在service中也能弹出alertdialog
 */
public class AlertDialogUtils {
    AlertDialog.Builder builder;
    Dialog dialog;

    public AlertDialogUtils(Context ctx) {
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Alert");
        builder.setMessage("You are not the owner!");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public void showAlertDialog(){
        dialog.show();
    }
}
