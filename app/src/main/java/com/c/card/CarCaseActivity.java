package com.c.card;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CarCaseActivity extends ListActivity implements
		OnItemClickListener {
	Cursor cursor;
	String logo, head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] from = { "logo", "name", "title", "address" };
		int[] to = { R.id.card_Logo, R.id.card_Name, R.id.card_Title,
				R.id.card_Address };
		DBAdapter dbAdapter = new DBAdapter(this);//�������ݿ�
		cursor = dbAdapter.queryAll();//����DBAdapter���queryAll()����
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.activity_carcase, cursor, from, to);
		setListAdapter(adapter);//ע��������
		this.getListView().setOnItemClickListener(this);
	}

	@Override //���ĳһ��Ƭ��Ϣʱ��ת��������ʹ�ֵ
	public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
		// TODO Auto-generated method stub
		cursor = (Cursor) this.getListView().getItemAtPosition(index);
		if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("logo"))) == 2130837507) {
			logo = "0";
		}
		if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("logo"))) == 2130837508) {
			logo = "1";
		}
		if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("logo"))) == 2130837509) {
			logo = "2";
		}
		if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("logo"))) == 2130837510) {
			logo = "3";
		}
		if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("logo"))) == 2130837511) {
			head = "0";
		}
		if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("logo"))) == 2130837505) {
			head = "1";
		}

		Intent intent = new Intent(CarCaseActivity.this, MainActivity.class);
		intent.putExtra("name", cursor.getString(cursor.getColumnIndex("name")));
		intent.putExtra("title",
				cursor.getString(cursor.getColumnIndex("title")));
		intent.putExtra("address",
				cursor.getString(cursor.getColumnIndex("address")));
		intent.putExtra("postcode",
				cursor.getString(cursor.getColumnIndex("postcode")));
		intent.putExtra("phone",
				cursor.getString(cursor.getColumnIndex("phone")));
		intent.putExtra("mailbox",
				cursor.getString(cursor.getColumnIndex("mailbox")));
		intent.putExtra("autograph",
				cursor.getString(cursor.getColumnIndex("autograph")));
		intent.putExtra("homepage",
				cursor.getString(cursor.getColumnIndex("homepahe")));
		intent.putExtra("logo", logo);
		intent.putExtra("head", head);
		startActivity(intent);
		finish();
	}

	@Override//��Ӳ˵�
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("CarCaseAcitivity", "menu--->" + menu);
		setIconEnable(menu, true);//4.0���ϰ汾Ĭ�Ϲرգ���Ҫ��
		getMenuInflater().inflate(R.menu.main_cardall, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

	//Ϊ4.0���ϰ汾����ͼ�귽��
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

	@Override//ɾ��������Ƭ
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_DelectAll) {
			new AlertDialog.Builder(CarCaseActivity.this)
			.setTitle("ȷ��ɾ��").setMessage("ȷ��ɾ��������Ƭ��")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					new DBAdapter(CarCaseActivity.this).delectAll();//����DBAdapter��delectAll()����
					Toast.makeText(CarCaseActivity.this,"������Ƭ��¼��ɾ��", Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(CarCaseActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
				}
			}).setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub

				}
			}).show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override//���·��ؼ�ʱ����ת��������
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			checkExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void checkExit() {
		Intent intent = new Intent(CarCaseActivity.this, MainActivity.class);

		startActivity(intent);
		finish();
	}

}
