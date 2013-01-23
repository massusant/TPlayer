package com.heavenly.ticket.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.heavenly.ticket.model.Station;
import com.heavenly.ticket.transaction.StationTransaction;

public class StationSuggestionListAdapter extends BaseAdapter implements
		Filterable {	
	
	private Context mContext;
	
	
	public StationSuggestionListAdapter(Context context) {
		super();
		mContext = context;
		StationTransaction.initFullList(context);
	}

	@Override
	public int getCount() {
		if (mData != null && !mData.isEmpty()) {
			return mData.size();
		}
		return 0;
	}

	@Override
	public Station getItem(int arg0) {
		if (mData != null && !mData.isEmpty()) {
			return mData.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		if (mData != null && !mData.isEmpty()) {
			return mData.get(arg0).getSeq();
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext,
					android.R.layout.simple_dropdown_item_1line, null);
		}
		Station item = getItem(position);
		TextView text = (TextView) convertView;
		text.setText(item.getName());
		return text;
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	
	private ArrayList<Station> mData;
	
	private Filter mFilter = new Filter() {
		FilterResults mFilterResults = new FilterResults();
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (constraint == null || constraint.length() < 1) {
				return null;
			}
			String prefix = constraint.toString();
			ArrayList<Station> suggs = new ArrayList<Station>();
			ArrayList<Station> data = StationTransaction.FULL_STATION_LIST;
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getSpell().startsWith(prefix.toLowerCase(Locale.ENGLISH))
						|| data.get(i).getName().startsWith(prefix)) {
					suggs.add(data.get(i));
				}
			}
			mFilterResults.values = suggs;
			mFilterResults.count = suggs.size();
			return mFilterResults;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			if (results == null) {
				mData = null;
			} else {
				mData = (ArrayList<Station>) results.values;
			}
			notifyDataSetChanged();
		}
		
	};

}