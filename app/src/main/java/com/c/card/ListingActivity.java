package com.c.card;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.LiveFolders;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListingActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.activity_listing, new String[] { "message" },
				new int[] { R.id.listing });

		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Intent intent = getIntent();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("name"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("title"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("address"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("postcode"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("phone"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("mailbox"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("autograph"));
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("message", intent.getStringExtra("homepage"));
		list.add(map);
		return list;

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			checkExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void checkExit() {

		Intent intent = new Intent(ListingActivity.this, MainActivity.class);
		Intent intent1 = getIntent();

		intent.putExtra("name", intent1.getStringExtra("name"));
		intent.putExtra("title", intent1.getStringExtra("title"));
		intent.putExtra("address", intent1.getStringExtra("address"));
		intent.putExtra("postcode", intent1.getStringExtra("postcode"));
		intent.putExtra("phone", intent1.getStringExtra("phone"));
		intent.putExtra("mailbox", intent1.getStringExtra("mailbox"));
		intent.putExtra("autograph", intent1.getStringExtra("autograph"));
		intent.putExtra("homepage", intent1.getStringExtra("homepage"));

		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("ListingActivity", "menu--->" + menu);
		setIconEnable(menu, true);
		getMenuInflater().inflate(R.menu.main_activity01, menu);
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
		return super.onOptionsItemSelected(item);
	}
}
