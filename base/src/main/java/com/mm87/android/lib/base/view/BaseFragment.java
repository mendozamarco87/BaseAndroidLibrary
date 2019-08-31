package com.mm87.android.lib.base.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mm87.android.lib.base.R;
import com.mm87.android.lib.base.rest.IRestClientManager;
import com.mm87.android.lib.base.rest.RestClient;
import com.mm87.android.lib.base.util.ViewUtils;

import java.util.LinkedList;
import java.util.Queue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseFragment extends Fragment implements IBaseFragment, IRestClientManager {

    protected FragmentActivity activity;
    protected boolean isResumed;
    protected ProgressDialog progressDialog;
    private Queue<Pair<DialogFragment, String>> dialogQueue = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.activity = getActivity();
        View view = inflater.inflate(getIdResLayout(), container, false);
        ViewUtils.bindViewAnnotation(getActivity(), this, view);
        init(savedInstanceState, view);

        return view;
    }

    protected abstract int getIdResLayout();

    protected abstract void init(Bundle savedInstanceState, View v);

    @Override
    public void onResume() {
        super.onResume();
        while (!dialogQueue.isEmpty()) {
            Pair<DialogFragment, String> pair = dialogQueue.poll();
            pair.first.show(this.activity.getSupportFragmentManager(), pair.second);
        }
    }

    public boolean isInBackStackFR() {
        for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++) {
            if (getFragmentManager().getBackStackEntryAt(i).getName()
                    .equals(getTag())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void showAlert(final IDialogOk listener, CharSequence title, CharSequence message) {
        showAlert(listener, title, message, getString(R.string.ok), getString(R.string.cancel));
    }

    @Override
    public void showAlert(final IDialogOk listener, CharSequence title, CharSequence message, CharSequence okButtonName, CharSequence cancelButtonName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity)
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
        if (isResumed()) {
            dialog.show(this.activity.getSupportFragmentManager(), TAG);
        } else {
            dialogQueue.add(new Pair<>(dialog, TAG));
        }
    }

    @Override
    public void showProgressDialog(CharSequence message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this.activity);
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

    public void runOnUiThread(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
    }

    @Override
    public boolean getInternetState() {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);

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
