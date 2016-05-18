package com.c.card;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import android.R.mipmap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Canvas extends Activity {
	public static Canvas instance;
	public static String logoId = null;
	public static String headId = null;
	public static String name = null;
	public static String title = null;
	public static String address = null;
	public static String postcode = null;
	public static String phone = null;
	public static String mailbox = null;
	public static String autograph = null;
	public static String homepage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		title = intent.getStringExtra("title");
		address = intent.getStringExtra("address");
		postcode = intent.getStringExtra("postcode");
		phone = intent.getStringExtra("phone");
		mailbox = intent.getStringExtra("mailbox");
		autograph = intent.getStringExtra("autograph");
		homepage = intent.getStringExtra("homepage");
		logoId = intent.getStringExtra("logo");
		headId = intent.getStringExtra("head");

		instance=this;
		setContentView(new MyView(this));

	}

	@Override
	// ���·��ؼ�ʱ����ת��������
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			checkExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void checkExit() {
		Intent intent = new Intent(Canvas.this, MainActivity.class);

		startActivity(intent);
		finish();
	}

	// ��Ӳ˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("Canvas", "menu--->" + menu);
		setIconEnable(menu, true);// 4.0���ϰ汾Ĭ�Ϲرգ���Ҫ��
		getMenuInflater().inflate(R.menu.main_canvas, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

	// Ϊ4.0���ϰ汾����ͼ�귽��
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

	//

	@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			if (item.getItemId() == R.id.action_Exit) {// �˳��˵�
				finish();
			}
			if(item.getItemId()==R.id.action_Save){//����canvas
				MyView.Save();
				Toast.makeText(Canvas.this, "�ѱ���ͼƬ", Toast.LENGTH_SHORT).show();
			}
			return super.onOptionsItemSelected(item);
		}
}
