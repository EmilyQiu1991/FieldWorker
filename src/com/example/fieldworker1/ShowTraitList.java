package com.example.fieldworker1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.ObservationDao;
import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;
import com.example.phpServer.TraitListPhpService;
import com.example.service.TraitListService;
import com.example.validator.MyApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ShowTraitList extends Activity {
	private static final String PREFS_NAME = "MyPrefsFile";
	private TableLayout table = null;
	private Button deleteButton;
	private Button addButton;
	private Button submitButton;
	private TraitListContentDao traitListContentDao;
	private PredefineValueDao predefineValueDao;
	private TraitListDao traitListDao;
	private ObservationDao obserDao;
	private TraitDao traitDao;
	private TraitListService traitListService;
	private ArrayList<String> deletedTrait;
	private ArrayList<Integer> deletedRow;
	private ArrayList<String> currentTraits;
	private TraitListPhpService traitListPhpService;
	private String username;
	private TraitList tl;
    private AddLogDao addLogDao;
    private DeleteLogDao deleteLogDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template);
		table = (TableLayout) findViewById(R.id.templateTalbe);
		deleteButton = (Button) findViewById(R.id.deleteButton);
		addButton = (Button) findViewById(R.id.addButton);
		submitButton = (Button) findViewById(R.id.submitButton);
		deletedTrait = new ArrayList<String>();
		deletedRow = new ArrayList<Integer>();

		traitListContentDao = new TraitListContentDao(ShowTraitList.this);
		obserDao = new ObservationDao(ShowTraitList.this);
		predefineValueDao = new PredefineValueDao(ShowTraitList.this);
		traitListDao = new TraitListDao(ShowTraitList.this);
		traitDao = new TraitDao(ShowTraitList.this);
		addLogDao=new AddLogDao(this);
		deleteLogDao=new DeleteLogDao(this);
		traitListService = new TraitListService(ShowTraitList.this);
		Intent intent = getIntent();

		tl = (TraitList) intent
				.getSerializableExtra(TraitListActivity2.SER_KEY);

		System.out.println("ShowTraitListActivity: " + tl);

		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		username = mySharedPreferences.getString("username", "");
		traitListPhpService = new TraitListPhpService(this, username);

		currentTraits = (ArrayList<String>) traitListContentDao
				.searchTraitNames(tl.getTraitListID());
		if (tl.getNameVersion() == 0)
			setTitle(tl.getTraitListName());
		else
			setTitle(tl.getTraitListName() + "_" + tl.getNameVersion());
		generateList();

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentTraits.removeAll(deletedTrait);
				table.removeAllViews();
				generateAgain(currentTraits);
				/*
				 * for (int i = 0; i < deletedRow.size(); i++) { TableRow
				 * row=(TableRow) findViewById(deletedRow.get(i));
				 * table.removeView(row); }
				 */
				deletedRow.clear();
				deletedTrait.clear();
				System.out.println("after delete click:" + currentTraits);

			}
		});

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addTraitToTraitList();

			}
		});
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (obserDao.searchObservationsWithTraitList(
						tl.getTraitListID(),username).size() == 0) {
					
                    traitListService.updateTraitList(tl,currentTraits);
                    if (MyApplication.isNetworkOnline()) {
  						traitListPhpService.updateTraitList(tl,currentTraits);
					}
                    else {
                        List<TraitListContent> contents=traitListContentDao.getTraitContents(tl.getTraitListID());
                        for(TraitListContent content:contents)
                        {
                        	deleteLogDao.insert(new DeleteLog(UUID.randomUUID().hashCode(), "TraitListContent", content.getTraitListID(), content.getTraitID()));
                        }
						for (int i = 0; i < currentTraits.size(); i++) {
							Integer traitId=traitDao.findIdbyName(currentTraits.get(i));
							addLogDao.insert(new AddLog(UUID.randomUUID().hashCode(), "TraitListContent", tl.getTraitListID(), traitId));
						}
					}
                    alertMessage();
				} else
					showDialog_Layout(ShowTraitList.this);
			}
		});
	}

	private void generateList() {
		// search the trait names
		List<Trait> traits = new ArrayList<Trait>();
		// if (MyApplication.isNetworkOnline()) {
		// traits=
		// }
		// else
		traits = traitListContentDao.searchTraitsByTraitListID(tl
				.getTraitListID());

		for (Trait t : traits) {
			if (t.getWidgetName().equals("Spinner")) {
				appendSpinner(t.getTraitName(),
						predefineValueDao.search1(t.getTraitID()), t.getUnit());
			} else if (t.getWidgetName().equals("EditText")) {
				appendEditText(t.getTraitName(), t.getUnit());
			} else if (t.getWidgetName().equals("CheckBox")) {
				appendCheckBox(t.getTraitName(),
						predefineValueDao.search1(t.getTraitID()), t.getUnit());
			} else if (t.getWidgetName().equals("Slider")) {
				appendSlider(t.getTraitName(),
						predefineValueDao.search1(t.getTraitID()), t.getUnit());
			}

		}
	}

	private void appendSpinner(final String traitName, String[] pValues,
			String unit) {
		final int id = table.getChildCount();
		TableRow row = new TableRow(this);
		row.setId(id);
		TextView traitNameTextView = new TextView(this);
		traitNameTextView.setText(traitName);
		traitNameTextView.setTextSize(15);

		Spinner spinner = new Spinner(this);
		ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, pValues);
		spinner.setAdapter(adp);
		CheckBox checkBox = new CheckBox(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Toast.makeText(getApplicationContext(), traitName,
							Toast.LENGTH_SHORT).show();
					deletedTrait.add(traitName);
					deletedRow.add(id);
				} else {
					deletedTrait.remove(traitName);
				}
			}
		});

		/*
		 * Button deleteButton=new Button(this);
		 * 
		 * deleteButton.setText("Delete"); deleteButton.setTextSize(15);
		 * deleteButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub System.out.println(v.toString()); table.removeView((View)
		 * v.getParent()); traitListService.deleteTraitFromList(traitListName,
		 * traitName); } });
		 */
		row.addView(traitNameTextView);
		row.addView(spinner);
		TableRow.LayoutParams params = (TableRow.LayoutParams) spinner
				.getLayoutParams();
		params.span = 2;
		spinner.setLayoutParams(params);
		TextView unitTextView = new TextView(this);

		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		unitTextView.setGravity(Gravity.LEFT);
		row.addView(unitTextView);
		row.addView(checkBox);
		// row.addView(deleteButton);
		table.addView(row);

	}

	private void appendEditText(final String traitName, String unit) {
		final int id = table.getChildCount();
		TableRow row = new TableRow(this);
		row.setId(id);
		TextView traitNameTextView = new TextView(this);
		traitNameTextView.setText(traitName);
		traitNameTextView.setTextSize(15);
		EditText editText = new EditText(this);

		CheckBox checkBox = new CheckBox(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Toast.makeText(getApplicationContext(), traitName,
							Toast.LENGTH_SHORT).show();
					deletedTrait.add(traitName);
					deletedRow.add(id);
				} else {
					deletedTrait.remove(traitName);
				}
			}
		});
		/*
		 * Button deleteButton=new Button(this);
		 * 
		 * deleteButton.setText("Delete"); deleteButton.setTextSize(15);
		 * deleteButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub System.out.println(v.toString());
		 * traitListService.deleteTraitFromList(traitListName, traitName);
		 * table.removeView((View) v.getParent()); } });
		 */
		row.addView(traitNameTextView);
		row.addView(editText);
		TableRow.LayoutParams params = (TableRow.LayoutParams) editText
				.getLayoutParams();
		params.span = 2;
		editText.setLayoutParams(params);
		TextView unitTextView = new TextView(this);
		unitTextView.setGravity(Gravity.LEFT);
		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		row.addView(unitTextView);
		// row.addView(deleteButton);
		row.addView(checkBox);
		table.addView(row);
	}

	private void appendCheckBox(final String traitName, String[] pValues,
			String unit) {
		final int id = table.getChildCount();
		TableRow row = new TableRow(this);
		row.setId(id);
		TextView traitNameTextView = new TextView(this);
		traitNameTextView.setText(traitName);
		traitNameTextView.setTextSize(15);

		ListView listview = new ListView(this);
		listview.setDivider(null);
		final ArrayList<Boolean> checkedItem = new ArrayList<Boolean>();
		ArrayList<String> array = new ArrayList<String>();
		// initial checkedItem and array
		for (int i = 0; i < pValues.length; i++) {
			array.add(pValues[i]);
			checkedItem.add(false);
		}

		final CheckBoxAdapter chedapter = new CheckBoxAdapter(this, array,
				checkedItem);
		listview.setAdapter(chedapter);
		listview.setItemsCanFocus(false);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("click an item");
				// TODO Auto-generated method stub
				if (checkedItem.get(position) == true) {
					checkedItem.set(position, false);

				} else {
					checkedItem.set(position, true);
				}
				chedapter.getView(position, view, parent);
			}

		});
		listview.setPadding(5, 0, 0, 5);
		CheckBox checkBox = new CheckBox(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Toast.makeText(getApplicationContext(), traitName,
							Toast.LENGTH_SHORT).show();
					deletedTrait.add(traitName);
					deletedRow.add(id);
				} else {
					deletedTrait.remove(traitName);
					deletedRow.remove(id);
				}
			}
		});
		/*
		 * Button deleteButton=new Button(this);
		 * 
		 * deleteButton.setText("Delete"); deleteButton.setTextSize(15);
		 * deleteButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * traitListService.deleteTraitFromList(traitListName, traitName);
		 * table.removeView((View) v.getParent()); } });
		 */
		TextView unitTextView = new TextView(this);

		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		unitTextView.setGravity(Gravity.LEFT);
		row.addView(traitNameTextView);
		TableRow.LayoutParams params1 = (TableRow.LayoutParams) traitNameTextView
				.getLayoutParams();
		params1.gravity = Gravity.TOP;
		traitNameTextView.setLayoutParams(params1);

		row.addView(listview);
		TableRow.LayoutParams params = (TableRow.LayoutParams) listview
				.getLayoutParams();
		params.span = 2;
		listview.setLayoutParams(params);
		row.addView(unitTextView);
		// row.addView(deleteButton);
		row.addView(checkBox);
		table.addView(row);
	}

	public void appendSlider(final String traitName, String[] pValues,
			String unit) {
		final int min = Math.min(Integer.parseInt(pValues[0]),
				Integer.parseInt(pValues[1]));
		int max = Math.max(Integer.parseInt(pValues[0]),
				Integer.parseInt(pValues[1]));
		final int id = table.getChildCount();
		TableRow row = new TableRow(this);
		row.setId(id);
		TextView traitNameTextView = new TextView(this);
		traitNameTextView.setText(traitName);
		traitNameTextView.setTextSize(15);

		final TextView maxTextView = new TextView(this);
		maxTextView.setText(String.valueOf(min));
		maxTextView.setGravity(Gravity.LEFT);
		SeekBar seekBar = new SeekBar(this);
		seekBar.setMax(Math.max(Integer.parseInt(pValues[0]),
				Integer.parseInt(pValues[1])));
		seekBar.setProgress(min);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				System.out.println(String.valueOf(progress));
				maxTextView.setText(String.valueOf(progress + min));
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
		TextView unitTextView = new TextView(this);

		unitTextView.setText(unit);
		unitTextView.setGravity(Gravity.LEFT);
		unitTextView.setTextSize(15);
		row.addView(traitNameTextView);
		row.addView(seekBar);
		row.addView(maxTextView);
		row.addView(unitTextView);
		CheckBox checkBox = new CheckBox(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Toast.makeText(getApplicationContext(), traitName,
							Toast.LENGTH_SHORT).show();
					deletedTrait.add(traitName);
					deletedRow.add(id);
				} else {
					deletedTrait.remove(traitName);
				}
			}
		});
		/*
		 * Button deleteButton=new Button(this);
		 * 
		 * deleteButton.setText("Delete"); deleteButton.setTextSize(15);
		 * deleteButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub System.out.println(v.toString());
		 * traitListService.deleteTraitFromList(traitListName, traitName);
		 * table.removeView((View) v.getParent()); } });
		 */
		// row.addView(deleteButton);
		row.addView(checkBox);
		table.addView(row);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, R.string.addTrait);
		// menu.add(0,obserList,2,R.string.obserList);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			System.out.println("click trait list");
			addTraitToTraitList();
		}
		return super.onOptionsItemSelected(item);

	}

	private void addTraitToTraitList() {
		final List<String> checkedItems = new ArrayList<String>();
		List<String> nameList = traitDao.findAllTraitNames(username);
		System.out.println("after click add button : " + currentTraits);
		nameList.removeAll(currentTraits);
		final String[] traitNames = new String[nameList.size()];
		for (int i = 0; i < traitNames.length; i++) {
			traitNames[i] = nameList.get(i);
		}
		if (traitNames.length == 0) {

		}
		boolean[] checked = new boolean[traitNames.length];
		for (int i = 0; i < checked.length; i++) {
			checked[i] = false;
		}
		new AlertDialog.Builder(ShowTraitList.this)
				.setCancelable(false)
				.setTitle("Choose Traits")
				.setMultiChoiceItems(traitNames, checked,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									checkedItems.add(traitNames[which]);
								}
							}
						})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						// add items to database
						for (int i1 = 0; i1 < checkedItems.size(); i1++) {

							currentTraits.add(checkedItems.get(i1));
							System.out.println("after click add ok button : "
									+ currentTraits);

							// traitListService.addTraitToTraitList(checkedItems.get(i1),
							// traitListid);
						}
						generateAgain(currentTraits);
						dialoginterface.dismiss();
						/*
						 * Intent intent=new Intent();
						 * intent.putExtra("traitListId",traitListid );
						 * intent.putExtra("traitListName", traitListName);
						 * intent.setClass(ShowTraitList.this,
						 * ShowTraitList.class);
						 * ShowTraitList.this.startActivity(intent); finish();
						 */
					}
				}).show();

	}

	public void generateAgain(ArrayList<String> traits) {
		table.removeAllViews();
		for (int i1 = 0; i1 < traits.size(); i1++) {
			Trait t = traitDao.searchByTraitName(traits.get(i1));
			if (t.getWidgetName().equals("Spinner")) {
				appendSpinner(t.getTraitName(),
						predefineValueDao.search1(t.getTraitID()), t.getUnit());
			} else if (t.getWidgetName().equals("EditText")) {
				appendEditText(t.getTraitName(), t.getUnit());
			} else if (t.getWidgetName().equals("CheckBox")) {
				appendCheckBox(t.getTraitName(),
						predefineValueDao.search1(t.getTraitID()), t.getUnit());
			} else if (t.getWidgetName().equals("Slider")) {
				appendSlider(t.getTraitName(),
						predefineValueDao.search1(t.getTraitID()), t.getUnit());
			}

		}
	}

	private void showDialog_Layout(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater
				.inflate(R.layout.dialoglayout, null);
		final EditText edtInput = (EditText) textEntryView
				.findViewById(R.id.edtInput);
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		// builder.setIcon(R.drawable.icon);
		builder.setTitle("Input a new trait list name");
		builder.setView(textEntryView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Integer traitListID = UUID.randomUUID().hashCode();
				TraitList traitList = new TraitList(traitListID, edtInput
						.getText().toString(), username,1);
				if (MyApplication.isNetworkOnline()) {
					traitListPhpService.addTraitList(traitList, currentTraits);
				}
                
				traitListService.addTraitList(traitList, currentTraits,
						MyApplication.isNetworkOnline());

				Intent intent = new Intent();
				intent.setClass(ShowTraitList.this, TraitListActivity2.class);
				ShowTraitList.this.startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// setTitle("");
					}
				});
		builder.show();

	}
	
		 public void alertMessage() {           
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setMessage("Modify trait list successfully")
                          .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) { 
                                  // continue with delete
                              }
                           })
                          .show();
      }



}

