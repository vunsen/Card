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
		// �жϽ��յ��Ĺ㲥�Ƿ�Ϊ�յ��Ķ��ŵ�Broadcast Action
		if ("android.provider.Telephony.SMS_RECEIVED"
				.equals(intent.getAction())) {
			sb = new StringBuilder();
			// ������SMS������������
			bundle = intent.getExtras();
			if (bundle != null) {// �ж��Ƿ�������ͨ��pdus���Ի�ý��յ������ж���Ϣ
				Object[] SMSData = (Object[]) bundle.get("pdus");
				/* �������Ŷ���array,�������յ��Ķ��󳤶�������array�Ĵ�С */
				smsMessages = new SmsMessage[SMSData.length];
				for (int i = 0; i < SMSData.length; i++) {
					smsMessages[i] = SmsMessage
							.createFromPdu((byte[]) SMSData[i]);
				}
				/* �������Ķ��źϲ��Զ�����Ϣ��StringBuilder���� */
				for (SmsMessage currentMessage : smsMessages) {
					msgSource = currentMessage.getDisplayOriginatingAddress();
					sb.append(currentMessage.getDisplayMessageBody());
				}
			}
			if (sb.toString().startsWith("card;")) {
				new AlertDialog.Builder(context)
						.setTitle("������Ƭ")
						.setMessage(msgSource + "��������Ҫ������")
						.setPositiveButton("ȷ��",
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
										Toast.makeText(context, "�ѽ���",
												Toast.LENGTH_LONG).show();

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

		}
	}
}
