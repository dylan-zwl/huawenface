package com.huawen.huawenface.sdk.act;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    public void showProgressDialog(int textResId, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        try {
            if (progressDialog != null)
                hideProgressDialog();
            progressDialog = ProgressDialog.show(this, null, getString(textResId), true, cancelable, cancelListener);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public void showToast(final int textResId) {
        showToast(getString(textResId));
    }

    public void showSnak(String message) {
//        Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnak(final int textResId) {
        showSnak(getString(textResId));
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
