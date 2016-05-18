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
	/** 媒体播放类 */
	private MediaPlayer mediaPlayer;
	/** 是否开始的标识 */
	private boolean isStart = false;

	/** 对话框实例 */
	private AlertDialog sendSMSDialog;
	/** 自定义ACTION常数，作为广播的Intent Filter识别常数 */
	private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	/** 创建两个广播对象 */
	private MyBroadcastReceiver myBroadcastReceiver1;
	private MyBroadcastReceiver myBroadcastReceiver2;
	private StringBuilder sb;

	/** 重力感应管理者 */
	private SensorManager sensorManager;

	private Sensor accelerometerSensor;
	// 晃动重力感应上一次时间
	private long lastUpdate = -1;
	// 晃动重力感应的现坐标
	private float x, y, z;
	// 晃动重力感应的上一次坐标
	private float last_x, last_y, last_z;
	// 晃动重力感应的力度
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

		// 读取数据文件
		try {

			// 读取存储在ROM的名片信息
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

			// 读取存储在SD卡的名片信息
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

		// 接受各个Activity传来的值
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

		// 注册上下文菜单
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

		// 申请数据库
		dbAdapter = new DBAdapter(this);

		// 检查当前网络
		try {// 检测手机网络，抛出异常
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
			if (!isStart) {// 防止连续打开两个文件
				playFromRawFile(R.raw.horizon);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//

		// 把当前名片编辑成短信信息
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
				// 查询邮编所在地区
				try {
					String SERVER_URL_Postcode = "http://webservice.webxml.com.cn/WebServices/ChinaZipSearchWebService.asmx/getAddressByZipCode";// 定义需要获取的内容来源地址
					final HttpPost request_Postcode = new HttpPost(
							SERVER_URL_Postcode);// 根据内容来源地址创建一个Http请求
					List<BasicNameValuePair> params_Postcode = new ArrayList<BasicNameValuePair>();
					params_Postcode.add(new BasicNameValuePair("theZipCode",
							postcode));// 添加必须的参数

					params_Postcode.add(new BasicNameValuePair("userID", ""));// 添加必须的参数
					request_Postcode.setEntity(new UrlEncodedFormEntity(
							params_Postcode, HTTP.UTF_8)); // 设置参数的编码
					final HttpClient httpClient_Postcode = new DefaultHttpClient();
					// 发送请求并获取反馈
					HttpResponse httpResponse = httpClient_Postcode
							.execute(request_Postcode);
					// 解析返回的内容
					if (httpResponse.getStatusLine().getStatusCode() != 404) {
						String postcodeResult = EntityUtils
								.toString(httpResponse.getEntity());
						byte[] PC_data = postcodeResult.getBytes();
						ByteArrayInputStream Byte_Postcode = new ByteArrayInputStream(
								PC_data);
						city = pullParserPostcode(Byte_Postcode);
						((TextView) findViewById(R.id.Postcode))
								.setText(postcode + "(" + city + ")");
						if (city.endsWith("市")) {
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
				// 查询所在地区天气
				try {

					String SERVER_URL_City = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asxm/getWeather";// 定义需要获取的内容来源地址
					final HttpPost request_City = new HttpPost(SERVER_URL_City);// 根据内容来源地址创建一个Http请求
					List<BasicNameValuePair> params_City = new ArrayList<BasicNameValuePair>();
					params_City
							.add(new BasicNameValuePair("theCityName", city));// 添加必须的参数
					params_City.add(new BasicNameValuePair("theUserID", ""));// 添加必须的参数
					request_City.setEntity(new UrlEncodedFormEntity(
							params_City, HTTP.UTF_8)); // 设置参数的编码
					final HttpClient httpClient_Postcode = new DefaultHttpClient();
					// 发送请求并获取反馈
					HttpResponse httpResponse_City = httpClient_Postcode
							.execute(request_City);
					// 解析返回的内容
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
				// 查询手机号码归属地
				try {
					String SERVER_URL_Phone = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";// 定义需要获取的内容来源地址
					HttpPost request_Phone = new HttpPost(SERVER_URL_Phone);// 根据内容来源地址创建一个Http请求
					List<BasicNameValuePair> params_Phone = new ArrayList<BasicNameValuePair>();
					params_Phone.add(new BasicNameValuePair("mobileCode", phone
							.substring(0, 11)));// 添加必须的参数
					params_Phone.add(new BasicNameValuePair("userID", ""));// 添加必须的参数
					request_Phone.setEntity(new UrlEncodedFormEntity(
							params_Phone, HTTP.UTF_8)); // 设置参数的编码
					HttpClient httpClient_Phone = new DefaultHttpClient();
					// 发送请求并获取反馈
					HttpResponse httpResponse_PC = httpClient_Phone
							.execute(request_Phone);
					if (httpResponse_PC.getStatusLine().getStatusCode() != 404) {
						String result = EntityUtils.toString(httpResponse_PC
								.getEntity());
						// 把收到的数据显示到控件上
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

	// 解析邮编所在地区XML
	private String pullParserPostcode(InputStream Byte_Postcode)
			throws Exception {
		String City = null;
		// TODO Auto-generated method stub
		// 创建pull解析对象
		XmlPullParser parser = Xml.newPullParser();
		// 将xml文件以输入流的形式传递给 pull解析对象，并设置解析对象按照 utf-8 的编码进行解析
		parser.setInput(Byte_Postcode, "UTF-8");
		// pull解析对象在解析xml时，会返回一个代表解析位置的值。
		// START_DOCUMENT ： 开始解析文档
		// START_TAG ： 开始解析标签
		// END_TAG : 标签结束
		// END_DOCUMENT ：xml文档解析结束
		// 得到当前解析的位置
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			// switch 解析的位置
			switch (type) {
			// 开始解析文档的时候，初始化对象集合
			case XmlPullParser.START_DOCUMENT:
				City = null;
				break;
			// 开始解析标签的时候，根据标签的不同名称。做不同操作
			case XmlPullParser.START_TAG:
				// 标签为 的时候，取出标签的属性城市值，并保存到Person对象里
				if ("CITY".equals(parser.getName())) {
					City = parser.nextText();
				}
				break;
			// 当解析到标签结束的时候执行
			case XmlPullParser.END_TAG:
				// 如果结束标签名是person，那么将Person对象保存到personList中
				if ("CITY".equals(parser.getName())) {
					break;
				}
			}
			// 当前解析位置结束，指向下一个位置
			type = parser.next();
		}
		Byte_Postcode.close();
		// 返回获取的值
		return City;
	}

	//

	// 解析天气XML文件
	private String pullParserCity(InputStream Byte_City) throws Exception {
		int i = 0;
		String Weather = null;
		StringBuilder sb = new StringBuilder();
		// TODO Auto-generated method stub
		// 创建pull解析对象
		XmlPullParser parser = Xml.newPullParser();
		// 将xml文件以输入流的形式传递给 pull解析对象，并设置解析对象按照 utf-8 的编码进行解析
		parser.setInput(Byte_City, "UTF-8");
		// pull解析对象在解析xml时，会返回一个代表解析位置的值。
		// START_DOCUMENT ： 开始解析文档
		// START_TAG ： 开始解析标签
		// END_TAG : 标签结束
		// END_DOCUMENT ：xml文档解析结束
		// 得到当前解析的位置
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			// switch 解析的位置
			switch (type) {
			// 开始解析文档的时候，初始化对象集合
			case XmlPullParser.START_DOCUMENT:
				Weather = null;
				break;
			// 开始解析标签的时候，根据标签的不同名称。做不同操作
			case XmlPullParser.START_TAG:
				// 标签为 的时候，取出标签的属性城市值，并保存到Person对象里
				if ("string".equals(parser.getName()) && (i == 0 || i == 4)) {
					Weather = parser.nextText();
					sb.append(Weather).append(" ");

				}
				break;
			// 当解析到标签结束的时候执行
			case XmlPullParser.END_TAG:
				// 如果结束标签名是person，那么将Person对象保存到personList中
				if ("string".equals(parser.getName())) {
					break;
				}
			}
			// 当前解析位置结束，指向下一个位置
			type = parser.next();
			i++;
		}
		Byte_City.close();
		// 返回获取的值
		return sb.toString();
	}

	//

	// 解析电话归属地XML文件
	private String pullParserPhone(InputStream Byte_Phone) throws Exception {
		String place = null;
		// TODO Auto-generated method stub
		// 创建pull解析对象
		XmlPullParser parser = Xml.newPullParser();
		// 将xml文件以输入流的形式传递给 pull解析对象，并设置解析对象按照 utf-8 的编码进行解析
		parser.setInput(Byte_Phone, "UTF-8");
		// pull解析对象在解析xml时，会返回一个代表解析位置的值。
		// START_DOCUMENT ： 开始解析文档
		// START_TAG ： 开始解析标签
		// END_TAG : 标签结束
		// END_DOCUMENT ：xml文档解析结束
		// 得到当前解析的位置
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			// switch 解析的位置
			switch (type) {
			// 开始解析文档的时候，初始化对象集合
			case XmlPullParser.START_DOCUMENT:
				place = null;
				break;
			// 开始解析标签的时候，根据标签的不同名称。做不同操作
			case XmlPullParser.START_TAG:
				// 标签为 的时候，取出标签的属性城市值，并保存到Person对象里
				if ("string".equals(parser.getName())) {
					place = parser.nextText();
				}
				break;
			// 当解析到标签结束的时候执行
			case XmlPullParser.END_TAG:
				// 如果结束标签名是person，那么将Person对象保存到personList中
				if ("string".equals(parser.getName())) {
					break;
				}
			}
			// 当前解析位置结束，指向下一个位置
			type = parser.next();
		}
		Byte_Phone.close();
		// 返回获取的值
		return place;
	}

	//

	// 检查网络方法
	private int checkNetworkInfo() {
		// TODO Auto-generated method stub
		int netReturn = 0;
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = conMan.getActiveNetworkInfo();// 获取网络连通性
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();// 获取3G网络连通性，值CONNECTED
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();// 获取WIFI网络连通性，值CONNECTED

		if (networkinfo == null || !networkinfo.isAvailable()) {// 网络为空或者不连通
			Toast.makeText(this, "没有可以使用的网络", Toast.LENGTH_SHORT).show();
			netReturn = 0;
		}

		if (mobile.toString().equals("CONNECTED")) {
			Toast.makeText(this, "当前网络连接的是3G", Toast.LENGTH_SHORT).show();
			netReturn = 1;
		}

		if (wifi.toString().equals("CONNECTED")) {
			Toast.makeText(this, "当前网络连接的是wifi", Toast.LENGTH_SHORT).show();
			netReturn = 2;
		}
		return netReturn;
	}

	@Override
	// 添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {

		Log.d("MainActivity", "menu--->" + menu);
		setIconEnable(menu, true);// 4.0以上版本默认关闭，需要打开
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

	// 为4.0以上版本增加图标方法
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

	// 菜单项选择方法
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		// 点击编辑名片菜单
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

		// 发送名片方法
		if (item.getItemId() == R.id.action_Send) {

			sendCustomerSMS("", sb.toString());
		}
		//

		// 删除当前名片菜单
		if (item.getItemId() == R.id.action_Delect) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("删除名片")
					.setMessage("真的要删除吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 调用删除单张名片方法
									dbAdapter
											.delectOne(((TextView) findViewById(R.id.Name))
													.getText().toString());
									Toast.makeText(MainActivity.this, "已删除名片",
											Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(
											MainActivity.this,
											CarCaseActivity.class);
									startActivity(intent);
									finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).show();
		}
		//

		// 全部名片菜单
		if (item.getItemId() == R.id.action_CardAll) {
			Intent intent = new Intent(MainActivity.this, CarCaseActivity.class);
			startActivity(intent);
			finish();

		}
		//

		// 当前名片列表视图菜单
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

		// 恢复初始值菜单
		if (item.getItemId() == R.id.action_Initial) {

			new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.action_Initial)
					.setMessage("真的要恢复吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// 利用硬编码恢复默认值
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
									Toast.makeText(MainActivity.this, "已恢复初始值",
											Toast.LENGTH_SHORT).show();

									// 保存默认值方法
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
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).show();
		}
		//

		// 退出菜单
		if (item.getItemId() == R.id.action_Exit) {
			finish();
		}
		return super.onOptionsItemSelected(item);
		//
	}

	//

	// 上下文菜单项方法
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()) {
		case R.id.Name:
			menu.add(0, M_NAME, 0, "恢复初始值");
			break;
		case R.id.Title:
			menu.add(0, M_TITLE, 0, "恢复初始值");
			break;
		case R.id.Address:
			menu.add(0, M_ADDRESS, 0, "恢复初始值");
			break;
		case R.id.Postcode:
			menu.add(0, M_POSTCODE, 0, "恢复初始值");
			break;
		case R.id.Phone:
			menu.add(0, 51, 0, "编辑后拨打");
			menu.add(0, 52, 0, "拨打电话");
			menu.add(0, 53, 0, "发送短信");
			menu.add(0, M_PHONE, 0, "恢复初始值");

			break;
		case R.id.Mailbox:
			menu.add(0, 61, 0, "发送邮件发送");
			menu.add(0, M_MAILBOX, 0, "恢复初始值");
			break;
		case R.id.Autograph:
			menu.add(0, M_AUTOGRAPH, 0, "恢复初始值");
			break;
		case R.id.Homepage:
			menu.add(0, 81, 0, "使用内置浏览器访问主页");
			menu.add(0, 82, 0, "使用其他浏览器访问主页");
			menu.add(0, M_HOMEPAHE, 0, "恢复初始值");
			break;
		case R.id.img_logo:
			menu.add(0, 91, 0, "手机拍照");
			menu.add(0, 92, 0, "从相册选择");
			menu.add(0, M_LOGO, 0, "恢复初始值");
			break;
		case R.id.img_me:
			menu.add(0, 101, 0, "手机拍照");
			menu.add(0, 102, 0, "从相册选择");
			menu.add(0, M_HEAD, 0, "恢复初始值");
			break;
		}
	}

	// 点击某ID恢复初始值方法
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

	// 编辑号码
	private void EditCallPhone(String phoneNumber) {
		try {
			Intent myIntentDial = new Intent("android.intent.action.DIAL",
					Uri.parse("tel:" + phoneNumber));
			startActivity(myIntentDial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callPhone(String phoneNumber) {// 直接拨打号码
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
	 * 从Raw获取数据
	 */
	private void playFromRawFile(int id) {
		try {
			// 初始化MediaPlayer，包含初始化MediaPlayer，设置数据源，准备数据
			mediaPlayer = MediaPlayer.create(this, id);
			// 开始播放
			mediaPlayer.start();
			// 设置监听
			setListener();
			isStart = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 界面切换，停止播放
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
	 * 停止播放
	 */
	private void stop() {
		try {
			if (mediaPlayer != null) {
				isStart = false;
				// 停止
				mediaPlayer.stop();
				// 释放资源
				mediaPlayer.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		// 文件播出完毕后调用
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer arg0) {
						try {
							stop();
							Toast.makeText(MainActivity.this, "播放完毕！",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		// 发生错误时调用
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				try {
					stop();
					Toast.makeText(MainActivity.this,
							"发生错误！" + arg1 + " " + arg2, Toast.LENGTH_LONG)
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
						if (number == null || number.length() == 0) {// 判断电话是否为空
							Toast.makeText(MainActivity.this, "电话号码不能为空！",
									Toast.LENGTH_LONG).show();
							sendCustomerSMS(number, content);
						} else {

							try {
								// 创建SmsManager对象
								SmsManager smsManager = SmsManager.getDefault();
								// 创建自定义Action常数的Intent(给PendingIntent参数之用)
								Intent itSend = new Intent(SMS_SEND_ACTIOIN);
								Intent itDeliver = new Intent(
										SMS_DELIVERED_ACTION);

								// sentIntent参数为传送后接受的广播信息PendingIntent
								PendingIntent sendPI = PendingIntent
										.getBroadcast(getApplicationContext(),
												0, itSend, 0);

								// deliveryIntent参数为送达后接受的广播信息PendingIntent
								PendingIntent deliverPI = PendingIntent
										.getBroadcast(getApplicationContext(),
												0, itDeliver, 0);

								// 发送SMS短信，注意倒数的两个PendingIntent参数
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
					Toast.makeText(MainActivity.this, "短信已发送！",
							Toast.LENGTH_LONG).show();
				}
				if (intent.getAction().equals(SMS_DELIVERED_ACTION)) {
					Toast.makeText(MainActivity.this, "短信已送达！",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	//
	/**
	 * 感应方法，当前的坐标变化时，调用此方法
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			// 本次时间
			long curTime = System.currentTimeMillis();

			if ((curTime - lastUpdate) > 100) {

				// 在方法onSensorChanged中这样可以获得坐标数据
				x = event.values[SensorManager.DATA_X];
				y = event.values[SensorManager.DATA_Y];
				z = event.values[SensorManager.DATA_Z];

				System.out.println("TYPE_ACCELEROMETER---- x:" + x + " y:" + y
						+ " z:" + z);

				// 两次的时间差
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				// 获得x,y,z的变化值
				float deltaX = x - last_x;
				float deltaY = y - last_y;
				float deltaZ = z - last_z;

				// 计算甩动速度
				double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY
						+ deltaZ * deltaZ)
						/ diffTime * 3000;

				// System.out.println("-----speed:" + speed);
				if (speed > SHAKE_THRESHOLD) {

					// System.out.println("TYPE_ACCELEROMETER---- x:" + x +
					// " y:" + y +
					// " z:" + z);

					if (x > 0 || x < 0) {// 向左甩动或向右甩动
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
		dbAdapter.open(); // 打开数据库

		// 注册广播
		smsReceiver = new SMSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, filter);
		//

		// 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver
		final IntentFilter mFilter1 = new IntentFilter(SMS_SEND_ACTIOIN);
		myBroadcastReceiver1 = new MyBroadcastReceiver();
		// 开启广播
		registerReceiver(myBroadcastReceiver1, mFilter1);

		// 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver
		final IntentFilter mFilter2 = new IntentFilter(SMS_DELIVERED_ACTION);
		myBroadcastReceiver2 = new MyBroadcastReceiver();
		// 开启广播
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

		// 取消注册广播
		unregisterReceiver(smsReceiver);
		unregisterReceiver(myBroadcastReceiver1);
		unregisterReceiver(myBroadcastReceiver2);
		//

		super.onDestroy();
	}

	@Override
	protected void onStop() {

		dbAdapter.close(); // 关闭数据库
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
		super.onStop();

		// 保存数据文件方法
		try {
			// 保存数据到ROM
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

			// 保存数据到SD卡，需要在AndroidManifest.xml添加权限
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
