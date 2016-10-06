package com.ks.dblab.kshelper.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by jo on 2016-05-02.
 */
public class DialogSupport {
    /* Context */
    private Context context = null;
    /* AlertDialog */
    private AlertDialog alertDialog = null;
    /* ConfirmDialog */
    private AlertDialog confirmDialog = null;
    /* Toast */
    private Toast toast = null;
    /* ProgressDialog */
    private ProgressDialog progressDialog = null;

    public DialogSupport(Context context) {
        this.context = context;
    }

    public void showAlert(String title, String message) {
        this.showAlert(title, message, null);
    }

    public void showAlert(String title, String message, DialogInterface.OnClickListener onClickPositiveButton) {
        try {
            this.alertDialog = new AlertDialog.Builder(this.context).setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("확인", onClickPositiveButton).create();
            this.alertDialog.setTitle(title);
            this.alertDialog.setMessage(message);
            this.alertDialog.show();
        } catch (Exception e) {
        }
    }

    public void showAlert(String title, String message, boolean setCancle,
                          DialogInterface.OnClickListener onClickPositiveButton) {
        try {
            this.alertDialog = new AlertDialog.Builder(this.context).setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("확인", onClickPositiveButton).create();
            this.alertDialog.setTitle(title);
            this.alertDialog.setMessage(message);
            this.alertDialog.setCancelable(setCancle);
            this.alertDialog.show();
        } catch (Exception e) {
        }
    }

    public void showInfo(String title, String message, DialogInterface.OnClickListener onClickPositiveButton) {
        try {
            this.alertDialog = new AlertDialog.Builder(this.context).setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("확인", onClickPositiveButton).create();
            this.alertDialog.setTitle(title);
            this.alertDialog.setMessage(message);
            this.alertDialog.show();
        } catch (Exception e) {
        }
    }

    public void showConfirm1(String title, String message, DialogInterface.OnClickListener onClickPositiveButton,
                             DialogInterface.OnClickListener onClickNegativeButton) {
        try {
            this.confirmDialog = new AlertDialog.Builder(this.context).setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("예", onClickPositiveButton).setNegativeButton("아니오", onClickNegativeButton)
                    .create();
            this.confirmDialog.setTitle(title);
            this.confirmDialog.setCancelable(true);
            this.confirmDialog.setMessage(message);
            this.confirmDialog.show();
        } catch (Exception e) {
        }
    }

    public void showConfirm2(String title, String message, DialogInterface.OnClickListener onClickPositiveButton,
                             DialogInterface.OnClickListener onClickNegativeButton, DialogInterface.OnClickListener onClickNeutralButton) {

        try {
            this.confirmDialog = new AlertDialog.Builder(this.context).setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("예", onClickPositiveButton).setNegativeButton("아니오", onClickNegativeButton)
                    .setNeutralButton("취소", onClickNeutralButton).create();
            this.confirmDialog.setTitle(title);
            this.confirmDialog.setCancelable(true);
            this.confirmDialog.setMessage(message);
            this.confirmDialog.show();
        } catch (Exception e) {
        }
    }

    public void showConfirm3(String title, String message, DialogInterface.OnClickListener onClickPositiveButton,
                             DialogInterface.OnClickListener onClickNegativeButton) {

        try {
            this.confirmDialog = new AlertDialog.Builder(this.context).setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("예", onClickPositiveButton).setNegativeButton("아니오", onClickNegativeButton)
                    .create();
            this.confirmDialog.setTitle(title);
            this.confirmDialog.setCancelable(false);
            this.confirmDialog.setMessage(message);
            this.confirmDialog.show();
        } catch (Exception e) {
        }
    }

    public void showToast(String message) {

        try {
            if (this.toast == null) {
                this.toast = Toast.makeText(this.context, message, Toast.LENGTH_SHORT);
            } else {
                this.toast.cancel();
                this.toast = Toast.makeText(this.context, message, Toast.LENGTH_SHORT);
            }

            this.toast.show();
        } catch (Exception e) {
        }
    }

    public void hideToast() {

        try {
            if (this.toast != null) {
                this.toast.cancel();
            }
        } catch (Exception e) {
        }
    }

    public void showProgress(String title, String message) {
        this.showProgress(title, message, true);
    }

    public void showProgress(String title, String message, Boolean cancelable) {

        try {
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            this.progressDialog.setTitle(title);
            this.progressDialog.setMessage(message);
            this.progressDialog.setCancelable(cancelable);
            // this.m_ProgressDialog.setCanceledOnTouchOutside(cancelable);
            this.progressDialog.show();
        } catch (Exception e) {
        }
    }

    public void hideProgress() {

        try {
            if (this.progressDialog != null) {
                this.progressDialog.cancel();
            }
        } catch (Exception e) {
        }
    }
}
