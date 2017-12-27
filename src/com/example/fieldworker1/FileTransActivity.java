package com.example.fieldworker1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FileTransActivity extends Activity {

	private Button uploadExcelButton, chooseExcelButtonUp, downloadExcelButton,
			chooseExcelButtonDown;
	private Button uploadImageButton, chooseImageButtonUp, downloadImageButton,
			chooseImageButtonDown;
	private EditText excelSelectedEditTextUp, excelSelectedEditTextDown;
	private EditText imageSelectedEditTextUp, imageSelectedEditTextDown;

	private static String path, imagePath;
	private ProgressDialog progressDialog;
	private String deviceId;

	//private String Url = "http://172.31.183.9:8888/sync/";
	private AsyncHttpClient client;
	private final String TAG = "FileTransActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_trans);

		client = new AsyncHttpClient();

		uploadExcelButton = (Button) findViewById(R.id.Excel_File_Upload_Button_Up);
		chooseExcelButtonUp = (Button) findViewById(R.id.Excel_File_Choose_Button_Up);
		excelSelectedEditTextUp = (EditText) findViewById(R.id.Excel_File_Selected_Up);
		downloadExcelButton = (Button) findViewById(R.id.Excel_File_Download_Button_Down);
		chooseExcelButtonDown = (Button) findViewById(R.id.Excel_File_Choose_Button_Down);
		excelSelectedEditTextDown = (EditText) findViewById(R.id.Excel_File_Selected_Down);

		chooseExcelButtonUp.setOnClickListener(new chooseExcelListener());
		uploadExcelButton.setOnClickListener(new uploadExcelListener());

		chooseExcelButtonDown.setOnClickListener(new ChooseFileDownListener());
		downloadExcelButton.setOnClickListener(new DownloadExcelListener());

		uploadImageButton = (Button) findViewById(R.id.Image_File_Upload_Button);
		chooseImageButtonUp = (Button) findViewById(R.id.Image_File_Choose_Button_Up);
		imageSelectedEditTextUp = (EditText) findViewById(R.id.Image_File_Selected_Up);
		downloadImageButton = (Button) findViewById(R.id.Image_File_Download_Button);
		chooseImageButtonDown = (Button) findViewById(R.id.Image_File_Choose_Button_Down);
		imageSelectedEditTextDown = (EditText) findViewById(R.id.Image_File_Selected_Down);

		chooseImageButtonUp.setOnClickListener(new chooseImageListener());
		uploadImageButton.setOnClickListener(new uploadImageListener());

		chooseImageButtonDown.setOnClickListener(new ChooseImageDownListener());
		downloadImageButton.setOnClickListener(new DownloadImageListener());

		progressDialog = new ProgressDialog(FileTransActivity.this);
		progressDialog.setTitle("Promote");
		progressDialog.setMessage("downloading, wait for a moment...");
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		deviceId = deviceUuid.toString();

	}

	// upload excel button listener
	class uploadExcelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				if (!excelSelectedEditTextUp.getText().toString().equals(""))
					upLoadByAsyncHttpClient();
				else {
					Toast.makeText(FileTransActivity.this,
							"Select a file firstly please.", Toast.LENGTH_LONG)
							.show();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// upload image button listener
	class uploadImageListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				if (!imageSelectedEditTextUp.getText().toString().equals(""))
					upLoadImageByAsyncHttpClient();
				else {
					Toast.makeText(FileTransActivity.this,
							"Select a image firstly please.", Toast.LENGTH_LONG)
							.show();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// upload excel method
	private void upLoadByAsyncHttpClient() throws FileNotFoundException {

		RequestParams params = new RequestParams();
		params.put("uploadfile", new File(path));
		params.put("deviceID", deviceId);
		client.post(Constant.urlString + "uploadExcel.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, String arg1) {
						super.onSuccess(arg0, arg1);
						Toast.makeText(FileTransActivity.this,
								"Upload excel successfully.", Toast.LENGTH_LONG)
								.show();
					}
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						
						if (statusCode == 404) {
							Toast.makeText(FileTransActivity.this,
									"Requested resource not found",
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(FileTransActivity.this,
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									FileTransActivity.this,
									"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	// upload image method
	private void upLoadImageByAsyncHttpClient() throws FileNotFoundException {

		RequestParams params = new RequestParams();
		params.put("uploadImage", new File(imagePath));
		params.put("deviceID", deviceId);
		client.post(Constant.urlString + "uploadImage.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, String arg1) {
						super.onSuccess(arg0, arg1);
						Toast.makeText(FileTransActivity.this,
								"Upload image successfully.", Toast.LENGTH_LONG)
								.show();
					}
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						
						if (statusCode == 404) {
							Toast.makeText(FileTransActivity.this,
									"Requested resource not found",
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(FileTransActivity.this,
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									FileTransActivity.this,
									"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	// choose excel for uploading listener
	class chooseExcelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				final String pathStr = sdcardDir.getPath() + "/Excel";
				File folder = new File(pathStr);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				final HashMap<String, String> fileList = new HashMap<String, String>();
				getFileList(folder, fileList);

				final String[] selection = new String[fileList.keySet().size()];

				int i = 0;
				for (String str : fileList.keySet())
					selection[i++] = str;
				if (selection.length == 0)
					Toast.makeText(
							FileTransActivity.this,
							"There is no excel at present, and export one please.",
							Toast.LENGTH_LONG).show();

				new AlertDialog.Builder(FileTransActivity.this)
						.setTitle("Choose a Excel File")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setSingleChoiceItems(selection, 0,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

										excelSelectedEditTextUp
												.setText(selection[which]);
										path = "";
										path += fileList.get(selection[which]);
									}
								}).setNegativeButton("Cancel", null).show();

			} else
				Toast.makeText(
						FileTransActivity.this,
						"Your SDcard is unaviailable, and inert your SDcard please.:)",
						Toast.LENGTH_SHORT).show();

		}

	}

	private void getFileList(File path, HashMap<String, String> fileList) {
		// TODO Auto-generated method stub
		if (path.isDirectory()) {
			File[] files = path.listFiles();

			if (null == files)
				return;
			for (int i = 0; i < files.length; i++) {
				getFileList(files[i], fileList);
			}
		} else {
			
			
			String filePath = path.getAbsolutePath();
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			fileList.put(fileName, filePath);
		}
	}

	// choose image for uploading listener
	class chooseImageListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				final String pathStr = sdcardDir.getPath()
						+ "/ASoohue/CameraCache";
				File folder = new File(pathStr);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				final HashMap<String, String> fileListImage = new HashMap<String, String>();
				getFileList(folder, fileListImage);

				final String[] selection = new String[fileListImage.keySet()
						.size()];

				int i = 0;
				for (String str : fileListImage.keySet())
					selection[i++] = str;
				if (selection.length == 0)
					Toast.makeText(FileTransActivity.this,
							"There is no image at present.", Toast.LENGTH_LONG)
							.show();

				new AlertDialog.Builder(FileTransActivity.this)
						.setTitle("Choose a Image File")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setSingleChoiceItems(selection, 0,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

										imageSelectedEditTextUp
												.setText(selection[which]);
										imagePath = "";
										imagePath += fileListImage
												.get(selection[which]);
									}
								}).setNegativeButton("Cancel", null).show();

			} else
				Toast.makeText(
						FileTransActivity.this,
						"Your SDcard is unaviailable, and inert your SDcard please.:)",
						Toast.LENGTH_SHORT).show();

		}
	}

	// choose a excel for downloading listener
	class ChooseFileDownListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			RequestParams params = new RequestParams();
			// params.put("deviceId", deviceId);
			client.get(Constant.urlString + "getFileList.php?deviceId=" + deviceId,
					new AsyncHttpResponseHandler() {
						public void onSuccess(String response) {
							ArrayList<String> lists = new ArrayList<String>();
							try {
								JSONObject JSONObject = new JSONObject(response);
								JSONArray data = JSONObject
										.getJSONArray("files");
								for (int i = 0; i < data.length(); i++) {
									JSONObject info = data.getJSONObject(i);
									if (info.getString("fileType").equals(
											"Excel")) {
										lists.add(info.getString("fileName"));
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							final String[] selections = new String[lists.size()];
							for (int i = 0; i < lists.size(); i++)
								selections[i] = lists.get(i);
							if (selections.length == 0)
								Toast.makeText(
										FileTransActivity.this,
										"There is no excel file on server, and upload one firstly please.",
										Toast.LENGTH_LONG).show();

							new AlertDialog.Builder(FileTransActivity.this)
									.setTitle("Choose a Excel File")
									.setIcon(android.R.drawable.ic_dialog_info)
									.setSingleChoiceItems(
											selections,
											0,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													excelSelectedEditTextDown
															.setText(selections[which]);
												}
											})
									.setNegativeButton("Cancel", null).show();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							if (statusCode == 404) {
								Toast.makeText(FileTransActivity.this,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(FileTransActivity.this,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										FileTransActivity.this,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

	}

	// choose image for downloading listener
	class ChooseImageDownListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			RequestParams params = new RequestParams();
			client.get(Constant.urlString + "getFileList.php?deviceId=" + deviceId,
					new AsyncHttpResponseHandler() {
						public void onSuccess(String response) {
							ArrayList<String> lists = new ArrayList<String>();
							try {
								JSONObject JSONObject = new JSONObject(response);
								JSONArray data = JSONObject
										.getJSONArray("files");
								for (int i = 0; i < data.length(); i++) {
									JSONObject info = data.getJSONObject(i);
									if (info.getString("fileType").equals(
											"Image")) {
										lists.add(info.getString("fileName"));
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							final String[] selections = new String[lists.size()];
							for (int i = 0; i < lists.size(); i++)
								selections[i] = lists.get(i);
							if (selections.length == 0)
								Toast.makeText(
										FileTransActivity.this,
										"There is no image on server, and upload one firstly please.",
										Toast.LENGTH_LONG).show();

							new AlertDialog.Builder(FileTransActivity.this)
									.setTitle("Choose a Image File")
									.setIcon(android.R.drawable.ic_dialog_info)
									.setSingleChoiceItems(
											selections,
											0,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													imageSelectedEditTextDown
															.setText(selections[which]);
												}
											})
									.setNegativeButton("Cancel", null).show();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							if (statusCode == 404) {
								Toast.makeText(FileTransActivity.this,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(FileTransActivity.this,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										FileTransActivity.this,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

	}

	// download excel listener
	class DownloadExcelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!excelSelectedEditTextDown.getText().toString().equals("")) {
				String str = excelSelectedEditTextDown.getText().toString();
				new MyAsyncTask().execute(Constant.urlString + "Files/" + str);
			} else {
				Toast.makeText(FileTransActivity.this,
						"Select a file firstly please.", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	// download image listener
	class DownloadImageListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!imageSelectedEditTextDown.getText().toString().equals("")) {
				String str = imageSelectedEditTextDown.getText().toString();
				new MyAsyncTaskImage().execute(Constant.urlString + "Files/" + str);
			} else {
				Toast.makeText(FileTransActivity.this,
						"Select a image firstly please.", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected byte[] doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			byte[] file = new byte[] {};
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				if (httpEntity != null
						&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					long file_length = httpEntity.getContentLength();
					long total_length = 0;
					int length = 0;
					byte[] data = new byte[1024];
					inputStream = httpEntity.getContent();
					while (-1 != (length = inputStream.read(data))) {
						total_length += length;
						byteArrayOutputStream.write(data, 0, length);
						int progress = ((int) (total_length / (float) file_length) * 100);
						publishProgress(progress);
					}
				}
				file = byteArrayOutputStream.toByteArray();
				inputStream.close();
				byteArrayOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
			return file;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				String path = sdcardDir.getPath() + "/Excel";
				File folder = new File(path);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				path += "/" + excelSelectedEditTextDown.getText().toString();
				File file = new File(path);
				if (!file.exists()) {
					try {
						file.createNewFile();
						ByteArrayInputStream input = new ByteArrayInputStream(
								result);
						FileOutputStream output = new FileOutputStream(file);
						while (input.read(result) != -1) {
							output.write(result);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(FileTransActivity.this,
						"Downlaod it on Excel folder successfully.",
						Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
			}
		}
	}

	// download image class
	public class MyAsyncTaskImage extends AsyncTask<String, Integer, byte[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected byte[] doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			byte[] image = new byte[] {};
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				if (httpEntity != null
						&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					long file_length = httpEntity.getContentLength();
					long total_length = 0;
					int length = 0;
					byte[] data = new byte[1024];
					inputStream = httpEntity.getContent();
					while (-1 != (length = inputStream.read(data))) {
						total_length += length;
						byteArrayOutputStream.write(data, 0, length);
						int progress = ((int) (total_length / (float) file_length) * 100);
						publishProgress(progress);
					}
				}
				image = byteArrayOutputStream.toByteArray();
				inputStream.close();
				byteArrayOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
			return image;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				File sdcardDir = Environment.getExternalStorageDirectory();
				String path = sdcardDir.getPath() + "/ASoohue/CameraCache";
				File folder = new File(path);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				path += "/" + imageSelectedEditTextDown.getText().toString();
				File file = new File(path);
				if (!file.exists()) {
					try {
						file.createNewFile();
						ByteArrayInputStream input = new ByteArrayInputStream(
								result);
						FileOutputStream output = new FileOutputStream(file);
						while (input.read(result) != -1) {
							output.write(result);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(
						FileTransActivity.this,
						"Downlaod it on /ASoohue/CameraCache folder successfully.",
						Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
			}
		}
	}

}
