package com.example.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.db.*;
import com.example.domain.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class UserDao {

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public UserDao(Context context) {
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}
	public String findPassword(String username) {
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			if (username.equals(c.getString(0))) {
				String password = "";
				password += c.getString(1);
				c.close();
				return password;
			}

		}
		c.close();
		closeDB();
		return "";
	}
	public void add(User u) {
		db.beginTransaction();
		try {
			db.execSQL("INSERT INTO User" + 
					 " VALUES('" + u.getUserName() + "','" + u.getPassword()
					+ "');");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public boolean validateUsernmae( String un){
		Cursor c =  db.rawQuery("SELECT * FROM User", null);
		while( c.moveToNext() ){
			if( un.equals(c.getString(0))){
				c.close();
				closeDB();
				return true;
			}
				
		}
		c.close();
		closeDB();
		return false;
	}
	
	public String query() {
		String result = "Username" + "\t" + "Password" + "\t" + "\n";
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			result += c.getString(c.getColumnIndex("username")) + "\t";
			result += c.getString(c.getColumnIndex("password")) + "\t" + "\n";
		}
		c.close();
		closeDB();
		return result;
	}

	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM User", null);
		return c;
	}

	
	// -1: no such username 
	// 0: wrong password
	// 1: successfully login
	public int check( User user ) {
		if (!db.isOpen()) {
			System.out.println("!db.isOpen()");
			db=helper.getWritableDatabase();
		}
		int result = -1;
		Cursor c = queryTheCursor();
		User u;
		while (c.moveToNext()) {
			u = new User(c.getString(c.getColumnIndex("username")),
					c.getString(c.getColumnIndex("password")) );
			if( u.getUserName().equals(user.getUserName()) ){
				result = 0;
				if( u.getPassword().equals(user.getPassword()))
					result = 1;
			}
		}
		c.close();
		return result;
	}
	
	public void resetPassword( String username, String password ){
		db.beginTransaction();
		try {
			db.execSQL("UPDATE User SET password = '" + password +
					 "' WHERE username = '" + username + "';");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
public ArrayList<HashMap<String,String>> userList(){
		
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("username", c.getString(c.getColumnIndex("username")));
			map.put("password", c.getString(c.getColumnIndex("password")));
			list.add(map);
		}
		c.close();
		return list;
	}
	public void closeDB() {
		// TODO Auto-generated method stub
		db.close();
	}
}
