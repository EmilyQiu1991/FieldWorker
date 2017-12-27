package com.example.fieldworker1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.dao.TraitDao;
import com.example.dao.TraitListDao;
import com.example.domain.TraitList;

import com.example.phpServer.TraitListPhpService;
import com.example.service.TraitListService;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTraitList1 extends Activity {
	   private String TAG="AddTraitList";
	   private TraitDao traitDao;
	   private TraitListService traitListService;
	   private TraitListPhpService traitListPhpService;
	   private List<String> checkedItems;
	   private Button button;
	   private Button chooseTraits;
	   private EditText traitListName;
	   private TextView traitsTextView;
	   private Validator validator;
	   private TraitListDao traitListDao;
	   private String username;
	   @Override
	   public void onCreate(Bundle savedInstanceBundle)
	   {
		   super.onCreate(savedInstanceBundle);
		   System.out.println("AddTraitList1 starts");
		   setContentView(R.layout.add_trait_list1);
		   Intent intent=getIntent();
			username=intent.getStringExtra("username");
			traitsTextView=(TextView) findViewById(R.id.traits);
			checkedItems=new ArrayList<String>();
			traitListDao=new TraitListDao(AddTraitList1.this);
			validator=new Validator(this);
			validator.setValidationListener(new TraitListNameValidator());
			Rule<EditText> uniqueTraitListName=new Rule<EditText>("Trait List Name already exists") {
            
				@Override
				public boolean isValid(EditText arg0) {
					
					return traitListDao.checkTraitListName(traitListName.getText().toString(),username);
				}
			};
			validator.put(traitListName,uniqueTraitListName);
			traitListName=(EditText) findViewById(R.id.traitListName);
			//list=(ListView)findViewById(R.id.listView);
			button=(Button)findViewById(R.id.result);
			//button.setOnClickListener(this);
			chooseTraits=(Button) findViewById(R.id.chooseTrait);
			//get traits from database
			traitDao=new TraitDao(AddTraitList1.this);
			traitListService=new TraitListService(AddTraitList1.this);
			traitListPhpService=new TraitListPhpService(AddTraitList1.this,username);
			
			chooseTraits.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {						
					List<String> nameList= traitDao.findAllTraitNames(username);   				
						final String[] traitNames=new String[nameList.size()];
				        for (int i = 0; i < traitNames.length; i++) {
							traitNames[i]=nameList.get(i);
						}
						boolean[] checked=new boolean[traitNames.length];
						for(int i=0;i<checked.length;i++)
						{
							checked[i]=false;
						}
						new AlertDialog.Builder(AddTraitList1.this).setCancelable(false)
				        .setTitle("Choose Traits")
				        .setMultiChoiceItems(traitNames, checked, 
								new DialogInterface.OnMultiChoiceClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which, boolean isChecked) {
										if (isChecked) {
										     
											if(!checkedItems.contains(traitNames[which]))
											{
											checkedItems.add(traitNames[which]);
											//Toast.makeText(getApplicationContext(), array[which], Toast.LENGTH_SHORT).show();
											}
											}
									}
								})
						.setPositiveButton("OK",
							new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialoginterface, int i)
								{
									
	                                //traitListService.addTraitList(traitListName.getText().toString(), checkedItems);
								    dialoginterface.dismiss();
								    traitsTextView.setVisibility(View.VISIBLE);
								   
								    String s = "";
								    for (int j = 0; j < checkedItems.size(); j++) {
										s+=checkedItems.get(j)+"\n";
									}
								    traitsTextView.setText(s);
								    
								}
							}).show();
						button.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								validator.validate();
								
								
							}
						});
		}
		
		
});
}
		class TraitListNameValidator implements ValidationListener{

			@Override
			public void onValidationFailed(View failedView, Rule<?> failedRule) {
				// TODO Auto-generated method stub
				String message = failedRule.getFailureMessage();

		        if (failedView instanceof EditText) {
		            failedView.requestFocus();
		            ((EditText) failedView).setError(message);
		        } else {
		            Toast.makeText(AddTraitList1.this, message, Toast.LENGTH_SHORT).show();
		        }
			}

			@Override
			public void onValidationSucceeded() {
				Integer traitListID=UUID.randomUUID().hashCode();
				//TraitList traitList=new TraitList(traitListID, traitListName.getText().toString(), username);
				TraitList traitList=new TraitList(traitListID, traitListName.getText().toString(), username, 1);
				if (isNetworkOnline()) {
					traitListPhpService.addTraitList(traitList, checkedItems);
				}
				
			 	traitListService.addTraitList(traitList, checkedItems,isNetworkOnline());
				
				
				
				Intent intent=new Intent();
				intent.setClass(AddTraitList1.this, TraitListActivity2.class);
				AddTraitList1.this.startActivity(intent);
				finish();
			}
			public boolean isNetworkOnline() {
			    boolean status=false;
			    try{
			        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			        NetworkInfo netInfo = cm.getNetworkInfo(0);
			        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
			            status= true;
			        }else {
			            netInfo = cm.getNetworkInfo(1);
			            if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
			                status= true;
			        }
			    }catch(Exception e){
			        e.printStackTrace();  
			        return false;
			    }
			    return status;

			    }  
	    	
	    }
}