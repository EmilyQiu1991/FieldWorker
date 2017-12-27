package com.example.fieldworker1;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.Destroyable;

import com.example.dao.ObserContentDao;
import com.example.dao.ObservationDao;
import com.example.dao.TraitListDao;
import com.example.domain.Observation;
import com.example.domain.User;
import com.example.fieldworker1.ListViewSubClass.OnDeleteListener;
import com.example.fieldworker1.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ObservationListActivity extends ListActivity {

	private User user;
	private ListViewSubClass mListView;
	private List<HashMap<String, String>> list;
	private MyAdapter listAdapter;
	private ObservationDao observationDao;
	private ObserContentDao obserContentDao;
	private TraitListDao traitListDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_observation_list);
		Intent intent = getIntent();
		user = new User(intent.getStringExtra("username"),
				intent.getStringExtra("password"));
		mListView = (ListViewSubClass) findViewById(android.R.id.list);
		observationDao = new ObservationDao(this);
		obserContentDao = new ObserContentDao(this);
		traitListDao = new TraitListDao(this);

		try {
			showObservations();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showObservations() throws ParseException {
		// TODO Auto-generated method stub
		list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		ArrayList<HashMap<String, String>> Observations = ObservationDao
				.showInList( user );
		for (Iterator<HashMap<String, String>> iterator = Observations
				.iterator(); iterator.hasNext();) {
			map = iterator.next();
			list.add(map);
		}
		listAdapter = new MyAdapter(this, list, R.layout.observationlist_item,
				new String[] { "observation_name", "observation_date",
						"observation_delete" }, new int[] {
						R.id.observationItem_name, R.id.observationItem_date,
						R.id.observationItem_delete });
		mListView.setAdapter(listAdapter);
		mListView.setOnDeleteListener(new OnDeleteListener() {

			@Override
			public void onDelete(int index) {
				// TODO Auto-generated method stub
				String observationName = list.get(index)
						.get("observation_name");
				int id = observationDao.findIdByName(observationName);
				java.util.Date current = new java.util.Date();
				if (observationDao.findObervationById(id).getDeleteTime() != null) {
					java.util.Date deadline = observationDao
							.findObervationById(id).getDeleteTime();

					if (current.before(deadline)) {
						observationDao.deleteById(id);
						obserContentDao.deleteById(id);
						Intent intent = new Intent();
						intent.putExtra("username", user.getUserName());
						intent.putExtra("password", user.getPassword());
						intent.setClass(ObservationListActivity.this,
								ObservationListActivity.class);
						startActivity(intent);
						finish();
					} else {
						Toast toast = Toast
								.makeText(ObservationListActivity.this,
										"You cannot delete " + observationName
												+ " after deadline.",
										Toast.LENGTH_LONG);
						toast.show();
					}
				} else {
					observationDao.deleteById(id);
					obserContentDao.deleteById(id);
					Intent intent = new Intent();
					intent.putExtra("username", user.getUserName());
					intent.putExtra("password", user.getPassword());
					intent.setClass(ObservationListActivity.this,
							ObservationListActivity.class);
					startActivity(intent);
					finish();
				}

			}

		});

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		intent.putExtra("username", user.getUserName());
		intent.putExtra("password", user.getPassword());
		String observationName = list.get(position).get("observation_name");
		intent.putExtra("observationID",
				observationDao.findIdByName(observationName));
		intent.setClass(ObservationListActivity.this, ObservationActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.observationlist, menu);
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
			intent.setClass(ObservationListActivity.this,
					ResetPasswordActivity.class);
			intent.putExtra("password", user.getPassword());
			intent.putExtra("username", user.getUserName());
			startActivity(intent);
			finish();
			return true;
		} else if (id == R.id.action_logout) {
			Intent intent = new Intent();
			intent.setClass(ObservationListActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("flag", 1);
			startActivity(intent);
			finish();
			return true;
		} else if (id == R.id.action_observation_create) {
			if (traitListDao.findAll().size() == 0)
				Toast.makeText(ObservationListActivity.this,
						"You must create a trait list before doing this.:)",
						Toast.LENGTH_SHORT).show();
			else {
				Intent intent = new Intent(ObservationListActivity.this,
						CreateObservationActivity.class);
				intent.putExtra("username", user.getUserName());
				intent.putExtra("password", user.getPassword());

				startActivity(intent);
				finish();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	class MyAdapter extends SimpleAdapter {
		private LayoutInflater inflater = null;
		private List<Map<String, Object>> styles = null;

		public List<Map<String, Object>> getStyles() {
			return styles;
		}

		public void setStyles(List<Map<String, Object>> styles) {
			this.styles = styles;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View result = super.getView(position, convertView, parent);
			if (result != null) {
				inflater.inflate(R.layout.activity_observation_list, null);
			}
			return result;
		}

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			inflater = LayoutInflater.from(context);
		}
	}
}
