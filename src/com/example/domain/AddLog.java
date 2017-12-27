package com.example.domain;

public class AddLog {
	private Integer addLogID;
	private String tableName;
	private Integer firstID;
	private Integer secondID;
	private String stringID;

	public Integer getAddLogID() {
		return addLogID;
	}

	public void setAddLogID(Integer addLogID) {
		this.addLogID = addLogID;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getFirstID() {
		return firstID;
	}

	public void setFirstID(Integer firstID) {
		this.firstID = firstID;
	}

	public Integer getSecondID() {
		return secondID;
	}

	public void setSecondID(Integer secondID) {
		this.secondID = secondID;
	}

	public AddLog(Integer addLogID, String tableName, Integer firstID,
			Integer secondID) {
		super();
		this.addLogID = addLogID;
		this.tableName = tableName;
		this.firstID = firstID;
		this.secondID = secondID;
	}

	public AddLog(Integer addLogID, String tableName, Integer firstID) {
		super();
		this.addLogID = addLogID;
		this.tableName = tableName;
		this.firstID = firstID;
	}

	public AddLog(Integer addLogID, String tableName, String stringID) {
		super();
		this.addLogID = addLogID;
		this.tableName = tableName;
		this.stringID = stringID;
	}

	public String getStringID() {
		return stringID;
	}

	public void setStringID(String stringID) {
		this.stringID = stringID;
	}

	public AddLog(Integer addLogID, String tableName, Integer firstID,
			Integer secondID, String stringID) {
		super();
		this.addLogID = addLogID;
		this.tableName = tableName;
		this.firstID = firstID;
		this.secondID = secondID;
		this.stringID = stringID;
	}

}
