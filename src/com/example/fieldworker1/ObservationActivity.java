package com.example.fieldworker1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.ObserContentDao;
import com.example.dao.ObservationDao;
import com.example.dao.PredefineValueDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;
import com.example.domain.Observation;
import com.example.domain.Trait;
import com.example.domain.User;
import com.example.fieldworker1.R;
import com.example.excel.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

public class ObservationActivity extends Activity {

	private User user;
	private Observation observation;
	private HashMap<Integer, String> traitValues;
	private String max;
	private String commentStr;

	private EditText obserNameField;
	private EditText traitListText;
	private TableLayout traitTable;
	private EditText obserDeadlineField;
	private Button exportExcelButton;
	private DatabaseConnector con;
	private TextView sliderValue;
	private Gallery imageGallery;
	private Button drawing;
	private Button comment;
	private String drawingPath;

	public static List<Trait> traits;

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
		traitListDao = new TraitListDao(this);

		traitListContentDao = new TraitListContentDao(this);
		predefineValueDao = new PredefineValueDao(this);
		traitListDao = new TraitListDao(this);
		observationDao = new ObservationDao(this);
		obserContentDao = new ObserContentDao(this);
		addLogDao = new AddLogDao(this);
		deleteLogDao = new DeleteLogDao(this);

		con = new DatabaseConnector(this);
		max = "";

		Intent intent = getIntent();
		user = new User(intent.getStringExtra("username"),
				intent.getStringExtra("password"));
		observation = observationDao.findObervationById(intent.getIntExtra(
				"observationID", -1));
		setContentView(R.layout.activity_observation);

		traitListText = (EditText) findViewById(R.id.traitListObser);
		System.out.println("observation: "+ observation);
		
		String str = traitListDao.findById(observation.getTraitListID()).getTraitVersionName();
		System.out.println("?????"+str);
		traitListText.setText(str);
		traitListText.setEnabled(false);
		traitListText.setTextColor(Color.BLACK);
		obserNameField = (EditText) findViewById(R.id.observationNameFieldObser);
		String name = observation.getObservationName().replace("---", "");

		obserNameField.setText(name.replace(str, ""));
		obserNameField.setEnabled(false);
		obserNameField.setTextColor(Color.BLACK);
		traitTable = (TableLayout) findViewById(R.id.observation_table);
		obserDeadlineField = (EditText) findViewById(R.id.observationDeletingDeadlineField);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (observation.getDeleteTime() != null)
			obserDeadlineField.setText(sdf.format(observation.getDeleteTime()));
		else
			obserDeadlineField.setText("");
		obserDeadlineField.setEnabled(false);
		obserDeadlineField.setTextColor(Color.BLACK);

		imageGallery = (Gallery) findViewById(R.id.gallery_obser);
		if (observation.getPhotoPath() == null) {
			GalleryAdapter galleryAdapter = new GalleryAdapter(
					ObservationActivity.this, true, null);
			imageGallery.setAdapter(galleryAdapter);
		} else {
			GalleryAdapter galleryAdapter = new GalleryAdapter(
					ObservationActivity.this, false, observation.getPhotoPath()
							.split(","));
			imageGallery.setAdapter(galleryAdapter);

		}
		exportExcelButton = (Button) findViewById(R.id.exportExcelButton);
		exportExcelButton.setOnClickListener(new ExportListener());

		drawing = (Button) findViewById(R.id.observation_drawing_button);
		comment = (Button) findViewById(R.id.observation_comment_button);

		drawingPath = "";
		if (observation.getPaintingPath() != null)
			drawingPath += observation.getPaintingPath();
		commentStr = "";
		if (observation.getComment() != null) {
			commentStr += observation.getComment();
		}

