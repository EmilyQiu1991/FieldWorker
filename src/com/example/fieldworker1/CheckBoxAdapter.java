package com.example.fieldworker1;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class CheckBoxAdapter extends BaseAdapter{
	 private LayoutInflater mInflater;
	 private ArrayList<Boolean> checkedItem;
	 private ArrayList<String> array;
     public CheckBoxAdapter(Context context,ArrayList<String> array,ArrayList<Boolean> checkedItem)
     {
 
     	this.mInflater=LayoutInflater.from(context);
     	this.array=array;
     	this.checkedItem=checkedItem;
     }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return array.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			// TODO Auto-generated method stub
			CheckedTextView name;
			View view;
			final int p=position;
			if (converView==null) {
				view=mInflater.inflate(R.layout.trait, parent,false);
			}
			else {
				view=converView;
			}
			name=(CheckedTextView)view.findViewById(R.id.text);
			final String s=(String)array.get(position);
			name.setText(s);
			if (checkedItem.get(p)==true) {
				name.setChecked(true);
			}
			else {
				name.setChecked(false);
			}
			return view;
		}
 	
 }


