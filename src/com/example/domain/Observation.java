package com.example.domain;

import java.util.Date;

public class Observation implements Comparable<Observation>{

	private int observationID;
	private String observationName;
	private String username;
	private int traitListID;
	private Date createTime;
	private Date deleteTime;
	private String photoPath;
	private String paintingPath;
	private String comment;

	public Observation() {
		super();
	}

	public Observation(int oID, String observationName, String username,
			int tlID, Date createTime, Date deleteTime, String path,
			String paintingPath, String comment) {
		observationID = oID;
		this.observationName = observationName;
		this.username = username;
		traitListID = tlID;
		this.createTime = createTime;
		this.deleteTime = deleteTime;
		photoPath = path;
		this.paintingPath = paintingPath;
		this.comment = comment;
	}

	public int getObservationID() {
		return observationID;
	}

	public void setObservationID(int observationID) {
		this.observationID = observationID;
	}

	public String getObservationName() {
		return observationName;
	}

	public void setObservationName(String observationName) {
		this.observationName = observationName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTraitListID() {
		return traitListID;
	}

	public void setTraitListID(int traitListID) {
		this.traitListID = traitListID;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getPaintingPath() {
		return paintingPath;
	}

	public void setPaintingPath(String paintingPath) {
		this.paintingPath = paintingPath;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Observation [observationID=" + observationID
				+ ", observationName=" + observationName + ", username="
				+ username + ", traitListID=" + traitListID + ", createTime="
				+ createTime + ", deleteTime=" + deleteTime + ", photoPath="
				+ photoPath + ", paintingPath=" + paintingPath + ", comment="
				+ comment + "]";
	}

	@Override
	public int compareTo(Observation another) {
		return getCreateTime().compareTo(another.getCreateTime());
	}

}