		drawing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(ObservationActivity.this,
						NoteActivity.class);
				intent.putExtra("check", true);
				intent.putExtra("paths", drawingPath);
				intent.putExtra("observationName", obserNameField.getText()
						.toString());
				intent.putExtra("observationDataName",
						observation.getObservationName());
				startActivityForResult(intent, 2);
			}

		});

		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ObservationActivity.this);
				final EditText commentArea = new EditText(
						ObservationActivity.this);
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
								observationDao.updateObservationComment(
										commentStr,
										observation.getObservationID());

								AddLog addObserLog = new AddLog(UUID
										.randomUUID().hashCode(),
										"Observation", observation
												.getObservationID());
								if (!addLogDao.checkExist(addObserLog)) {
									DeleteLog deleteObserLog = new DeleteLog(
											UUID.randomUUID().hashCode(),
											"Observation", observation
													.getObservationID());
									deleteLogDao.insert(deleteObserLog);

									addLogDao.insert(addObserLog);

									List<Trait> traits = traitListContentDao
											.searchTraitsByTraitListID(observation
													.getTraitListID());
									AddLog addObserContLog;
									for (Trait t : traits) {
										addObserContLog = new AddLog(UUID
												.randomUUID().hashCode(),
												"ObserContent", observation
														.getObservationID(), t
														.getTraitID());
										if (!addLogDao
												.checkExist(addObserContLog)) {
											DeleteLog deleteObserContLog = new DeleteLog(
													UUID.randomUUID()
															.hashCode(),
													"ObserContent",
													observation
															.getObservationID(),
													t.getTraitID());
											deleteLogDao
													.insert(deleteObserContLog);

											addLogDao.insert(addObserContLog);
										}
									}

								}
							}
						});
				builder.show();
			}

		});

		traits = new ArrayList<Trait>();
		generateList(observation.getTraitListID());

	}

	private void generateList(int traitListId) {
		// TODO Auto-generated method stub
		traits = traitListContentDao.searchTraitsByTraitListID(traitListId);
		traitValues = obserContentDao.findTraitValueById(observation
				.getObservationID());
		for (Trait t : traits) {
			String[] plv = predefineValueDao.search1(t.getTraitID());
			if (!t.getWidgetName().equals("Spinner")) {
				for (int i = 0; i < plv.length; i++) {
					if (i == 0) {
						appendNewRow(t, t.getTraitName(), plv[i], true);
					} else {
						if (t.getWidgetName().equals("Slider"))
							break;
						appendNewRow(t, t.getTraitName(), plv[i], false);
					}
				}

			} else {
				// go for spinner with string array(plv) to construct
				appendNewRow(t, t.getTraitName(), plv, true);
			}
			if (plv.length == 0) {
				appendNewRow(t, t.getTraitName(), "", true);
			}
		}
	}

	// For Spinner
	private void appendNewRow(Trait t, String traitName, String[] plv,
			boolean flag) {
		if (traitValues.get(t.getTraitID()) == null)
			return;
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

		s.setEnabled(false);
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

		if (traitValues.get(trait.getTraitID()) == null)
			return;
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
			cb.setClickable(false);
			row.addView(cb);
		} else if (trait.getWidgetName().equals("EditText")) {

			final EditText view = new EditText(this);
			if (!traitValues.get(trait.getTraitID()).isEmpty())
				view.setText(traitValues.get(trait.getTraitID()));
			view.setEnabled(false);
			row.addView(view);

		} else if (trait.getWidgetName().equals("Slider")) {
			TableRow sliderValueRow = new TableRow(this);
			sliderValue = new TextView(this);
			sliderValue.setText(traitValues.get(trait.getTraitID()));
			sliderValue.setGravity(Gravity.CENTER_HORIZONTAL);
			sliderValueRow.addView(sliderValue);
			sliderValueRow.setGravity(Gravity.CENTER_HORIZONTAL);
			traitTable.addView(sliderValueRow);

			PredefineValueDao preDao = new PredefineValueDao(this);

			for (String s : preDao.search1(trait.getTraitID()))
				if (!s.equals(pValues)) {
					max = s;
				}
			int progress = 0;
			if (Integer.parseInt(max) < Integer.parseInt(pValues)) {
				final String min = max;
				max = pValues;
				progress = (int) ((Float.parseFloat(traitValues.get(trait
						.getTraitID())) - Float.parseFloat(min))
						/ (Float.parseFloat(max) - Float.parseFloat(min)) * 100.00);
			} else {
				progress = (int) ((Float.parseFloat(traitValues.get(trait
						.getTraitID())) - Float.parseFloat(pValues))
						/ (Float.parseFloat(max) - Float.parseFloat(pValues)) * 100.00);
			}
			SeekBar slider = new SeekBar(this);
			if (!traitValues.get(trait.getTraitID()).isEmpty())
				slider.setProgress(progress);
			slider.setEnabled(false);
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

	class ExportListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// validate folder
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				String path = sdcardDir.getPath() + "/Excel";
				File folder = new File(path);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				SQLiteDatabase myDB = con.getDB();
				ExportExcel export = new ExportExcel(myDB, "");
				export.exportObservation(observation);
				Toast toast = Toast
						.makeText(
								ObservationActivity.this,
								"The "
										+ observation.getObservationName()
										+ " has been exported into a excel stored in /sdcard/Excel folder",
								Toast.LENGTH_LONG);
				toast.show();
			} else
				Toast.makeText(
						ObservationActivity.this,
						"Your SDcard is unaviailable, and inert your SDcard please.:)",
						Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.observation, menu);
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
			intent.setClass(ObservationActivity.this,
					ResetPasswordActivity.class);
			intent.putExtra("password", user.getPassword());
			intent.putExtra("username", user.getUserName());
			startActivity(intent);
			return true;
		} else if (id == R.id.action_logout) {
			Intent intent = new Intent();
			intent.setClass(ObservationActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("flag", 1);
			finish();
			startActivity(intent);

		} else if (id == R.id.edit_observation) {

			if (observation.getDeleteTime() == null) {
				Intent intent = new Intent(ObservationActivity.this,
						EditObservation.class);
				intent.putExtra("username", user.getUserName());
				intent.putExtra("password", user.getPassword());
				intent.putExtra("observationID", observation.getObservationID());
				startActivity(intent);
				this.finish();
			} else if (observation.getDeleteTime().before(new java.util.Date())) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = sdf.format(observation.getDeleteTime());
				Toast toast = Toast.makeText(ObservationActivity.this,
						"You cannot modify " + observation.getObservationName()
								+ " after " + dateStr, Toast.LENGTH_SHORT);
				toast.show();
			} else {
				Intent intent = new Intent(ObservationActivity.this,
						EditObservation.class);
				intent.putExtra("username", user.getUserName());
				intent.putExtra("password", user.getPassword());
				intent.putExtra("observationID", observation.getObservationID());
				startActivity(intent);
				this.finish();
			}

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		drawingPath += data.getStringExtra("path");
		observationDao.updateObservationPaintingPath(drawingPath,
				observation.getObservationID());

		// For addLog and deleteLog table
		AddLog addObserLog = new AddLog(UUID.randomUUID().hashCode(),
				"Observation", observation.getObservationID());
		if (!addLogDao.checkExist(addObserLog)) {
			DeleteLog deleteObserLog = new DeleteLog(UUID.randomUUID()
					.hashCode(), "Observation", observation.getObservationID());
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

		}// end

	}

}
