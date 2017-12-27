package com.example.domain;

import java.util.UUID;

public class PredefineValue {

	private int predefineValID;
	private int traitID;
	private String value;

	public PredefineValue() {
		super();
	}

	public PredefineValue(int pvID, int tID, String value) {
		predefineValID = pvID;
		traitID = tID;
		this.value = value;
	}

	public PredefineValue(int traitID, String value) {
		super();
		Integer pvID = UUID.randomUUID().hashCode();
		predefineValID = pvID;
		this.traitID = traitID;
		this.value = value;
	}

	public int getPredefineValueID() {
		return predefineValID;
	}

	public void setPredefineValueID(int predefineValueID) {
		this.predefineValID = predefineValueID;
	}

	public int getTraitID() {
		return traitID;
	}

	public void setTraitID(int traitID) {
		this.traitID = traitID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "predefineValue [predefineValID=" + predefineValID
				+ ", traitID=" + traitID + ", value=" + value + "]";
	}

}
