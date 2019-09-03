package com.mm87.android.lib.base.rest;

import android.annotation.TargetApi;
import android.os.Build;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


@TargetApi(Build.VERSION_CODES.KITKAT)
public abstract class RestClient extends JsonHttpResponseHandler {

    protected IRestClientManager iRestClientManager;
    protected boolean success;
    protected boolean finish;

    public RestClient(IRestClientManager iRestClientManager) {
        this.iRestClientManager = iRestClientManager;
    }

    public void setiRestClientManager(IRestClientManager iRestClientManager) {
        this.iRestClientManager = iRestClientManager;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFinish() {
        return finish;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        success = true;
        onSuccess(statusCode, response.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        success = true;
        onSuccess(statusCode, response.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        success = true;
        onSuccess(statusCode, responseString);
    }

    protected abstract String getURL();

    protected abstract int getTimeOut();

    protected abstract String getMethod();

    protected abstract void onSuccess(int statusCode, String response);

    @Override
    public void onFinish() {
        finish = true;
        if (iRestClientManager != null) {
            iRestClientManager.onFinish(this);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        onFailureIRestClient(statusCode, throwable.getMessage());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        onFailureIRestClient(statusCode, throwable.getMessage());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        onFailureIRestClient(statusCode, throwable.getMessage());
    }

    protected void onFailureIRestClient(int statusCode, String message) {
        if (iRestClientManager != null) {
            iRestClientManager.onFailure(this, statusCode, message);
        }
    }

    public void onError(String code, String error, String data) {
        if (iRestClientManager != null) {
            iRestClientManager.onError(this, code, error, data);
        }
    }

    protected void httpPostWithJson(JSONObject jsonObject) {
        try {
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
            AsyncHttpClientManager.allowCircularRedirects();
            AsyncHttpClientManager.setTimeOut(getTimeOut());
            AsyncHttpClientManager.post(null, getURL(), getMethod(), null,
                    stringEntity, "application/json", this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callPostWithJson(JSONObject jsonObject) {
        if (checkInternet()) {
            httpPostWithJson(jsonObject);
        }
    }

    public void callPostWithJsonAndShowProgress(JSONObject jsonObject, CharSequence message) {
        if (checkInternet()) {
            httpPostWithJson(jsonObject);
            if (iRestClientManager != null) {
                iRestClientManager.showProgressDialog(this, message);
            }
        }
    }

    protected void httpGet() {
        AsyncHttpClientManager.allowCircularRedirects();
        AsyncHttpClientManager.setTimeOut(getTimeOut());
        AsyncHttpClientManager.get(getURL(), getMethod(), this);
    }

    public void callGet() {
        if (checkInternet()) {
            httpGet();
        }
    }

    public void callGetAndShowProgress(CharSequence message) {
        if (checkInternet()) {
            httpGet();
            if (iRestClientManager != null) {
                iRestClientManager.showProgressDialog(this, message);
            }
        }
    }

    protected void httpPost() {
        AsyncHttpClientManager.allowCircularRedirects();
        AsyncHttpClientManager.setTimeOut(getTimeOut());
        AsyncHttpClientManager.post(null, getURL(), getMethod(), null, null,
                "application/json", this);
    }

    public void callPost() {
        if (checkInternet()) {
            httpPost();
        }
    }

    public void callPostAndShowProgress(CharSequence message) {
        if (checkInternet()) {
            httpPost();
            if (iRestClientManager != null) {
                iRestClientManager.showProgressDialog(this, message);
            }
        }
    }

    protected boolean checkInternet() {
        if (iRestClientManager == null) return true;
        if (iRestClientManager.getInternetState()) {
            return true;
        } else {
            iRestClientManager.onFailureInternet(this);
            return false;
        }
    }
}
