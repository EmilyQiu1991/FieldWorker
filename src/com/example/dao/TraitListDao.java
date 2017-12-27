package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.domain.Trait;
import com.example.domain.TraitList;

public class TraitListDao {
	private DatabaseHelper dbHelper;

	public TraitListDao(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void insert(TraitList traitList) {
		int size=findByName(traitList.getTraitListName(), traitList.getUsername()).size();
		if (size>0) {
              traitList.setNameVersion(size);
		}
		else
		{
			traitList.setNameVersion(0);
		}
		if (findNameById(traitList.getTraitListID()) == null) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(
					"INSERT INTO TraitList(traitListID,traitListName,username,nameVersion,accessible) VALUES(?,?,?,?,?)",
					new Object[] { traitList.getTraitListID(),
							traitList.getTraitListName(),
							traitList.getUsername(),
							traitList.getNameVersion(),
							traitList.getAccessible()});
			db.close();
		}
	}

	public void insertDownload(TraitList traitList) {
		
		if (findNameById(traitList.getTraitListID()) == null) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(
					"INSERT INTO TraitList(traitListID,traitListName,username,nameVersion) VALUES(?,?,?,?)",
					new Object[] { traitList.getTraitListID(),
							traitList.getTraitListName(),
							traitList.getUsername(),
							traitList.getNameVersion()});
			db.close();
		}
	}

	public List<TraitList> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"SELECT traitListID,traitListName,username FROM TraitList where accessible=1",
						null);
		List<TraitList> traitLists = new ArrayList<TraitList>();
		while (cursor.moveToNext()) {
			Integer traitListID = cursor.getInt(0);
			String traitListName = cursor.getString(1);
			String usernameString = cursor.getString(2);
			traitLists.add(new TraitList(traitListID, traitListName,
					usernameString));

		}
		cursor.close();
		db.close();
		return traitLists;
	}

	public List<TraitList> findByUsername(String username) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"SELECT traitListID,traitListName,username,nameVersion FROM TraitList where username=? and accessible=1",
						new String[] { username });
		List<TraitList> traitLists = new ArrayList<TraitList>();
		while (cursor.moveToNext()) {
			Integer traitListID = cursor.getInt(0);
			String traitListName = cursor.getString(1);
			String usernameString = cursor.getString(2);
			int nameVersion=cursor.getInt(3);
			traitLists.add(new TraitList(traitListID, traitListName,
					usernameString,1,nameVersion));

		}
		cursor.close();
		db.close();
		return traitLists;
	}

//	public Integer findIdByName(String name) {
//		int id;
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		String sqlString = "SELECT traitListID FROM TraitList WHERE traitListName="
//				+ "'" + name + "'";
//		Cursor cursor = db.rawQuery(sqlString, null);
//
//		if (cursor.moveToNext()) {
//			id = cursor.getInt(0);
//			db.close();
//			cursor.close();
//			return id;
//		} else {
//			db.close();
//			cursor.close();
//			return null;
//		}
//
//	}
    public List<TraitList> findByName(String traitListName,String username)
    {
    	List<TraitList> result=new ArrayList<TraitList>();
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitListID,traitListName,username,accessible,nameVersion FROM TraitList WHERE traitListName=?"
				          +" AND accessible=1 AND username=?";
		Cursor cursor = db.rawQuery(sqlString,new String[]{traitListName,username} );
		while(cursor.moveToNext())
		{		
		Integer id = cursor.getInt(0);
		String username1 = cursor.getString(2);
		Integer accessible = cursor.getInt(3);
		Integer nameVersion=cursor.getInt(4);
		result.add(new TraitList(id, traitListName, username1, accessible,nameVersion));
		}
		cursor.close();
		db.close();
		
		return result;
    }
	public void delete(TraitList t,boolean isUsed) {
		
		String sqlString="";
		if (isUsed) 
			sqlString = "UPDATE TraitList SET accessible=0 WHERE traitListID="
					+ "'" + t.getTraitListID() + "'";		
		else
		    sqlString = "delete from TraitList WHERE traitListID="
				+ "'" + t.getTraitListID() + "'";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sqlString);
		db.close();
	}

	public String findNameById(Integer id) {
		String name;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitListName FROM TraitList WHERE traitListID="
				+ id;
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToNext()) {
			name = cursor.getString(0);
			cursor.close();
			db.close();
			
			return name;
		} else
		{
			cursor.close();
			db.close();
		
		}
		return null;
	}

	public boolean checkTraitListName(String traitListName, String username) {
		System.out.println(traitListName);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM TraitList WHERE traitListName=" + "'"
				+ traitListName + "' AND username=" + "'" + username + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		
		if (count == 0)
			return true;
		else
			return false;
	}

	public TraitList findById(Integer id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitListID,traitListName,username,accessible,nameVersion FROM TraitList WHERE traitListID="
				+ id;
		Cursor cursor = db.rawQuery(sqlString, null);
		cursor.moveToNext();
		String name = cursor.getString(1);
		String username = cursor.getString(2);
		Integer accessible = cursor.getInt(3);
		Integer nameVersion=cursor.getInt(4);
		TraitList tList = new TraitList(id, name, username, accessible,nameVersion);
		cursor.close();
		db.close();
		
		return tList;
	}
}
