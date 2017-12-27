package com.example.fieldworker1;

import java.util.ArrayList;
import java.util.Collections;

import com.example.dao.ObservationDao;
import com.example.domain.Observation;
import com.example.domain.Trait;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DataChart extends Activity {
	private ArrayList<Trait> traitName;
	private ArrayList<Integer> observationIDs;
	private MultipleTemperatureChart mChart;
	private ObservationDao oDao;
	private String chartType;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		oDao=new ObservationDao(DataChart.this);
		Intent intent=getIntent();
		traitName=(ArrayList<Trait>) intent.getSerializableExtra("traitName");
		intent.getStringArrayListExtra("observationName");
		for(Trait t:traitName)
		System.out.println("DataChart traitSelected:"+t.getTraitID());
		observationIDs=intent.getIntegerArrayListExtra("observationID");
		System.out.println(observationIDs+"$$$$");

		chartType=intent.getStringExtra("chartType");
		ArrayList<Observation> observations=new ArrayList<Observation>();
		
		for(Integer i:observationIDs)
		{
			observations.add(oDao.findObervationById(i));
		}
		Collections.sort(observations);
		for (int i=0;i<observationIDs.size();i++) {
			observationIDs.set(i, observations.get(i).getObservationID());
		}
		
		mChart=new MultipleTemperatureChart(traitName, observationIDs, chartType,DataChart.this);
		Intent intent2=new Intent();
		intent2=mChart.execute(DataChart.this);
		startActivity(intent2);
		this.finish();
	}
	public void generateChart()
	{
		
	}
	

}
