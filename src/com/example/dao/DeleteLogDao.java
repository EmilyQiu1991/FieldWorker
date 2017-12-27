package com.example.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;

import com.example.db.DatabaseHelper;
import com.example.domain.AddLog;
import com.example.domain.DeleteLog;

public class DeleteLogDao {
	private DatabaseHelper dbHelper; 
	private String deviceId;
	public DeleteLogDao(Context context)
	{
		dbHelper=new DatabaseHelper(context);
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    deviceId = deviceUuid.toString();
	}
	public void insert(DeleteLog deleteLog)
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.execSQL("INSERT INTO DeleteLog(deleteLogID,tableName,firstID,secondID,stringID) VALUES(?,?,?,?,?)", new Object[]{deleteLog.getDeleteLogID(),deleteLog.getTableName(),deleteLog.getFirstID(),deleteLog.getSecondID(),deleteLog.getStringID()});
	    db.close();
	}
	public List<DeleteLog> findAll()
	{
		SQLiteDatabase  db=dbHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("SELECT deleteLogID,tableName,firstID,secondID,stringID FROM DeleteLog", null);
		List<DeleteLog> DeleteLogs=new ArrayList<DeleteLog>();
		while (cursor.moveToNext()) {
			Integer deleteLogID=cursor.getInt(0);
			String tableName=cursor.getString(1);
			Integer firstID=cursor.getInt(2);
			Integer secondID=cursor.getInt(3);
			String stringID=cursor.getString(4);
			DeleteLogs.add(new DeleteLog(deleteLogID,tableName,firstID,secondID,stringID));
			
		}
		cursor.close();
		db.close();
		return DeleteLogs;
	} 
	public void clearTable()
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM DeleteLog");
	    db.close();
	}

	public ArrayList<HashMap<String,String>> deleteUser() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList result = new ArrayList<HashMap<String,String>>();
		Cursor cursor = db
				.rawQuery(
						"SELECT stringID FROM DeleteLog WHERE tableName = 'User'",
						null);
		HashMap<String,String> map;
		while(cursor.moveToNext()){
			map = new HashMap<String,String>();
			map.put("username", cursor.getString(0) );
			map.put("deviceId", deviceId);
			result.add(map);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public ArrayList<HashMap<String,String>> deleteObservation() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList result = new ArrayList<HashMap<String,String>>();
		Cursor cursor = db
				.rawQuery(
						"SELECT firstID FROM DeleteLog WHERE tableName = 'Observation'",
						null);
		HashMap<String,String> map;
		while(cursor.moveToNext()){
			map = new HashMap<String,String>();
			map.put("observationID", cursor.getString(0));
			map.put("deviceId", deviceId);
			result.add(map);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public ArrayList<HashMap<String,String>> deleteObserContent() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList result = new ArrayList<HashMap<String,String>>();
		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM DeleteLog WHERE tableName = 'ObserContent'",
						null);
		HashMap<String,String> map;
		while(cursor.moveToNext()){
			map = new HashMap<String,String>();
			map.put("firstID", cursor.getString(cursor.getColumnIndex("firstID")) );
			map.put("secondID", cursor.getString(cursor.getColumnIndex("secondID")) );
			map.put("deviceId", deviceId);
			result.add(map);
		}
		cursor.close();
		db.close();
		return result;
	}

	public List<DeleteLog> findAllByTableName(String string) {
		// TODO Auto-generated method stub
		SQLiteDatabase  db=dbHelper.getReadableDatabase();
		String sqlString="SELECT deleteLogID,tableName,firstID,secondID,stringID FROM DeleteLog WHERE tableName="+"'" +string+"'";
		
		Cursor cursor=db.rawQuery(sqlString, null);
		List<DeleteLog> deleteLogs=new ArrayList<DeleteLog>();
		while (cursor.moveToNext()) {
			Integer deleteLogID=cursor.getInt(0);
			
			Integer firstID=cursor.getInt(2);
			Integer secondID=cursor.getInt(3);
			String stringID=cursor.getString(4);
			deleteLogs.add(new DeleteLog(deleteLogID,string,firstID,secondID,stringID));
			
		}
		cursor.close();
		db.close();
		return deleteLogs;
	}
	public void deleteByTableName(String tableName) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM DeleteLog WHERE tableName="+"'"+tableName+"'");
	    db.close();
	}

}
