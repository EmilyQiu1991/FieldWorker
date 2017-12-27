package com.example.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.dao.ObservationDao;
import com.example.dao.TraitDao;
import com.example.domain.Trait;

import android.R.integer;
import android.content.Context;

public class DataChartService {
	private ObservationDao observationDao;
	private ArrayList<Trait> traits;
	private ArrayList<Integer> observations;
	public DataChartService(Context context,ArrayList<Trait> traits,ArrayList<Integer> observations)
	{
		new TraitDao(context);
		observationDao=new ObservationDao(context);
		this.setTraits(traits);
		this.setObservations(observations);
		
	}
    public List<Date[]> getX() throws ParseException
    {
    	List<Date[]> x = new ArrayList<Date[]>();
    	Date[] xLables=new Date[observations.size()];
    	
    	for(int i=0;i<observations.size();i++)
    	{
    		xLables[i]=observationDao.getDateByObserId(observations.get(i));
    	}
    	for (int i = 0; i < traits.size(); i++) {
    		x.add(xLables);
		}
		return x;
    }
    public List<double[]> getValues()
    {
    	List<double[]> values = new ArrayList<double[]>();
    	
    	for (int i = 0; i < traits.size(); i++) {
    		double[] yValue=new double[observations.size()];
    		int traitID=traits.get(i).getTraitID();
    		for (int j = 0; j < observations.size(); j++) {
    			yValue[j]=observationDao.getTraitValue(traitID, observations.get(j));
    			System.out.println(traitID+";"+observations.get(j)+";"+yValue[j]);
			}
    		values.add(yValue);
			
		}
		return values;
    	
    }
	public ArrayList<Integer> getObservations() {
		return observations;
	}
	public void setObservations(ArrayList<Integer> observations) {
		this.observations = observations;
	}
	public ArrayList<Trait> getTraits() {
		return traits;
	}
	public void setTraits(ArrayList<Trait> traits2) {
		this.traits = traits2;
	}
}
