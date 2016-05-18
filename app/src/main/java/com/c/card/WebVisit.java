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
		getWindow().requestFeature(Window.FEATURE_PROGRESS);// ��title bar��������
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);// ��title bar��������
		
		setContentView(R.layout.activity_web_visit);

		initWebView();
	}

	/**
	 * ��ʼ��webview
	 */
	private void initWebView() {
		// �õ�webView������
		Intent intent = getIntent();
		browserUrl = "http://"+intent.getStringExtra("URL");

		webView = (WebView) findViewById(R.id.webView);
		// ֧��JavaScript
		webView.getSettings().setJavaScriptEnabled(true);
		// ֧������
		webView.getSettings().setBuiltInZoomControls(true);
		// ֧�ֱ�������
		webView.getSettings().setSaveFormData(false);

		// �������
		webView.clearCache(true);
		// �����ʷ��¼
		webView.clearHistory();
		// ��������
		webView.loadUrl(browserUrl);

		// ����
		webView.setWebViewClient(new WebViewClient() {

			/** ��ʼ����ҳ�� */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setProgressBarIndeterminateVisibility(true);// ���ñ������Ĺ�������ʼ
				browserUrl = url;
				super.onPageStarted(view, url, favicon);
			}

			/** �������¼� */
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

			/** ���󷵻� */
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			/** ҳ��������� */
			@Override
			public void onPageFinished(WebView view, String url) {
				setProgressBarIndeterminateVisibility(false);// ���ñ������Ĺ�����ֹͣ
				super.onPageFinished(view, url);
			}

		});

		webView.setWebChromeClient(new WebChromeClient() {
			/** ���ý����� */
			public void onProgressChanged(WebView view, int newProgress) {
				// ���ñ������Ľ������İٷֱ�
				WebVisit.this.getWindow().setFeatureInt(
						Window.FEATURE_PROGRESS, newProgress * 100);
				super.onProgressChanged(view, newProgress);
			}

			/** ���ñ��� */
			public void onReceivedTitle(WebView view, String title) {
				WebVisit.this.setTitle(title);
				super.onReceivedTitle(view, title);
			}
		});

		
	}
	/**
	 * ����
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
	 * ���񷵻ؼ�
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack())) {
			menuBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
