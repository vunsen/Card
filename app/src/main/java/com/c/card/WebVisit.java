package com.c.card;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebVisit extends Activity {

	private WebView webView;
	private String browserUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);// 用title bar做进度条
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);// 用title bar做滚动条
		
		setContentView(R.layout.activity_web_visit);

		initWebView();
	}

	/**
	 * 初始化webview
	 */
	private void initWebView() {
		// 得到webView的引用
		Intent intent = getIntent();
		browserUrl = "http://"+intent.getStringExtra("URL");

		webView = (WebView) findViewById(R.id.webView);
		// 支持JavaScript
		webView.getSettings().setJavaScriptEnabled(true);
		// 支持缩放
		webView.getSettings().setBuiltInZoomControls(true);
		// 支持保存数据
		webView.getSettings().setSaveFormData(false);

		// 清除缓存
		webView.clearCache(true);
		// 清除历史记录
		webView.clearHistory();
		// 联网载入
		webView.loadUrl(browserUrl);

		// 设置
		webView.setWebViewClient(new WebViewClient() {

			/** 开始载入页面 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setProgressBarIndeterminateVisibility(true);// 设置标题栏的滚动条开始
				browserUrl = url;
				super.onPageStarted(view, url, favicon);
			}

			/** 捕获点击事件 */
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

			/** 错误返回 */
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			/** 页面载入完毕 */
			@Override
			public void onPageFinished(WebView view, String url) {
				setProgressBarIndeterminateVisibility(false);// 设置标题栏的滚动条停止
				super.onPageFinished(view, url);
			}

		});

		webView.setWebChromeClient(new WebChromeClient() {
			/** 设置进度条 */
			public void onProgressChanged(WebView view, int newProgress) {
				// 设置标题栏的进度条的百分比
				WebVisit.this.getWindow().setFeatureInt(
						Window.FEATURE_PROGRESS, newProgress * 100);
				super.onProgressChanged(view, newProgress);
			}

			/** 设置标题 */
			public void onReceivedTitle(WebView view, String title) {
				WebVisit.this.setTitle(title);
				super.onReceivedTitle(view, title);
			}
		});

		
	}
	/**
	 * 返回
	 */
	private void menuBack(){
		webView.goBack();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("WebVisit", "menu--->" + menu);
		setIconEnable(menu, true);
		getMenuInflater().inflate(R.menu.main_web, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);
			m.invoke(menu, enable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_Exit) {
			finish();
		}
		if(item.getItemId()==R.id.action_Return){
			Intent intent=new Intent(WebVisit.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 捕获返回键
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack())) {
			menuBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
