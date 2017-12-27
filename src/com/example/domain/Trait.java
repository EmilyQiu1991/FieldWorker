package com.example.domain;

import java.io.Serializable;
import java.util.UUID;

import android.R.integer;

public class Trait implements Serializable {
	private static final long serialVersionUID = -5354800692647877820L;
	private int traitID;
	public Trait(int traitID, String traitName, String widgetName, String unit,
			int accessible) {
		super();
		this.traitID = traitID;
		this.traitName = traitName;
		this.widgetName = widgetName;
		this.unit = unit;
		this.accessible = accessible;
	}

	private String traitName;
	private String widgetName;
	private String unit;
	private int accessible;
    private String username;
    private int nameVersion;
	public int getAccessible() {
		return accessible;
	}

	public void setAccessible(int accessible) {
		this.accessible = accessible;
	}

	public Trait(int traitID, String tn, String wn, String unit,String username,int nameVersion) {
		this.traitID = traitID;
		this.traitName = tn;
		this.widgetName = wn;
		this.unit = unit;
		this.username=username;
		this.nameVersion=nameVersion;
	}

	public Trait(int traitID, String traitName, String widgetName, String unit,
			int accessible, String username, int nameVersion) {
		super();
		this.traitID = traitID;
		this.traitName = traitName;
		this.widgetName = widgetName;
		this.unit = unit;
		this.accessible = accessible;
		this.username = username;
		this.nameVersion = nameVersion;
	}

	public Trait(String traitName, String widgetName, String unit,String username,int accessible,int nameVersion) {
		super();
		Integer traitID = UUID.randomUUID().hashCode();
		this.traitID = traitID;
		this.traitName = traitName;
		this.widgetName = widgetName;
		this.unit = unit;
		this.username=username;
		this.accessible=accessible;
		this.nameVersion=nameVersion;
	}

	public Trait() {
		super();
	}

	public int getTraitID() {
		return traitID;
	}

	public void setTraitID(int traitID) {
		this.traitID = traitID;
	}

	public String getTraitName() {
		return traitName;
	}

	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getNameVersion() {
		return nameVersion;
	}

	public void setNameVersion(int nameVersion) {
		this.nameVersion = nameVersion;
	}

}
