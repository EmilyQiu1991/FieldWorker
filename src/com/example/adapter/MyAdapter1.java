package com.example.adapter;

import java.util.List;

import com.example.domain.TraitList;


import android.R;
import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class MyAdapter1 extends BaseAdapter {
    private List<TraitList> mList;
    private Context mContext;
	public MyAdapter1(List<TraitList> mList, Context mContext) {
		super();
		this.mList = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {	
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
		convertView=_LayoutInflater.inflate(R.layout.simple_spinner_dropdown_item, null);
		if(convertView!=null)
		{
		CheckedTextView _TextView=(CheckedTextView) convertView.findViewById(R.id.text1);
			TraitList t=mList.get(position);
			if (t.getNameVersion() == 0) {
				_TextView.setText(t.getTraitListName());
			} else {
				_TextView.setText(t.getTraitListName() + "_"
						+ t.getNameVersion());
			}
		    
		}
		return convertView;
	}

}
