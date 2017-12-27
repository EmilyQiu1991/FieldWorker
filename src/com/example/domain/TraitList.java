package com.example.domain;

import java.io.Serializable;
import java.util.Set;

import android.R.integer;

public class TraitList implements Serializable{

	private int traitListID;
	private String traitListName;
	private String username;
	private Set<Trait> traits;
	private int accessible;
    private String deviceID;
    private int nameVersion;
	public TraitList(int traitListID, String traitListName, String username,
			 String deviceID) {
		super();
		this.traitListID = traitListID;
		this.traitListName = traitListName;
		this.username = username;				
		this.deviceID = deviceID;
	}

	public TraitList() {
		super();
	}

	public TraitList(int tlID, String tln, String username, Set traits) {
		traitListID = tlID;
		traitListName = tln;
		this.username = username;
		this.traits = traits;
	}

	public TraitList(int traitListID, String traitListName, String username) {
		super();
		this.traitListID = traitListID;
		this.traitListName = traitListName;
		this.username = username;
	}

	public TraitList(Integer id, String name, String username2,
			int accessible2) {
		super();
		this.traitListID = id;
		this.traitListName = name;
		this.username = username2;
		this.accessible=accessible2;
	}

	public TraitList(Integer id, String traitListName2, String username2,
			Integer accessible2, Integer nameVersion2) {
		this.traitListID = id;
		this.traitListName = traitListName2;
		this.username = username2;
		this.accessible=accessible2;
		this.nameVersion=nameVersion2;
	}

	public Set<Trait> getTraits() {
		return traits;
	}

	public void setTraits(Set<Trait> traits) {
		this.traits = traits;
	}

	public int getTraitListID() {
		return traitListID;
	}

	public void setTraitListID(int traitListID) {
		this.traitListID = traitListID;
	}

	public String getTraitListName() {
		return traitListName;
	}

	public void setTraitListName(String traitListName) {
		this.traitListName = traitListName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
    public String getTraitVersionName()
    {
    	String result="";
    	if (nameVersion == 0) {
			result=traitListName;
		} else {
			result=traitListName + "_"
					+ nameVersion;
		}
    	return result;
    }
	

	public int getAccessible() {
		return accessible;
	}

	public void setAccessible(int accessible) {
		this.accessible = accessible;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public int getNameVersion() {
		return nameVersion;
	}

	public void setNameVersion(int nameVersion) {
		this.nameVersion = nameVersion;
	}

	@Override
	public String toString() {
		return "TraitList [traitListID=" + traitListID + ", traitListName="
				+ traitListName + ", username=" + username + ", traits="
				+ traits + ", accessible=" + accessible + ", deviceID="
				+ deviceID + ", nameVersion=" + nameVersion + "]";
	}

	
}
