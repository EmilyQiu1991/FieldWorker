package com.example.phpServer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.PredefineValue;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;

import android.R.integer;
import android.content.Context;
import android.os.AsyncTask;

public class DownloadTraitAndTraitList extends
		AsyncTask<String, Integer, String> {
    private Context context;
	public DownloadTraitAndTraitList(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> param=new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("username", arg0[1]));
		InputStream is=null;
		String result="";
		try {
			HttpEntity httpEntity=new UrlEncodedFormEntity(param,"utf-8");
			HttpClient httpClient=new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(arg0[0]);
			httpPost.setEntity(httpEntity);
			
			HttpResponse response=httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				HttpEntity entity=response.getEntity();
				is=entity.getContent();
			}
			else {
				System.out.println("DownloadTraitAndTraitList response status wrong");
			}
			BufferedReader br=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			String line=null;
			if (br != null) {
				while ((line = br.readLine()) != null) {
					sb.append(line + '\n');
				}
			}
			is.close();
			result = sb.toString();
			System.out.println("DownloadTraitAndTraitList get the json result:\n"+result);
		} catch (Exception e) {
			System.out.println("DownloadTraitAndTraitList"+e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		TraitListDao tlDao=new TraitListDao(context);
		TraitListContentDao traitListContentDao=new TraitListContentDao(context);
		PredefineValueDao predefineValueDao=new PredefineValueDao(context);
		TraitDao traitDao=new TraitDao(context);
		
		JSONArray jArray;
		try {
			jArray = new JSONArray(result);
			if (jArray.length()>0) {
				JSONArray traitLists=jArray.getJSONArray(0);
				JSONArray traits=jArray.getJSONArray(1);
				
				JSONArray predefineValues=jArray.getJSONArray(3);
				JSONArray traitListContents=jArray.getJSONArray(2);
				if (traitLists.length()>0) {
					for (int i = 0; i < traitLists.length(); i++) {
						JSONObject traitList=traitLists.getJSONObject(i);
						Integer traitListID=traitList.getInt("traitListID");
						String traitListName=traitList.getString("traitListName");
						String username=traitList.getString("username");
						int access=traitList.getInt("access");
						int nameVersion=traitList.getInt("nameVersion");
						tlDao.insert(new TraitList(traitListID, traitListName, username, access, nameVersion));
					}
				}
				if (traits.length()>0) {
					for(int i=0;i<traits.length();i++)
					{
					  JSONObject trait=traits.getJSONObject(i);
					  Integer traitID=trait.getInt("traitID");
					  String traitName=trait.getString("traitName");
					  String widgetName=trait.getString("widgetName");
					  String unit=trait.getString("unit");
					  Integer accessible=trait.getInt("access");	
					  String username=trait.getString("username");
					  int nameVersion=trait.getInt("nameVersion");
					  traitDao.insert(new Trait(traitID, traitName, widgetName, unit, accessible, username, nameVersion),username);
					}
				}
				if (traitListContents.length()>0) {
					for (int i = 0; i < traitListContents.length(); i++) {
						JSONObject traitListContent=traitListContents.getJSONObject(i);
						int traitListID=traitListContent.getInt("traitListID");
						int traitID=traitListContent.getInt("traitID");
						traitListContentDao.insert(new TraitListContent(traitListID, traitID));
						
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

			
			}
		} catch (JSONException e) {
			
			System.out.println("DownloadTraitAndTraitList onPostExecute:"+e);
		}
		

		
	}
	
    
}
