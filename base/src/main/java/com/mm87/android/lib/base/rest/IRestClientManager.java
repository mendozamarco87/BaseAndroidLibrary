package com.mm87.android.lib.base.rest;

public interface IRestClientManager {

	boolean getInternetState();

	void onFinish(RestClient restClient);

	void onFailureInternet(RestClient restClient);

	void onFailure(RestClient restClient, int statusCode, String message);

	void onError(RestClient restClient, String code, String message, String data);

	void showProgressDialog(RestClient restClient, CharSequence message);
}
