package com.example.domain;

public class TraitListContent {

	private int traitListID;
	private int traitID;

	public TraitListContent() {
		super();
	}

	public TraitListContent(int tlID, int tID) {
		traitListID = tlID;
		traitID = tID;
	}

	public int getTraitListID() {
		return traitListID;
	}

	public void setTraitListID(int traitListID) {
		this.traitListID = traitListID;
	}

	public int getTraitID() {
		return traitID;
	}

	public void setTraitID(int traitID) {
		this.traitID = traitID;
	}

	@Override
	public String toString() {
		return "traitListContent [traitListID=" + traitListID + ", traitID="
				+ traitID + "]";
	}

}
