package com.example.dao;

import java.text.SimpleDateFormat;
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
import com.example.domain.AddLog;
import com.example.domain.ObserContent;
import com.example.domain.Observation;

public class AddLogDao {
	private DatabaseHelper dbHelper; 
	private UserDao userDao;
	private ObservationDao observationDao;
	private ObserContentDao obserContentDao;
	private String deviceId;
	public AddLogDao(Context context)
	{
		dbHelper=new DatabaseHelper(context);
		userDao = new UserDao(context);
		observationDao = new ObservationDao(context);
		obserContentDao = new ObserContentDao(context);
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    setDeviceId(deviceUuid.toString());

	}
	public void insert(AddLog addLog)
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.execSQL("INSERT INTO AddLog(addLogID,tableName,firstID,secondID,stringID) VALUES(?,?,?,?,?)", new Object[]{addLog.getAddLogID(),addLog.getTableName(),addLog.getFirstID(),addLog.getSecondID(),addLog.getStringID()});
	    db.close();
	}
	public List<AddLog> findAll()
	{
		SQLiteDatabase  db=dbHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("SELECT addLogID,tableName,firstID,secondID,stringID FROM AddLog", null);
		List<AddLog> AddLogs=new ArrayList<AddLog>();
		while (cursor.moveToNext()) {
			Integer addLogID=cursor.getInt(0);
			String tableName=cursor.getString(1);
			Integer firstID=cursor.getInt(2);
			Integer secondID=cursor.getInt(3);
			String stringID=cursor.getString(4);
			AddLogs.add(new AddLog(addLogID,tableName,firstID,secondID,stringID));
			
		}
		cursor.close();
		db.close();
		return AddLogs;
	} 
	public void clearTable()
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM AddLog where tableName='Observation' or tableName='ObserContent' or tableName='User'");
	    db.close();
	}
	public List<AddLog> findAllByTableName(String tableName)
	{
		SQLiteDatabase  db=dbHelper.getReadableDatabase();
		String sqlString="SELECT addLogID,tableName,firstID,secondID,stringID FROM AddLog WHERE tableName="+"'" +tableName+"'";
		
		Cursor cursor=db.rawQuery(sqlString, null);
		List<AddLog> AddLogs=new ArrayList<AddLog>();
		while (cursor.moveToNext()) {
			Integer addLogID=cursor.getInt(0);
			
			Integer firstID=cursor.getInt(2);
			Integer secondID=cursor.getInt(3);
			String stringID=cursor.getString(4);
			AddLogs.add(new AddLog(addLogID,tableName,firstID,secondID,stringID));
			
		}
		cursor.close();
		db.close();
		return AddLogs;
	}
	
	public void deleteByTableName(String tableName)
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM AddLog WHERE tableName="+"'"+tableName+"'");
	    db.close();
	}
	public boolean checkExist(AddLog targetLog) {
		int location1, location2;
		if (targetLog.getFirstID() != null) {
			if (targetLog.getSecondID() != null) {
				location1 = 2;
				location2 = 3;
			} else {
				location1 = 2;
				location2 = 0;
			}
		} else {
			location1 = 4;
			location2 = 0;
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM AddLog WHERE tableName = " + "'"
				+ targetLog.getTableName() + "'", null);
		if (location2 == 0) {
			while (c.moveToNext()) {
				if (location1 == 2) {
					if (c.getInt(location1) == targetLog.getFirstID())
						c.close();
				        db.close();
						return true;
				} else {
					if (c.getString(4).equals(targetLog.getStringID()))
						c.close();
			             db.close();
						return true;
				}
			}
		} else {
			while (c.moveToNext()) {
				if (c.getInt(2) == targetLog.getFirstID()
						&& c.getInt(3) == targetLog.getSecondID())
					c.close();
		        db.close();
					return true;
			}
		}
		c.close();
        db.close();
		return false;
	}

	

	public ArrayList<HashMap<String, String>> addUser() {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.rawQuery(
				"SELECT stringID FROM AddLog WHERE tableName = 'User'", null);
		HashMap<String, String> map;
		while (c.moveToNext()) {
			map = new HashMap<String, String>();
			map.put("username", c.getString(0));
			map.put("deviceId", deviceId);
			map.put("password", userDao.findPassword(c.getString(0)));
			list.add(map);
		}
		c.close();
		db.close();
		return list;
	}

	public ArrayList<HashMap<String, String>> addObservation() {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor c = db.rawQuery(
				"SELECT firstID FROM AddLog WHERE tableName = 'Observation'",
				null);
		HashMap<String, String> map;
		Observation observation;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		while (c.moveToNext()) {
			map = new HashMap<String, String>();
			observation = observationDao.findObervationById(c.getInt(0));
			map.put("observationID",
					String.valueOf(observation.getObservationID()) );
			map.put("deviceId", deviceId);
			map.put("observationName", observation.getObservationName());
			map.put("username", observation.getUsername());
			map.put("traitListID", String.valueOf(observation.getTraitListID()));
			map.put("createTime", sdf.format(observation.getCreateTime()));
			if (observation.getDeleteTime() != null)
				map.put("endTime", sdf.format(observation.getDeleteTime()));
			else {
				map.put("endTime", "");
			}
			map.put("photoPath", observation.getPhotoPath());
			map.put("paintingPath", observation.getPaintingPath());
			map.put("comment", observation.getComment());
			list.add(map);
		}
		c.close();
		db.close();
		return list;
	}

	public ArrayList<HashMap<String, String>> addObserContent() {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.rawQuery(
				"SELECT * FROM AddLog WHERE tableName = 'ObserContent'", null);
		HashMap<String, String> map;
		ObserContent obserContent;
		while (c.moveToNext()) {
			map = new HashMap<String, String>();
			obserContent = obserContentDao.findObserContentByMultiID(
					c.getInt(2), c.getInt(3));
			map.put("relation_id", String.valueOf(obserContent.getRelation_id()));
			map.put("observationID",
					String.valueOf(obserContent.getObservationID()));
			map.put("traitID", String.valueOf(obserContent.getTraitID()));
			map.put("deviceId", deviceId);
			map.put("traitValue", obserContent.getTraitValue());
			map.put("editable", String.valueOf(obserContent.getEditable()));
			list.add(map);
		}
		c.close();
		db.close();
		return list;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	

}
