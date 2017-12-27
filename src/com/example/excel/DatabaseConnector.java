package com.example.excel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;

public class DatabaseConnector {

	private static DatabaseHelper helper;
	private static SQLiteDatabase db;

	public DatabaseConnector() {
		// TODO Auto-generated constructor stub
		super();
	}

	public DatabaseConnector(Context context) {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public SQLiteDatabase getDB() {
		return db;
	}

}
