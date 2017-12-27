package com.example.domain;

public class ObserContent {

	private int relation_id;
	private int observationID;
	private int traitID;
	private String traitValue;
	private int editable;

	public ObserContent() {
		super();
	}

	public ObserContent(int relationID,int oID, int tID, String tv, int editable) {
		relation_id = relationID;
		observationID = oID;
		traitID = tID;
		traitValue = tv;
		this.editable = editable;
	}

	public int getEditable() {
		return editable;
	}

	public void setEditable(int editable) {
		this.editable = editable;
	}

	public int getObservationID() {
		return observationID;
	}

	public void setObservationID(int observationID) {
		this.observationID = observationID;
	}

	public int getTraitID() {
		return traitID;
	}

	public void setTraitID(int traitID) {
		this.traitID = traitID;
	}

	public String getTraitValue() {
		return traitValue;
	}

	public void setTraitValue(String traitValue) {
		this.traitValue = traitValue;
	}

	
	public int getRelation_id() {
		return relation_id;
	}

	public void setRelation_id(int relation_id) {
		this.relation_id = relation_id;
	}

	@Override
	public String toString() {
		return "observationContent [relation_id=" + relation_id +", observationID=" + observationID
				+ ", traitID=" + traitID + ", traitValue=" + traitValue
				+ ", editable=" + editable + "]";
	}

}
