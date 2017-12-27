package com.example.asynTask;

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

import android.os.AsyncTask;

public class LogoutAsynTask extends AsyncTask<String, Integer, String> {

	@Override
	protected String doInBackground(String... params) {
		HttpPost httpPost=new HttpPost(params[0]);
		List<NameValuePair> param=new ArrayList<NameValuePair>();
		System.out.println("LogoutAsynTask username:"+params[1]);
		param.add(new BasicNameValuePair("username", params[1]));
		
		InputStream is=null;
		String result = null;
		try {
			HttpEntity httpEntity=new UrlEncodedFormEntity(param,"utf-8");
			httpPost.setEntity(httpEntity);
			
			HttpClient httpClient=new DefaultHttpClient();
			HttpResponse httpResponse=httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				HttpEntity entity=httpResponse.getEntity();
				is=entity.getContent();
			}
			BufferedReader br=new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb=new StringBuilder();
			String line=null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			}
		catch (Exception e) {
			System.out.println("LogoutAsynTask error:"+e);
		}
		System.out.println("LogoutAsynTask result:"+result);
		return result;
	}

}
