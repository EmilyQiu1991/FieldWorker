package com.example.fieldworker1;

import com.example.dao.PredefineValueDao;
import com.example.dao.TraitDao;
import com.example.domain.Trait;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;

public class ShowTrait extends Activity {
	private DisplayTraits displayTraits;
	private TableLayout table=null;
	private TraitDao traitDao;
	private PredefineValueDao predefineValueDao;
	private String traitName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template1);
		table=(TableLayout) findViewById(R.id.templateTalbe);
		traitDao=new TraitDao(ShowTrait.this);
		predefineValueDao=new PredefineValueDao(ShowTrait.this);
		Intent intent=getIntent();
		
		Trait t=(Trait) intent.getSerializableExtra("clickTrait");
		displayTraits=new DisplayTraits(t, table, ShowTrait.this);
		if (t.getWidgetName().equals("Spinner")) {
			displayTraits.appendSpinner(t.getTraitName(),predefineValueDao.search1(t.getTraitID()), t.getUnit());
		}
		else if (t.getWidgetName().equals("EditText")) {
			displayTraits.appendEditText(t.getTraitName(),t.getUnit());
		}
		else if(t.getWidgetName().equals("CheckBox")){
			displayTraits.appendCheckBox(t.getTraitName(), predefineValueDao.search1(t.getTraitID()), t.getUnit());
		}
		else if(t.getWidgetName().equals("Slider")){
			displayTraits.appendSlider(t.getTraitName(), predefineValueDao.search1(t.getTraitID()), t.getUnit());
		}
		else if(t.getWidgetName().equals("RadioButton")){
			displayTraits.appendRadioButton(t.getTraitName(), predefineValueDao.search1(t.getTraitID()), t.getUnit());
		}
			
	}
	

}
