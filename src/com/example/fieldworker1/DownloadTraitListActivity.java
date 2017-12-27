package com.example.fieldworker1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.PredefineValue;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;
import com.example.phpServer.ShowTraitListPhp;
import com.example.validator.MyApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadTraitListActivity extends Activity {
	private static final String PREFS_NAME = "MyPrefsFile";
	public final static String SER_KEY = "com.example.fieldworker1.tra";
	private List<TraitList> allTraitLists;
	private ArrayList<TraitList> selectedTLists;
	private ArrayList<TraitList> newSelectedTraitLists;
	private ArrayList<TraitListContent> traitListContents;
	private ListView listView;
	private Button button;
	private String username;
	MyCustomAdapter dataAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
        System.out.println("DownloadTraitListActivity starts");
		setContentView(R.layout.download_traitlist);
		listView = (ListView) findViewById(R.id.listView1);
		button = (Button) findViewById(R.id.download);
		button.setOnClickListener(new DownloadTraitListListener());
		// get username from shared preferences
		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		username = mySharedPreferences.getString("username", "");
		selectedTLists = new ArrayList<TraitList>();
		traitListContents=new ArrayList<TraitListContent>();
		newSelectedTraitLists=new ArrayList<TraitList>();
		allTraitLists=new ArrayList<TraitList>();
		if (!MyApplication.isNetworkOnline()) {
			System.out.println("please connect the network first");
		} else {
			try {
				displayListView();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void displayListView() throws InterruptedException {
		String url = Constant.urlString + "FindAllTraitLists.php";
		FindAllAsyncTask findAllAsyncTask = new FindAllAsyncTask();
		findAllAsyncTask.execute(url);
	}

	private class MyCustomAdapter extends ArrayAdapter<TraitList> {
		private ArrayList<TraitList> traitLists;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<TraitList> lists) {

			super(context, textViewResourceId, lists);
			this.traitLists = new ArrayList<TraitList>();
			traitLists.addAll(lists);
		}

		private class ViewHolder {
			TextView username;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.traitlist_info, null);
				holder = new ViewHolder();
				holder.username = (TextView) convertView
						.findViewById(R.id.username);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
				holder.name.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						TraitList traitList = (TraitList) cb.getTag();
						if (cb.isChecked()) {
							selectedTLists.add(traitList);
						} else {
							selectedTLists.remove(traitList);
						}
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TraitList traitList = traitLists.get(position);
			holder.username.setText("--" + traitList.getUsername());
			holder.name.setText(traitList.getTraitListName());
			holder.name.setTag(traitList);
			return convertView;
		}

	}

	class DownloadTraitListListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Gson gson = new GsonBuilder().create();
			String strJson = gson.toJson(selectedTLists);
			DownloadTraitListAsyncTask downloadTask = new DownloadTraitListAsyncTask();
			String url = Constant.urlString + "downloadTraitList.php";
			downloadTask.execute(url, strJson);
			
			

		}

	}

	class DownloadTraitListAsyncTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;

		@Override
		protected String doInBackground(String... params) {
			// request param
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("lists", params[1]));
			InputStream is = null;
			String result = "";// the return string
			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);
				httpPost.setEntity(httpEntity);

				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
				}
				else {
					System.out.println("response status wrong");
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				if (br != null) {
					while ((line = br.readLine()) != null) {
						sb.append(line + '\n');
					}
				}
				is.close();
				result = sb.toString();
				System.out.println("get the json result:"+result);
			} catch (Exception e) {
				System.out.println(e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//save selectedTraitLists 
			TraitListDao tlDao=new TraitListDao(DownloadTraitListActivity.this);
			TraitListContentDao traitListContentDao=new TraitListContentDao(DownloadTraitListActivity.this);
			PredefineValueDao predefineValueDao=new PredefineValueDao(DownloadTraitListActivity.this);
			TraitDao traitDao=new TraitDao(DownloadTraitListActivity.this);
			//save Traits and PredefineVals
			JSONArray jArray;
			try {
				jArray = new JSONArray(result);
				if (jArray.length()>0) {
					JSONArray traits=jArray.getJSONArray(0);
					
					JSONArray predefineValues=jArray.getJSONArray(1);
					JSONArray traitListContent=jArray.getJSONArray(2);
					
					List<Trait> traits2=new ArrayList<Trait>();
					List<TraitListContent> traitListContent2=new ArrayList<TraitListContent>();
					List<PredefineValue> predefineValue2=new ArrayList<PredefineValue>();
					for(TraitList tl:selectedTLists)
					{
						tl.setUsername(username);
						int newtraitListID=UUID.randomUUID().hashCode();
						for (int i = 0; i < traitListContent.length(); i++) {
							JSONObject content=traitListContent.getJSONObject(i);
							int traitListID=content.getInt("traitListID");
							int traitID=content.getInt("traitID");							
							if (traitListID==tl.getTraitListID()) {	
								TraitListContent tContent=new TraitListContent(newtraitListID, traitID);
								traitListContentDao.insert(tContent);
								traitListContents.add(tContent);
							}
							
							
						}
						tl.setTraitListID(newtraitListID);
						tl.setAccessible(1);
						tlDao.insert(tl);
						newSelectedTraitLists.add(tl);
					}
					System.out.println("during download:"+traitListContents.size());
					if (traits.length()>0) {
						for(int i=0;i<traits.length();i++)
						{
						  JSONObject trait=traits.getJSONObject(i);
						  Integer traitID=trait.getInt("traitID");
						  String traitName=trait.getString("traitName");
						  String widgetName=trait.getString("widgetName");
						  String unit=trait.getString("unit");
						  Integer accessible=trait.getInt("access");						  
						  traitDao.insert(new Trait(traitID, traitName, widgetName, unit,accessible),username);
						}
					}
					if (predefineValues.length()>0) {
						for (int i = 0; i < predefineValues.length(); i++) {
							JSONObject predefineVal=predefineValues.getJSONObject(i);
							int predefineValID=predefineVal.getInt("predefineValID");
							int traitID=predefineVal.getInt("traitID");
							String value=predefineVal.getString("value");							
							predefineValueDao.insert(new PredefineValue(predefineValID, traitID, value));
							
						}
					}

					Gson gson = new GsonBuilder().create();
					String url2=Constant.urlString+"AddDownloadTraitList.php";
					String strJson2=gson.toJson(newSelectedTraitLists);
					String strJson3=gson.toJson(traitListContents);
					AddDownloadTraitListAsyn aListAsyn=new AddDownloadTraitListAsyn();
					System.out.println("Before add traitListContents:"+traitListContents.size());
					aListAsyn.execute(url2,strJson2,strJson3);
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

	}
    class AddDownloadTraitListAsyn extends AsyncTask<String, Integer, String>{
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("traitLists", params[1]));
			param.add(new BasicNameValuePair("traitListContents", params[2]));
			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);
				httpPost.setEntity(httpEntity);

				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					System.out.println("add successully");
					
				}
				else {
					System.out.println("response status wrong");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		
    	
    
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(DownloadTraitListActivity.this,
					"Download Successfully, you can view them in TraitList Management",
					Toast.LENGTH_LONG).show();
		}
		
    }
	class FindAllAsyncTask extends AsyncTask<String, Integer, String> {		
		
		@Override
		protected void onPostExecute(String result) {
			try {

				JSONArray jArray = new JSONArray(result);
				if (jArray.length() > 0) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json_data = jArray.getJSONObject(i);

						Integer traitListID = json_data.getInt("traitListID");
						String traitListName = json_data
								.getString("traitListName");
						String username = json_data.getString("username");
						String deviceID = json_data.getString("deviceID");
						allTraitLists.add(new TraitList(traitListID, traitListName,
								username, deviceID));
					}

				} else {
					result = "Can't find Your name!";
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			MyCustomAdapter dataAdapter = new MyCustomAdapter(
					DownloadTraitListActivity.this, R.layout.traitlist_info,
					(ArrayList<TraitList>) allTraitLists);
			listView.setAdapter(dataAdapter);

			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TraitList traitList = (TraitList) parent
							.getItemAtPosition(position);
					Intent intent = new Intent(DownloadTraitListActivity.this,
							ShowTraitListPhp.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(SER_KEY, traitList);
					intent.putExtras(mBundle);

					Toast.makeText(DownloadTraitListActivity.this,
							"Clicked on Row: " + traitList.getTraitListName(),
							Toast.LENGTH_LONG).show();
					DownloadTraitListActivity.this.startActivity(intent);
				}
			});
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			HttpPost httpRequest = new HttpPost(params[0]);
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
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
                System.out.println("findAll result:"+result);
			} catch (Exception e) {
				
			}

			return result;
		}

	}

}
