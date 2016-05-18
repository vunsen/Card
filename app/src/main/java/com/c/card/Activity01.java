package com.c.card;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
//import android.support.v4.widget.SearchViewCompatIcs.MySearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity01 extends Activity {

	private Gallery ga, ga_head;
	private int logo_index, head_index;
	private String logoId, headId;
	private Integer[] myImageIds = new MyImage().ImageId();
	private Integer[] headImageIds = new MyImage().headImageId();

	DBAdapter dbAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity01);

		// ���������洫����ֵ
		final Intent intent = getIntent();
		logoId = intent.getStringExtra("logo");
		headId = intent.getStringExtra("head");
		((EditText) findViewById(R.id.et_Name)).setText(intent
				.getStringExtra("name"));
		((EditText) findViewById(R.id.et_Title)).setText(intent
				.getStringExtra("title"));
		((EditText) findViewById(R.id.et_Address)).setText(intent
				.getStringExtra("address"));
		((EditText) findViewById(R.id.et_Postcode)).setText(intent
				.getStringExtra("postcode"));
		((EditText) findViewById(R.id.et_Phone)).setText(intent
				.getStringExtra("phone"));
		((EditText) findViewById(R.id.et_Mailbox)).setText(intent
				.getStringExtra("mailbox"));
		((EditText) findViewById(R.id.et_Autograph)).setText(intent
				.getStringExtra("autograph"));
		((EditText) findViewById(R.id.et_Homepage)).setText(intent
				.getStringExtra("homepage"));
		//

		// ����������
		// logo������
		ga = (Gallery) findViewById(R.id.gallery);// ��ȡGallery
		ga.setAdapter(new ImageAdapter(this));// ע��������
		ga.setOnItemSelectedListener(new OnItemSelectedListener() {// ��ȡѡȡͼƬ���±�

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				logo_index = (position + Integer.parseInt(logoId))
						% myImageIds.length;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
		//

		// ͷ��������
		ga_head = (Gallery) findViewById(R.id.gallery_head);// ��ȡGallery
		ga_head.setAdapter(new ImageAdapter01(this));// ע��������
		ga_head.setOnItemSelectedListener(new OnItemSelectedListener() {// ��ȡѡȡͼƬ���±�

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				head_index = (position + Integer.parseInt(headId))
						% headImageIds.length;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
		//

		Button bt01 = (Button) findViewById(R.id.bt_Cancel); // ��ȡȡ����ť
		Button bt02 = (Button) findViewById(R.id.bt_Confirm);// ��ȡȷ����ť
		dbAdapter = new DBAdapter(this);

		bt01.setOnClickListener(new View.OnClickListener() {// ȡ����ť���������������洫����ֵ�ٴ���ȥ

			@Override
			public void onClick(View v) {
				Intent intent11 = getIntent();
				Intent intent1 = new Intent(Activity01.this, MainActivity.class);
				intent1.putExtra("name", intent11.getStringExtra("name"));
				intent1.putExtra("title", intent11.getStringExtra("title"));
				intent1.putExtra("address", intent11.getStringExtra("address"));
				intent1.putExtra("postcode",
						intent11.getStringExtra("postcode"));
				intent1.putExtra("phone", intent11.getStringExtra("phone"));
				intent1.putExtra("mailbox", intent11.getStringExtra("mailbox"));
				intent1.putExtra("autograph",
						intent11.getStringExtra("autograph"));
				intent1.putExtra("homepage",
						intent11.getStringExtra("homepage"));
				intent1.putExtra("logo", logoId);
				intent1.putExtra("head", headId);
				startActivity(intent1);
				finish();

			}
		});

		// ȷ����ť������
		bt02.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// ����ȷ����ť���ѱ༭�õ����ݱ��浽���ݿ�
				People p = new People();
				p.Name = ((EditText) findViewById(R.id.et_Name)).getText()
						.toString();
				p.Title = ((EditText) findViewById(R.id.et_Title)).getText()
						.toString();
				p.Address = ((EditText) findViewById(R.id.et_Address))
						.getText().toString();
				p.Postcode = ((EditText) findViewById(R.id.et_Postcode))
						.getText().toString();
				p.Phone = ((EditText) findViewById(R.id.et_Phone)).getText()
						.toString();
				p.Mailbox = ((EditText) findViewById(R.id.et_Mailbox))
						.getText().toString();
				p.Autograph = ((EditText) findViewById(R.id.et_Autograph))
						.getText().toString();
				p.Homepahe = ((EditText) findViewById(R.id.et_Homepage))
						.getText().toString();
				p.Logo = new String(myImageIds[(logo_index)] + "");
				p.Head = new String(headImageIds[(head_index)] + "");
				if (dbAdapter.insert(p) > 0) {
					Log.i("���ݿ���Ϣ", "�����¼�ɹ�");
				} else {
					Log.i("���ݿ���Ϣ", "�����¼ʧ��");
				}
				if (dbAdapter.update(((EditText) findViewById(R.id.et_Name))
						.getText().toString(), p) > 0) {
					Log.i("���ݿ���Ϣ", "���¼�¼�ɹ�");
				} else {
					Log.i("���ݿ���Ϣ", "���¼�¼ʧ��");
				}

				// �ѱ༭�õ����ݴ���������
				Intent intent2 = new Intent(Activity01.this, MainActivity.class);
				intent2.putExtra("name",
						((EditText) findViewById(R.id.et_Name)).getText()
								.toString());
				intent2.putExtra("title",
						((EditText) findViewById(R.id.et_Title)).getText()
								.toString());
				intent2.putExtra("address",
						((EditText) findViewById(R.id.et_Address)).getText()
								.toString());
				intent2.putExtra("postcode",
						((EditText) findViewById(R.id.et_Postcode)).getText()
								.toString());
				intent2.putExtra("phone",
						((EditText) findViewById(R.id.et_Phone)).getText()
								.toString());
				intent2.putExtra("mailbox",
						((EditText) findViewById(R.id.et_Mailbox)).getText()
								.toString());
				intent2.putExtra("autograph",
						((EditText) findViewById(R.id.et_Autograph)).getText()
								.toString());
				intent2.putExtra("homepage",
						((EditText) findViewById(R.id.et_Homepage)).getText()
								.toString());
				intent2.putExtra("logo", new String((logo_index) + ""));
				intent2.putExtra("head", new String((head_index) + ""));
				startActivity(intent2);
				finish();

			}
		});
		//

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		dbAdapter.open();// �����ݿ�
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		dbAdapter.close();// �ر����ݿ�
		super.onStop();
	}

	public class ImageAdapter extends BaseAdapter { // �������ڲ��෽��
		int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c) { // ������
			mContext = c;
		}

		@Override
		public int getCount() { // ѭ��ͼƬ����
			// TODO Auto-generated method stub
			return myImageIds.length;
		}

		// ��д����getItemId������ͼ������id
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// ����ImageView ����
			ImageView imageview = new ImageView(mContext);
			// ����ͼƬ��ImageView����
			imageview.setImageResource(myImageIds[(position + Integer
					.parseInt(logoId)) % myImageIds.length]);
			// ��������ͼƬ�Ŀ�͸�
			imageview.setScaleType(ImageView.ScaleType.FIT_XY);
			// ��������Layout�Ŀ�͸�
			imageview.setLayoutParams(new Gallery.LayoutParams(300, 100));
			// ����Gallery�ı���ͼ
			imageview.setBackgroundResource(mGalleryItemBackground);
			// ����ImageView����
			return imageview;
		}

	}

	public class ImageAdapter01 extends BaseAdapter {// �������ڲ��෽��
		int mGalleryItemBackground;
		private Context mContext01;

		public ImageAdapter01(Context c) { // ������
			mContext01 = c;
		}

		@Override
		public int getCount() {// ѭ��ͼƬ����
			// TODO Auto-generated method stub
			return headImageIds.length;
		}

		// ��д����getItemId������ͼ������id
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// ����ImageView ����
			ImageView imageview01 = new ImageView(mContext01);
			// ����ͼƬ��ImageView����
			imageview01.setImageResource(headImageIds[(position + Integer
					.parseInt(headId)) % headImageIds.length]);
			// ��������Layout�Ŀ�͸�
			imageview01.setScaleType(ImageView.ScaleType.FIT_XY);
			// ��������Layout�Ŀ�͸�
			imageview01.setLayoutParams(new Gallery.LayoutParams(200, 200));
			// ����Gallery�ı���ͼ
			imageview01.setBackgroundResource(mGalleryItemBackground);
			// ����ImageView����
			return imageview01;
		}

	}

	// ���°����¼�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			checkExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void checkExit() {
		Intent intent11 = getIntent();
		Intent intent = new Intent(Activity01.this, MainActivity.class);

		intent.putExtra("name", intent11.getStringExtra("name"));
		intent.putExtra("title", intent11.getStringExtra("title"));
		intent.putExtra("address", intent11.getStringExtra("address"));
		intent.putExtra("postcode", intent11.getStringExtra("postcode"));
		intent.putExtra("phone", intent11.getStringExtra("phone"));
		intent.putExtra("mailbox", intent11.getStringExtra("mailbox"));
		intent.putExtra("autograph", intent11.getStringExtra("autograph"));
		intent.putExtra("homepage", intent11.getStringExtra("homepage"));
		intent.putExtra("logo", logoId);
		intent.putExtra("head", headId);
		startActivity(intent);
		finish();
	}
	//

	// ��Ӳ˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("Activity01", "menu--->" + menu);
		setIconEnable(menu, true);// 4.0���ϰ汾Ĭ�Ϲرգ���Ҫ��
		getMenuInflater().inflate(R.menu.main_activity01, menu);
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
		return super.onOptionsItemSelected(item);
	}

}
