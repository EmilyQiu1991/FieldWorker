package com.example.dao;

import java.io.Closeable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.db.DatabaseHelper;
import com.example.domain.Observation;
import com.example.domain.User;

import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.http.SslCertificate.DName;
import android.provider.SyncStateContract.Helpers;

public class ObservationDao {

	private static DatabaseHelper helper;
	private static SQLiteDatabase db;

	public ObservationDao() {
		// TODO Auto-generated constructor stub
		super();
	}

	public ObservationDao(Context context) {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public String query( User user) {
		String result = "obserName" + "\t" + "user" + "\t" + "TraitList" + "\t"
				+ "Delete Time" + "\t" + "Create Time" + "\t" +  "Photo Path" + "\t"+"\n";
		Cursor c = queryTheCursor( user );
		while (c.moveToNext()) {
			result += c.getString(c.getColumnIndex("observationName")) + "\t";
			result += c.getString(c.getColumnIndex("username")) + "\t";
			result += c.getString(c.getColumnIndex("traitListID")) + "\t";
			result += c.getString(c.getColumnIndex("endTime")) + "\t";
			result += c.getString(c.getColumnIndex("createTime")) + "\t";
			result += c.getString(c.getColumnIndex("photoPath")) + "\t" + "\n";
		}
		c.close();
		closeDB();
		return result;
	}

	public static Cursor queryTheCursor( User user) {
		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM Observation WHERE username = " + "'" + user.getUserName() + "'", null);
		return c;
	}

	public static boolean validateObserName(String name) {
		Cursor c = db.rawQuery("SELECT observationName FROM observation", null);
		while (c.moveToNext()) {
			if (name.equals(c.getString(0)))
				return true;
		}

		c.close();
		db.close();
		return false;
	}

	public static ArrayList<HashMap<String, String>> showInList( User user)
			throws ParseException {
		Cursor c = queryTheCursor( user );
		ArrayList<HashMap<String, String>> obserList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		while (c.moveToNext()) {
			map.put("observation_name", c.getString(1));
			if (!c.getString(5).equals(""))
				map.put("observation_delete", c.getString(5).substring(0, 10));
			else
				map.put("observation_delete", "");
			map.put("observation_date", c.getString(4).substring(0, 10));
			obserList.add(map);
			map = new HashMap<String, String>();
		}

		c.close();
		db.close();
		return obserList;
	}

	public void closeDB() {
		// TODO Auto-generated method stub
		db.close();
	}

	public void addObservation(Observation observation) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str ;
		if( observation.getDeleteTime() == null ){
			str = "";
		}else{
			str = sdf.format(observation.getDeleteTime());
		}

		db = helper.getWritableDatabase();
		db.execSQL(
				"INSERT INTO Observation(observationID,observationName,username,traitListID,endTime,photoPath,paintingPath,comment)"
						+ " VALUES(?,?,?,?,?,?,?,?)",
				new Object[] { observation.getObservationID(),
						observation.getObservationName(),
						observation.getUsername(),
						observation.getTraitListID(),
						str,
						observation.getPhotoPath(),
						observation.getPaintingPath(),
						observation.getComment()});
		closeDB();
	}

	public int findIdByName(String name) {
		int id;
		db = helper.getWritableDatabase();
		String sqlString = "SELECT observationID FROM Observation WHERE observationName="
				+ "'" + name + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		cursor.moveToNext();
		id = cursor.getInt(0);

		cursor.close();
		db.close();
		return id;
	}

