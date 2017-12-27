package com.example.fieldworker1;

import java.text.SimpleDateFormat;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.example.fieldworker1.R;
import com.example.adapter.MyAdapter1;
import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.ObserContentDao;
import com.example.dao.ObservationDao;
import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.ObserContent;
import com.example.domain.Observation;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.domain.User;

import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CreateObservationActivity extends Activity {

	private final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/ASoohue/CameraCache");

	private EditText obserNameField, deadlineField;
	private Spinner traitListSpi;
	private TableLayout traitTable;
	private Button datePicker;
	private HashMap<String,TextView> sliderValues;
	private TextView sliderValue;
	private ImageButton cameraButton;
	private Gallery imageGallery;
	private Button drawing;
	private Button comment;
	private String drawingPath;
	private String[] paths;

	public static List<Trait> traits;
	private static final int DATE_PICKER_ID = 1;
	private static String max;
	private String FileName;
	private ArrayList<String> photoPath;

	private User user;
	private String traitListName;
	private String observationName;
	private Observation observation;
	private Integer traitListID;
	private String[] traitNames;
	private String[] values;
	private int[] editable;
	private Integer observationID;
	private Date deletingDeadline;
	private String commentStr;

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

		Intent intent = getIntent();
		user = new User(intent.getStringExtra("username"),
				intent.getStringExtra("password"));
		// ------------------------------XXXXXXXXX

		sliderValues = new HashMap<String,TextView>();
		
		setContentView(R.layout.activity_observation_create);

		obserNameField = (EditText) findViewById(R.id.observationNameField);
		deadlineField = (EditText) findViewById(R.id.modifyDeadline_create);
		traitListSpi = (Spinner) findViewById(R.id.traitListSpinner);
		traitTable = (TableLayout) findViewById(R.id.observation_create_table);
		datePicker = (Button) findViewById(R.id.observationDeadlineButton);
		drawing = (Button) findViewById(R.id.observation_create_drawing_button);
		comment = (Button) findViewById(R.id.observation_create_comment_button);

		traitDao = new TraitDao(this);
		traitListDao = new TraitListDao(this);
		traitListContentDao = new TraitListContentDao(this);
		predefineValueDao = new PredefineValueDao(this);
		traitListDao = new TraitListDao(this);
		observationDao = new ObservationDao(this);
		obserContentDao = new ObserContentDao(this);
		addLogDao = new AddLogDao(this);
		deleteLogDao = new DeleteLogDao(this);
		
		// construct traitlist selection
		
		List<TraitList> selections = traitListDao.findByUsername(user.getUserName());
//		List<String> selections = new ArrayList<String>();
//		for (Iterator<TraitList> iterator = traitLists.iterator(); iterator
//				.hasNext();) {
//			selections.add(((TraitList) iterator.next()).getTraitListName());
//		}
//
//		ArrayAdapter traitListAdapter = new ArrayAdapter(this,
//				android.R.layout.simple_spinner_item, selections);
//		traitListAdapter
//				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		
		MyAdapter1 traitListAdapter=new MyAdapter1(selections, this);
		traitListSpi.setAdapter(traitListAdapter);
        
		traitListSpi.setOnItemSelectedListener(new spinnerListener());
		traitListName = ((TraitList)traitListSpi.getItemAtPosition(0)).getTraitVersionName();

		cameraButton = (ImageButton) findViewById(R.id.cameraButton_Create);
		cameraButton.setOnClickListener(new CameraListener());

		imageGallery = (Gallery) findViewById(R.id.observation_create_gallery);
		GalleryAdapter galleryAdapter = new GalleryAdapter(
				CreateObservationActivity.this, true, null);
		imageGallery.setAdapter(galleryAdapter);

		observationName = "";
		drawingPath = "";
		deletingDeadline = null;
		photoPath = new ArrayList<String>();
		max = "";
		commentStr = "";
		traits = new ArrayList<Trait>();
		observationID = UUID.randomUUID().hashCode();
		observation = null;

		datePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_PICKER_ID);
			}

		});

		drawing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (obserNameField.getText().equals("")) {
					Toast.makeText(CreateObservationActivity.this,
							"Input observation name firstly please.:)",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(CreateObservationActivity.this,
							NoteActivity.class);
					intent.putExtra("check", false);
					intent.putExtra("paths", drawingPath);
					intent.putExtra("observationName", obserNameField.getText()
							.toString());
					startActivityForResult(intent, 2);

				}
			}

		});

		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CreateObservationActivity.this);
				final EditText commentArea = new EditText(
						CreateObservationActivity.this);
				commentArea.setText(commentStr);
				commentArea.setInputType(131073);
				commentArea.setMinLines(4);
				commentArea.setBackgroundResource(R.drawable.et);
				commentArea.setPadding(10, 10, 10, 10);
				builder.setTitle("Comments")
						.setIcon(android.R.drawable.ic_input_add)
						.setView(commentArea).setNegativeButton("Cancel", null);
				builder.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								commentStr = "";
								commentStr += commentArea.getText().toString();
							}
						});
				builder.show();
			}

		});
	}

	DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub

			deletingDeadline = new Date((year - 1900), monthOfYear, dayOfMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format(deletingDeadline);
			deadlineField.setText(str);
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
			return new DatePickerDialog(this, datePickerListener, year,
					(month - 1), day);
		}

		return null;
	}

	private void generateList(int traitListId) {
		// TODO Auto-generated method stub

		traits = traitListContentDao.searchTraitsByTraitListID(traitListId);
		traitNames = new String[traits.size()];
		for (int i = 0; i < traits.size(); i++)
			traitNames[i] = traits.get(i).getTraitName();
		values = new String[traits.size()];
		for (int i = 0; i < values.length; i++)
			values[i] = "";
		editable = new int[traits.size()];
		for (int i = 0; i < editable.length; i++)
			editable[i] = 0;
		int index = 0;

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
				// go for spinner with string array(plv) to construct
				appendNewRow(t, t.getTraitName(), plv, true);
			}

			// editText
			if (plv.length == 0) {
				appendNewRow(t, t.getTraitName(), "", true);
			}

		}

	}

	// For spinner
	private void appendNewRow(final Trait t, String traitName, String[] plv,
			boolean flag) {
		// TODO Auto-generated method stub
		TableRow row = new TableRow(this);
		TextView traitNameTextView = new TextView(this);
		if (flag) {
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(18);
			traitNameTextView.setTextColor(Color.BLACK);
			traitNameTextView.setPadding(3, 3, 3, 3);
			traitNameTextView.setGravity(Gravity.LEFT);
			// traitNameTextView.setMaxWidth(90);
		}

		row.addView(traitNameTextView);

		Spinner s = new Spinner(this);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, plv);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
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
		row.addView(s);
		TextView unitText = new TextView(this);
		if (t.getUnit() != null) {
			unitText.setText(t.getUnit());
		}
		row.addView(unitText);
		if (flag) {

			CheckBox editableBox = new CheckBox(this);
			editableBox.append("Modifiable");
			editableBox.setTextSize(11);
			editableBox.setPadding(1, 15, 1, 1);
			editableBox.setGravity(Gravity.LEFT);
			editableBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							int index = 0;
							for (int i = 0; i < traitNames.length; i++) {
								if (traitNames[i].equals(t.getTraitName()))
									index = i;
							}
							if (isChecked) {
								editable[index] = 1;
							} else {
								editable[index] = 0;
							}
						}
					});
			row.addView(editableBox);
		}
		row.setMinimumHeight(90);
		traitTable.addView(row, new TableLayout.LayoutParams());
	}

	// checkBox editText slider
	private void appendNewRow(final Trait trait, final String traitName,
			final String pValues, boolean flag) {

		TableRow row = new TableRow(this);
		TextView traitNameTextView = new TextView(this);
		if (flag) {
			traitNameTextView.setText(traitName);
			traitNameTextView.setTextSize(18);
			traitNameTextView.setTextColor(Color.BLACK);
			traitNameTextView.setPadding(3, 3, 3, 3);
			traitNameTextView.setGravity(Gravity.LEFT);
			// traitNameTextView.setMaxWidth(90);
		}

		row.addView(traitNameTextView);

		if (trait.getWidgetName().equals("CheckBox")) {
			CheckBox cb = new CheckBox(this);
			cb.append(pValues);
			cb.setTextSize(15);
			cb.setPadding(3, 3, 3, 3);
			cb.setGravity(Gravity.LEFT);
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
						values[index].replace(pValues, "");
					}
				}
			});
			row.addView(cb);
			TextView unitText = new TextView(this);
			if (trait.getUnit() != null) {
				unitText.setText(trait.getUnit());
			}
			row.addView(unitText);
			if (flag) {

				CheckBox editableBox = new CheckBox(this);
				editableBox.append("Modifiable");
				editableBox.setTextSize(11);
				editableBox.setPadding(1, 15, 1, 1);
				editableBox.setGravity(Gravity.LEFT);
				editableBox
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								int index = 0;
								for (int i = 0; i < traitNames.length; i++) {
									if (traitNames[i].equals(trait
											.getTraitName()))
										index = i;
								}
								if (isChecked) {
									editable[index] = 1;
								} else {
									editable[index] = 0;
								}
							}
						});
				row.addView(editableBox);

			}
		} else if (trait.getWidgetName().equals("EditText")) {
			final EditText view = new EditText(this);
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
			row.addView(view);
			TextView unitText = new TextView(this);
			if (trait.getUnit() != null) {
				unitText.setText(trait.getUnit());
			}
			row.addView(unitText);
			CheckBox editableBox = new CheckBox(this);
			editableBox.append("Modifiable");
			editableBox.setTextSize(11);
			editableBox.setPadding(1, 15, 1, 1);
			editableBox.setGravity(Gravity.LEFT);
			editableBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							int index = 0;
							for (int i = 0; i < traitNames.length; i++) {
								if (traitNames[i].equals(trait.getTraitName()))
									index = i;
							}
							if (isChecked) {
								editable[index] = 1;
							} else {
								editable[index] = 0;
							}
						}
					});
			row.addView(editableBox);

		} else if (trait.getWidgetName().equals("Slider")) {

			TableRow sliderValueRow = new TableRow(this);

			sliderValue = new TextView(this);
			sliderValue.setGravity(Gravity.CENTER_HORIZONTAL);
			sliderValueRow.addView(sliderValue);
			sliderValueRow.setGravity(Gravity.CENTER_HORIZONTAL);
			traitTable.addView(sliderValueRow);
			sliderValues.put(String.valueOf(trait.getTraitID()),sliderValue );
			PredefineValueDao preDao = new PredefineValueDao(this);
			for (String s : preDao.search1(trait.getTraitID()))
				if (!s.equals(pValues))
					max = s;

			SeekBar slider = new SeekBar(this);
			if (Integer.parseInt(max) < Integer.parseInt(pValues)) {
				// pValues--- maximum
				final String min = max;
				max = pValues;
				if (!max.equals(""))
					slider.setMax(Integer.parseInt(max) - Integer.parseInt(min));
				int index = 0;
				for (int i = 0; i < traitNames.length; i++)
					if (traitNames[i].equals(trait.getTraitName()))
						index = i;
				values[index] = "";
				values[index] += (Integer.parseInt(min));
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
						//sliderValues.get(sliderindex.get)
						sliderValue.setText(""
								+ (Integer.parseInt(min) + value));
					}

				});
			} else {
				if (!max.equals(""))
					slider.setMax(Integer.parseInt(max)
							- Integer.parseInt(pValues));
				int index = 0;
				for (int i = 0; i < traitNames.length; i++)
					if (traitNames[i].equals(trait.getTraitName()))
						index = i;
				values[index] = "";
				values[index] += (Integer.parseInt(pValues));
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
						sliderValues.get(String.valueOf(trait.getTraitID())).setText(""
								+ (Integer.parseInt(pValues) + value));
						//sliderValue.setText();
					}

				});
			}
			// listener

			row.addView(slider);

			TextView unitText = new TextView(this);
			if (trait.getUnit() != null) {
				unitText.setText(trait.getUnit());
			}
			row.addView(unitText);
			CheckBox editableBox = new CheckBox(this);
			editableBox.append("Modifiable");
			editableBox.setTextSize(11);
			editableBox.setPadding(1, 15, 1, 1);
			editableBox.setGravity(Gravity.LEFT);
			editableBox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							int index = 0;
							for (int i = 0; i < traitNames.length; i++) {
								if (traitNames[i].equals(trait.getTraitName()))
									index = i;
							}
							if (isChecked) {
								editable[index] = 1;
							} else {
								editable[index] = 0;
							}
						}
					});
			row.addView(editableBox);
		}
		if (flag)
			row.setMinimumHeight(90);
		else
			row.setMinimumHeight(40);
		traitTable.addView(row, new TableLayout.LayoutParams());
	}

	class spinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			for (int i = 0; i < traitTable.getChildCount(); i++) {
				TableRow row = (TableRow) traitTable.getChildAt(i);
				row.setVisibility(8);
			}
			TraitList t=(TraitList) arg0.getItemAtPosition(arg2);
			traitListName =t.getTraitVersionName();
			// constructe trait table
			traitListID = t.getTraitListID();
			generateList(traitListID);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}

	class CameraListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			CharSequence[] items = { "Camera", "Photo album", "Cancel" };
			new AlertDialog.Builder(CreateObservationActivity.this)
					.setTitle("Photo Source")
					.setItems(items, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent;
							// TODO Auto-generated method stub
							switch (which) {

							case 0:
								if (obserNameField.getText().length() == 0) {
									Toast.makeText(
											CreateObservationActivity.this,
											"Input observation name firstly please.:)",
											Toast.LENGTH_SHORT).show();
								} else {
									String state = Environment
											.getExternalStorageState();
									if (state.equals(Environment.MEDIA_MOUNTED)) {
										intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										FileName = obserNameField.getText()
												+ "-"
												+ System.currentTimeMillis()
												+ ".jpg";
										File f = new File(PHOTO_DIR, FileName);
										intent.putExtra(
												MediaStore.EXTRA_OUTPUT,
												Uri.fromFile(f));
										startActivityForResult(intent, which);
									} else {
										Toast.makeText(
												CreateObservationActivity.this,
												"The SDcard is unavailable",
												Toast.LENGTH_LONG).show();
									}
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
			new AlertDialog.Builder(CreateObservationActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Warning")
					.setMessage("Are you sure to delete this image?")
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									File f = new File(paths[position
											% paths.length]);
									if (f.exists()) {
										f.delete();
										photoPath.remove(paths[position
												% paths.length]);
										if (photoPath.size() != 0) {
											paths = new String[photoPath.size()];
											for (int i = 0; i < paths.length; i++)
												paths[i] = photoPath.get(i);
											final GalleryAdapter galleryAdapter = new GalleryAdapter(
													CreateObservationActivity.this,
													false, paths);
											imageGallery
													.setAdapter(galleryAdapter);
											imageGallery
													.setOnItemLongClickListener(new imageDeleteListener());
										} else {
											GalleryAdapter galleryAdapter = new GalleryAdapter(
													CreateObservationActivity.this,
													true, null);
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
				paths = new String[photoPath.size()];
				for (int i = 0; i < paths.length; i++)
					paths[i] = photoPath.get(i);
				final GalleryAdapter galleryAdapter = new GalleryAdapter(
						CreateObservationActivity.this, false, paths);
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
			paths = new String[photoPath.size()];
			for (int i = 0; i < paths.length; i++)
				paths[i] = photoPath.get(i);
			GalleryAdapter galleryAdapter = new GalleryAdapter(
					CreateObservationActivity.this, false, paths);
			imageGallery.setAdapter(galleryAdapter);
			imageGallery.setOnItemLongClickListener(new imageDeleteListener());
			break;
		default:
			drawingPath = data.getStringExtra("path");
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.createobservation, menu);
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
			intent.setClass(CreateObservationActivity.this,
					ResetPasswordActivity.class);
			intent.putExtra("password", user.getPassword());
			intent.putExtra("username", user.getUserName());
			startActivity(intent);
			finish();
			return true;
		} else if (id == R.id.action_logout) {
			// logout action
			Intent intent = new Intent();
			intent.setClass(CreateObservationActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("flag", 1);
			finish();
			startActivity(intent);
		} else if (id == R.id.submit_observation_create) {
			observationName = "";
			observationName += obserNameField.getText().toString();
			if (observationName == "") {
				Toast toast = Toast.makeText(CreateObservationActivity.this,
						"Please input observation name. :)", Toast.LENGTH_LONG);
				toast.show();
			} else if (ObservationDao.validateObserName(observationName)) {
				obserNameField.setText("");
				Toast toast = Toast.makeText(CreateObservationActivity.this,
						"This observation name has been existing",
						Toast.LENGTH_LONG);
				toast.show();
			} else {
				// --------------
				String path = ",";
				for (int i = 0; i < photoPath.size(); i++)
					path += photoPath.get(i) + ",";
				observation = new Observation(observationID, observationName
						+ "---" + traitListName, user.getUserName(),
						traitListID, null, deletingDeadline, path, drawingPath,
						commentStr);
				observationDao.addObservation(observation);

				AddLog addObserLog = new AddLog(UUID.randomUUID().hashCode(),
						"Observation", observationID);
				addLogDao.insert(addObserLog);

				AddLog addObserConLog;
				for (int i = 0; i < traitNames.length; i++) {
//					ObserContent obserContent = new ObserContent(UUID.randomUUID().hashCode(), observationID,
//							traitDao.findIdByName(traitNames[i]), values[i],
//							editable[i]);
					ObserContent obserContent = new ObserContent(UUID.randomUUID().hashCode(), observationID,
						traits.get(i).getTraitID(), values[i],
							editable[i]);
					obserContentDao.addObserContent(obserContent);
//					addObserConLog = new AddLog(UUID.randomUUID().hashCode(),
//							"ObserContent", observationID,
//							traitDao.findIdByName(traitNames[i]));
					addObserConLog = new AddLog(UUID.randomUUID().hashCode(),
							"ObserContent", observationID,
							traits.get(i).getTraitID());
					addLogDao.insert(addObserConLog);

				
				}
				Intent intent = new Intent(CreateObservationActivity.this,
						ObservationListActivity.class);
				intent.putExtra("username", user.getUserName());
				intent.putExtra("password", user.getPassword());
				startActivity(intent);	
				this.finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
