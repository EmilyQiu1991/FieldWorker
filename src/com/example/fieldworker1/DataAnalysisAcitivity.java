package com.example.fieldworker1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.adapter.MyAdapter1;
import com.example.adapter.TraitAdapter;
import com.example.dao.ObservationDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.fieldworker1.MainActivity.ValidateUsernamesynTask;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.R.integer;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DataAnalysisAcitivity extends Activity {
	private TraitListDao traitListDao;
	private TraitListContentDao traitListContentDao;
	private ObservationDao observationDao;
	private Date from, to;
	private String chartType;
	private List<Trait> traits;
	private ArrayList<Trait> traitSelected;
	private ArrayList<Trait> traitSelected1;
	private ArrayList<String> obserSelected;
	private ArrayList<Integer> obserIDsSelected;
	private List<String> observations;
	private List<Integer> observationIDs;
	private List<String> observationsForDate;
	private List<Integer> obserIDsForDate;
	private TableLayout obserTable;
	private RadioGroup group;
	private EditText fromDate;
	private EditText toDate;
	private Spinner traitListSpinner;
	private Spinner traitSpinner;
	private Spinner trait2Spinner;
	private Button submit;
	private RadioButton bar;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_choose_observations);
		SharedPreferences mySharedPreferences = getSharedPreferences(
				"MyPrefsFile", 0);
		username = mySharedPreferences.getString("username", "");

		traitListDao = new TraitListDao(this);
		traitListContentDao = new TraitListContentDao(this);
		observationDao = new ObservationDao(this);

		traitSelected = new ArrayList<Trait>();
		traitSelected1 = new ArrayList<Trait>();
		obserSelected = new ArrayList<String>();
		obserIDsSelected = new ArrayList<Integer>();

		bar = (RadioButton) findViewById(R.id.trait1_bar_button);
		bar.setChecked(true);
		chartType = "Bar";
		obserTable = (TableLayout) findViewById(R.id.observation_choose_table);
		group = (RadioGroup) findViewById(R.id.trait1_radioGroup);
		fromDate = (EditText) findViewById(R.id.trait1_from_date);
		toDate = (EditText) findViewById(R.id.trait1_to_date);
		traitSpinner = (Spinner) findViewById(R.id.trait1Spinner_dialog);
		trait2Spinner = (Spinner) findViewById(R.id.trait2Spinner_dialog);
		traitListSpinner = (Spinner) findViewById(R.id.traitListSpinner_dialog);
		submit = (Button) findViewById(R.id.submit);

		List<String> selections = new ArrayList<String>();
		List<TraitList> traitLists = traitListDao.findByUsername(username);
		MyAdapter1 traitListAdapter = new MyAdapter1(traitLists, this);
		traitListSpinner.setAdapter(traitListAdapter);
		traitListSpinner
				.setOnItemSelectedListener(new TraitListSpinnerListener());

		traitSpinner.setOnItemSelectedListener(new traitSpinnerListener());
		trait2Spinner.setOnItemSelectedListener(new trait2SpinnerListener());

		fromDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);
			}
		});
		toDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(1);

			}
		});
		group = (RadioGroup) findViewById(R.id.trait1_radioGroup);

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) DataAnalysisAcitivity.this
						.findViewById(radioButtonId);
				chartType = rb.getText().toString();
			}

		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Validate()) {
					
					Intent intentAnalyseObser = new Intent();
					Bundle mBundle=new Bundle();
					
					mBundle.putSerializable("traitName", traitSelected);
					intentAnalyseObser.putExtras(mBundle);
					intentAnalyseObser.putExtra("chartType", chartType);
					intentAnalyseObser.putStringArrayListExtra(
							"observationName", obserSelected);
					intentAnalyseObser.putIntegerArrayListExtra(
							"observationID", obserIDsSelected);
					intentAnalyseObser.setClass(DataAnalysisAcitivity.this,
							DataChart.class);
					DataAnalysisAcitivity.this
							.startActivity(intentAnalyseObser);
					DataAnalysisAcitivity.this.finish();
				}

			}

			private boolean Validate() {

				if (obserIDsSelected.size() == 0) {
					Crouton.makeText(DataAnalysisAcitivity.this,
							"Choose at least 2 observations", Style.ALERT)
							.show();
					return false;
				}
				for (Trait t:traitSelected1) {
					if (t.getTraitID()!=traitSelected.get(0).getTraitID()) {
						traitSelected.add(t);
					}
					
				}

				for (Trait t : traitSelected) {
					if (!t.getWidgetName().equalsIgnoreCase("Slider")) {
						Crouton.makeText(DataAnalysisAcitivity.this, t.getTraitName()
								+ "can not be used to generate a chart!",
								Style.ALERT);
						return false;
					}
					return true;

				}
				return true;

			}
		});

	}

	class TraitListSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			fromDate.setText("");
			from = null;
			toDate.setText("");
			to = null;
			obserTable.removeAllViews();
			TraitList t = (TraitList) parent.getItemAtPosition(position);
			List<Trait> selections2 = traitListContentDao
					.searchTraitsByTraitListID(t.getTraitListID());
			traits = traitListContentDao.searchTraitsByTraitListID(t
					.getTraitListID());
			for (int i = 0; i < selections2.size(); i++) {
				if (i == selections2.size())
					break;

				if (traits.get(i).getUnit() == null) {
					traits.remove(i);
					selections2.remove(i);
					i--;
				}
			}
			if (selections2.size() == 0)
				Toast.makeText(DataAnalysisAcitivity.this,
						"There is no analysable trait", Toast.LENGTH_SHORT)
						.show();
			TraitAdapter traitAdapter = new TraitAdapter(selections2,
					DataAnalysisAcitivity.this);

			TraitAdapter trait2Adapter = new TraitAdapter(selections2,
					DataAnalysisAcitivity.this);

			traitSpinner.setAdapter(traitAdapter);
			traitSpinner.setOnItemSelectedListener(new traitSpinnerListener());

			trait2Spinner.setAdapter(trait2Adapter);
			trait2Spinner
					.setOnItemSelectedListener(new trait2SpinnerListener());
      
			observations = observationDao.searchObservationsWithTraitList(
					t.getTraitListID(), username);
			observationIDs = observationDao
					.searchObservationsWithTraitList1(t.getTraitListID());
			obserIDsForDate = observationDao
					.searchObservationsWithTraitList1(t.getTraitListID());
			observationsForDate = observationDao
					.searchObservationsWithTraitList(
							t.getTraitListID(), username);
			refreshObserTable();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	private void refreshObserTable() {
		obserTable.removeAllViews();
		obserIDsSelected.clear();
		String str = "";
		int index = 0;
		for (int i = 0; i < observations.size(); i++) {
			TableRow row = new TableRow(DataAnalysisAcitivity.this);
			str = "";
			str += observations.get(i);
			index = str.indexOf("---");
			CheckBox select = new CheckBox(DataAnalysisAcitivity.this);
			select.setId(observationIDs.get(i));
			if (index != -1)
				select.append(str.substring(0, index));
			else
				select.append(str);

			select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						obserSelected.add(buttonView.getText().toString());
						if (!obserIDsSelected.contains(buttonView.getId()))
						{
							obserIDsSelected.add(buttonView.getId());
						}
					} else {
						obserSelected.remove(buttonView.getText().toString());
						for(int i=obserIDsSelected.size()-1;i>=0;i--)
						{
							if (obserIDsSelected.get(i).equals(buttonView.getId())) {
								obserIDsSelected.remove(i);
							}
						}
						
					}
				}
			});
			row.addView(select);
			obserTable.addView(row);
		}
	}

	class traitSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			traitSelected.clear();
			Trait t = (Trait) parent.getItemAtPosition(position);
				traitSelected.add(t);
				System.out.println("DataAnalysisAcitivity traitSpinnerListener add traitSelected:"+t.getTraitName());
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	class trait2SpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			traitSelected1.clear();
			Trait t = (Trait) parent.getItemAtPosition(position);			
				traitSelected1.add(t);
				
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	DatePickerDialog.OnDateSetListener fromDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub

			from = new Date((year - 1900), monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			fromDate.setText(sdf.format(from));

			for (int i = 0; i < observationsForDate.size(); i++) {
				if (i == observationsForDate.size())
					break;
				if (observationDao
						.findObervationById(
								observationDao.findIdByName(observationsForDate
										.get(i))).getCreateTime().before(from)) {
					observationsForDate.remove(i);

					i--;
				}

			}
			for (int i = 0; i < obserIDsForDate.size(); i++) {
				if (i == obserIDsForDate.size())
					break;
				if (observationDao.findObervationById(obserIDsForDate.get(i))
						.getCreateTime().before(from)) {
					obserIDsForDate.remove(i);

					i--;
				}

			}
			refreshObserTableForDate();

			observationsForDate = new ArrayList<String>();
			for (int i = 0; i < observations.size(); i++)
				observationsForDate.add(observations.get(i));

			obserIDsForDate = new ArrayList<Integer>();
			for (Integer id : observationIDs)
				obserIDsForDate.add(id);
		}

	};

	DatePickerDialog.OnDateSetListener toDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub

			to = new Date((year - 1900), monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate.setText(sdf.format(to));

			for (int i = 0; i < observationsForDate.size(); i++) {

				if (i == observationsForDate.size())
					break;
				if (observationDao
						.findObervationById(
								observationDao.findIdByName(observationsForDate
										.get(i))).getCreateTime().after(to)) {
					observationsForDate.remove(i);
					i--;
				}

			}

			for (int i = 0; i < obserIDsForDate.size(); i++) {
				if (i == obserIDsForDate.size())
					break;
				if (observationDao.findObervationById(obserIDsForDate.get(i))
						.getCreateTime().after(to)) {
					obserIDsForDate.remove(i);

					i--;
				}

			}
			refreshObserTableForDate();

			observationsForDate = new ArrayList<String>();
			for (int i = 0; i < observations.size(); i++)
				observationsForDate.add(observations.get(i));

			obserIDsForDate = new ArrayList<Integer>();
			for (Integer id : observationIDs)
				obserIDsForDate.add(id);
		}

	};

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		java.util.Date current = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int year = Integer.parseInt(sdf.format(current).substring(0, 4));
		int month = Integer.parseInt(sdf.format(current).substring(5, 7));
		int day = Integer.parseInt(sdf.format(current).substring(8, 10));
		switch (id) {
		case 0:
			return new DatePickerDialog(this, fromDatePickerListener, 2014, 9,
					1);
		case 1:
			return new DatePickerDialog(this, toDatePickerListener, year,
					(month - 1), day);
		}

		return null;
	}

	private void refreshObserTableForDate() {
		obserTable.removeAllViews();
		obserIDsSelected.clear();
		String str = "";
		int index = 0;
		for (int i = 0; i < observationsForDate.size(); i++) {
			TableRow row = new TableRow(DataAnalysisAcitivity.this);
			str = "";
			str += observationsForDate.get(i);
			index = str.indexOf("---");
			CheckBox select = new CheckBox(DataAnalysisAcitivity.this);
			select.setId(obserIDsForDate.get(i));
			if (index != -1)
				select.append(str.substring(0, index));
			else
				select.append(str);

			select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						obserSelected.add(buttonView.getText().toString());
						if (!obserIDsSelected.contains(buttonView.getId()))
							obserIDsSelected.add(buttonView.getId());
					} else {
						obserSelected.remove(buttonView.getText().toString());
						for(int i=obserIDsSelected.size()-1;i>=0;i--)
						{
							if (obserIDsSelected.get(i).equals(buttonView.getId())) {
								obserIDsSelected.remove(i);
							}
						}
					}
				}
			});
			row.addView(select);
			obserTable.addView(row);
		}
	}

}
