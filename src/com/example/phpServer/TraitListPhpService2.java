package com.example.phpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.domain.TraitList;
import com.example.fieldworker1.Constant;
import com.example.fieldworker1.R;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TraitListPhpService2 {
	private Context context;
	private String username;
	//private static final String UR_STRING = "http://172.31.201.109:8888";
	public final static String SER_KEY = "com.example.fieldworker1.tra";

	public TraitListPhpService2(Context context, String username) {
		super();
		this.context = context;
		this.username = username;
	}

	public void findAll(ListView mListView, Context context,
			ArrayList<TraitList> list) throws InterruptedException {
		String url = Constant.urlString + "server.php";
		FindAllAsyncTask findAllAsyncTask = new FindAllAsyncTask(mListView,
				context, list);
		findAllAsyncTask.execute(url);
	}

	class FindAllAsyncTask extends AsyncTask<String, Integer, String> {

		private ListView mListView;
		private ArrayList<TraitList> list;

		public FindAllAsyncTask(ListView mListView2, Context context,
				ArrayList<TraitList> list2) {
			this.mListView = mListView2;
			this.list = list2;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
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
						list.add(new TraitList(traitListID, traitListName,
								username, deviceID));
					}

				} else {
					result = "Can't find Your name!";
				}
			} catch (Exception e) {
				// TODO: handle exceptiSon
				// Log.e("log_tag", "Error parsing data "+e.toString());
			}
			MyCustomAdapter dataAdapter = new MyCustomAdapter(context,
					R.layout.traitlist_info, list);
			mListView.setAdapter(dataAdapter);

			mListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TraitList traitList = (TraitList) parent
							.getItemAtPosition(position);
					Intent intent = new Intent(context, ShowTraitListPhp.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(SER_KEY, traitList);
					intent.putExtras(mBundle);
                    
					Toast.makeText(context,
							"Clicked on Row: " + traitList.getTraitListName(),
							Toast.LENGTH_LONG).show();
					context.startActivity(intent);
				}
			});
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

			} catch (Exception e) {
				// TODO: handle exception
			}

			return result;
		}

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
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
						// TODO Auto-generated method stub
						CheckBox cb = (CheckBox) v;
						TraitList traitList = (TraitList) cb.getTag();
						Toast.makeText(
								context,
								"Clicked on Checkbox: " + cb.getText() + " is "
										+ cb.isChecked(), Toast.LENGTH_LONG)
								.show();

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
}