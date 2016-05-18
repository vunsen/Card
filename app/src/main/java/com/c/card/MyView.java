package com.c.card;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

public class MyView extends View {
	private static Integer[] myImageIds = new MyImage().ImageId();
	private static Integer[] headImageIds = new MyImage().headImageId();
	public static Bitmap bm = null;
	Bitmap bitmap = null;
	Bitmap logo = null;
	Bitmap head = null;
	private static String logoId = null;
	private static String headId = null;
	private static String name = null;
	private static String title = null;
	private static String address = null;
	private static String postcode = null;
	private static String phone = null;
	private static String mailbox = null;
	private static String autograph = null;
	private static String homepage = null;

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		setFocusable(true);
		logoId = Canvas.logoId;
		headId = Canvas.headId;
		name = Canvas.name;
		title = Canvas.title;
		address = Canvas.address;
		postcode = Canvas.postcode;
		phone = Canvas.phone;
		mailbox = Canvas.mailbox;
		autograph = Canvas.autograph;
		homepage = Canvas.homepage;
		try {

			InputStream is = null;
			InputStream lg = null;
			InputStream hd = null;
			Resources rs = Canvas.instance.getResources();
			is = rs.openRawResource(R.drawable.back);
			bitmap = BitmapFactory.decodeStream(is);
			lg = rs.openRawResource(myImageIds[Integer.parseInt(logoId)]);
			logo = BitmapFactory.decodeStream(lg);
			hd = rs.openRawResource(headImageIds[Integer.parseInt(headId)]);
			head = BitmapFactory.decodeStream(hd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Rect src = new Rect(0, 0, 440, 658);
		Rect dst = new Rect(0, 0, 480, 800);

		Paint paint = new Paint();
		paint.setColor(Color.rgb(00, 00, 00));
		paint.setTextSize(40);
		paint.setAntiAlias(true);

		bm = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		canvas.drawBitmap(bitmap, src, dst, paint);
		canvas.drawBitmap(logo, new Rect(0, 0, 440, 90), new Rect(20, 30, 460,
				120), paint);
		canvas.drawBitmap(head, new Rect(0, 0, 440, 440), new Rect(100, 165,
				155, 220), paint);
		canvas.drawText(name, 165, 220, paint);

		paint.setColor(Color.rgb(00, 255, 255));
		paint.setTextSize(28);
		canvas.drawText(title, 295, 225, paint);

		paint.setColor(Color.rgb(00, 00, 00));
		paint.setTextSize(20);
		canvas.drawText("地 址:", 70, 270, paint);
		canvas.drawText(address, 140, 270, paint);
		canvas.drawText("邮 编:", 70, 300, paint);
		canvas.drawText(postcode, 140, 300, paint);
		canvas.drawText("电 话:", 70, 330, paint);
		canvas.drawText(phone, 140, 330, paint);
		canvas.drawText("邮 箱:", 70, 360, paint);
		canvas.drawText(mailbox, 140, 360, paint);
		canvas.drawText("签 名:", 70, 390, paint);
		canvas.drawText(autograph, 140, 390, paint);
		canvas.drawText("主 页:", 70, 420, paint);
		canvas.drawText(homepage, 140, 420, paint);

		canvas.save(android.graphics.Canvas.ALL_SAVE_FLAG);
		canvas.restore();

	}

	public static void Save() {
		File file = new File("/sdcard/CardImg/" + name + ".png");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 80, fos);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
