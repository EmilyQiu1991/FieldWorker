package com.example.fieldworker1;

import java.util.ArrayList;
import java.util.List;

import com.example.asynTask.LogoutAsynTask;
import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.ObservationDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.User;
import com.example.phpServer.TraitListPhpService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends Activity {

	private static final String PREFS_NAME = "MyPrefsFile";
    private BroadcastReceiver mReceiver;
	private ListView listView;
	private User user;
	private AddLogDao addLogDao;
	private DeleteLogDao deleteLogDao;
	@Override
	protected void onResume() {
		super.onStart();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mReceiver=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action=intent.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					ConnectivityManager con=(ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
					NetworkInfo info=con.getActiveNetworkInfo();
					if (info!=null&&info.isAvailable()) {
						String name = info.getTypeName();
	                    System.out.println("current network name：" + name);
					}
					else {
						System.out.println("no available network");
	                }
				}
				
			}
		};
		registerReceiver(mReceiver, intentFilter);
		
	}
	

	@Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
      
    }
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		LogoutAsynTask lTask=new LogoutAsynTask();
		lTask.execute( Constant.urlString + "Logout.php",user.getUserName());
		System.out.println("HomeActivity onDestroy");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		mySharedPreferences.getString("username", "");
		Intent intent = getIntent();
		user = new User(intent.getStringExtra("username"),
				intent.getStringExtra("password"));
		if (isNetworkOnline()) {
			TraitListPhpService traitListPhpService = new TraitListPhpService(
					HomeActivity.this,user.getUserName());

			// synchonize the data of delete log and add log and updata sqlite
			// traitList table
			// firstly, add log table
			List<AddLog> addLogs = new ArrayList<AddLog>();
			addLogDao = new AddLogDao(this);
			deleteLogDao = new DeleteLogDao(this);
			addLogs = addLogDao.findAllByTableName("TraitList");
			List<AddLog> addLogs2 = new ArrayList<AddLog>();
			addLogs2 = addLogDao.findAllByTableName("TraitListContent");
			List<AddLog> addLogs3=new ArrayList<AddLog>();
			addLogs3=addLogDao.findAllByTableName("Trait");
			List<AddLog> addLogs4=new ArrayList<AddLog>();
			addLogs4=addLogDao.findAllByTableName("PredefineVal");
			
			
			
			List<DeleteLog> deleteLogs = new ArrayList<DeleteLog>();
			List<DeleteLog> deleteLogs2 = new ArrayList<DeleteLog>();
			deleteLogs = deleteLogDao.findAllByTableName("TraitList");
			deleteLogs2=deleteLogDao.findAllByTableName("TraitListContent");
			// JSONArray jsonArray=JSONArray.fromObject(addLogs);
			System.out.println("Add log size:"+addLogs.size());
			if (addLogs.size() > 0 || deleteLogs.size() > 0
					|| addLogs2.size() > 0||deleteLogs2.size()>0||addLogs3.size()>0||addLogs4.size()>0) {
                System.out.println("home activity addLogs size not empty");
				traitListPhpService.synTraitList(addLogs, addLogs2,addLogs3,addLogs4, deleteLogs,deleteLogs2);
			}

		}
		

		List<String> selection = new ArrayList<String>();
		selection.add("Observation Management");
		selection.add("TraitList Management");
		
		selection.add("Trait Management");
		selection.add("Observation Analysis");
		selection.add("Upload/Download Image/Excel File");
        selection.add("More TraitList");
		listView = new ListView(this);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, selection));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this,
							ObservationListActivity.class);
					intent.putExtra("username", user.getUserName());
					intent.putExtra("password", user.getPassword());
					startActivity(intent);
				} else if (position == 1) {
					Intent intent = new Intent();
					intent.putExtra("username", user.getUserName());
					intent.setClass(HomeActivity.this, TraitListActivity2.class);
					//intent.setClass(HomeActivity.this, DownloadTraitListActivity.class);
					startActivity(intent);
				} else if (position == 2) {
					Intent intent = new Intent();
					intent.putExtra("username", user.getUserName());
					intent.setClass(HomeActivity.this, TraitActivity.class);
					startActivity(intent);

				} else if (position == 3) {
					Intent intent = new Intent();
				
					intent.setClass(HomeActivity.this, DataAnalysisAcitivity.class);
					startActivity(intent);
				} else if (position == 5) {
					Intent intent=new Intent();
					intent.setClass(HomeActivity.this, DownloadTraitListActivity.class);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this, FileTransActivity.class);
					intent.putExtra("username", user.getUserName());
					intent.putExtra("password", user.getPassword());
					startActivity(intent);
				}
			}

		});

		setContentView(listView);

	}

	

	
	

	
	

	public boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_reset_password) {
			Intent intent = new Intent();
			intent.setClass(HomeActivity.this, ResetPasswordActivity.class);
			intent.putExtra("password", user.getPassword());
			intent.putExtra("username", user.getUserName());
			startActivity(intent);
			return true;
		} else {
			LogoutAsynTask lTask=new LogoutAsynTask();
//			String UR_STRING = "http://172.31.201.109:8888//workspace/test/Logout.php";
//			System.out.println("HomeActivity username:"+user.getUserName());
			lTask.execute( Constant.urlString + "Logout.php",user.getUserName());
			Intent intent = new Intent();
			intent.setClass(HomeActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("flag", 1);
			startActivity(intent);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}
}
