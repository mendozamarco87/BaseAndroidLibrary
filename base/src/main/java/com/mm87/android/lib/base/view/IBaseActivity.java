package com.mm87.android.lib.base.view;

import android.content.res.Resources;

import androidx.fragment.app.DialogFragment;

public interface IBaseActivity {

    void showAlert(IDialogOk listener, CharSequence title, CharSequence message);

    void showAlert(IDialogOk listener, CharSequence title, CharSequence message, CharSequence okButtonName, CharSequence cancelButtonName);

    Resources getResources();

    public void showProgressDialog(CharSequence message);

    public void showDialogFragment(DialogFragment dialog, String TAG);

    boolean isNullOrEmpty(CharSequence charSequence);
}
