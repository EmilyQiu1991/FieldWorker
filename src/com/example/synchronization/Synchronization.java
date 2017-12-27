package com.example.synchronization;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.dao.AddLogDao;
import com.example.dao.DeleteLogDao;
import com.example.dao.UserDao;
import com.example.fieldworker1.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class Synchronization {

	ProgressDialog prgDialog;
	Context context;
	//private String ipString = "172.31.183.9";

	public Synchronization(Context context) {
		prgDialog = new ProgressDialog(context);
		prgDialog
				.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
		prgDialog.setCancelable(false);
		this.context = context;
	}

	public void syn() {
		// Create AsycHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		DeleteLogDao deleteLogDao = new DeleteLogDao(context);

		// synchronize delete user
		ArrayList<HashMap<String, String>> deleteUser = deleteLogDao.deleteUser();
		if (deleteUser.size() != 0) {
			prgDialog.show();
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(deleteUser);
			params.put("deleteUser", strJSON);
			client.post(Constant.urlString+"deleteUser.php",
					params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							prgDialog.hide();
							// Toast.makeText(context, "DB Sync completed!",
							// Toast.LENGTH_SHORT).show();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							prgDialog.hide();
							if (statusCode == 404) {
								Toast.makeText(context,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(context,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										context,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

		// synchronize delete Observation
		ArrayList<HashMap<String, String>> deleteObservation = deleteLogDao.deleteObservation();
		if (deleteObservation.size() != 0) {
			prgDialog.show();
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(deleteObservation);
			params.put("deleteObservation", strJSON);
			client.post(Constant.urlString
					+ "deleteObservation.php", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							prgDialog.hide();
							// Toast.makeText(context, "DB Sync completed!",
							// Toast.LENGTH_SHORT).show();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							prgDialog.hide();
							if (statusCode == 404) {
								Toast.makeText(context,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(context,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										context,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

		// synchronize delete ObserContent
		ArrayList<HashMap<String, String>> deleteObserContent = deleteLogDao
				.deleteObserContent();
		if (deleteObserContent.size() != 0) {
			prgDialog.show();
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(deleteObserContent);
			params.put("deleteObserContent", strJSON);
			client.post(Constant.urlString
					+ "deleteObserContent.php", params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							prgDialog.hide();
							// Toast.makeText(context, "DB Sync completed!",
							// Toast.LENGTH_SHORT).show();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							prgDialog.hide();
							if (statusCode == 404) {
								Toast.makeText(context,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(context,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										context,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

		deleteLogDao.clearTable();

		AddLogDao addLogDao = new AddLogDao(context);

		// synchronize add User
		ArrayList<HashMap<String, String>> addUser = addLogDao.addUser();
		if (addUser.size() != 0) {
			prgDialog.show();
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(addUser);
			params.put("addUser", strJSON);
			client.post( Constant.urlString+ "addUser.php",
					params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							prgDialog.hide();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							prgDialog.hide();
							if (statusCode == 404) {
								Toast.makeText(context,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(context,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										context,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

		// synchronize add Observation
		ArrayList<HashMap<String, String>> addObservation = addLogDao
				.addObservation();
		if (addObservation.size() != 0) {
			prgDialog.show();
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(addObservation);
			params.put("addObservation", strJSON);
			client.post(Constant.urlString + "addObservation.php",
					params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							prgDialog.hide();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							prgDialog.hide();
							if (statusCode == 404) {
								Toast.makeText(context,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(context,
										"Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										context,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

		// synchronize add Observation Content
		ArrayList<HashMap<String, String>> addObserContent = addLogDao
				.addObserContent();

		if (addObserContent.size() != 0) {
			prgDialog.show();
			Gson gson = new GsonBuilder().create();
			String strJSON = gson.toJson(addObserContent);
			params.put("addObserContent", strJSON);
			client.post(
					Constant.urlString + "addObserContent.php",
					params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							prgDialog.hide();
							// Toast.makeText(context, "DB Sync completed!",
							// Toast.LENGTH_SHORT).show();
						}

						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub
							prgDialog.hide();
							if (statusCode == 404) {
								Toast.makeText(context,
										"Requested resource not found",
										Toast.LENGTH_LONG).show();
							} else if (statusCode == 500) {
								Toast.makeText(context,
										"Something went wrong at server end ",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										context,
										"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}

		addLogDao.clearTable();
		Toast.makeText(context, "DB Sync completed!", Toast.LENGTH_SHORT)
				.show();

	}
}
