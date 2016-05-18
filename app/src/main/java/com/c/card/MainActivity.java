package com.c.card;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData.Item;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.drm.DrmStore.Action;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.text.BidiFormatter;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	private static final int M_NAME = 10;
	private static final int M_TITLE = 20;
	private static final int M_ADDRESS = 30;
	private static final int M_POSTCODE = 40;
	private static final int M_PHONE = 50;
	private static final int M_MAILBOX = 60;
	private static final int M_AUTOGRAPH = 70;
	private static final int M_HOMEPAHE = 80;
	private static final int M_LOGO = 90;
	private static final int M_HEAD = 100;
	private FileOutputStream fosSDCard;
	String logoId, headId;
	private String logoTemp, headTemp;
	private Integer[] myImageIds = new MyImage().ImageId();
	private Integer[] headImageIds = new MyImage().headImageId();
	SMSReceiver smsReceiver;

	String city;
	String place;
	String weather;
	/** ý�岥���� */
	private MediaPlayer mediaPlayer;
	/** �Ƿ�ʼ�ı�ʶ */
	private boolean isStart = false;

	/** �Ի���ʵ�� */
	private AlertDialog sendSMSDialog;
	/** �Զ���ACTION��������Ϊ�㲥��Intent Filterʶ���� */
	private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	/** ���������㲥���� */
	private MyBroadcastReceiver myBroadcastReceiver1;
	private MyBroadcastReceiver myBroadcastReceiver2;
	private StringBuilder sb;

	/** ������Ӧ������ */
	private SensorManager sensorManager;

	private Sensor accelerometerSensor;
	// �ζ�������Ӧ��һ��ʱ��
	private long lastUpdate = -1;
	// �ζ�������Ӧ��������
	private float x, y, z;
	// �ζ�������Ӧ����һ������
	private float last_x, last_y, last_z;
	// �ζ�������Ӧ������
	private static final int SHAKE_THRESHOLD = 300;
	Bitmap logo = null;
	Bitmap head = null;
	Bitmap bmLogo = null;
	Bitmap bmHead = null;
	DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ȡ�����ļ�
		try {

			// ��ȡ�洢��ROM����Ƭ��Ϣ
			FileInputStream fin = openFileInput("test.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			((TextView) findViewById(R.id.Name)).setText(br.readLine());
			((TextView) findViewById(R.id.Title)).setText(br.readLine());
			((TextView) findViewById(R.id.Address)).setText(br.readLine());
			((TextView) findViewById(R.id.Postcode)).setText(br.readLine());
			((TextView) findViewById(R.id.Phone)).setText(br.readLine());
			((TextView) findViewById(R.id.Mailbox)).setText(br.readLine());
			((TextView) findViewById(R.id.Autograph)).setText(br.readLine());
			((TextView) findViewById(R.id.Homepage)).setText(br.readLine());

			logoTemp = br.readLine();
			((ImageView) findViewById(R.id.img_logo))
					.setImageResource(myImageIds[Integer.parseInt(logoTemp)]);
			FileInputStream logoFile=new FileInputStream(logoTemp);
			BufferedInputStream logoBm=new BufferedInputStream(logoFile);
			bmLogo = BitmapFactory.decodeStream(logoBm);
			if (bmLogo != null) {
				((ImageView) findViewById(R.id.img_logo))
						.setImageBitmap(bmLogo);
			}

			headTemp = br.readLine();
			((ImageView) findViewById(R.id.img_me))
					.setImageResource(headImageIds[Integer.parseInt(headTemp)]);
			br.close();
			fin.close();

			// ��ȡ�洢��SD������Ƭ��Ϣ
			FileInputStream finSDCard = new FileInputStream("SDCardtest.txt");
			BufferedReader brSDCard = new BufferedReader(new InputStreamReader(
					fin));
			((TextView) findViewById(R.id.Name)).setText(brSDCard.readLine());
			((TextView) findViewById(R.id.Title)).setText(brSDCard.readLine());
			((TextView) findViewById(R.id.Address))
					.setText(brSDCard.readLine());
			((TextView) findViewById(R.id.Postcode)).setText(brSDCard
					.readLine());
			((TextView) findViewById(R.id.Phone)).setText(brSDCard.readLine());
			((TextView) findViewById(R.id.Mailbox))
					.setText(brSDCard.readLine());
			((TextView) findViewById(R.id.Autograph)).setText(brSDCard
					.readLine());
			((TextView) findViewById(R.id.Homepage)).setText(brSDCard
					.readLine());
			logoTemp = brSDCard.readLine();
			((ImageView) findViewById(R.id.img_logo))
					.setImageResource(myImageIds[Integer.parseInt(logoTemp)]);
			logoFile=new FileInputStream(logoTemp);
			logoBm=new BufferedInputStream(logoFile);
			bmLogo = BitmapFactory.decodeFile(logoTemp);
			if (bmLogo != null) {
				((ImageView) findViewById(R.id.img_logo))
						.setImageBitmap(bmLogo);
			}
			headTemp = brSDCard.readLine();
			((ImageView) findViewById(R.id.img_me))
					.setImageResource(headImageIds[Integer.parseInt(headTemp)]);
			brSDCard.close();
			finSDCard.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		//

		// ���ܸ���Activity������ֵ
		Intent intent = getIntent();

		logoId = intent.getStringExtra("logo");
		headId = intent.getStringExtra("head");
		if (logoId == null) {
			logoId = logoTemp;
		}
		if (headId == null) {
			headId = headTemp;
		}
		if (intent.getStringExtra("name") != null) {
			((TextView) findViewById(R.id.Name)).setText(intent
					.getStringExtra("name"));
			((TextView) findViewById(R.id.Title)).setText(intent
					.getStringExtra("title"));
			((TextView) findViewById(R.id.Address)).setText(intent
					.getStringExtra("address"));
			((TextView) findViewById(R.id.Postcode)).setText(intent
					.getStringExtra("postcode"));
			((TextView) findViewById(R.id.Phone)).setText(intent
					.getStringExtra("phone"));
			((TextView) findViewById(R.id.Mailbox)).setText(intent
					.getStringExtra("mailbox"));
			((TextView) findViewById(R.id.Autograph)).setText(intent
					.getStringExtra("autograph"));
			((TextView) findViewById(R.id.Homepage)).setText(intent
					.getStringExtra("homepage"));

			((ImageView) findViewById(R.id.img_logo))
					.setImageResource(myImageIds[Integer.parseInt(logoId)]);
			((ImageView) findViewById(R.id.img_me))
					.setImageResource(headImageIds[Integer.parseInt(headId)]);
		}

		File file = new File("/sdcard/CardImg/photo");
		if (!file.exists())
			file.mkdirs();

		// ע�������Ĳ˵�
		registerForContextMenu(findViewById(R.id.Name));
		registerForContextMenu(findViewById(R.id.Title));
		registerForContextMenu(findViewById(R.id.Address));
		registerForContextMenu(findViewById(R.id.Postcode));
		registerForContextMenu(findViewById(R.id.Phone));
		registerForContextMenu(findViewById(R.id.Mailbox));
		registerForContextMenu(findViewById(R.id.Autograph));
		registerForContextMenu(findViewById(R.id.Homepage));
		registerForContextMenu(findViewById(R.id.img_logo));
		registerForContextMenu(findViewById(R.id.img_me));

		// �������ݿ�
		dbAdapter = new DBAdapter(this);

		// ��鵱ǰ����
		try {// ����ֻ����磬�׳��쳣
			int netReturn = checkNetworkInfo();
			if (netReturn > 0) {
				if (android.os.Build.VERSION.SDK_INT >= 14) {
					StrictMode
							.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
									.detectDiskReads().detectDiskWrites()
									.detectNetwork().penaltyLog().build());
					StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
							.detectLeakedSqlLiteObjects()
							.detectLeakedClosableObjects().penaltyLog()
							.penaltyDeath().build());
				}
				selectCity(((TextView) findViewById(R.id.Postcode)).getText()
						.toString().substring(0, 6));
				selectPlace(((TextView) findViewById(R.id.Phone)).getText()
						.toString().substring(0, 11));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//

		//
		try {
			if (!isStart) {// ��ֹ�����������ļ�
				playFromRawFile(R.raw.horizon);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//

		// �ѵ�ǰ��Ƭ�༭�ɶ�����Ϣ
		sb = new StringBuilder();
		sb.append("card;")
				.append(((TextView) findViewById(R.id.Name)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Title)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Address)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Postcode)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Phone)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Mailbox)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Autograph)).getText()
						.toString())
				.append(";")
				.append(((TextView) findViewById(R.id.Homepage)).getText()
						.toString()).append(";");
		//

	}

	public void selectCity(final String postcode) {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ��ѯ�ʱ����ڵ���
				try {
					String SERVER_URL_Postcode = "http://webservice.webxml.com.cn/WebServices/ChinaZipSearchWebService.asmx/getAddressByZipCode";// ������Ҫ��ȡ��������Դ��ַ
					final HttpPost request_Postcode = new HttpPost(
							SERVER_URL_Postcode);// ����������Դ��ַ����һ��Http����
					List<BasicNameValuePair> params_Postcode = new ArrayList<BasicNameValuePair>();
					params_Postcode.add(new BasicNameValuePair("theZipCode",
							postcode));// ��ӱ���Ĳ���

					params_Postcode.add(new BasicNameValuePair("userID", ""));// ��ӱ���Ĳ���
					request_Postcode.setEntity(new UrlEncodedFormEntity(
							params_Postcode, HTTP.UTF_8)); // ���ò����ı���
					final HttpClient httpClient_Postcode = new DefaultHttpClient();
					// �������󲢻�ȡ����
					HttpResponse httpResponse = httpClient_Postcode
							.execute(request_Postcode);
					// �������ص�����
					if (httpResponse.getStatusLine().getStatusCode() != 404) {
						String postcodeResult = EntityUtils
								.toString(httpResponse.getEntity());
						byte[] PC_data = postcodeResult.getBytes();
						ByteArrayInputStream Byte_Postcode = new ByteArrayInputStream(
								PC_data);
						city = pullParserPostcode(Byte_Postcode);
						((TextView) findViewById(R.id.Postcode))
								.setText(postcode + "(" + city + ")");
						if (city.endsWith("��")) {
							city = city.substring(0, city.length() - 1);

						}
						selectWeather(city);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
		//
	}

	public void selectWeather(final String city) {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ��ѯ���ڵ�������
				try {

					String SERVER_URL_City = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asxm/getWeather";// ������Ҫ��ȡ��������Դ��ַ
					final HttpPost request_City = new HttpPost(SERVER_URL_City);// ����������Դ��ַ����һ��Http����
					List<BasicNameValuePair> params_City = new ArrayList<BasicNameValuePair>();
					params_City
							.add(new BasicNameValuePair("theCityName", city));// ��ӱ���Ĳ���
					params_City.add(new BasicNameValuePair("theUserID", ""));// ��ӱ���Ĳ���
					request_City.setEntity(new UrlEncodedFormEntity(
							params_City, HTTP.UTF_8)); // ���ò����ı���
					final HttpClient httpClient_Postcode = new DefaultHttpClient();
					// �������󲢻�ȡ����
					HttpResponse httpResponse_City = httpClient_Postcode
							.execute(request_City);
					// �������ص�����
					if (httpResponse_City.getStatusLine().getStatusCode() != 404) {
						String postcodeResult = EntityUtils
								.toString(httpResponse_City.getEntity());
						byte[] City_data = postcodeResult.getBytes();
						ByteArrayInputStream Byte_City = new ByteArrayInputStream(
								City_data);
						weather = pullParserCity(Byte_City);
						((TextView) findViewById(R.id.Weather))
								.setText(weather);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
		//
	}

	public void selectPlace(final String phone) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ��ѯ�ֻ����������
				try {
					String SERVER_URL_Phone = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";// ������Ҫ��ȡ��������Դ��ַ
					HttpPost request_Phone = new HttpPost(SERVER_URL_Phone);// ����������Դ��ַ����һ��Http����
					List<BasicNameValuePair> params_Phone = new ArrayList<BasicNameValuePair>();
					params_Phone.add(new BasicNameValuePair("mobileCode", phone
							.substring(0, 11)));// ��ӱ���Ĳ���
					params_Phone.add(new BasicNameValuePair("userID", ""));// ��ӱ���Ĳ���
					request_Phone.setEntity(new UrlEncodedFormEntity(
							params_Phone, HTTP.UTF_8)); // ���ò����ı���
					HttpClient httpClient_Phone = new DefaultHttpClient();
					// �������󲢻�ȡ����
					HttpResponse httpResponse_PC = httpClient_Phone
							.execute(request_Phone);
					if (httpResponse_PC.getStatusLine().getStatusCode() != 404) {
						String result = EntityUtils.toString(httpResponse_PC
								.getEntity());
						// ���յ���������ʾ���ؼ���
						byte[] PH_data = result.getBytes();
						ByteArrayInputStream Byte_Phone = new ByteArrayInputStream(
								PH_data);
						String place = pullParserPhone(Byte_Phone);
						((TextView) findViewById(R.id.Phone)).setText(place);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
		//
	}

	// �����ʱ����ڵ���XML
	private String pullParserPostcode(InputStream Byte_Postcode)
			throws Exception {
		String City = null;
		// TODO Auto-generated method stub
		// ����pull��������
		XmlPullParser parser = Xml.newPullParser();
		// ��xml�ļ�������������ʽ���ݸ� pull�������󣬲����ý��������� utf-8 �ı�����н���
		parser.setInput(Byte_Postcode, "UTF-8");
		// pull���������ڽ���xmlʱ���᷵��һ���������λ�õ�ֵ��
		// START_DOCUMENT �� ��ʼ�����ĵ�
		// START_TAG �� ��ʼ������ǩ
		// END_TAG : ��ǩ����
		// END_DOCUMENT ��xml�ĵ���������
		// �õ���ǰ������λ��
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			// switch ������λ��
			switch (type) {
			// ��ʼ�����ĵ���ʱ�򣬳�ʼ�����󼯺�
			case XmlPullParser.START_DOCUMENT:
				City = null;
				break;
			// ��ʼ������ǩ��ʱ�򣬸��ݱ�ǩ�Ĳ�ͬ���ơ�����ͬ����
			case XmlPullParser.START_TAG:
				// ��ǩΪ ��ʱ��ȡ����ǩ�����Գ���ֵ�������浽Person������
				if ("CITY".equals(parser.getName())) {
					City = parser.nextText();
				}
				break;
			// ����������ǩ������ʱ��ִ��
			case XmlPullParser.END_TAG:
				// ���������ǩ����person����ô��Person���󱣴浽personList��
				if ("CITY".equals(parser.getName())) {
					break;
				}
			}
			// ��ǰ����λ�ý�����ָ����һ��λ��
			type = parser.next();
		}
		Byte_Postcode.close();
		// ���ػ�ȡ��ֵ
		return City;
	}

	//

	// ��������XML�ļ�
	private String pullParserCity(InputStream Byte_City) throws Exception {
		int i = 0;
		String Weather = null;
		StringBuilder sb = new StringBuilder();
		// TODO Auto-generated method stub
		// ����pull��������
		XmlPullParser parser = Xml.newPullParser();
		// ��xml�ļ�������������ʽ���ݸ� pull�������󣬲����ý��������� utf-8 �ı�����н���
		parser.setInput(Byte_City, "UTF-8");
		// pull���������ڽ���xmlʱ���᷵��һ���������λ�õ�ֵ��
		// START_DOCUMENT �� ��ʼ�����ĵ�
		// START_TAG �� ��ʼ������ǩ
		// END_TAG : ��ǩ����
		// END_DOCUMENT ��xml�ĵ���������
		// �õ���ǰ������λ��
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			// switch ������λ��
			switch (type) {
			// ��ʼ�����ĵ���ʱ�򣬳�ʼ�����󼯺�
			case XmlPullParser.START_DOCUMENT:
				Weather = null;
				break;
			// ��ʼ������ǩ��ʱ�򣬸��ݱ�ǩ�Ĳ�ͬ���ơ�����ͬ����
			case XmlPullParser.START_TAG:
				// ��ǩΪ ��ʱ��ȡ����ǩ�����Գ���ֵ�������浽Person������
				if ("string".equals(parser.getName()) && (i == 0 || i == 4)) {
					Weather = parser.nextText();
					sb.append(Weather).append(" ");

				}
				break;
			// ����������ǩ������ʱ��ִ��
			case XmlPullParser.END_TAG:
				// ���������ǩ����person����ô��Person���󱣴浽personList��
				if ("string".equals(parser.getName())) {
					break;
				}
			}
			// ��ǰ����λ�ý�����ָ����һ��λ��
			type = parser.next();
			i++;
		}
		Byte_City.close();
		// ���ػ�ȡ��ֵ
		return sb.toString();
	}

	//

	// �����绰������XML�ļ�
	private String pullParserPhone(InputStream Byte_Phone) throws Exception {
		String place = null;
		// TODO Auto-generated method stub
		// ����pull��������
		XmlPullParser parser = Xml.newPullParser();
		// ��xml�ļ�������������ʽ���ݸ� pull�������󣬲����ý��������� utf-8 �ı�����н���
		parser.setInput(Byte_Phone, "UTF-8");
		// pull���������ڽ���xmlʱ���᷵��һ���������λ�õ�ֵ��
		// START_DOCUMENT �� ��ʼ�����ĵ�
		// START_TAG �� ��ʼ������ǩ
		// END_TAG : ��ǩ����
		// END_DOCUMENT ��xml�ĵ���������
		// �õ���ǰ������λ��
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			// switch ������λ��
			switch (type) {
			// ��ʼ�����ĵ���ʱ�򣬳�ʼ�����󼯺�
			case XmlPullParser.START_DOCUMENT:
				place = null;
				break;
			// ��ʼ������ǩ��ʱ�򣬸��ݱ�ǩ�Ĳ�ͬ���ơ�����ͬ����
			case XmlPullParser.START_TAG:
				// ��ǩΪ ��ʱ��ȡ����ǩ�����Գ���ֵ�������浽Person������
				if ("string".equals(parser.getName())) {
					place = parser.nextText();
				}
				break;
			// ����������ǩ������ʱ��ִ��
			case XmlPullParser.END_TAG:
				// ���������ǩ����person����ô��Person���󱣴浽personList��
				if ("string".equals(parser.getName())) {
					break;
				}
			}
			// ��ǰ����λ�ý�����ָ����һ��λ��
			type = parser.next();
		}
		Byte_Phone.close();
		// ���ػ�ȡ��ֵ
		return place;
	}

	//

	// ������緽��
	private int checkNetworkInfo() {
		// TODO Auto-generated method stub
		int netReturn = 0;
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = conMan.getActiveNetworkInfo();// ��ȡ������ͨ��
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();// ��ȡ3G������ͨ�ԣ�ֵCONNECTED
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();// ��ȡWIFI������ͨ�ԣ�ֵCONNECTED

		if (networkinfo == null || !networkinfo.isAvailable()) {// ����Ϊ�ջ��߲���ͨ
			Toast.makeText(this, "û�п���ʹ�õ�����", Toast.LENGTH_SHORT).show();
			netReturn = 0;
		}

		if (mobile.toString().equals("CONNECTED")) {
			Toast.makeText(this, "��ǰ�������ӵ���3G", Toast.LENGTH_SHORT).show();
			netReturn = 1;
		}

		if (wifi.toString().equals("CONNECTED")) {
			Toast.makeText(this, "��ǰ�������ӵ���wifi", Toast.LENGTH_SHORT).show();
			netReturn = 2;
		}
		return netReturn;
	}

	@Override
	// ��Ӳ˵�
	public boolean onCreateOptionsMenu(Menu menu) {

		Log.d("MainActivity", "menu--->" + menu);
		setIconEnable(menu, true);// 4.0���ϰ汾Ĭ�Ϲرգ���Ҫ��
		getMenuInflater().inflate(R.menu.main, menu);
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

	// �˵���ѡ�񷽷�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		// ����༭��Ƭ�˵�
		if (item.getItemId() == R.id.action_Edit) {
			Intent intent = new Intent(MainActivity.this, Activity01.class);

			intent.putExtra("name", ((TextView) findViewById(R.id.Name))
					.getText().toString());
			intent.putExtra("title", ((TextView) findViewById(R.id.Title))
					.getText().toString());
			intent.putExtra("address", ((TextView) findViewById(R.id.Address))
					.getText().toString());
			if (((TextView) findViewById(R.id.Postcode)).getText().length() > 6) {
				intent.putExtra("postcode",
						((TextView) findViewById(R.id.Postcode)).getText()
								.toString().substring(0, 6));
			} else {
				intent.putExtra("postcode",
						((TextView) findViewById(R.id.Postcode)).getText()
								.toString());
			}
			if (((TextView) findViewById(R.id.Phone)).getText().length() > 11) {
				intent.putExtra("phone", ((TextView) findViewById(R.id.Phone))
						.getText().toString().substring(0, 11));
			} else {
				intent.putExtra("phone", ((TextView) findViewById(R.id.Phone))
						.getText().toString());
			}

			intent.putExtra("mailbox", ((TextView) findViewById(R.id.Mailbox))
					.getText().toString());
			intent.putExtra("autograph",
					((TextView) findViewById(R.id.Autograph)).getText()
							.toString());
			intent.putExtra("homepage",
					((TextView) findViewById(R.id.Homepage)).getText()
							.toString());
			if (logoId == null) {
				logoId = "0";
			}
			if (headId == null) {
				headId = "0";
			}
			intent.putExtra("logo", logoId);
			intent.putExtra("head", headId);

			startActivity(intent);
			finish();
		}
		//

		// ������Ƭ����
		if (item.getItemId() == R.id.action_Send) {

			sendCustomerSMS("", sb.toString());
		}
		//

		// ɾ����ǰ��Ƭ�˵�
		if (item.getItemId() == R.id.action_Delect) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("ɾ����Ƭ")
					.setMessage("���Ҫɾ����")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// ����ɾ��������Ƭ����
									dbAdapter
											.delectOne(((TextView) findViewById(R.id.Name))
													.getText().toString());
									Toast.makeText(MainActivity.this, "��ɾ����Ƭ",
											Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(
											MainActivity.this,
											CarCaseActivity.class);
									startActivity(intent);
									finish();
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).show();
		}
		//

		// ȫ����Ƭ�˵�
		if (item.getItemId() == R.id.action_CardAll) {
			Intent intent = new Intent(MainActivity.this, CarCaseActivity.class);
			startActivity(intent);
			finish();

		}
		//

		// ��ǰ��Ƭ�б���ͼ�˵�
		if (item.getItemId() == R.id.action_Listing) {
			Intent intent = new Intent(MainActivity.this, ListingActivity.class);

			intent.putExtra("name", ((TextView) findViewById(R.id.Name))
					.getText().toString());
			intent.putExtra("title", ((TextView) findViewById(R.id.Title))
					.getText().toString());
			intent.putExtra("address", ((TextView) findViewById(R.id.Address))
					.getText().toString());
			intent.putExtra("postcode",
					((TextView) findViewById(R.id.Postcode)).getText()
							.toString());
			intent.putExtra("phone", ((TextView) findViewById(R.id.Phone))
					.getText().toString());
			intent.putExtra("mailbox", ((TextView) findViewById(R.id.Mailbox))
					.getText().toString());
			intent.putExtra("autograph",
					((TextView) findViewById(R.id.Autograph)).getText()
							.toString());
			intent.putExtra("homepage",
					((TextView) findViewById(R.id.Homepage)).getText()
							.toString());

			startActivity(intent);
			finish();
		}
		//

		//
		if (item.getItemId() == R.id.action_Canvas) {
			Intent intent = new Intent(MainActivity.this, Canvas.class);
			intent.putExtra("name", ((TextView) findViewById(R.id.Name))
					.getText().toString());
			intent.putExtra("title", ((TextView) findViewById(R.id.Title))
					.getText().toString());
			intent.putExtra("address", ((TextView) findViewById(R.id.Address))
					.getText().toString());
			if (((TextView) findViewById(R.id.Postcode)).getText().length() > 6) {
				intent.putExtra("postcode",
						((TextView) findViewById(R.id.Postcode)).getText()
								.toString().substring(0, 6));
			} else {
				intent.putExtra("postcode",
						((TextView) findViewById(R.id.Postcode)).getText()
								.toString());
			}
			if (((TextView) findViewById(R.id.Phone)).getText().length() > 11) {
				intent.putExtra("phone", ((TextView) findViewById(R.id.Phone))
						.getText().toString().substring(0, 11));
			} else {
				intent.putExtra("phone", ((TextView) findViewById(R.id.Phone))
						.getText().toString());
			}

			intent.putExtra("mailbox", ((TextView) findViewById(R.id.Mailbox))
					.getText().toString());
			intent.putExtra("autograph",
					((TextView) findViewById(R.id.Autograph)).getText()
							.toString());
			intent.putExtra("homepage",
					((TextView) findViewById(R.id.Homepage)).getText()
							.toString());
			intent.putExtra("logo", logoId);
			intent.putExtra("head", headId);
			startActivity(intent);
			finish();
		}
		//

		// �ָ���ʼֵ�˵�
		if (item.getItemId() == R.id.action_Initial) {

			new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.action_Initial)
					.setMessage("���Ҫ�ָ���")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// ����Ӳ����ָ�Ĭ��ֵ
									// ((TextView) findViewById(R.id.Name))
									// .setText(R.string.Name);
									// ((TextView) findViewById(R.id.Title))
									// .setText(R.string.Title);
									// ((TextView) findViewById(R.id.Address))
									// .setText(R.string.Address);
									// ((TextView) findViewById(R.id.Postcode))
									// .setText(R.string.Postcode);
									// ((TextView) findViewById(R.id.Phone))
									// .setText(R.string.Phone);
									// ((TextView) findViewById(R.id.Mailbox))
									// .setText(R.string.Mailbox);
									// ((TextView) findViewById(R.id.Autograph))
									// .setText(R.string.Autograph);
									// ((TextView) findViewById(R.id.Homepage))
									// .setText(R.string.Homepage);
									// ((ImageView) findViewById(R.id.img_logo))
									// .setImageResource(R.drawable.logo);
									// logoId="0";
									// ((ImageView) findViewById(R.id.img_me))
									// .setImageResource(R.drawable.me);
									// headId="0";

									SharedPreferences obtain = getSharedPreferences(
											"Login", MainActivity.MODE_PRIVATE);
									((TextView) findViewById(R.id.Name))
											.setText(obtain.getString("name",
													"nobody"));
									((TextView) findViewById(R.id.Title))
											.setText(obtain.getString("title",
													""));
									((TextView) findViewById(R.id.Address))
											.setText(obtain.getString(
													"address", ""));
									((TextView) findViewById(R.id.Postcode))
											.setText(obtain.getString(
													"postcode", ""));
									((TextView) findViewById(R.id.Phone))
											.setText(obtain.getString("phone",
													""));
									((TextView) findViewById(R.id.Mailbox))
											.setText(obtain.getString(
													"mailbox", ""));
									((TextView) findViewById(R.id.Autograph))
											.setText(obtain.getString(
													"autograph", ""));
									((TextView) findViewById(R.id.Homepage))
											.setText(obtain.getString(
													"homepage", ""));

									((ImageView) findViewById(R.id.img_logo))
											.setImageResource(Integer
													.parseInt(obtain.getString(
															"logo", "")));
									logoId = "0";
									((ImageView) findViewById(R.id.img_me))
											.setImageResource(Integer
													.parseInt(obtain.getString(
															"head", "")));
									headId = "0";
									Toast.makeText(MainActivity.this, "�ѻָ���ʼֵ",
											Toast.LENGTH_SHORT).show();

									// ����Ĭ��ֵ����
									// SharedPreferences settings =
									// getSharedPreferences(
									// "Login", MainActivity.MODE_PRIVATE);
									// SharedPreferences.Editor ed = settings
									// .edit();
									// ed.putString(
									// "name",
									// ((TextView) findViewById(R.id.Name))
									// .getText().toString());
									// ed.putString(
									// "title",
									// ((TextView) findViewById(R.id.Title))
									// .getText().toString());
									// ed.putString(
									// "address",
									// ((TextView) findViewById(R.id.Address))
									// .getText().toString());
									// ed.putString(
									// "postcode",
									// ((TextView) findViewById(R.id.Postcode))
									// .getText().toString());
									// ed.putString(
									// "phone",
									// ((TextView) findViewById(R.id.Phone))
									// .getText().toString());
									// ed.putString(
									// "mailbox",
									// ((TextView) findViewById(R.id.Mailbox))
									// .getText().toString());
									// ed.putString(
									// "autograph",
									// ((TextView) findViewById(R.id.Autograph))
									// .getText().toString());
									// ed.putString(
									// "homepage",
									// ((TextView) findViewById(R.id.Homepage))
									// .getText().toString());
									// ed.putString(
									// "logo",new
									// String((myImageIds[Integer.parseInt(logoId)])
									// +""));
									// ed.putString(
									// "head",new
									// String((headImageIds[Integer.parseInt(headId)])
									// +
									// ""));
									// ed.commit();
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).show();
		}
		//

		// �˳��˵�
		if (item.getItemId() == R.id.action_Exit) {
			finish();
		}
		return super.onOptionsItemSelected(item);
		//
	}

	//

	// �����Ĳ˵����
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()) {
		case R.id.Name:
			menu.add(0, M_NAME, 0, "�ָ���ʼֵ");
			break;
		case R.id.Title:
			menu.add(0, M_TITLE, 0, "�ָ���ʼֵ");
			break;
		case R.id.Address:
			menu.add(0, M_ADDRESS, 0, "�ָ���ʼֵ");
			break;
		case R.id.Postcode:
			menu.add(0, M_POSTCODE, 0, "�ָ���ʼֵ");
			break;
		case R.id.Phone:
			menu.add(0, 51, 0, "�༭�󲦴�");
			menu.add(0, 52, 0, "����绰");
			menu.add(0, 53, 0, "���Ͷ���");
			menu.add(0, M_PHONE, 0, "�ָ���ʼֵ");

			break;
		case R.id.Mailbox:
			menu.add(0, 61, 0, "�����ʼ�����");
			menu.add(0, M_MAILBOX, 0, "�ָ���ʼֵ");
			break;
		case R.id.Autograph:
			menu.add(0, M_AUTOGRAPH, 0, "�ָ���ʼֵ");
			break;
		case R.id.Homepage:
			menu.add(0, 81, 0, "ʹ�����������������ҳ");
			menu.add(0, 82, 0, "ʹ�����������������ҳ");
			menu.add(0, M_HOMEPAHE, 0, "�ָ���ʼֵ");
			break;
		case R.id.img_logo:
			menu.add(0, 91, 0, "�ֻ�����");
			menu.add(0, 92, 0, "�����ѡ��");
			menu.add(0, M_LOGO, 0, "�ָ���ʼֵ");
			break;
		case R.id.img_me:
			menu.add(0, 101, 0, "�ֻ�����");
			menu.add(0, 102, 0, "�����ѡ��");
			menu.add(0, M_HEAD, 0, "�ָ���ʼֵ");
			break;
		}
	}

	// ���ĳID�ָ���ʼֵ����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		SharedPreferences obtain = getSharedPreferences("Login",
				MainActivity.MODE_PRIVATE);
		switch (item.getItemId()) {
		case 10:
			((TextView) findViewById(R.id.Name)).setText(obtain.getString(
					"name", "nobody"));
			break;
		case 20:
			((TextView) findViewById(R.id.Title)).setText(obtain.getString(
					"title", ""));
			break;
		case 30:
			((TextView) findViewById(R.id.Address)).setText(obtain.getString(
					"address", ""));
			break;
		case 40:
			((TextView) findViewById(R.id.Postcode)).setText(obtain.getString(
					"postcode", ""));
			break;
		case 50:
			((TextView) findViewById(R.id.Phone)).setText(obtain.getString(
					"phone", ""));
			break;
		case 51:
			EditCallPhone(((TextView) findViewById(R.id.Phone)).getText()
					.toString().substring(0, 11));
			break;
		case 52:
			callPhone(((TextView) findViewById(R.id.Phone)).getText()
					.toString().substring(0, 11));
			break;
		case 53:
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.putExtra("address",
						((TextView) findViewById(R.id.Phone)).getText()
								.toString().substring(0, 11));
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 60:
			((TextView) findViewById(R.id.Mailbox)).setText(obtain.getString(
					"mailbox", ""));
			break;
		case 61:
			try {
				Intent intent_Email = new Intent(
						android.content.Intent.ACTION_SENDTO);
				intent_Email.setData(Uri.parse("mailto:"
						+ ((TextView) findViewById(R.id.Mailbox)).getText()
								.toString()));
				startActivity(intent_Email);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 70:
			((TextView) findViewById(R.id.Autograph)).setText(obtain.getString(
					"autograph", ""));
			break;
		case 80:
			((TextView) findViewById(R.id.Homepage)).setText(obtain.getString(
					"homepage", ""));
			break;
		case 81:
			Intent intent = new Intent(MainActivity.this, WebVisit.class);
			intent.putExtra("URL", ((TextView) findViewById(R.id.Homepage))
					.getText().toString());
			startActivity(intent);
			break;
		case 82:
			String url = "http://"
					+ ((TextView) findViewById(R.id.Homepage)).getText()
							.toString();
			Intent intent_url = new Intent(Intent.ACTION_VIEW);
			intent_url.setData(Uri.parse(url));
			startActivity(intent_url);
			break;
		case 90:
			((ImageView) findViewById(R.id.img_logo)).setImageResource(Integer
					.parseInt(obtain.getString("logo", "")));
			break;
		case 91:
			Intent intent_LogoImage = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			// Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			startActivityForResult(intent_LogoImage, 100);
			break;
		case 92:
			Intent intent_LogoPhoto = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent_LogoPhoto, 101);
		case 100:
			((ImageView) findViewById(R.id.img_me)).setImageResource(Integer
					.parseInt(obtain.getString("head", "")));
			break;
		case 101:
			Intent intent_HeadImage = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			// Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			startActivityForResult(intent_HeadImage, 200);
			break;
		case 102:
			Intent intent_HeadPhoto = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent_HeadPhoto, 201);

		}
		return super.onContextItemSelected(item);
	}

	//

	// �༭����
	private void EditCallPhone(String phoneNumber) {
		try {
			Intent myIntentDial = new Intent("android.intent.action.DIAL",
					Uri.parse("tel:" + phoneNumber));
			startActivity(myIntentDial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callPhone(String phoneNumber) {// ֱ�Ӳ������
		try {
			Intent myIntentDial = new Intent(Intent.ACTION_CALL,
					Uri.parse("tel:" + phoneNumber));
			startActivity(myIntentDial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//

	//
	/**
	 * ��Raw��ȡ����
	 */
	private void playFromRawFile(int id) {
		try {
			// ��ʼ��MediaPlayer��������ʼ��MediaPlayer����������Դ��׼������
			mediaPlayer = MediaPlayer.create(this, id);
			// ��ʼ����
			mediaPlayer.start();
			// ���ü���
			setListener();
			isStart = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����л���ֹͣ����
	 */
	@Override
	protected void onPause() {
		try {
			stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	/**
	 * ֹͣ����
	 */
	private void stop() {
		try {
			if (mediaPlayer != null) {
				isStart = false;
				// ֹͣ
				mediaPlayer.stop();
				// �ͷ���Դ
				mediaPlayer.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ü���
	 */
	private void setListener() {
		// �ļ�������Ϻ����
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer arg0) {
						try {
							stop();
							Toast.makeText(MainActivity.this, "������ϣ�",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		// ��������ʱ����
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				try {
					stop();
					Toast.makeText(MainActivity.this,
							"��������" + arg1 + " " + arg2, Toast.LENGTH_LONG)
							.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});

	}

	//

	//
	private void sendCustomerSMS(final String content1, final String content2) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.showdialog_smsentry, null);
		final EditText editText_phone = (EditText) textEntryView
				.findViewById(R.id.showdialog_smsentry_edittext_phone);
		editText_phone.setText(content1);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(getString(R.string.title));
		builder.setView(textEntryView);

		builder.setPositiveButton(getString(R.string.certain),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String number = editText_phone.getText().toString();
						String content = content2;
						Log.i("number", number);
						Log.i("e", content);
						if (number == null || number.length() == 0) {// �жϵ绰�Ƿ�Ϊ��
							Toast.makeText(MainActivity.this, "�绰���벻��Ϊ�գ�",
									Toast.LENGTH_LONG).show();
							sendCustomerSMS(number, content);
						} else {

							try {
								// ����SmsManager����
								SmsManager smsManager = SmsManager.getDefault();
								// �����Զ���Action������Intent(��PendingIntent����֮��)
								Intent itSend = new Intent(SMS_SEND_ACTIOIN);
								Intent itDeliver = new Intent(
										SMS_DELIVERED_ACTION);

								// sentIntent����Ϊ���ͺ���ܵĹ㲥��ϢPendingIntent
								PendingIntent sendPI = PendingIntent
										.getBroadcast(getApplicationContext(),
												0, itSend, 0);

								// deliveryIntent����Ϊ�ʹ����ܵĹ㲥��ϢPendingIntent
								PendingIntent deliverPI = PendingIntent
										.getBroadcast(getApplicationContext(),
												0, itDeliver, 0);

								// ����SMS���ţ�ע�⵹��������PendingIntent����
								smsManager.sendTextMessage(number, null,
										content, sendPI, deliverPI);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				});

		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		sendSMSDialog = builder.create();
		sendSMSDialog.show();
		sendSMSDialog.setCanceledOnTouchOutside(true);
	}

	//

	//
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.getAction().equals(SMS_SEND_ACTIOIN)) {
					Toast.makeText(MainActivity.this, "�����ѷ��ͣ�",
							Toast.LENGTH_LONG).show();
				}
				if (intent.getAction().equals(SMS_DELIVERED_ACTION)) {
					Toast.makeText(MainActivity.this, "�������ʹ",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	//
	/**
	 * ��Ӧ��������ǰ������仯ʱ�����ô˷���
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			// ����ʱ��
			long curTime = System.currentTimeMillis();

			if ((curTime - lastUpdate) > 100) {

				// �ڷ���onSensorChanged���������Ի����������
				x = event.values[SensorManager.DATA_X];
				y = event.values[SensorManager.DATA_Y];
				z = event.values[SensorManager.DATA_Z];

				System.out.println("TYPE_ACCELEROMETER---- x:" + x + " y:" + y
						+ " z:" + z);

				// ���ε�ʱ���
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				// ���x,y,z�ı仯ֵ
				float deltaX = x - last_x;
				float deltaY = y - last_y;
				float deltaZ = z - last_z;

				// ����˦���ٶ�
				double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY
						+ deltaZ * deltaZ)
						/ diffTime * 3000;

				// System.out.println("-----speed:" + speed);
				if (speed > SHAKE_THRESHOLD) {

					// System.out.println("TYPE_ACCELEROMETER---- x:" + x +
					// " y:" + y +
					// " z:" + z);

					if (x > 0 || x < 0) {// ����˦��������˦��
						sendCustomerSMS("", sb.toString());
					}

				}

				last_x = x;
				last_y = y;
				last_z = z;
			}

		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	//

	//
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				logo = (Bitmap) bundle.get("data");
				((ImageView) findViewById(R.id.img_logo)).setImageBitmap(logo);
			}
		}
		if (requestCode == 101) {

			try {
				ContentResolver resolver = this.getContentResolver();
				Uri uri = data.getData();
				logo = MediaStore.Images.Media.getBitmap(resolver, uri);
				// String[] pojo={MediaStore.Images.Media.DATA};
				// Cursor cursor=managedQuery(uri, pojo, null, null, null);
				// int
				// column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// cursor.moveToFirst();
				// String path=cursor.getString(column_index);
				File file = new File("/sdcard/CardImg/photo/logo.png");
				FileOutputStream fos = null;
				fos = new FileOutputStream(file);
				logo.compress(Bitmap.CompressFormat.PNG, 80, fos);

				((ImageView) findViewById(R.id.img_logo)).setImageBitmap(logo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (requestCode == 200) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				head = (Bitmap) bundle.get("data");
				((ImageView) findViewById(R.id.img_me)).setImageBitmap(head);
			}
		}
		if (requestCode == 201) {
			try {
				ContentResolver resolver = this.getContentResolver();
				Uri uri = data.getData();
				head = MediaStore.Images.Media.getBitmap(resolver, uri);
				// String[] pojo={MediaStore.Images.Media.DATA};
				// Cursor cursor=managedQuery(uri, pojo, null, null, null);
				// int
				// column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// cursor.moveToFirst();
				// String path=cursor.getString(column_index);
				File file = new File("/sdcard/CardImg/photo/head.png");
				FileOutputStream fos = null;
				fos = new FileOutputStream(file);
				head.compress(Bitmap.CompressFormat.PNG, 80, fos);
				((ImageView) findViewById(R.id.img_me)).setImageBitmap(head);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	//

	//
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		dbAdapter.open(); // �����ݿ�

		// ע��㲥
		smsReceiver = new SMSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, filter);
		//

		// �Զ���IntentFilterΪSENT_SMS_ACTIOIN Receiver
		final IntentFilter mFilter1 = new IntentFilter(SMS_SEND_ACTIOIN);
		myBroadcastReceiver1 = new MyBroadcastReceiver();
		// �����㲥
		registerReceiver(myBroadcastReceiver1, mFilter1);

		// �Զ���IntentFilterΪDELIVERED_SMS_ACTION Receiver
		final IntentFilter mFilter2 = new IntentFilter(SMS_DELIVERED_ACTION);
		myBroadcastReceiver2 = new MyBroadcastReceiver();
		// �����㲥
		registerReceiver(myBroadcastReceiver2, mFilter2);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (sensorManager != null) {
			sensorManager.registerListener(this, accelerometerSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		// ȡ��ע��㲥
		unregisterReceiver(smsReceiver);
		unregisterReceiver(myBroadcastReceiver1);
		unregisterReceiver(myBroadcastReceiver2);
		//

		super.onDestroy();
	}

	@Override
	protected void onStop() {

		dbAdapter.close(); // �ر����ݿ�
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
		super.onStop();

		// ���������ļ�����
		try {
			// �������ݵ�ROM
			FileOutputStream fos = openFileOutput("test.txt",
					Context.MODE_PRIVATE);
			fos.write((((TextView) findViewById(R.id.Name)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Title)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Address)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Postcode)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Phone)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Mailbox)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Autograph)).getText()
					.toString() + "\r\n").getBytes());
			fos.write((((TextView) findViewById(R.id.Homepage)).getText()
					.toString() + "\r\n").getBytes());

			if (logo != null) {
				fos.write(("/sdcard/CardImg/photo/logo.png" + "\r\n")
						.getBytes());
			} else {
				fos.write(new String((logoId) + "\r\n").getBytes());
			}
			if (head != null) {
				fos.write(("/sdcard/CardImg/photo/head.png" + "\r\n")
						.getBytes());
			} else {
				fos.write(new String((headId) + "\r\n").getBytes());
			}
			fos.close();

			// �������ݵ�SD������Ҫ��AndroidManifest.xml���Ȩ��
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				fosSDCard = new FileOutputStream(
						Environment.getExternalStorageDirectory()
								+ "/SDCardtest.txt");
				fosSDCard.write((((TextView) findViewById(R.id.Name)).getText()
						.toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Title))
						.getText().toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Address))
						.getText().toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Postcode))
						.getText().toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Phone))
						.getText().toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Mailbox))
						.getText().toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Autograph))
						.getText().toString() + "\r\n").getBytes());
				fosSDCard.write((((TextView) findViewById(R.id.Homepage))
						.getText().toString() + "\r\n").getBytes());
				if (logo != null) {
					fosSDCard.write(("/sdcard/CardImg/photo/logo.png" + "\r\n")
							.getBytes());
				} else {
					fosSDCard.write(new String((logoId) + "\r\n").getBytes());
				}
				if (head != null) {
					fosSDCard.write(("/sdcard/CardImg/photo/head.png" + "\r\n")
							.getBytes());
				} else {
					fosSDCard.write(new String((headId) + "\r\n").getBytes());
				}
				fosSDCard.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//

	}

}
