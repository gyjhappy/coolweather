package com.coolweather.app.util;

public interface HttpCallbackListener {

	void onFinish(String responce);
	
	void onError(Exception e);
}
