package com.example.fieldworker1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.fieldworker1.R;
import com.example.phpServer.DownloadTraitAndTraitList;
import com.example.synchronization.Synchronization;
import com.example.validator.MyApplication;
import com.example.asynTask.LogoutAsynTask;
import com.example.dao.AddLogDao;
import com.example.dao.ObserContentDao;
import com.example.dao.ObservationDao;
import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.dao.UserDao;
import com.example.domain.AddLog;
import com.example.domain.ObserContent;
import com.example.domain.Observation;
import com.example.domain.PredefineValue;
import com.example.domain.Trait;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;
import com.example.domain.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	public static final String PREFS_NAME = "MyPrefsFile";
	private UserDao userHelper;
	private AddLogDao addLogDao;
	private TraitListDao traitListDao;
	private TraitDao traitDao;
	private PredefineValueDao predefineValueDao;
	private ObservationDao observationDao;
	private ObserContentDao obserContentDao;
	private TraitListContentDao traitListContentDao;
	public static String selection;
	private SimpleDateFormat sdf;
	private String deviceId;
	private List<Integer> observationIDList;
	private Button register, login;
	private EditText usernameText, passwordText;
	AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		selection = "";

		if (isNetworkConnected(this)) {
			Synchronization syn = new Synchronization(this);
			syn.syn();
		} else {
			builder = new AlertDialog.Builder(MainActivity.this,
					AlertDialog.THEME_DEVICE_DEFAULT_DARK);
			builder.setTitle("Warning")
					.setIcon(android.R.drawable.stat_sys_warning)
					.setMessage(
							"Your network is unavailable, and please connect to synchronize data.")
					.setPositiveButton("connect", new ConnectListener())
					.setNegativeButton("Cancel", null);
			builder.create().show();
		}

		userHelper = new UserDao(this);
		addLogDao = new AddLogDao(this);
		obserContentDao = new ObserContentDao(this);
		observationDao = new ObservationDao(this);
		traitDao = new TraitDao(this);
		traitListContentDao = new TraitListContentDao(this);
		traitListDao = new TraitListDao(this);
		predefineValueDao = new PredefineValueDao(this);

		sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		register = (Button) findViewById(R.id.registerButton);
		login = (Button) findViewById(R.id.LoginButton);
		usernameText = (EditText) findViewById(R.id.usernameTextField);
		passwordText = (EditText) findViewById(R.id.passwordTextField);
		register.setOnClickListener(new RegisterListener());
		login.setOnClickListener(new LoginListener());

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

		observationIDList = new ArrayList<Integer>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		userHelper.closeDB();// release database resource
		builder = null;
		LogoutAsynTask lTask = new LogoutAsynTask();
		SharedPreferences mySharedPreferences = getSharedPreferences(
				PREFS_NAME, 0);
		String username = mySharedPreferences.getString("username", "");
		lTask.execute(Constant.urlString + "Logout.php", username);
		System.out.println("MainActivity onDestroy()");

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

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = getIntent();
		int x = intent.getIntExtra("flag", -1);
		if (x == 0) {
			finish();
		}
	}

	class RegisterListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String username = usernameText.getText().toString();
			String password = passwordText.getText().toString();
			usernameText.setText("");
			passwordText.setText("");
			if (!MyApplication.isNetworkOnline()) {
				Toast toast = Toast.makeText(MainActivity.this,
						"Registration needs a network connection",
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			} else {
				ValidateUsernamesynTask task = new ValidateUsernamesynTask(
						username, password);
				String url = Constant.urlString + "validateUsername.php";
				task.execute(url);
			}

		}

	}

	class LoginListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String username = usernameText.getText().toString();
			String password = passwordText.getText().toString();
			if (isNetworkConnected(MainActivity.this)) {
				CheckLoginAsyncTask cAsyncTask = new CheckLoginAsyncTask();
				String url = Constant.urlString + "UserLogin.php";
				cAsyncTask.execute(url);

			} else {
				User u = new User(username, password);
				int loginResult = userHelper.check(u);
				if (loginResult == -1) {
					Toast toast = Toast.makeText(MainActivity.this,
							"There is no such user! please register :)",
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (loginResult == 0) {
					Toast toast = Toast.makeText(MainActivity.this,
							"Password is incorrect.", Toast.LENGTH_SHORT);
					toast.show();
				} else {
					
					SharedPreferences mySharedPreferences = getSharedPreferences(
							PREFS_NAME, Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = mySharedPreferences
							.edit();
					editor.putString("username", u.getUserName());
					editor.commit();

					Intent intent = new Intent();
					intent.putExtra("username", u.getUserName());
					intent.putExtra("password", u.getPassword());
					
					intent.setClass(MainActivity.this, HomeActivity.class);
					startActivity(intent);
					// finish();
				}

			}
		}

	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	class ConnectListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
			startActivity(intent);
		}

	}

	class ValidateUsernamesynTask extends AsyncTask<String, integer, Integer> {
		private String username;
		private String password;

		public ValidateUsernamesynTask(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		@Override
		protected Integer doInBackground(String... params) {
			HttpPost httpRequest = new HttpPost(params[0]);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("username", username));
			param.add(new BasicNameValuePair("password", password));
			InputStream is = null;
			String result = "";

			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpRequest.setEntity(httpEntity);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();

					is = entity.getContent();
					// System.out.println("***"+EntityUtils.toString(entity));

				} else {
					System.out.println("request error"
							+ httpResponse.getStatusLine());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();

			} catch (Exception e) {

			}
			System.out.println(result);

			return Integer.parseInt(result.replaceAll("\\s", ""));
		}

		@Override
		protected void onPostExecute(Integer result) {
			System.out.println("onPostExecute result:" + result);
			if (result == 1) {
				System.out.println("true");
				User u = new User(username, password);
				userHelper.add(u);

				Toast toast = Toast.makeText(MainActivity.this,
						"Congratulation. You have registered successfully.",
						Toast.LENGTH_SHORT);
				toast.show();
			} else {
				System.out.println("false");
				Toast toast = Toast.makeText(MainActivity.this,
						"There is existing user, input another name please :)",
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
		}

	}

	class CheckLoginAsyncTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			HttpPost httpPost = new HttpPost(params[0]);
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("username", usernameText.getText()
					.toString()));
			param.add(new BasicNameValuePair("password", passwordText.getText()
					.toString()));
			InputStream is = null;
			String result = null;
			try {
				HttpEntity httpEntity = new UrlEncodedFormEntity(param, "utf-8");
				httpPost.setEntity(httpEntity);

				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();
					is = entity.getContent();
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Toast toast = Toast.makeText(MainActivity.this,
						"Connection denied or wrong request",
						Toast.LENGTH_SHORT);
				toast.show();
				System.out.println("MainActivity CheckLoginAsyncTask " + e);
				return null;
			}
			System.out.println("MainActivity result:" + result);
			return Integer.parseInt(result.replaceAll("\\s", ""));
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				User user = new User(usernameText.getText().toString(),
						passwordText.getText().toString());
				int loginResult = userHelper.check(user);
				if (loginResult == -1) {
					if (isNetworkConnected(MainActivity.this)) {
						String url=Constant.urlString+"DownloadTraitAndTraitList.php";
						DownloadTraitAndTraitList download=new DownloadTraitAndTraitList(MainActivity.this);
						download.execute(url,usernameText.getText().toString());
                         
					}
					LoginOnline(user);
					
				} else {
					SharedPreferences mySharedPreferences = getSharedPreferences(
							PREFS_NAME, Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = mySharedPreferences
							.edit();
					editor.putString("username", usernameText.getText()
							.toString());
					editor.commit();
					Intent intent = new Intent();
					intent.putExtra("username", usernameText.getText()
							.toString());
					intent.putExtra("password", passwordText.getText()
							.toString());
					intent.setClass(MainActivity.this, HomeActivity.class);
					startActivity(intent);
				}
			} else if (result == 2) {
				Toast toast = Toast.makeText(MainActivity.this,
						"The user already login, can not login again",
						Toast.LENGTH_SHORT);
				toast.show();
			} else if (result == 0) {
				Toast toast = Toast.makeText(MainActivity.this,
						"Wrong password", Toast.LENGTH_SHORT);
				toast.show();
			} else if (result == 3) {
				Toast toast = Toast.makeText(MainActivity.this,
						"The user does not exist", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void LoginOnline(final User user) {

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("username", user.getUserName());
		params.put("password", user.getPassword());
		params.put("deviceID", deviceId);
		client.post(Constant.urlString + "loginCheckUser.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject JSONObject = new JSONObject(response);
							int loginRes = Integer.parseInt(JSONObject
									.getString("success"));
							switch (loginRes) {
							case 1:
								final JSONArray data = JSONObject
										.getJSONArray("observations");
								final String[] selection = new String[data
										.length()];
								for (int i = 0; i < data.length(); i++) {
									JSONObject info = data.getJSONObject(i);
									selection[i] = info
											.getString("observationName");
								}
								final boolean[] flags = new boolean[selection.length];
								new AlertDialog.Builder(MainActivity.this)
										.setTitle("Select some observation")
										.setMultiChoiceItems(
												selection,
												flags,
												new DialogInterface.OnMultiChoiceClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which,
															boolean isChecked) {
														// TODO Auto-generated
														// method stub
														flags[which] = isChecked;
													}

												})
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														// TODO Auto-generated
														// method stub
														sdf = new SimpleDateFormat(
																"yyyy-MM-dd");
														userHelper.add(user);
														JSONObject info;
														List<Integer> observationID = new ArrayList<Integer>();
														List<Integer> traitListID = new ArrayList<Integer>();
														Observation observation;

														for (int i = 0; i < flags.length; i++) {
															if (flags[i]) {
																try {
																	info = data
																			.getJSONObject(i);
																	observationID
																			.add(info
																					.getInt("observationID"));
																	System.out
																			.println("traitlist ID:"
																					+ info.getInt("traitListID"));
																	if (!traitListID
																			.contains(info
																					.getInt("traitListID")))
																		traitListID
																				.add(info
																						.getInt("traitListID"));
																	if (info.getString(
																			"endTime")
																			.equals("")
																			|| info.getString(
																					"endTime")
																					.equals("0000-00-00")) {
																		observation = new Observation(
																				info.getInt("observationID"),
																				info.getString("observationName"),
																				info.getString("username"),
																				info.getInt("traitListID"),
																				sdf.parse(info
																						.getString("createTime")),
																				null,
																				info.getString("photoPath"),
																				info.getString("paintingPath"),
																				info.getString("comment"));
																	}

																	else {
																		observation = new Observation(
																				info.getInt("observationID"),
																				info.getString("observationName"),
																				info.getString("username"),
																				info.getInt("traitListID"),
																				sdf.parse(info
																						.getString("createTime")),
																				sdf.parse(info
																						.getString("endTime")),
																				info.getString("photoPath"),
																				info.getString("paintingPath"),
																				info.getString("comment"));
																	}

																	observationDao
																			.addObservation(observation);

																} catch (JSONException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																} catch (ParseException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																}
															}

														}

														downloadAssociatedData(
																observationID,
																traitListID);
														Intent intent = new Intent();
														intent.putExtra(
																"username",
																user.getUserName());
														intent.putExtra(
																"password",
																user.getPassword());
														intent.setClass(
																MainActivity.this,
																HomeActivity.class);
														startActivity(intent);
													}
												})
										.setNegativeButton("Cancel", null)
										.show();
								break;
							case 0:
								Toast.makeText(
										MainActivity.this,
										"The password is incorrect, and input again please.",
										Toast.LENGTH_LONG).show();
								break;
							case -1:
								Toast.makeText(
										MainActivity.this,
										"This user is not existing, and registrater please",
										Toast.LENGTH_LONG).show();
								break;
							default:
								Toast.makeText(MainActivity.this,
										"Server error", Toast.LENGTH_LONG)
										.show();

							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub

						if (statusCode == 404) {
							Toast.makeText(MainActivity.this,
									"Requested resource not found",
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(MainActivity.this,
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									MainActivity.this,
									"Unexpected Error occcured! [Most common Error: something went wrong at server end]",
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	public void downloadAssociatedData(List<Integer> observationID,
			List<Integer> traitListID) {
		// TODO Auto-generated method stub
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		map.put("observationID", observationID);
		map.put("traitListID", traitListID);
		Gson gson = new GsonBuilder().create();
		String strJSON = gson.toJson(map);
		params.put("ForObservation", strJSON);
		client.post(Constant.urlString + "downloadObservation.php", params,
				new AsyncHttpResponseHandler() {

					public void onSuccess(String response) {
						// System.out.println( response );
						try {
							JSONObject JSONObject = new JSONObject(response);
							JSONArray traitLists = JSONObject
									.getJSONArray("traitList");
							JSONArray traitListContents = JSONObject
									.getJSONArray("traitListContent");
							JSONArray traits = JSONObject.getJSONArray("trait");
							JSONArray predefines = JSONObject
									.getJSONArray("predefineValue");
							JSONArray observationContents = JSONObject
									.getJSONArray("observationContent");

							JSONObject object;

							// download trait list
							TraitList traitList;
							for (int i = 0; i < traitLists.length(); i++) {
								object = traitLists.getJSONObject(i);
								traitList = new TraitList(object
										.getInt("traitListID"), object
										.getString("traitListName"), object
										.getString("username"), object
										.getInt("access"));
								traitListDao.insert(traitList);
							}

							// download trait
							Trait trait;
							for (int i = 0; i < traits.length(); i++) {
								object = traits.getJSONObject(i);
								trait = new Trait(object.getInt("traitID"),
										object.getString("traitName"), object
												.getString("widgetName"),
										object.getString("unit"), usernameText
												.getText().toString(), 0);
								traitDao.insert(trait,usernameText.getText().toString());
							}

							// download trait List content
							TraitListContent traitListContent;
							for (int i = 0; i < traitListContents.length(); i++) {
								object = traitListContents.getJSONObject(i);
								traitListContent = new TraitListContent(object
										.getInt("traitListID"), object
										.getInt("traitID"));
								traitListContentDao.insert(traitListContent);
								;
							}

							// download predefine Value data
							PredefineValue predefineVal;
							for (int i = 0; i < predefines.length(); i++) {
								object = predefines.getJSONObject(i);
								predefineVal = new PredefineValue(object
										.getInt("predefineValID"), object
										.getInt("traitID"), object
										.getString("value"));
								predefineValueDao.insert(predefineVal);
							}

							// download observation content
							ObserContent obserContent;
							System.out.println("?????ObserContent: " + observationContents);
							for (int i = 0; i < observationContents.length(); i++) {
								object = observationContents.getJSONObject(i);
								System.out.println("!!!!!!!ObserContent: " + object);
								obserContent = new ObserContent(object
										.getInt("relation_id"), object
										.getInt("observationID"), object
										.getInt("traitID"), object
										.getString("traitValue"), object
										.getInt("editable"));
								obserContentDao.addObserContent(obserContent);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub
						if (statusCode == 404) {
							Toast.makeText(MainActivity.this,
									"Requested resource not found",
									Toast.LENGTH_LONG).show();
						} else if (statusCode == 500) {
							Toast.makeText(MainActivity.this,
									"Something went wrong at server end",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									MainActivity.this,
									"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

}
