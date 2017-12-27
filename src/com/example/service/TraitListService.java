package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.R.integer;
import android.content.Context;

import com.example.dao.AddLogDao;
import com.example.dao.ObservationDao;
import com.example.dao.TraitDao;
import com.example.dao.TraitListContentDao;
import com.example.dao.TraitListDao;
import com.example.domain.AddLog;
import com.example.domain.TraitList;
import com.example.domain.TraitListContent;

public class TraitListService {
	private TraitListDao traitListDao;
	private TraitListContentDao traitListContentDao;
	private TraitDao traitDao;
	private AddLogDao addLogDao;
	private ObservationDao obserDao;

	public TraitListService(Context context) {
		traitListDao = new TraitListDao(context);
		traitListContentDao = new TraitListContentDao(context);
		traitDao = new TraitDao(context);
		addLogDao = new AddLogDao(context);
		obserDao = new ObservationDao(context);
	}

	public void addTraitToTraitList(String traitName, Integer traitListID) {
		Integer traitID = traitDao.findIdbyName(traitName);
		System.out.println("traitid:" + traitID);
		traitListContentDao.insert(new TraitListContent(traitListID, traitID));
	}

	public void addTraitList(TraitList traitList, List<String> traits, boolean b) {

		// System.out.println(new TraitList(traitListID, traitListName,
		// "aa").toString());
		traitListDao.insert(traitList);
		for (String s : traits) {
			Integer traitID = traitDao.findIdbyName(s);
			traitListContentDao.insert(new TraitListContent(traitList
					.getTraitListID(), traitID));
		}
		// //save the action into AddLog Table
		if (!b) {

			addLogDao.insert(new AddLog(UUID.randomUUID().hashCode(),
					"TraitList", traitList.getTraitListID(), null));

			for (String s : traits) {
				Integer traitID = traitDao.findIdbyName(s);
				// save the action into AddLog Table
				addLogDao
						.insert(new AddLog(UUID.randomUUID().hashCode(),
								"TraitListContent", traitList.getTraitListID(),
								traitID));
			}
		}
	}

	public void deleteTraitList(TraitList t) {
		if (obserDao.searchObservationsWithTraitList(t.getTraitListID(),
				t.getUsername()).size() == 0) {
			traitListDao.delete(t, false);
			traitListContentDao.deleteTraitListContent(t.getTraitListID());
		} else
			traitListDao.delete(t, true);
	}

	public boolean isUsed(TraitList t) {
		if (obserDao.searchObservationsWithTraitList(t.getTraitListID(),
				t.getUsername()).size() == 0) {
			return false;
		} else
			return true;
	}

	// public void deleteTraitFromList(String TraitListName, String TraitName)
	// {
	// int traitListID=traitListDao.findIdByName(TraitListName);
	// int traitID=traitDao.findIdByName(TraitName);
	// traitListContentDao.delete(traitListID, traitID);
	// }
	public void updateTraitList(TraitList tl, ArrayList<String> currentTraits) {
		int traitListID = tl.getTraitListID();
		traitListContentDao.deleteTraitListContent(traitListID);
		for (int i = 0; i < currentTraits.size(); i++) {
			int traitId = traitDao.findIdbyName(currentTraits.get(i));
			traitListContentDao.insert(new TraitListContent(traitListID,
					traitId));
		}

	}

}
