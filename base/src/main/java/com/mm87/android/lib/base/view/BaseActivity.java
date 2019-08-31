package com.mm87.android.lib.base.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.Window;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mm87.android.lib.base.R;
import com.mm87.android.lib.base.rest.IRestClientManager;
import com.mm87.android.lib.base.rest.RestClient;
import com.mm87.android.lib.base.util.ViewUtils;

import java.util.LinkedList;
import java.util.Queue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public abstract class BaseActivity extends AppCompatActivity implements
        IBaseActivity, IRestClientManager {

    private FirebaseAnalytics mFirebaseAnalytics;
    protected boolean isResumed;
    protected ProgressDialog progressDialog;

    private Queue<Pair<DialogFragment, String>> dialogQueue = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransitionStart();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(getIdResLayout());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        ViewUtils.bindViewAnnotation(this, this, getWindow().getDecorView());
        init(savedInstanceState);
        initAnalytics(getAnalyticsScreen());
    }

    protected abstract int getIdResLayout();

    protected abstract String getScreenName();

    protected abstract void init(Bundle savedInstanceState);

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        while (!dialogQueue.isEmpty()) {
            Pair<DialogFragment, String> pair = dialogQueue.poll();
            pair.first.show(getSupportFragmentManager(), pair.second);
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionEnd();
    }

    @Override
    public void onBackPressed() {
        ViewUtils.hideSoftKeyboard(this);
        super.onBackPressed();
    }

    protected void overridePendingTransitionStart() {
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    protected void overridePendingTransitionEnd() {
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void showAlert(final IDialogOk listener, CharSequence title, CharSequence message) {
        showAlert(listener, title, message, getString(R.string.ok), getString(R.string.cancel));
    }

    @Override
    public void showAlert(final IDialogOk listener, CharSequence title, CharSequence message, CharSequence okButtonName, CharSequence cancelButtonName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton(okButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onOk();
                }
            }
        });

        if (listener instanceof IDialogOkCancel) {
            builder.setNegativeButton(cancelButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((IDialogOkCancel) listener).onCancel();
                }
            });
        }

        builder.create().show();
    }

    @Override
    public void showDialogFragment(DialogFragment dialog, String TAG) {
        if (isResumed) {
            dialog.show(getSupportFragmentManager(), TAG);
        } else {
            dialogQueue.add(new Pair<>(dialog, TAG));
        }
    }

    @Override
    public void showProgressDialog(CharSequence message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
        }
        if (message != null && message.length() > 0) {
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    @Override
    public boolean isNullOrEmpty(CharSequence charSequence) {
        return charSequence == null || (charSequence.length() <= 0);
    }

    public void showProgressDialog(RestClient restClient, CharSequence message) {
        showProgressDialog(message);
    }

    public void initAnalytics(String nameScreen) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, nameScreen);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
    }

    protected String getAnalyticsScreen() {
        return getScreenName();
    }

    @Override
    public boolean getInternetState() {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("wifi"))
                if (ni.isConnected())
                    hasConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("mobile"))
                if (ni.isConnected())
                    hasConnectedMobile = true;
        }
        return hasConnectedWifi || hasConnectedMobile;
    }

    @Override
    public void onFinish(RestClient restClient) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailureInternet(RestClient restClient) {
        showAlert(null, getString(R.string.alert), getString(R.string.internet_error_info));
    }

    @Override
    public void onFailure(RestClient restClient, int statusCode, String message) {
        showAlert(null, getString(R.string.alert), getString(R.string.connection_error_info));
    }

    @Override
    public void onError(RestClient restClient, String code, String message) {
        showAlert(null, getString(R.string.alert), message);
    }
}
