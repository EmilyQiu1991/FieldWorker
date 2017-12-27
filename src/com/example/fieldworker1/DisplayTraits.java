package com.example.fieldworker1;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.domain.Trait;

public class DisplayTraits {
	private Trait trait;
    private TableLayout table;
    private Context context;
	public DisplayTraits(Trait trait,TableLayout table,Context context) {
		super();
		this.trait = trait;
		this.table=table;
		this.context=context;
	}
	public void appendSpinner(String traitName,String[] pValues,String unit)
	{
		int id=table.getChildCount();
		TableRow row=new TableRow(context);
		row.setId(id);
		TextView traitNameTextView=new TextView(context);
		traitNameTextView.setText(traitName);
		traitNameTextView.setTextSize(15);
		Spinner spinner=new Spinner(context);
        ArrayAdapter<String> adp=new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,pValues);
        spinner.setAdapter(adp);
        
        Button deleteButton=new Button(context);
       
        deleteButton.setText("Edit");
        deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(v.toString());
				table.removeView((View) v.getParent());
			}
		});
        row.addView(traitNameTextView);
        row.addView(spinner);
     //   row.addView(deleteButton);
TextView unitTextView=new TextView(context);
		
		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		row.addView(unitTextView);
        table.addView(row);
		
	}
	public void appendEditText(String traitName,String unit) {
		int id=table.getChildCount();
		TableRow row=new TableRow(context);
		row.setId(id);
		TextView traitNameTextView=new TextView(context);
		traitNameTextView.setText(traitName);
		traitNameTextView.setTextSize(15);
		EditText editText=new EditText(context);
		
		Button deleteButton=new Button(context);
	       
        deleteButton.setText("Edit");
        deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(v.toString());
				table.removeView((View) v.getParent());
			}
		});
        row.addView(traitNameTextView);
        row.addView(editText);
       //row.addView(deleteButton);
TextView unitTextView=new TextView(context);
		
		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		row.addView(unitTextView);
        table.addView(row);
	}
	public void appendSlider(String traitName,String[] pValues,String unit) {
		final int min=Math.min(Integer.parseInt(pValues[0]), Integer.parseInt(pValues[1]));
		int max=Math.max(Integer.parseInt(pValues[0]), Integer.parseInt(pValues[1]));
		int id=table.getChildCount();
		TableRow row=new TableRow(context);
		TextView traitNameTextView=new TextView(context);
		traitNameTextView.setGravity(Gravity.TOP);
		traitNameTextView.setTextSize(15);
		traitNameTextView.setText(traitName);		
		final TextView maxTextView=new TextView(context);
		maxTextView.setText(String.valueOf(min));
		maxTextView.setGravity(Gravity.LEFT);
		SeekBar seekBar=new SeekBar(context);
		seekBar.setMax(Math.max(Integer.parseInt(pValues[0]), Integer.parseInt(pValues[1])));
		seekBar.setProgress(min);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				System.out.println(String.valueOf(progress));
				maxTextView.setText(String.valueOf(progress+min));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
		});
		TextView unitTextView=new TextView(context);
		
		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		row.addView(traitNameTextView);
		row.addView(seekBar);
		row.addView(maxTextView);
		row.addView(unitTextView);
		table.addView(row);
	}
	public void appendCheckBox(String traitName,String[] pValues,String unit)
	{
		int id=table.getChildCount();
		TableRow row=new TableRow(context);
		row.setId(id);
		TextView traitNameTextView=new TextView(context);
		traitNameTextView.setGravity(Gravity.TOP);
		traitNameTextView.setTextSize(15);
		//traitNameTextView.setLayoutParams(params)
		traitNameTextView.setText(traitName);
		ListView listview=new ListView(context);
		listview.setDivider(null);
		 final ArrayList<Boolean> checkedItem=new ArrayList<Boolean>();
		ArrayList<String> array=new ArrayList<String>();
		//initial checkedItem and array
		for (int i = 0; i < pValues.length; i++) {
			array.add(pValues[i]);
			checkedItem.add(false);
		}
		
		final CheckBoxAdapter chedapter=new CheckBoxAdapter(context,array,checkedItem);
		listview.setAdapter(chedapter);
		listview.setItemsCanFocus(false);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				System.out.println("click an item");
				// TODO Auto-generated method stub
				if (checkedItem.get(position)==true) {
					checkedItem.set(position, false);
					
				}
				else {
					checkedItem.set(position, true);
				}
				chedapter.getView(position, view, parent);
			}
			
		});
		Button deleteButton=new Button(context);
	       
        deleteButton.setText("Edit");
        deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(v.toString());
				table.removeView((View) v.getParent());
			}
		});
        row.addView(traitNameTextView);
        row.addView(listview);
      // row.addView(deleteButton);
TextView unitTextView=new TextView(context);
		
		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		row.addView(unitTextView);
        table.addView(row);
	}
	public void appendRadioButton(String traitName, String[] search1,
			String unit) {
		// TODO Auto-generated method stub
		int id=table.getChildCount();
		TableRow row=new TableRow(context);
		row.setId(id);
		TextView traitNameTextView=new TextView(context);
		traitNameTextView.setGravity(Gravity.TOP);
		traitNameTextView.setTextSize(15);
		//traitNameTextView.setLayoutParams(params)
		traitNameTextView.setText(traitName);
		RadioGroup radioGroup=new RadioGroup(context);
		for(int i=0;i<search1.length;i++)
		{
			RadioButton radioButton=new RadioButton(context);
			radioButton.setText(search1[i]);
			radioGroup.addView(radioButton);
		}
		row.addView(traitNameTextView);
		row.addView(radioGroup);
        TextView unitTextView=new TextView(context);
		
		unitTextView.setText(unit);
		unitTextView.setTextSize(15);
		row.addView(unitTextView);
        table.addView(row);
	}

}
