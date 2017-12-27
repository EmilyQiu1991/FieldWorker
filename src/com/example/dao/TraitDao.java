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

import eu.inmite.android.lib.validations.form.annotations.Custom;

public class TraitDao {
	private DatabaseHelper dbHelper;

	public TraitDao(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void insert(Trait t,String username) {
		//search by traitid, if finding nothing by id, it can be added to Trait Table
		if (findNameById(t.getTraitID())==null) {
        int size=findByName(t.getTraitName(),username).size();
        if (size>0) {
            t.setNameVersion(size);
		}
		else
		{
			t.setNameVersion(0);
		}		
		
		SQLiteDatabase sdb = dbHelper.getWritableDatabase();

		String sqlString = "INSERT INTO Trait(traitID,traitName,widgetName,unit,username,accessible,nameVersion) values (?,?,?,?,?,?,?) ";
		Object obj[] = { t.getTraitID(), t.getTraitName(), t.getWidgetName(),
				t.getUnit(),username,t.getAccessible(),t.getNameVersion() };
		sdb.execSQL(sqlString, obj);
		sdb.close();
		}
	}

	private List<Trait> findByName(String traitName, String username) {
		
		List<Trait> result=new ArrayList<Trait>();
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitID,traitName,widgetName,unit,username,accessible,nameVersion FROM Trait WHERE traitName=?"
				          +" AND accessible=1 AND username=?";
		Cursor cursor = db.rawQuery(sqlString,new String[]{traitName,username} );
		while(cursor.moveToNext())
		{		
		Integer id = cursor.getInt(0);
		String widget=cursor.getString(2);
		String unit=cursor.getString(3);
		String username1 = cursor.getString(4);
		Integer accessible = cursor.getInt(5);
		Integer nameVersion=cursor.getInt(6);
		result.add(new Trait(id, traitName, widget, unit, username1, nameVersion));
		}
		cursor.close();
		db.close();
		
		return result;
	}

	public List<String> findAllTraitNames(String username) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT traitName FROM Trait WHERE accessible=1 AND username="+"'"+username+"'", null);
		List<String> traitNames = new ArrayList<String>();
		while (cursor.moveToNext()) {

			String traitName = cursor.getString(0);

			traitNames.add(traitName);

		}
		cursor.close();
		db.close();
		return traitNames;
	}

	public Integer findIdbyName(String name) {
		Integer id;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("SELECT traitID FROM Trait WHERE traitName=" + "'"
						+ name + "'", null);
		cursor.moveToNext();
		id=cursor.getInt(0);
		cursor.close();
		db.close();
		return id;
	}

	public ArrayList<Trait> findAllForOne(String username) {
		// TODO Auto-generated method stub
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM Trait where accessible=1 and username="+"'"+username+"'";
		Cursor cursor = sdb.rawQuery(sqlString, null);
		ArrayList<Trait> traits = new ArrayList<Trait>();
		while (cursor.moveToNext()) {
			Integer traitID = cursor.getInt(0);
			String traitName = cursor.getString(1);
			String widgetName = cursor.getString(2);			
			String unit = cursor.getString(3);
			String username1=cursor.getString(4);
			int nameVersion=cursor.getInt(6);
			traits.add(new Trait(traitID, traitName, widgetName, unit,username1,nameVersion));

		}
		cursor.close();
		sdb.close();
		return traits;
	}

	public Trait searchByTraitName(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM Trait"
				+ " WHERE traitName=" + "'" + name + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		cursor.moveToNext();
		Integer traitID = cursor.getInt(0);
		String traitName = cursor.getString(1);
		String widgetName = cursor.getString(2);
		String unit = cursor.getString(3);
		String username=cursor.getString(4);
		int nameVersion=cursor.getInt(6);
		cursor.close();
		db.close();
		return new Trait(traitID, traitName, widgetName, unit,username,nameVersion);
	}

	/*public int findIdByName(String name) {
		int id;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitID FROM Trait WHERE traitName=" + "'"
				+ name + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToNext()) {
			id = cursor.getInt(0);
			return id;
		}
		cursor.close();
		db.close();
		return 0;

	}*/

	public String findNameById(Integer id) {
		String name;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitName FROM Trait WHERE traitID = " + id;
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToNext()) {
			name = cursor.getString(0);
			cursor.close();
			db.close();
			return name;
		} else {
			cursor.close();
			db.close();
			return null;
		}
	}

	public void delete(Trait t,String username) {
		
		String sqlString="";
		if(isUsed(t.getTraitID(), username))
		
		sqlString = "UPDATE Trait SET accessible=0 WHERE traitID="
				+ "'" + t.getTraitID() + "'";
		else 
			sqlString="DELETE FROM Trait WHERE traitID='"+t.getTraitID()+"'";

		String sqlString2="DELETE FROM PredefineVal WHERE traitID="+"'"+t.getTraitID()+"'";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sqlString);
	    db.execSQL(sqlString2);
		db.close();
	}

	
	public boolean checkTraitName(String traitName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM Trait WHERE traitName=" + "'"
				+ traitName + "'";
		Cursor cursor = db.rawQuery(sqlString, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		if (count == 0)
			return true;
		else
			return false;

	}
   public boolean isUsed(int traitId,String username)
   {
	   SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM TraitList, TraitListContent WHERE TraitList.username=" + "'"
				+ username + "' AND TraitList.traitListID=TraitListContent.traitListID AND TraitListContent.traitID="+"'"+traitId+"'";
		Cursor cursor = db.rawQuery(sqlString, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		if (count == 0)
			return false;
		else
			return true;
   }
   public int getTraitId(String traitName,String username)
   {
	   int result=0;
	   SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT traitID FROM Trait WHERE traitName=" + "'"
				+ traitName + "' AND username="+"'"+username+"'";
		Cursor cursor = db.rawQuery(sqlString, null);
		while(cursor.moveToNext())
		  result=cursor.getInt(0);
		cursor.close();
		db.close();
		return result;
   }
   public Trait findById(Integer traitID)
   {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM Trait WHERE traitID = " + traitID;
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToNext()) {
			Integer traitID1 = cursor.getInt(0);
			String traitName = cursor.getString(1);
			String widgetName = cursor.getString(2);
			String unit = cursor.getString(3);
			String username=cursor.getString(4);
			int accessible=cursor.getInt(5);
			int nameVersion=cursor.getInt(6);
			cursor.close();
			db.close();
			return new Trait(traitID1, traitName, widgetName, unit,accessible,username,nameVersion);
		} else {
			cursor.close();
			db.close();
			return null;
		}
   }
}
