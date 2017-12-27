package com.example.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 33;
	static String name = "FWO";

	public DatabaseHelper(Context context, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context) {
		this(context, VERSION);
	}

	public DatabaseHelper(Context context, int version) {
		this(context, null, version);
	}

	@Override
	// only used one time when creating database
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a Database");
		db.execSQL("create table ObserContent(relation_id INTEGER PRIMARY KEY,observationID INTEGER,traitID INTEGER,traitValue VARCHAR(225),editable INTEGER)");
		db.execSQL("create table Observation(observationID INTEGER PRIMARY KEY, observationName VARCHAR(225) UNIQUE,username VARCHAR(45), traitListID INTEGER, createTime DATETIME NOT NULL default current_timestamp,endTime VARCHAR(45) ,photoPath VARCHAR(225), paintingPath VARCHAR(225), comment VARCHAR(225) )");
		db.execSQL("create table PredefineVal(predefineValID INTEGER PRIMARY KEY,traitID INTEGER, value VARCHAR(128))");
		db.execSQL("create table Trait(traitID INTEGER PRIMARY KEY,traitName VARCHAR(128),widgetName VARCHAR(128),username VARCHAR(45),unit VARCHAR(45),accessible INTEGER DEFAULT 1,nameVersion INTEGER DEFAULT 0)");
		db.execSQL("create table TraitList(traitListID INTEGER PRIMARY KEY,traitListName VARCHAR(128),username VARCHAR(45),accessible INTEGER DEFAULT 1, nameVersion INTEGER DEFAULT 0)");
		db.execSQL("create table TraitListContent(traitListID INTEGER,traitID INTEGER, PRIMARY KEY(traitListID,traitID))");
		db.execSQL("create table User(username VARCHAR(45),password VARCHAR(45), PRIMARY KEY(username))");
		db.execSQL("create table AddLog(addLogID INTEGER, tableName VARCHAR(45), firstID INTEGER, secondID INTEGER, stringID VARCHAR(45), PRIMARY KEY(addLogID))");
		db.execSQL("create table DeleteLog(deleteLogID INTEGER, tableName VARCHAR(45), firstID INTEGER, secondID INTEGER, stringID VARCHAR(45), PRIMARY KEY(deleteLogID))");
	}

	@SuppressLint("NewApi")
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		// db.execSQL("DROP TABLE IF EXISTS ObserContent");
		// db.execSQL("DROP TABLE IF EXISTS Observation");
		// db.execSQL("DROP TABLE IF EXISTS PredefineVal");
		// db.execSQL("DROP TABLE IF EXISTS Trait");
		//db.execSQL("DROP TABLE IF EXISTS Observation");
		// db.execSQL("DROP TABLE IF EXISTS TraitListContent");
		// db.execSQL("DROP TABLE IF EXISTS User");
		// db.execSQL("DROP TABLE IF EXISTS addLog");
		// db.execSQL("DROP TABLE IF EXISTS deleteLog");
		//onCreate(db);
		// db.execSQL("ALTER TABLE TraitList ADD COLUMN nameVersion INTEGER DEFAULT 1");
		// db.execSQL("ALTER TABLE TraitList ADD COLUMN accessible INTEGER DEFAULT 1");
		// db.execSQL("ALTER TABLE Trait ADD COLUMN accessible BLOB");
		 //db.execSQL("ALTER TABLE Observation ADD COLUMN relation_id INTEGER");
		// db.execSQL("ALTER TABLE ObserContent ADD COLUMN editable BLOB");
		// db.execSQL("ALTER TABLE Trait RENAME TO Trait1");
		// db.execSQL("create table Trait(traitID INTEGER PRIMARY KEY,traitName VARCHAR(128),widgetName VARCHAR(128),unit VARCHAR(45),accessible INTEGER DEFAULT 1)");
		// db.execSQL("insert into Trait(traitID,traitName,widgetName,unit) select traitID,traitName,widgetName,unit from Trait1");
		// db.execSQL("ALTER TABLE ObserContent RENAME TO ObserContent1");
		// db.execSQL("create table ObserContent(observationID INTEGER,traitID INTEGER,traitValue VARCHAR(225),editable INTEGER DEFAULT 1,PRIMARY KEY(observationID,traitID))");
		// db.execSQL("insert into ObserContent(observationID,traitID,traitValue) select observationID,traitID,traitValue from ObserContent1");
		// db.execSQL("DROP TABLE Trait1");
		// db.execSQL("DROP TABLE ObserContent1");
		// db.execSQL("DROP TABLE TraitList1");
	}

}
