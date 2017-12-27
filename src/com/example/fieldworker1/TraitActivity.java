package com.example.fieldworker1;

import java.util.ArrayList;
import com.example.dao.TraitDao;
import com.example.domain.Trait;
import com.example.fieldworker1.ListViewSubClass.OnDeleteListener;
import com.example.phpServer.DeleteTraitAsynTask;
import com.example.validator.MyApplication;

import android.app.ListActivity;
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

public class TraitActivity extends ListActivity {
private static final int AddTrait=1;
private static final String PREFS_NAME = "MyPrefsFile";
	private TraitDao traitDao;
	private ListViewSubClass mListView;
	private String username;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		username = mySharedPreferences.getString("username", "");
		traitDao=new TraitDao(TraitActivity.this);
		mListView=(ListViewSubClass) findViewById(android.R.id.list);
		showTraits();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,AddTrait,1,R.string.addTrait);
		//menu.add(0,obserList,2,R.string.obserList);
		
		return super.onCreateOptionsMenu(menu);
	}
    
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId()==AddTrait) {
			Intent intent=new Intent();
			intent.setClass(TraitActivity.this,AddTraitActivity.class);
			TraitActivity.this.startActivity(intent);
			finish();
			//showTraitList();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void showTraits() {
		System.out.println("start showTraits");
		
		final ArrayList<Trait> traits=traitDao.findAllForOne(username);
		System.out.println("TraitActivity traits:"+traits);
		final MyCustomAdapter dataAdapter=new MyCustomAdapter(this,  R.layout.trait_item, traits);
		
		
		//setListAdapter(listAdapter);
		mListView.setAdapter(dataAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Trait trait=(Trait) arg0.getItemAtPosition(arg2);
				Intent intent=new Intent();
				Bundle mBundle=new Bundle();
				mBundle.putSerializable("clickTrait",trait);
				intent.putExtras(mBundle);
				intent.setClass(TraitActivity.this, ShowTrait.class);
				TraitActivity.this.startActivity(intent);
				TraitActivity.this.finish();
				
			}
		});
		mListView.setOnDeleteListener(new OnDeleteListener(){

			@Override
			public void onDelete(int index) {
			
			 traitDao.delete(traits.get(index),username);
			 if (MyApplication.isNetworkOnline()) {
				DeleteTraitAsynTask deleteTraitAsynTask=new DeleteTraitAsynTask();
				Integer traitID=traits.get(index).getTraitID();
				String url=Constant.urlString+"DeleteTrait.php";
				String isUsed=Boolean.toString(traitDao.isUsed(traitID, username));
				deleteTraitAsynTask.execute(url,traitID+"",isUsed);
			}
			 traits.remove(index);
			 dataAdapter.notifyDataSetChanged();
			}
			
		});
		
	}

	
	private class MyCustomAdapter extends ArrayAdapter<Trait> {

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<Trait> lists) {
			super(context, textViewResourceId, lists);
		}

		private class ViewHolder {
			TextView traitName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.trait_item, null);
				holder = new ViewHolder();
				holder.traitName = (TextView) convertView
						.findViewById(R.id.trait_name);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Trait t=getItem(position);
			if (t.getNameVersion() == 0) {
				holder.traitName.setText(t.getTraitName());
			} else {
				holder.traitName.setText(t.getTraitName() + "_"
						+ t.getNameVersion());
			}
			return convertView;
		}
	}
	
	
}
