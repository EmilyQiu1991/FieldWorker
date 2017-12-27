package com.example.fieldworker1;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import com.example.fieldworker1.R;
import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.ObserContentDao;
import com.example.dao.ObservationDao;
import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.Observation;
import com.example.domain.Trait;
import com.example.domain.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditObservation extends Activity {

	private final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/ASoohue/CameraCache");
	private User user;
	private Observation observation;
	private String[] traitNames;
	private String[] values;
	private String FileName;
	private String max;
	private String[] oldPhotoPaths;
	private ArrayList<String> photoPath;
	private Bitmap bmp;
	private HashMap<Integer, String> traitValues;
	private HashMap<Integer, String> traitEditable;
	private HashMap<String, TextView> sliderValues;
	private Date deletingDeadline;

	private EditText obserNameField;
	private EditText traitListText;
	private TableLayout traitTable;
	private EditText obserDeadlineField;
	private Button addDeadline;
	private TextView sliderValue;
	private ImageButton cameraButton;
	private Gallery imageGallery;

	public static List<Trait> traits;
	private static final int DATE_PICKER_ID = 1;

	private TraitDao traitDao;
	private TraitListContentDao traitListContentDao;
	private TraitListDao traitListDao;
	private PredefineValueDao predefineValueDao;
	private ObservationDao observationDao;
	private ObserContentDao obserContentDao;
	private AddLogDao addLogDao;
	private DeleteLogDao deleteLogDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		traitDao = new TraitDao(this);
		traitListDao = new TraitListDao(this);
		traitListContentDao = new TraitListContentDao(this);
		predefineValueDao = new PredefineValueDao(this);
		traitListDao = new TraitListDao(this);
		observationDao = new ObservationDao(this);
		obserContentDao = new ObserContentDao(this);
		addLogDao = new AddLogDao(this);
		deleteLogDao = new DeleteLogDao(this);

		max = "";
		photoPath = new ArrayList<String>();

		sliderValues = new HashMap<String, TextView>();
		Intent intent = getIntent();
		user = new User(intent.getStringExtra("username"),
				intent.getStringExtra("password"));

		observation = observationDao.findObervationById(intent.getIntExtra(
				"observationID", -1));
		setContentView(R.layout.activity_observation_edit);

		traitListText = (EditText) findViewById(R.id.editTraitListObser);
		String str = traitListDao.findById(observation.getTraitListID())
				.getTraitVersionName();
		traitListText.setText(str);
		obserNameField = (EditText) findViewById(R.id.observationEditNameFieldObser);
		String name = observation.getObservationName().replace("---", "");
		obserNameField.setText(name.replace(str, ""));
		traitTable = (TableLayout) findViewById(R.id.observationEdit_table);
		obserDeadlineField = (EditText) findViewById(R.id.observationEditDeletingDeadlineField);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (observation.getDeleteTime() != null)
			obserDeadlineField.setText(sdf.format(observation.getDeleteTime()));
		else
			obserDeadlineField.setText("");
		obserDeadlineField.setEnabled(false);
		obserDeadlineField.setTextColor(Color.BLACK);
		addDeadline = (Button) findViewById(R.id.addDeletingDeadline);
		addDeadline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_PICKER_ID);
			}

		});

		cameraButton = (ImageButton) findViewById(R.id.cameraButton_Edit);
		cameraButton.setOnClickListener(new CameraListener());

		imageGallery = (Gallery) findViewById(R.id.gallery_observation_edit);
		if (observation.getPhotoPath() == null) {
			GalleryAdapter galleryAdapter = new GalleryAdapter(
					EditObservation.this, true, null);
			imageGallery.setAdapter(galleryAdapter);
		} else {

			oldPhotoPaths = observation.getPhotoPath().split(",");
			GalleryAdapter galleryAdapter = new GalleryAdapter(
					EditObservation.this, false, oldPhotoPaths);
			imageGallery.setAdapter(galleryAdapter);
			imageGallery.setOnItemLongClickListener(new imageDeleteListener());
			for (int i = 0; i < oldPhotoPaths.length; i++)
				photoPath.add(oldPhotoPaths[i]);
		}

		traits = new ArrayList<Trait>();
		generateList(observation.getTraitListID());

	}

	DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub

			deletingDeadline = new Date((year - 1900), monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			obserDeadlineField.setText(sdf.format(deletingDeadline));
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
		case DATE_PICKER_ID:
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);
		}

		return null;
	}

	private void generateList(int traitListId) {
		// TODO Auto-generated method stub

		traits = traitListContentDao.searchTraitsByTraitListID(traitListId);
		traitValues = obserContentDao.findTraitValueById(observation
				.getObservationID());
		traitNames = new String[traits.size()];
		int index = 0;
		for (Trait t : traits) {
			traitNames[index++] = t.getTraitName();
		}
		values = new String[traits.size()];
		for (int i = 0; i < values.length; i++)
			values[i] = "";
		int indexOfName = -1;
		Iterator it = traitValues.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			for (int i = 0; i < traitNames.length; i++) {
				if (traitNames[i].equals(traitDao.findNameById((Integer) entry
						.getKey()))) {
					indexOfName = i;
					break;
				}
			}
			values[indexOfName] = (String) entry.getValue();
		}

		traitEditable = obserContentDao.findTraitEditableById(observation
				.getObservationID());

		for (Trait t : traits) {
			String[] plv = predefineValueDao.search1(t.getTraitID());
			if (!t.getWidgetName().equals("Spinner")) {
				for (int i = 0; i < plv.length; i++) {
					if (i == 0) {
						appendNewRow(t, t.getTraitName(), plv[i], true);
					}

					else {
						if (t.getWidgetName().equals("Slider")) {
							break;
						}

						appendNewRow(t, t.getTraitName(), plv[i], false);
					}

				}

			} else {
				appendNewRow(t, t.getTraitName(), plv, true);
			}
			if (plv.length == 0) {
				appendNewRow(t, t.getTraitName(), "", true);
			}

		}
	}

	// for spinner
	private void appendNewRow(final Trait t, String traitName, String[] plv,
			boolean flag) {
		// TODO Auto-generated method stub
		TableRow row = new TableRow(this);
		TextView traitNameTextView = new TextView(this);
		if (flag) {
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(20);
			traitNameTextView.setPadding(3, 3, 3, 3);
			traitNameTextView.setGravity(Gravity.LEFT);
		}

		row.addView(traitNameTextView);

		Spinner s = new Spinner(this);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, plv);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);

		for (int i = 0; i < plv.length; i++) {
			if (plv[i].equals(traitValues.get(t.getTraitID())))
				s.setSelection(i);
		}
		if (traitEditable.get(t.getTraitID()).equals("0"))
			s.setEnabled(false);
		else {
			s.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					int index = 0;
					for (int i = 0; i < traitNames.length; i++) {
						if (traitNames[i].equals(t.getTraitName()))
							index = i;
					}

					values[index] = "";
					values[index] += arg0.getItemAtPosition(arg2).toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		}
		row.addView(s);
		TextView unitText = new TextView(this);
		if (t.getUnit() != null) {
			unitText.setText(t.getUnit());
		}
		row.addView(unitText);
		row.setMinimumHeight(90);
		traitTable.addView(row, new TableLayout.LayoutParams());
	}

	// checkBox editText slider
	@SuppressLint("NewApi")
	private void appendNewRow(final Trait trait, final String traitName,
			final String pValues, boolean flag) {

		TableRow row = new TableRow(this);
		TextView traitNameTextView = new TextView(this);
		if (flag) {
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(20);
			traitNameTextView.setPadding(3, 3, 3, 3);
			traitNameTextView.setGravity(Gravity.LEFT);
		}

		row.addView(traitNameTextView);

		if (trait.getWidgetName().equals("CheckBox")) {
			CheckBox cb = new CheckBox(this);
			cb.append(pValues);
			cb.setTextSize(15);
			cb.setPadding(3, 3, 3, 3);
			cb.setGravity(Gravity.LEFT);
			if (traitValues.get(trait.getTraitID()).contains(pValues))
				cb.setChecked(true);
			else {
				cb.setChecked(false);
			}
			if (traitEditable.get(trait.getTraitID()).equals("0")) {
				cb.setClickable(false);
				cb.setTextColor(Color.GRAY);
			}

			else {
				cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							int index = 0;
							for (int i = 0; i < traitNames.length; i++) {
								if (traitNames[i].equals(trait.getTraitName()))
									index = i;
							}
							values[index] += pValues + ",";

						} else {

							int index = 0;
							for (int i = 0; i < traitNames.length; i++)
								if (traitNames[i].equals(trait.getTraitName()))
									index = i;
							String string = values[index].replace(
									pValues + ",", "");
							values[index] = "";
							values[index] += string;
						}
					}
				});
			}
			row.addView(cb);
			TextView unitText = new TextView(this);
			if (trait.getUnit() != null) {
				unitText.setText(trait.getUnit());
			}
			row.addView(unitText);

		} else if (trait.getWidgetName().equals("EditText")) {
			final EditText view = new EditText(this);
			if (!traitValues.get(trait.getTraitID()).isEmpty())
				view.setText(traitValues.get(trait.getTraitID()));
			if (traitEditable.get(trait.getTraitID()).equals("0"))
				view.setEnabled(false);
			else {
				view.addTextChangedListener(new TextWatcher() {

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						int index = 0;
						for (int i = 0; i < traitNames.length; i++)
							if (traitNames[i].equals(trait.getTraitName()))
								index = i;
						values[index] = "";
						values[index] += view.getText().toString();
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

					}

				});
			}

			row.addView(view);
		} else if (trait.getWidgetName().equals("Slider")) {
			TableRow sliderValueRow = new TableRow(this);
			sliderValue = new TextView(this);
			sliderValue.setText(traitValues.get(trait.getTraitID()));
			sliderValue.setGravity(Gravity.CENTER_HORIZONTAL);
			sliderValueRow.addView(sliderValue);
			sliderValueRow.setGravity(Gravity.CENTER_HORIZONTAL);
			traitTable.addView(sliderValueRow);
			sliderValues.put("" + trait.getTraitID(), sliderValue);
			PredefineValueDao preDao = new PredefineValueDao(this);

			for (String s : preDao.search1(trait.getTraitID()))
				if (!s.equals(pValues)) {
					max = s;
				}
			int progress = 0;
			SeekBar slider = new SeekBar(this);

			if (traitEditable.get(trait.getTraitID()).equals("0")) {
				slider.setEnabled(false);
				if (Integer.parseInt(max) < Integer.parseInt(pValues)) {
					// pValues--- maximum
					final String min = max;
					max = pValues;
					if (!max.equals(""))
						slider.setMax(Integer.parseInt(max)
								- Integer.parseInt(min));
					progress = Integer.parseInt(traitValues.get(trait
							.getTraitID())) - Integer.parseInt(min);
					if (!traitValues.get(trait.getTraitID()).isEmpty()) {
						slider.setProgress(progress);
					}
				} else {
					if (!max.equals(""))
						slider.setMax(Integer.parseInt(max)
								- Integer.parseInt(pValues));
					progress = (int) (Integer.parseInt(traitValues.get(trait
							.getTraitID())) - Integer.parseInt(pValues));
					if (!traitValues.get(trait.getTraitID()).isEmpty()) {
						slider.setProgress(progress);
					}
				}
			}

			else {

				if (Integer.parseInt(max) < Integer.parseInt(pValues)) {
					// pValues--- maximum
					final String min = max;
					max = pValues;
					if (!max.equals(""))
						slider.setMax(Integer.parseInt(max)
								- Integer.parseInt(min));
					progress = Integer.parseInt(traitValues.get(trait
							.getTraitID())) - Integer.parseInt(min);
					if (!traitValues.get(trait.getTraitID()).isEmpty()) {
						slider.setProgress(progress);
					}
					slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							int value = seekBar.getProgress();
							int index = 0;
							for (int i = 0; i < traitNames.length; i++)
								if (traitNames[i].equals(trait.getTraitName()))
									index = i;
							values[index] = "";
							values[index] += (Integer.parseInt(min) + value);
							sliderValues
									.get(String.valueOf(trait.getTraitID()))
									.setText(
											""
													+ (Integer.parseInt(min) + value));
						}

					});
				} else {
					if (!max.equals(""))
						slider.setMax(Integer.parseInt(max)
								- Integer.parseInt(pValues));
					progress = (int) (Integer.parseInt(traitValues.get(trait
							.getTraitID())) - Integer.parseInt(pValues));
					if (!traitValues.get(trait.getTraitID()).isEmpty()) {
						slider.setProgress(progress);
					}
					slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							int value = seekBar.getProgress();
							int index = 0;
							for (int i = 0; i < traitNames.length; i++)
								if (traitNames[i].equals(trait.getTraitName()))
									index = i;
							values[index] = "";
							values[index] += (Integer.parseInt(pValues) + value);
							// sliderValue.setText(""
							// + (Integer.parseInt(pValues) + value));
							sliderValues
									.get(String.valueOf(trait.getTraitID()))
									.setText(
											""
													+ (Integer
															.parseInt(pValues) + value));

						}

					});
				}

			}
			row.addView(slider);
		}
		TextView unitText = new TextView(this);
		if (trait.getUnit() != null) {
			unitText.setText(trait.getUnit());
		}
		row.addView(unitText);
		if (flag)
			row.setMinimumHeight(90);
		else
			row.setMinimumHeight(40);
		traitTable.addView(row, new TableLayout.LayoutParams());
	}

	class CameraListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CharSequence[] items = { "Camera", "Photo album", "Cancel" };
			new AlertDialog.Builder(EditObservation.this)
					.setTitle("Photo Source")
					.setItems(items, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent;
							// TODO Auto-generated method stub
							switch (which) {

							case 0:
								String state = Environment
										.getExternalStorageState();
								if (state.equals(Environment.MEDIA_MOUNTED)) {
									intent = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									FileName = observation.getObservationName()
											+ "-" + System.currentTimeMillis()
											+ ".jpg";
									File f = new File(PHOTO_DIR, FileName);
									intent.putExtra(MediaStore.EXTRA_OUTPUT,
											Uri.fromFile(f));
									startActivityForResult(intent, which);
								} else {
									Toast.makeText(EditObservation.this,
											"The SDcard is unavailable",
											Toast.LENGTH_LONG).show();
								}
								break;

							case 1:
								intent = new Intent(Intent.ACTION_GET_CONTENT);
								intent.addCategory(Intent.CATEGORY_OPENABLE);
								intent.setType("image/*");
								startActivityForResult(intent.createChooser(
										intent, "Chose a photo album"), which);
								break;
							case 2:
							default:
								break;
							}
						}

					}).create().show();
		}

	}

	class imageDeleteListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {
			// TODO Auto-generated method stub
			new AlertDialog.Builder(EditObservation.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Worning")
					.setMessage("Are you sure to delete this image?")
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									File f = new File(oldPhotoPaths[position
											% oldPhotoPaths.length]);
									if (f.exists()) {
										f.delete();
										photoPath.remove(oldPhotoPaths[position
												% oldPhotoPaths.length]);
										if (photoPath.size() != 0) {
											oldPhotoPaths = new String[photoPath
													.size()];
											for (int i = 0; i < oldPhotoPaths.length; i++)
												oldPhotoPaths[i] = photoPath
														.get(i);
											final GalleryAdapter galleryAdapter = new GalleryAdapter(
													EditObservation.this,
													false, oldPhotoPaths);
											imageGallery
													.setAdapter(galleryAdapter);
											imageGallery
													.setOnItemLongClickListener(new imageDeleteListener());
										} else {
											GalleryAdapter galleryAdapter = new GalleryAdapter(
													EditObservation.this, true,
													null);
											imageGallery
													.setAdapter(galleryAdapter);
										}
									}
								}
							}).setNegativeButton("NO", null).show();
			return true;
		}

	}

	/*
	 * receive photo and handle it
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		SimpleDateFormat sdf = new SimpleDateFormat("");
		switch (requestCode) {
		case 0:
			File f = new File(PHOTO_DIR, FileName);
			if (f.exists()) {
				photoPath.add(f.getPath());
				oldPhotoPaths = new String[photoPath.size()];
				for (int i = 0; i < oldPhotoPaths.length; i++)
					oldPhotoPaths[i] = photoPath.get(i);
				GalleryAdapter galleryAdapter = new GalleryAdapter(
						EditObservation.this, false, oldPhotoPaths);
				imageGallery.setAdapter(galleryAdapter);
				imageGallery
						.setOnItemLongClickListener(new imageDeleteListener());
			} else {
				Toast.makeText(this, "Please reselect photo.:)",
						Toast.LENGTH_SHORT).show();
				return;
			}
			break;
		case 1:

			Uri uri = data.getData();
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			photoPath.add(path);
			oldPhotoPaths = new String[photoPath.size()];
			for (int i = 0; i < oldPhotoPaths.length; i++)
				oldPhotoPaths[i] = photoPath.get(i);
			GalleryAdapter galleryAdapter = new GalleryAdapter(
					EditObservation.this, false, oldPhotoPaths);
			imageGallery.setAdapter(galleryAdapter);
			imageGallery.setOnItemLongClickListener(new imageDeleteListener());
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editobservation, menu);
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
			intent.setClass(EditObservation.this, ResetPasswordActivity.class);
			intent.putExtra("password", user.getPassword());
			intent.putExtra("username", user.getUserName());
			startActivity(intent);
			return true;
		} else if (id == R.id.action_logout) {
			Intent intent = new Intent();
			intent.setClass(EditObservation.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("flag", 1);
			finish();
			startActivity(intent);
		} else if (id == R.id.submit_observation_edit) {
			observationDao.updateObservationName(obserNameField.getText()
					.toString() + "---" + traitListText.getText(),
					observation.getObservationID());

			if (deletingDeadline != null) {
				observationDao.updateObservationEndTime(deletingDeadline,
						observation.getObservationID());
			}

			for (int i = 0; i < traitNames.length; i++) {
				// obserContentDao.updateTraitValueById(values[i],
				// observation.getObservationID(),
				// traitDao.findIdByName(traitNames[i]));
				obserContentDao.updateTraitValueById(values[i], observation
						.getObservationID(), traits.get(i).getTraitID());
			}

			String path = "";
			for (int i = 0; i < photoPath.size(); i++)
				path += photoPath.get(i) + ",";

			observationDao.updateObservationPhotoPath(path,
					observation.getObservationID());

			AddLog addObserLog = new AddLog(UUID.randomUUID().hashCode(),
					"Observation", observation.getObservationID());
			if (!addLogDao.checkExist(addObserLog)) {
				DeleteLog deleteObserLog = new DeleteLog(UUID.randomUUID()
						.hashCode(), "Observation",
						observation.getObservationID());
				deleteLogDao.insert(deleteObserLog);

				addLogDao.insert(addObserLog);

				List<Trait> traits = traitListContentDao
						.searchTraitsByTraitListID(observation.getTraitListID());
				AddLog addObserContLog;
				for (Trait t : traits) {
					addObserContLog = new AddLog(UUID.randomUUID().hashCode(),
							"ObserContent", observation.getObservationID(),
							t.getTraitID());
					if (!addLogDao.checkExist(addObserContLog)) {
						DeleteLog deleteObserContLog = new DeleteLog(UUID
								.randomUUID().hashCode(), "ObserContent",
								observation.getObservationID(), t.getTraitID());
						deleteLogDao.insert(deleteObserContLog);

						addLogDao.insert(addObserContLog);
					}
				}

			}
			Intent intent = new Intent();
			intent.setClass(EditObservation.this, ObservationListActivity.class);
			intent.putExtra("username", user.getUserName());
			intent.putExtra("password", user.getPassword());
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