	public Observation findObervationById(Integer id) {
		Observation observation = null;
		db = helper.getWritableDatabase();
		String sqlString = "SELECT * FROM Observation WHERE observationID="
				+ "'" + id + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		cursor.moveToNext();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date, endDate;
		try {
			date = format1.parse(cursor.getString(4));
			if (cursor.getString(5).equals(""))
				endDate = null;
			else{
				endDate = format2.parse(cursor.getString(5));
			}
				
			observation = new Observation(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2), cursor.getInt(3),
					date, endDate,cursor.getString(6), cursor.getString(7), cursor.getString(8));
			db.close();
			return observation;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		cursor.close();
		db.close();
		return observation;
	}
	//modified on 05/04/2015
	public List<String> searchObservationsWithTraitList( Integer traitListID ,String username){
		ArrayList<String> res = new ArrayList<String>();
		db = helper.getWritableDatabase();
		String sqlString = "SELECT observationName FROM Observation WHERE traitListID="
				+ traitListID+" AND username="+"'"+username+"'";
		Cursor cursor = db.rawQuery(sqlString, null);
		while(cursor.moveToNext()){
			res.add(cursor.getString(0));
		}

		cursor.close();
		db.close();
		return res;
	}
   public List<Integer> searchObservationsWithTraitList1(Integer traitListID)
   {
	   ArrayList<Integer> res = new ArrayList<Integer>();
		db = helper.getWritableDatabase();
		String sqlString = "SELECT observationID FROM Observation WHERE traitListID="
				+ traitListID;
		Cursor cursor = db.rawQuery(sqlString, null);
		while(cursor.moveToNext()){
			res.add(cursor.getInt(0));
		}

		cursor.close();
		db.close();
		return res;
   }
	public void deleteById(Integer id) {
		db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM Observation WHERE observationID = " + id);
		db.close();
	}

	public void deleteByName(String name) {
		db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM Observation WHERE observationName = '" + name
				+ "'");
		db.close();
	}

	public void updateObservationEndTime(Date deadline, Integer id) {
		db = helper.getWritableDatabase();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(deadline);
		db.execSQL("UPDATE Observation SET endTime = '" + dateStr
				+ "' WHERE observationID = " + id);
		closeDB();
	}

	public void updateObservationName(String name, Integer id) {
		db = helper.getWritableDatabase();
		db.execSQL("UPDATE Observation SET observationName = '" + name
				+ "' WHERE observationID = " + id);
		closeDB();
	}
	public void updateObservationPhotoPath( String path, Integer id ){
		db = helper.getWritableDatabase();
		db.execSQL("UPDATE Observation SET photoPath = '" + path +"' WHERE observationID = " + id);
		closeDB();
	}
	
	public void updateObservationPaintingPath( String path, Integer id ){
		db = helper.getWritableDatabase();
		db.execSQL("UPDATE Observation SET paintingPath = '" + path +"' WHERE observationID = " + id);
		closeDB();
	}
	
	public void updateObservationComment( String comment, Integer id ){
		db = helper.getWritableDatabase();
		db.execSQL("UPDATE Observation SET comment = '" + comment +"' WHERE observationID = " + id);
		closeDB();
	}
	 public HashMap<Date, Double> chartXY(String observationName,int traitID) throws ParseException
	   {
		   HashMap<Date, Double> xy=new HashMap<Date, Double>();
		   int id=findIdByName(observationName);
		   SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		   String sqlString="SELECT Observation.createTime, ObserContent.traitValue"+
		                    " FROM Observation, ObserContent"+
				            " WHERE Observation.observationID="+"'"+id+"'"+
		                    " AND Observation.observationID=ObserContent.observationID"+
				            " AND ObserContent.traitID="+"'"+traitID+"'";
		   Cursor cursor=db.rawQuery(sqlString, null);
		   
		   while (cursor.moveToNext()) {
			Date x=format1.parse(cursor.getString(0));
			double y=Double.parseDouble(cursor.getString(1));
			xy.put(x, y);		
		   }

			cursor.close();
			db.close();
		   return xy;
	   }
	 public Date getDateByObserId(Integer id) throws ParseException
	 {
		 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		 String sqlString="SELECT createTime FROM Observation WHERE observationID="+"'"+id+"'";
		 Cursor cursor=db.rawQuery(sqlString, null);
		 Date xDate = null;
		 while (cursor.moveToNext()) {
			 xDate=format1.parse(cursor.getString(0));
			// System.out.println(xDate+"^^^");
			
		}
		 return xDate;
	 }
	 public double getTraitValue(int traitID,Integer observationID)
	 {
	
		 String sqlString="SELECT traitValue FROM ObserContent WHERE observationID="+"'"+observationID+"'"
				          +" AND traitID="+"'"+traitID+"'";
		 Cursor cursor=db.rawQuery(sqlString, null);
		 double y = 0;
		 while (cursor.moveToNext()) {
			y=cursor.getDouble(0);
		}
		 return y;
	 }
}
