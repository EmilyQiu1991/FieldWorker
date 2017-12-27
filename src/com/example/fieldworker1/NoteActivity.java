package com.example.fieldworker1;

import java.io.File;
import java.io.FileOutputStream;
import com.example.dao.ObservationDao;
import com.example.fieldworker1.GalleryView;
import com.example.fieldworker1.ImageAdapter;
import com.example.fieldworker1.CreateObservationActivity.imageDeleteListener;
import com.example.domain.Observation;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class NoteActivity extends Activity {
	private Button btn_save, btn_resume;
	private ImageView iv_canvas;
	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;
	private TextView obserNameView;
	private String pathObser;
	private String[] paths;
	private int index;
	private int observationID;
	private ObservationDao obserDao;

	private GalleryView gallery;
	private ImageAdapter adapter;
	private boolean isalive = true;
	private int count_drawble = 0;
	private int cur_index = 0;
	private static int MSG_UPDATE = 1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		Intent intent = getIntent();
		if (!intent.getStringExtra("paths").equals(""))
			pathObser = intent.getStringExtra("paths");
		else
			pathObser = "";
		paths = intent.getStringExtra("paths").split(",");

		obserDao = new ObservationDao(NoteActivity.this);
		if (intent.getBooleanExtra("check", false))
			observationID = obserDao.findIdByName(intent
					.getStringExtra("observationDataName"));
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setColor(Color.RED);

		obserNameView = (TextView) findViewById(R.id.observationName_note);

		obserNameView.setText(intent.getStringExtra("observationName"));
		obserNameView.setGravity(Gravity.CENTER_HORIZONTAL);

		iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
		gallery = (GalleryView) findViewById(R.id.note_gallery);
		gallery.setPadding(10, 30, 10, 10);
		gallery.setFitsSystemWindows(true);

		index = 0;

		btn_save = (Button) findViewById(R.id.btn_save);
		btn_resume = (Button) findViewById(R.id.btn_resume);

		btn_save.setOnClickListener(click);
		btn_resume.setOnClickListener(click);
		iv_canvas.setOnTouchListener(touch);

		if (!intent.getStringExtra("paths").equals("")) {
			adapter = new ImageAdapter(this, intent.getStringExtra("paths"));
			adapter.createReflectedImages();
			gallery.setAdapter(adapter);
			gallery.setSelection(3);
			gallery.setOnItemLongClickListener(new drawingDeleteListener());

			count_drawble = adapter.getCount();
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (isalive) {
						cur_index = cur_index % count_drawble;

						Message msg = mhandler.obtainMessage(MSG_UPDATE,
								cur_index, 0);
						mhandler.sendMessage(msg);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cur_index++;
					}
				}
			}).start();
		}

	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE) {

				gallery.setSelection(msg.arg1);
			}
		}
	};

	private View.OnTouchListener touch = new OnTouchListener() {
		float startX;
		float startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				if (baseBitmap == null) {
					baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
							iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(baseBitmap);
					canvas.drawColor(Color.WHITE);
				}
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float stopX = event.getX();
				float stopY = event.getY();

				canvas.drawLine(startX, startY, stopX, stopY, paint);

				startX = event.getX();
				startY = event.getY();

				iv_canvas.setImageBitmap(baseBitmap);
				break;
			case MotionEvent.ACTION_UP:

				break;
			default:
				break;
			}
			return true;
		}
	};
	private View.OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_save:
				saveBitmap();
				break;
			case R.id.btn_resume:
				resumeCanvas();
				break;
			default:
				break;
			}
			// finish();
		}
	};

	protected void saveBitmap() {
		try {
			String str = obserNameView.getText().toString();
			int index = str.indexOf("---");
			String name = "";
			if (index != -1)
				name += str.substring(0, index - 1);
			else
				name += str;

			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				final String pathStr = sdcardDir.getPath()
						+ "/ASoohue/CameraCache";
				File folder = new File(pathStr);
				if (!folder.exists()) {
					folder.mkdirs();
				}
			}
			
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/ASoohue/CameraCache", name + "---"
					+ System.currentTimeMillis() + ".png");
			String path = file.getPath();
			if( !file.exists())
				System.out.println("not exist");
			FileOutputStream stream = new FileOutputStream(file);
			baseBitmap.compress(CompressFormat.PNG, 100, stream);
			Toast.makeText(NoteActivity.this,
					"Save painting successfully in " + path, 0).show();

			pathObser += path + ",";
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			intent.putExtra("path", pathObser);
			sendBroadcast(intent);
			setResult(RESULT_OK, intent);
			finish();
		} catch (Exception e) {
			Toast.makeText(NoteActivity.this, "Save picture failure", 0).show();
			e.printStackTrace();
		}
	}

	protected void resumeCanvas() {
		if (baseBitmap != null) {
			baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
					iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(baseBitmap);
			canvas.drawColor(Color.WHITE);
			iv_canvas.setImageBitmap(baseBitmap);
			Toast.makeText(NoteActivity.this, "Clear canvas successfully", 0)
					.show();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		isalive = false;
	}

	class drawingDeleteListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {
			// TODO Auto-generated method stub
			new AlertDialog.Builder(NoteActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Worning")
					.setMessage("Are you sure to delete this drawing?")
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
										pathObser = pathObser.replace(
												paths[position % paths.length]
														+ ",", "");
										paths[position % paths.length] = "";
										String newPath = "";
										for (int i = 0; i < paths.length; i++)
											if (!paths[i].equals(""))
												newPath += paths[i] + ",";

										if (!newPath.equals("")) {
											adapter = new ImageAdapter(
													NoteActivity.this, newPath);
											adapter.createReflectedImages();
											gallery.setAdapter(adapter);
											gallery.setSelection(3);
											gallery.setOnItemLongClickListener(new drawingDeleteListener());

											count_drawble = adapter.getCount();
											new Thread(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated
													// method stub
													while (isalive) {
														cur_index = cur_index
																% count_drawble;

														Message msg = mhandler
																.obtainMessage(
																		MSG_UPDATE,
																		cur_index,
																		0);
														mhandler.sendMessage(msg);
														try {
															Thread.sleep(3000);
														} catch (InterruptedException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}
														cur_index++;
													}
												}
											}).start();
										}
									}
								}
							}).setNegativeButton("NO", null).show();
			return true;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// go here
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			intent.putExtra("path", pathObser);
			sendBroadcast(intent);
			setResult(RESULT_OK, intent);
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

}
