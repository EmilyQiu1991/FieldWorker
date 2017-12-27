package com.example.fieldworker1;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDate extends Date {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/*
	 * additional constructors
	 */

	@Override
	public String toString() {
		return dateFormat.format(this);
	}
}
