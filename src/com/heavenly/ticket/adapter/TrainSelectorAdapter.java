package com.heavenly.ticket.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.Train;

public class TrainSelectorAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Train> mData;
	
	public TrainSelectorAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(List<Train> data) {
		mData = data;
	}

	@Override
	public int getCount() {
		if (mData != null && !mData.isEmpty()) {
			return mData.size();
		}
		return 0;
	}

	@Override
	public Train getItem(int position) {
		if (mData != null && !mData.isEmpty()) {
			return mData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_train_selector, null);
			holder = new ViewHolder();
			holder.trainName = (TextView) convertView.findViewById(R.id.train_name);
			holder.startStation = (TextView) convertView.findViewById(R.id.start_station_name);
			holder.startTime = (TextView) convertView.findViewById(R.id.start_time);
			holder.endStation = (TextView) convertView.findViewById(R.id.end_station_name);
			holder.endTime = (TextView) convertView.findViewById(R.id.end_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Train item = getItem(position);
		holder.trainName.setText(item.getName());
		holder.startStation.setText(item.getStartStationName());
		holder.startTime.setText(item.getStartTime());
		holder.endStation.setText(item.getEndStationName());
		holder.endTime.setText(item.getEndTime());
				
		return convertView;
	}
	
	public static class ViewHolder {
		TextView trainName;
		TextView startStation;
		TextView startTime;
		TextView endStation;
		TextView endTime;
	}

}