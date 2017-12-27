package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.domain.PredefineValue;
import com.example.domain.Trait;
import com.example.fieldworker1.ListViewSubClass;

public class PredefineValueDao {
	private DatabaseHelper dbHelper;

	public PredefineValueDao(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public List<PredefineValue> search(int traitId) {
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sqlString = "SELECT * FROM PredefineVal WHERE traitID =" + "'"
				+ traitId + "'";
		List<PredefineValue> values = new ArrayList<PredefineValue>();
		Cursor cursor = sdb.rawQuery(sqlString, null);
		while (cursor.moveToNext()) {
			Integer predefineValID = cursor.getInt(0);
			Integer traitID = cursor.getInt(1);
			String value = cursor.getString(2);

			values.add(new PredefineValue(predefineValID, traitID, value));

		}
		cursor.close();
		sdb.close();
		return values;
	}

	// return the string array including all predefineValues
	public String[] search1(Integer traitId) {
		List<PredefineValue> values = new ArrayList<PredefineValue>();
		values = search(traitId);
		String[] valueStrings;
		valueStrings = new String[values.size()];
		for (int i = 0; i < valueStrings.length; i++) {
			valueStrings[i] = values.get(i).getValue();
		}
		return valueStrings;
	}

	public void insert(PredefineValue pValue)
	{
		if(findById(pValue.getPredefineValueID())==null)
		{
		SQLiteDatabase sdb=dbHelper.getWritableDatabase();
		String sqlString="INSERT INTO PredefineVal (predefineValID, traitID, value) values (?,?,?)";
		Object obj[]={pValue.getPredefineValueID(),pValue.getTraitID(),pValue.getValue()};
		sdb.execSQL(sqlString, obj);
		}
	}

	public PredefineValue findById(Integer id) {
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM PredefineVal WHERE predefineValID=" + id;
		Cursor cursor = sdb.rawQuery(sql, null);
		if (!cursor.moveToNext()) {
			cursor.close();
			sdb.close();
			return null;
		} else {
			Integer predefineValID = cursor.getInt(0);
			Integer traitID = cursor.getInt(1);
			String value = cursor.getString(2);
			cursor.close();
			sdb.close();
			return new PredefineValue(predefineValID, traitID, value);

		}

	}

}
