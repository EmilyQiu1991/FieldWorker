package com.example.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.domain.Trait;
import com.example.domain.TraitListContent;

import eu.inmite.android.lib.validations.form.annotations.Custom;

public class TraitListContentDao {
	private DatabaseHelper dbHelper; 
	public TraitListContentDao(Context context)
	{
		dbHelper=new DatabaseHelper(context);
	}
	public void insert(TraitListContent traitListContent)
	{
		if (!search(traitListContent)) {
			
		
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		
		String sqlString="insert into TraitListContent(traitListID,traitID) values(?,?)";
		Object obj[]={traitListContent.getTraitListID(),traitListContent.getTraitID()};
		sdb.execSQL(sqlString, obj);
		}
	}
	//search the trait list content according to the traitListID
	public List<Trait> searchTraitsByTraitListID(Integer traitListID)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlString="SELECT Trait.traitID, Trait.traitName, Trait.widgetName, Trait.unit,Trait.username, Trait.nameVersion"+
		                 " FROM TraitListContent,Trait"+
				         " WHERE TraitListContent.traitListID="+"'"+traitListID+"'"+" AND TraitListContent.traitID=Trait.traitID";
		List<Trait> traits=new ArrayList<Trait>();
		Cursor cursor=sdb.rawQuery(sqlString, null);
		System.out.println(cursor.getCount());
		while (cursor.moveToNext()) {
			Integer traitID=cursor.getInt(0);
			String traitName=cursor.getString(1);
			String widgetString=cursor.getString(2);
			String unit=cursor.getString(3);
			String username=cursor.getString(4);
			int nameVersion=cursor.getInt(5);
			traits.add(new Trait(traitID, traitName, widgetString, unit, username, nameVersion));
			
		}
		cursor.close();
		sdb.close();
		System.out.println(traits.size());
		return traits;
	}
	public List<TraitListContent> getTraitContents(Integer traitListID)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlString="SELECT * FROM TraitListContent WHERE traitListID="+"'"+traitListID+"'";
		List<TraitListContent> traits=new ArrayList<TraitListContent>();
		Cursor cursor=sdb.rawQuery(sqlString, null);
		System.out.println(cursor.getCount());
		while (cursor.moveToNext()) {
			Integer traitListID1=cursor.getInt(0);
			Integer traitID=cursor.getInt(1);
			traits.add(new TraitListContent(traitListID1,traitID));
			
		}
		cursor.close();
		sdb.close();
		return traits;
	}
	public List<String> searchTraitNames(Integer traitListID) {
		List<Trait> traits=new ArrayList<Trait>();
		List<String> traitNames=new ArrayList<String>();
		traits=searchTraitsByTraitListID(traitListID);
		for (Trait t:traits) {
			traitNames.add(t.getTraitName());
		}
		return traitNames;
	}
	public void delete(int traitListId, int traitId)
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		String sqlString="DELETE FROM TraitListContent WHERE traitListID="+"'"+traitListId+"'" +" AND traitID="+"'"+traitId+"'" ;
		db.execSQL(sqlString);
		db.close();
	}
	public boolean search(TraitListContent content)
	{
		SQLiteDatabase db=dbHelper.getReadableDatabase();
		String sql="SELECT * FROM TraitListContent WHERE traitListID="+content.getTraitListID()+" AND traitID="+content.getTraitID();
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			cursor.close();
			db.close();
			return true;//if exist ,return true
		}
		cursor.close();
		db.close();
		return false;
		
	}
	public void deleteTraitListContent(int traitListId)
	{
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		String sqlString="DELETE FROM TraitListContent WHERE traitListID="+"'"+traitListId+"'";
		db.execSQL(sqlString);
		db.close();
	}
	
}
