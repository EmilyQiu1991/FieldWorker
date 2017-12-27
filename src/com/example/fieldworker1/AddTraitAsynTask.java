package com.example.fieldworker1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.validator.MyApplication;

import android.os.AsyncTask;

public class AddTraitAsynTask extends AsyncTask<String, Integer, String> {
   
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> param=new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("username",arg0[1]));
		param.add(new BasicNameValuePair("trait",arg0[2]));
		param.add(new BasicNameValuePair("predefineValue",arg0[3]));
		param.add(new BasicNameValuePair("deviceId", MyApplication.getDeviceID()));
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
				System.out.println("AddTraitAsynTask response status wrong");
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
			System.out.println("AddTraitAsynTask get the json result:\n"+result);
		} catch (Exception e) {
			System.out.println("AddTraitAsynTask"+e);
		}
		return result;
	}

}
