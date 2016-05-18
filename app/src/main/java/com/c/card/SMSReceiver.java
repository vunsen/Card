package com.c.card;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	Bundle bundle;
	StringBuilder sb;
	SmsMessage[] smsMessages;
	String msgSource;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 判断接收到的广播是否为收到的短信的Broadcast Action
		if ("android.provider.Telephony.SMS_RECEIVED"
				.equals(intent.getAction())) {
			sb = new StringBuilder();
			// 接收由SMS传过来的数据
			bundle = intent.getExtras();
			if (bundle != null) {// 判断是否有数据通过pdus可以获得接收到的所有短信息
				Object[] SMSData = (Object[]) bundle.get("pdus");
				/* 构建短信对象array,并依据收到的对象长度来创建array的大小 */
				smsMessages = new SmsMessage[SMSData.length];
				for (int i = 0; i < SMSData.length; i++) {
					smsMessages[i] = SmsMessage
							.createFromPdu((byte[]) SMSData[i]);
				}
				/* 将送来的短信合并自定义信息于StringBuilder当中 */
				for (SmsMessage currentMessage : smsMessages) {
					msgSource = currentMessage.getDisplayOriginatingAddress();
					sb.append(currentMessage.getDisplayMessageBody());
				}
			}
			if (sb.toString().startsWith("card;")) {
				new AlertDialog.Builder(context)
						.setTitle("接收名片")
						.setMessage(msgSource + "发来短信要接收吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub										
										Intent mainIntent = new Intent(context,
												MainActivity.class);
										String[] info = sb.toString()
												.split(";");
										mainIntent.putExtra("name", info[1]);
										mainIntent.putExtra("title", info[2]);
										mainIntent.putExtra("address", info[3]);
										mainIntent
												.putExtra("postcode", info[4]);
										mainIntent.putExtra("phone", info[5]);
										mainIntent.putExtra("mailbox", info[6]);
										mainIntent.putExtra("autograph",
												info[7]);
										mainIntent
												.putExtra("homepage", info[8]);
										mainIntent.putExtra("receiver",info[0]);
										context.startActivity(mainIntent);
										Toast.makeText(context, "已接收",
												Toast.LENGTH_LONG).show();

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

		}
	}
}
