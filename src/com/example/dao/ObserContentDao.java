package com.example.dao;

import java.util.HashMap;

import com.example.db.DatabaseHelper;
import com.example.domain.ObserContent;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ObserContentDao {

	private DatabaseHelper helper;
	private static SQLiteDatabase db;

	public ObserContentDao() {
		// TODO Auto-generated constructor stub
		super();
	}

	public ObserContentDao(Context context) {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public String query() {
		String result = "observationID" + "\t" + "traitID" + "\t"
				+ "traitValue" + "\n";
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			result += c.getString(c.getColumnIndex("observationID")) + "\t";
			result += c.getString(c.getColumnIndex("traitID")) + "\t";
			result += c.getString(c.getColumnIndex("traitValue")) + "\t";
			result += c.getInt(c.getColumnIndex("editable")) + "\t" + "\n";
		}
		c.close();
		closeDB();
		return result;
	}

	public static Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM ObserContent", null);
		return c;
	}

	public void closeDB() {
		// TODO Auto-generated method stub
		db.close();
	}

	public void addObserContent(ObserContent obserContent) {
		db = helper.getWritableDatabase();
		db.execSQL(
				"INSERT INTO ObserContent(relation_id,observationID,traitID,traitValue,editable)"
						+ " VALUES(?,?,?,?,?)",
				new Object[] { obserContent.getRelation_id(),obserContent.getObservationID(),
						obserContent.getTraitID(),
						obserContent.getTraitValue(),
						obserContent.getEditable() });
		closeDB();
	}

	public HashMap<Integer, String> findTraitValueById(Integer id) {
		db = helper.getWritableDatabase();
		String sqlString = "SELECT * FROM ObserContent WHERE observationID="
				+ id;
		Cursor cursor = db.rawQuery(sqlString, null);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		while (cursor.moveToNext()) {
			map.put(cursor.getInt(2), cursor.getString(3));
		}

		cursor.close();
		closeDB();
		return map;
	}

	public HashMap<Integer, String> findTraitEditableById(Integer id) {
		db = helper.getWritableDatabase();
		String sqlString = "SELECT * FROM ObserContent WHERE observationID="
				+ id;
		Cursor cursor = db.rawQuery(sqlString, null);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		while (cursor.moveToNext()) {
			map.put(cursor.getInt(2), String.valueOf(cursor.getInt(4)));
		}

		cursor.close();
		closeDB();
		return map;
	}

	public void updateTraitValueById(String traitValue, Integer observationID,
			Integer traitID) {
		db = helper.getWritableDatabase();
		db.execSQL("UPDATE ObserContent SET traitValue = '" + traitValue
				+ "' WHERE observationID = " + observationID
				+ " AND traitID = " + traitID);
		closeDB();
	}

	public void deleteById(Integer id) {
		db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM ObserContent WHERE observationID = " + id);
		closeDB();
	}

	public ObserContent findObserContentByMultiID(int observationID, int traitID) {
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM ObserContent ", null);
		while (cursor.moveToNext()) {
			if (cursor.getInt(1) == observationID
					&& cursor.getInt(2) == traitID)
				return new ObserContent(cursor.getInt(0), cursor.getInt(1),cursor.getInt(2),
						cursor.getString(3), cursor.getInt(4));
		}
		cursor.close();
		closeDB();
		return null;
	}
}
