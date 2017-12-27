package com.example.domain;

public class DeleteLog {

	private Integer deleteLogID;
	private String tableName;
	private Integer firstID;
	private Integer secondID;
	private String stringID;

	public DeleteLog(Integer deleteLogID, String tableName, Integer firstID,
			Integer secondID) {
		super();
		this.deleteLogID = deleteLogID;
		this.tableName = tableName;
		this.firstID = firstID;
		this.secondID = secondID;
	}

	public DeleteLog(Integer deleteLogID, String tableName, Integer firstID) {
		super();
		this.deleteLogID = deleteLogID;
		this.tableName = tableName;
		this.firstID = firstID;
	}

	public DeleteLog(Integer deleteLogID, String tableName, Integer firstID,
			Integer secondID, String stringID) {
		super();
		this.deleteLogID = deleteLogID;
		this.tableName = tableName;
		this.firstID = firstID;
		this.secondID = secondID;
		this.stringID = stringID;
	}

	public DeleteLog(Integer deleteLogID, String tableName, String stringID) {
		super();
		this.deleteLogID = deleteLogID;
		this.tableName = tableName;
		this.stringID = stringID;
	}

	public String getStringID() {
		return stringID;
	}

	public void setStringID(String stringID) {
		this.stringID = stringID;
	}

	public Integer getDeleteLogID() {
		return deleteLogID;
	}

	public void setDeleteLogID(Integer deleteLogID) {
		this.deleteLogID = deleteLogID;
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

}
