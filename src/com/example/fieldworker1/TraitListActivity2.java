package com.example.fieldworker1;

import android.app.Activity;
import java.util.ArrayList;
import java.util.UUID;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.TraitList;
import com.example.fieldworker1.ListViewSubClass.OnDeleteListener;
import com.example.phpServer.TraitListPhpService;
import com.example.service.TraitListService;
import com.example.validator.MyApplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TraitListActivity2 extends Activity {
	private static final int traitList = 1;
	private static final String PREFS_NAME = "MyPrefsFile";
	public final static String SER_KEY = "com.example.fieldworker1.tra";
	
	private AddLogDao addLogDao;
	private DeleteLogDao deleteLogDao;
	private ListViewSubClass mListView;
	//private List<HashMap<String, String>> list;
	private String username;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("TraitListActivity2 starts");
		setContentView(R.layout.main);
		
		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		username = mySharedPreferences.getString("username", "");
		
		addLogDao = new AddLogDao(TraitListActivity2.this);
		deleteLogDao = new DeleteLogDao(TraitListActivity2.this);
		mListView = (ListViewSubClass) findViewById(android.R.id.list);
		try {
			showTraitList();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, traitList, 1, R.string.addTraitList);
		

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == traitList) {
			Intent intent = new Intent();
			intent.putExtra("username", username);
			System.out.println("Start AddTraitList1 Activity");
			intent.setClass(TraitListActivity2.this, AddTraitList1.class);
			TraitListActivity2.this.startActivity(intent);
			try {
				showTraitList();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	private void showTraitList() {
		final TraitListDao traitListDao=new TraitListDao(TraitListActivity2.this);
		final TraitListPhpService traitListPhpService=new TraitListPhpService(TraitListActivity2.this,username);
		final TraitListService tlService=new TraitListService(this);
		final ArrayList<TraitList> traitLists = (ArrayList<TraitList>) traitListDao
				.findByUsername(username);
		final MyCustomAdapter dataAdapter = new MyCustomAdapter(this,
				R.layout.triatlist, traitLists);
		mListView.setAdapter(dataAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				TraitList traitList=(TraitList) parent.getItemAtPosition(position);
				Intent intent=new Intent(TraitListActivity2.this,ShowTraitList.class);
				Bundle mBundle=new Bundle();
				mBundle.putSerializable(SER_KEY,traitList);
				intent.putExtras(mBundle);
				TraitListActivity2.this.startActivity(intent);
			}
		});
		mListView.setOnDeleteListener(new OnDeleteListener(){
			@Override
			public void onDelete(int index) {
				
			if (MyApplication.isNetworkOnline()) {				
				TraitList t=traitLists.get(index);
				String isUsed=Boolean.toString(tlService.isUsed(t));
				traitListPhpService.deleteTraitList(t.getTraitListID(),isUsed);
			}
			else {
				Integer traitListID=traitLists.get(index).getTraitListID();
				AddLog addLog=new AddLog(UUID.randomUUID().hashCode(), "TraitList", traitListID);
				if (addLogDao.checkExist(addLog)) {
					System.out.println("the traitList just added,have not added into server");
				}
				else{
				deleteLogDao.insert(new DeleteLog(UUID.randomUUID().hashCode(), "TraitList", traitListID));
				//addLogDao.insert(new AddLog(UUID.randomUUID().hashCode(), "TraitList", traitListID));
				}
			}
		    tlService.deleteTraitList(traitLists.get(index));	
			 traitLists.remove(index);
			 dataAdapter.notifyDataSetChanged();
			}
			
		});
		
	}

	private class MyCustomAdapter extends ArrayAdapter<TraitList> {

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<TraitList> lists) {
			super(context, textViewResourceId, lists);
		}

		private class ViewHolder {
			TextView traitListName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.triatlist, null);
				holder = new ViewHolder();
				holder.traitListName = (TextView) convertView
						.findViewById(R.id.traitList_name);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TraitList t=getItem(position);
			if (t.getNameVersion() == 0) {
				holder.traitListName.setText(t.getTraitListName());
			} else {
				holder.traitListName.setText(t.getTraitListName() + "_"
						+ t.getNameVersion());
			}
			return convertView;
		}
	}
	
}
