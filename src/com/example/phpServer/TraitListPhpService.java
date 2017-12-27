package com.example.phpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.PredefineValue;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;
import com.example.fieldworker1.Constant;
import com.example.fieldworker1.ListViewSubClass;
import com.example.fieldworker1.MyAdapter;
import com.example.fieldworker1.R;
import com.example.validator.MyApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TraitListPhpService {
	private Context context;
	private String username;
	

	public TraitListPhpService(Context context, String username) {
		super();
		this.context = context;
		this.username = username;
	}

	public void addTraitList(TraitList traitList, List<String> traits) {
		// insert into TraitList
		// insert traits length entries into TraitListContent
		String url = Constant.urlString+"TraitListService.php";
		new AddTraitListAsyncTask(traitList, traits).execute(url);
	}

	public void synTraitList(List<AddLog> addLogs, List<AddLog> addLogs2,List<AddLog> addLogs3,List<AddLog> addLogs4,
			List<DeleteLog> deleteLogs, List<DeleteLog> deleteLogs2) {
		final AddLogDao addLogDao = new AddLogDao(context);
		final DeleteLogDao deleteLogDao = new DeleteLogDao(context);
		TraitListDao traitListDao = new TraitListDao(context);
       TraitDao traitDao=new TraitDao(context);
       PredefineValueDao predefineValueDao=new PredefineValueDao(context);
		ArrayList<TraitList> traitLists = new ArrayList<TraitList>();
		ArrayList<TraitListContent> traits = new ArrayList<TraitListContent>();
		ArrayList<Trait> addedTraits=new ArrayList<Trait>();
		ArrayList<PredefineValue> predefineValues=new ArrayList<PredefineValue>();
		Integer[] deletedTraitList = new Integer[deleteLogs.size()];
		ArrayList<TraitListContent> deletedTraitContents=new ArrayList<TraitListContent>();
		for (int i = 0; i < addLogs.size(); i++) {
			Integer traitListID = addLogs.get(i).getFirstID();

			traitLists.add(traitListDao.findById(traitListID));
		}
		for (int i = 0; i < addLogs2.size(); i++) {
			Integer traitListID = addLogs2.get(i).getFirstID();
			Integer traitID = addLogs2.get(i).getSecondID();
			traits.add(new TraitListContent(traitListID, traitID));
		}
		for (int i = 0; i < addLogs3.size(); i++) {
			Integer traitID=addLogs3.get(i).getFirstID();
			addedTraits.add(traitDao.findById(traitID));
		}
		for (int i = 0; i < addLogs4.size(); i++) {
			Integer predefineValID=addLogs4.get(i).getFirstID();
			predefineValues.add(predefineValueDao.findById(predefineValID));
		}
		for (int i = 0; i < deleteLogs.size(); i++) {
			deletedTraitList[i] = deleteLogs.get(i).getFirstID();
		}
		for (int i = 0; i < deleteLogs2.size(); i++) {
			Integer traitListID = deleteLogs2.get(i).getFirstID();
			Integer traitID = deleteLogs2.get(i).getSecondID();
			deletedTraitContents.add(new TraitListContent(traitListID,traitID));
		}
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		Gson gson = new GsonBuilder().create();
		String strJSON = gson.toJson(traitLists);
		String strJSON2 = gson.toJson(traits);
		String strJSON5=gson.toJson(addedTraits);
		String strJSON6=gson.toJson(predefineValues);
		String strJSON3 = gson.toJson(deletedTraitList);
		String strJSON4 =gson.toJson(deletedTraitContents);
		params.put("TraitLists", strJSON);
		params.put("traitContents", strJSON2);
		params.put("addedTraits", strJSON5);
		params.put("predefineValues", strJSON6);
		params.put("deletedTraitList", strJSON3);
		params.put("deletedTraitContents", strJSON4);
		params.put("deviceId", getDeviceID());
		System.out.println(getDeviceID());
		System.out.println(strJSON3);
		client.post(Constant.urlString + "SynTraitList.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println(response);
						addLogDao.deleteByTableName("TraitList");
						addLogDao.deleteByTableName("TraitListContent");
						addLogDao.deleteByTableName("Trait");
						addLogDao.deleteByTableName("PredefineVal");
						deleteLogDao.deleteByTableName("TraitList");
						Toast.makeText(context, "TraitList Sync completed!",
								Toast.LENGTH_LONG).show();

					}

					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						/*
						 * prgDialog.hide(); if (statusCode == 404) {
						 * Toast.makeText(context,
						 * "Requested resource not found",
						 * Toast.LENGTH_LONG).show(); } else if (statusCode ==
						 * 500) { Toast.makeText(context,
						 * "Something went wrong at server end",
						 * Toast.LENGTH_LONG).show(); } else { Toast.makeText(
						 * context,
						 * "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]"
						 * , Toast.LENGTH_LONG).show(); }
						 */
					}
				});
	}

	public void findAll(ListViewSubClass mListView, Context context,
			List<HashMap<String, String>> list) throws InterruptedException {
		String url = Constant.urlString + "server.php";
		FindAllAsyncTask findAllAsyncTask = new FindAllAsyncTask(mListView,
				context, list);
		findAllAsyncTask.execute(url);
	}

	class AddTraitListAsyncTask extends AsyncTask<String, Integer, String> {

		private TraitList traitList;
		private List<String> traits;

		public AddTraitListAsyncTask(TraitList traitList, List<String> traits) {
			super();
			this.traitList = traitList;
			this.traits = traits;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			HttpPost httpRequest = new HttpPost(params[0]);
			TraitDao traitDao = new TraitDao(context);
			PredefineValueDao preDao=new PredefineValueDao(context);
			
			ArrayList<TraitListContent> contents = new ArrayList<TraitListContent>();
			ArrayList<Trait> allTraits=new ArrayList<Trait>();
			ArrayList<PredefineValue> preValues=new ArrayList<PredefineValue>();
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("traitListID", traitList
					.getTraitListID() + ""));
			param.add(new BasicNameValuePair("traitListName", traitList
					.getTraitListName()));
			param.add(new BasicNameValuePair("username", traitList
					.getUsername()));
			param.add(new BasicNameValuePair("deviceId", getDeviceID()));
			for (String s : traits) {
				Integer traitID = traitDao.findIdbyName(s);
				contents.add(new TraitListContent(traitList.getTraitListID(),
						traitID));
				Trait t=traitDao.searchByTraitName(s);
				preValues.addAll(preDao.search(t.getTraitID()));
				allTraits.add(t);
			}

			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(contents);
            String allTraitsJson=gson.toJson(allTraits);
            String preValueJson=gson.toJson(preValues);
			param.add(new BasicNameValuePair("Traits", strJSON));
			param.add(new BasicNameValuePair("allTraits", allTraitsJson));
			param.add(new BasicNameValuePair("preValues", preValueJson));
			param.add(new BasicNameValuePair("deviceId",MyApplication.getDeviceID() ));
			InputStream is = null;

			try {

				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);

				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();

					// is=entity.getContent();
					System.out.println("***" + EntityUtils.toString(entity));

				} else {
					// tv.setText("request error");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return "";
		}

	}

	class FindAllAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			List<TraitList> traitLists = new ArrayList<TraitList>();
			try {

				JSONArray jArray = new JSONArray(result);

				if (jArray.length() > 0) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json_data = jArray.getJSONObject(i);

						Integer traitListID = json_data.getInt("traitListID");
						String traitListName = json_data
								.getString("traitListName");
						String username = json_data.getString("username");
						traitLists.add(new TraitList(traitListID,
								traitListName, username));
					}

				} else {
					result = "Can't find Your name!";
				}
			} catch (Exception e) {
				// TODO: handle exception
				// Log.e("log_tag", "Error parsing data "+e.toString());
			}
			for (java.util.Iterator<TraitList> iterator = traitLists.iterator(); iterator
					.hasNext();) {
				TraitList t = (TraitList) iterator.next();

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("traitList_name", t.getTraitListName());
				// map.put("traitList_id", t.getTraitListID()+"");
				list.add(map);
			}

		}

		private ListViewSubClass mListView;
		private Context context;
		private List<HashMap<String, String>> list;

		public FindAllAsyncTask(ListViewSubClass mListView, Context context,
				List<HashMap<String, String>> list) {
			this.mListView = mListView;
			this.context = context;
			this.list = list;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			HttpPost httpRequest = new HttpPost(Constant.urlString
					+ "FindAllTraitLists.php");
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("username", username));	
			InputStream is = null;
			String result = "";

			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();

					is = entity.getContent();
					// System.out.println("***"+EntityUtils.toString(entity));

				} else {
					// tv.setText("request error");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				System.out.println(result);
			} catch (Exception e) {
				// TODO: handle exception
			}

			return result;
		}

	}

	public void deleteTraitList(Integer traitListID,String isUsed) {

		new DeleteTraitListAsyncTask().execute(traitListID+"",isUsed);
	}

	class DeleteTraitListAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpPost httpRequest = new HttpPost(Constant.urlString
					+ "DeleteTraitList.php");
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("traitListID", params[0]));
			param.add(new BasicNameValuePair("isUsed", params[1]));
			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();
					System.out.println("***" + EntityUtils.toString(entity));
				} else {
					System.out.println("request error");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

	}

	public String getDeviceID() {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return (deviceUuid.toString());

	}

	public void updateTraitList(TraitList tl, ArrayList<String> currentTraits) {
		List<Trait> traits=new ArrayList<Trait>();
		TraitDao tDao=new TraitDao(context);
		for (int i = 0; i < currentTraits.size(); i++) {
			traits.add(tDao.searchByTraitName(currentTraits.get(i)));
		}
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		Gson gson = new GsonBuilder().create();
		String strJSON2 = gson.toJson(traits);
		
		params.put("traitContents", strJSON2);
		params.put("deviceId", getDeviceID());
		params.put("traitListId", tl.getTraitListID()+"");
		client.post(Constant.urlString + "UpdateTraitList.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println("update trait list php:"+response);
					}

					public void onFailure(int statusCode, Throwable error,
							String content) {
						 if (statusCode == 404) 
							  System.out.println("Requested resource not found");
						
						 else if (statusCode ==500) 
							  
						 
							System.out.println("Something went wrong at server end");
						 
							 
						 else 
							 System.out.println( "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]");
							
					}
				});
		
	}
}
