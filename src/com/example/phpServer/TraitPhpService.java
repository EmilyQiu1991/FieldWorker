package com.example.phpServer;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TraitPhpService {
	private Context context;
	//private static final String UR_STRING="http://172.31.201.109:8888";
	public TraitPhpService(Context context) {
		super();
		this.context = context;
	}
	public void addTrait()
	{
//	  AsyncHttpClient client = new AsyncHttpClient();
//	  RequestParams params = new RequestParams();
//			
//				Gson gson = new GsonBuilder().create();
//				String strJSON = gson.toJson(traitLists);
//				String strJSON2=gson.toJson(traits);
//				String strJSON3=gson.toJson(deletedTraitList);
//				params.put("TraitLists", strJSON);
//				params.put("traitContents", strJSON2);
//				params.put("deletedTraitList",strJSON3 );
//				params.put("deviceId", getDeviceID());
//				System.out.println(getDeviceID());
//				System.out.println(strJSON3);
//				client.post(
//						UR_STRING+"/workspace/test/SynTraitList.php",
//						params, new AsyncHttpResponseHandler() {
//							@Override
//							public void onSuccess(String response) {
//								System.out.println(response);
//								    addLogDao.deleteByTableName("TraitList");
//								    addLogDao.deleteByTableName("TraitListContent");
//								    deleteLogDao.deleteByTableName("TraitList");
//									Toast.makeText(context, "TraitList Sync completed!",
//											Toast.LENGTH_LONG).show();
//								
//							}
//
//							public void onFailure(int statusCode, Throwable error,
//									String content) {
//								// TODO Auto-generated method stub
//							/*	prgDialog.hide();
//								if (statusCode == 404) {
//									Toast.makeText(context,
//											"Requested resource not found",
//											Toast.LENGTH_LONG).show();
//								} else if (statusCode == 500) {
//									Toast.makeText(context,
//											"Something went wrong at server end",
//											Toast.LENGTH_LONG).show();
//								} else {
//									Toast.makeText(
//											context,
//											"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
//											Toast.LENGTH_LONG).show();
//								}*/
//							}
//						});
	}
	public String getDeviceID()
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return(deviceUuid.toString());

	}
}
