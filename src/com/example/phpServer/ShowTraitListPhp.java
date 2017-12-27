package com.example.phpServer;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.domain.PredefineValue;
import com.example.domain.TraitList;
import com.example.fieldworker1.CheckBoxAdapter;
import com.example.fieldworker1.Constant;
import com.example.fieldworker1.DownloadTraitListActivity;
import com.example.fieldworker1.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ShowTraitListPhp extends Activity {
	private TraitList traitList;
	//private static final String UR_STRING = "http://172.31.201.109:8888";
	 private TableLayout table=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template1);
		table=(TableLayout) findViewById(R.id.templateTalbe);
		traitList = (TraitList) getIntent().getSerializableExtra(
				DownloadTraitListActivity.SER_KEY);
		
		AsynFindTraits asynFindTraits = new AsynFindTraits(traitList);
		String url = Constant.urlString + "server.php";
		asynFindTraits.execute(url);

	}

	class AsynFindTraits extends AsyncTask<String, Integer, String> {
		private TraitList traitList;

		public AsynFindTraits(TraitList traitList) {
			this.traitList = traitList;
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String result = "";
			InputStream is = null;
			HttpPost httpRequest = new HttpPost(Constant.urlString + "SearchTraits.php");
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(traitList);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("traitList", strJSON));

			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);

				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();

					 is=entity.getContent();
					//System.out.println("***" + EntityUtils.toString(entity));
					
					
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					result = sb.toString();
					is.close();
					
					

				} else {
					System.out.println("request error");
				}

			} catch (Exception e) {
				System.out.println(e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			try{
			
			JSONArray jArray = new JSONArray(result);
			System.out.println("***"+jArray.length());
			if (jArray.length() > 0) {
				
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					Integer traitID = json_data.getInt("traitID");
					String deviceID = json_data.getString("deviceID");
					String traitName=json_data.getString("traitName");
					String widgetName=json_data.getString("widgetName");
					String unit=json_data.getString("unit");
					
					JSONArray values=json_data.getJSONArray("predefineVal");
					ArrayList<PredefineValue> predefineValues=new ArrayList<PredefineValue>();
					if(values.length()>0)
					{
						
						for (int j = 0; j < values.length(); j++) {
							JSONObject json_value=values.getJSONObject(j);
							int predefineValID=json_value.getInt("predefineValID");
							String value=json_value.getString("value");
							predefineValues.add(new PredefineValue(predefineValID, traitID, value));
							
							
						}
					}
					String[] vaStrings=new String[predefineValues.size()];
					for (int k=0;k<predefineValues.size();k++) {
						vaStrings[k]=predefineValues.get(k).getValue();
					}
					if (widgetName.equals("Spinner")) {
						appendSpinner(traitName,vaStrings,unit);
					}
					else if (widgetName.equals("EditText")) {
						appendEditText(traitName,unit);
					}
					else if(widgetName.equals("CheckBox")){
						appendCheckBox(traitName, vaStrings,unit);
					}
					else if(widgetName.equals("Slider")){
						appendSlider(traitName, vaStrings,unit);
					}	
					
				}
				
			}
            }
            catch(Exception e){
            	System.out.println(e);
            }

		}
		
		
		private void appendSpinner(final String traitName,String[] pValues,String unit)
		{
			final int id=table.getChildCount();
			TableRow row=new TableRow(ShowTraitListPhp.this);
			row.setId(id);
			TextView traitNameTextView=new TextView(ShowTraitListPhp.this);
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(15);
			
			Spinner spinner=new Spinner(ShowTraitListPhp.this);
	        ArrayAdapter<String> adp=new ArrayAdapter<String>(ShowTraitListPhp.this, android.R.layout.simple_spinner_item,pValues);
	        spinner.setAdapter(adp);
	       
	        
	      
	        row.addView(traitNameTextView);
	        row.addView(spinner);
	        TableRow.LayoutParams params = (TableRow.LayoutParams)spinner.getLayoutParams();
			params.span = 2;
			spinner.setLayoutParams(params);
	        TextView unitTextView=new TextView(ShowTraitListPhp.this);
			
			unitTextView.setText(unit);
			unitTextView.setTextSize(15);
			unitTextView.setGravity(Gravity.LEFT);
			row.addView(unitTextView);
			
			//row.addView(deleteButton);
	        table.addView(row);
			
		}
		private void appendEditText(final String traitName,String unit) {
			final int id=table.getChildCount();
			TableRow row=new TableRow(ShowTraitListPhp.this);
			row.setId(id);
			TextView traitNameTextView=new TextView(ShowTraitListPhp.this);
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(15);
			EditText editText=new EditText(ShowTraitListPhp.this);
		
	        row.addView(traitNameTextView);
	        row.addView(editText);
	        TableRow.LayoutParams params = (TableRow.LayoutParams)editText.getLayoutParams();
			params.span = 2;
			editText.setLayoutParams(params);
	        TextView unitTextView=new TextView(ShowTraitListPhp.this);
			unitTextView.setGravity(Gravity.LEFT);
			unitTextView.setText(unit);
			unitTextView.setTextSize(15);
			row.addView(unitTextView);
			 //row.addView(deleteButton);
			
	        table.addView(row);
		}
		private void appendCheckBox(final String traitName,String[] pValues,String unit)
		{
			final int id=table.getChildCount();
			TableRow row=new TableRow(ShowTraitListPhp.this);
			row.setId(id);
			TextView traitNameTextView=new TextView(ShowTraitListPhp.this);
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(15);
			
			ListView listview=new ListView(ShowTraitListPhp.this);
			listview.setDivider(null);
			 final ArrayList<Boolean> checkedItem=new ArrayList<Boolean>();
			ArrayList<String> array=new ArrayList<String>();
			//initial checkedItem and array
			for (int i = 0; i < pValues.length; i++) {
				array.add(pValues[i]);
				checkedItem.add(false);
			}
			
			final CheckBoxAdapter chedapter=new CheckBoxAdapter(ShowTraitListPhp.this,array,checkedItem);
			listview.setAdapter(chedapter);
			listview.setItemsCanFocus(false);
			listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					System.out.println("click an item");
					// TODO Auto-generated method stub
					if (checkedItem.get(position)==true) {
						checkedItem.set(position, false);
						
					}
					else {
						checkedItem.set(position, true);
					}
					chedapter.getView(position, view, parent);
				}
				
			});
			listview.setPadding(5, 0, 0, 5);
			
	        TextView unitTextView=new TextView(ShowTraitListPhp.this);
			
			unitTextView.setText(unit);
			unitTextView.setTextSize(15);
			unitTextView.setGravity(Gravity.LEFT);
	        row.addView(traitNameTextView);
	        TableRow.LayoutParams params1 = (TableRow.LayoutParams)traitNameTextView.getLayoutParams();
			params1.gravity=Gravity.TOP;
			traitNameTextView.setLayoutParams(params1);
	        
	        row.addView(listview);
	        TableRow.LayoutParams params = (TableRow.LayoutParams)listview.getLayoutParams();
			params.span = 2;
			listview.setLayoutParams(params);
	        row.addView(unitTextView);
	       // row.addView(deleteButton);
	        
	        table.addView(row);
		}
		public void appendSlider(final String traitName,String[] pValues,String unit)
		{
		    final int min=Math.min(Integer.parseInt(pValues[0]), Integer.parseInt(pValues[1]));
		    int max=Math.max(Integer.parseInt(pValues[0]), Integer.parseInt(pValues[1]));
			final int id=table.getChildCount();
			TableRow row=new TableRow(ShowTraitListPhp.this);
			row.setId(id);
			TextView traitNameTextView=new TextView(ShowTraitListPhp.this);
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(15);
			
			final TextView maxTextView=new TextView(ShowTraitListPhp.this);
			maxTextView.setText(String.valueOf(min));
			maxTextView.setGravity(Gravity.LEFT);
			SeekBar seekBar=new SeekBar(ShowTraitListPhp.this);
			seekBar.setMax(Math.max(Integer.parseInt(pValues[0]), Integer.parseInt(pValues[1])));
			seekBar.setProgress(min);
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					System.out.println(String.valueOf(progress));
					maxTextView.setText(String.valueOf(progress+min));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
			});
			TextView unitTextView=new TextView(ShowTraitListPhp.this);
			
			unitTextView.setText(unit);
			unitTextView.setGravity(Gravity.LEFT);
			unitTextView.setTextSize(15);
			row.addView(traitNameTextView);
			row.addView(seekBar);
			row.addView(maxTextView);
			row.addView(unitTextView);
		
	        
	        table.addView(row);
		}
	}
}
