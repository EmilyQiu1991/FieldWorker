package com.example.adapter;

import java.util.List;

import com.example.domain.Trait;
import com.example.domain.Trait;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;



public class TraitAdapter extends BaseAdapter {
	 private List<Trait> mList;
	    private Context mContext;
		public TraitAdapter(List<Trait> mList, Context mContext) {
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
				Trait t=mList.get(position);
				if (t.getNameVersion() == 0) {
					_TextView.setText(t.getTraitName());
				} else {
					_TextView.setText(t.getTraitName() + "_"
							+ t.getNameVersion());
				}
			    
			}
			return convertView;
		}

		

}
