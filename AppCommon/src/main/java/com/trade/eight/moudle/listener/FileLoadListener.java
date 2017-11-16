package com.trade.eight.moudle.listener;


public interface FileLoadListener {

	void onLoadingStarted();

	void onLoadingFailed();

	void onLoadingComplete();

	void onLoadingCancelled();
}