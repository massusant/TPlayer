package com.heavenly.ticket.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.LeftTicketState;

public class LeftTicketStateAdapter extends BaseAdapter {

	private Context mContext;
	private List<LeftTicketState> mData;
	
	public LeftTicketStateAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(List<LeftTicketState> data) {
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
	public LeftTicketState getItem(int position) {
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
			convertView = View.inflate(mContext, R.layout.item_left_ticket_state, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.train_name);
			holder.timeGetOn = (TextView) convertView.findViewById(R.id.time_get_on);
			holder.timeGetOff = (TextView) convertView.findViewById(R.id.time_get_off);
			holder.leftTicket = (TextView) convertView.findViewById(R.id.ticket_left_state);
			holder.bookButton = (Button) convertView.findViewById(R.id.book_ticket);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LeftTicketState item = getItem(position);
		if (item != null) {
			holder.name.setText(item.getTrainNoShow());
			holder.timeGetOn.setText(item.getTimeGetOn());
			holder.timeGetOff.setText(item.getTimeGetOff());
			holder.leftTicket.setText(item.getsSeatNumString());
			if (item.isBookable()) {
				holder.bookButton.setVisibility(View.VISIBLE);
			} else {
				holder.bookButton.setVisibility(View.INVISIBLE);
			}
		}
		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView timeGetOn;
		TextView timeGetOff;
		TextView leftTicket;
		Button bookButton;
	}
}