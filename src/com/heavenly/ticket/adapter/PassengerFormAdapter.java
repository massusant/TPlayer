package com.heavenly.ticket.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.heavenly.ticket.R;

public class PassengerFormAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private int mCount = 1;
	private List<String> mData;
	
	public PassengerFormAdapter(Context context) {
		mContext = context;
	}
	
	public void setCount(int count) {
		mCount = count;
	}
	
	@Override
	public int getCount() {
		if (mData != null && !mData.isEmpty()) {
			return mData.size();
		}
		return mCount;
	}

	@Override
	public Object getItem(int position) {
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
			convertView = View.inflate(mContext,
					R.layout.item_passenger_info_form, null);
			holder = new ViewHolder();
			holder.addOneButton = (Button) convertView.findViewById(R.id.add_one_below);
			holder.removeLineButton = (Button) convertView.findViewById(R.id.remove_line);
			holder.addOneButton.setOnClickListener(this);
			holder.removeLineButton.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position < mCount - 1 || position == 4) {
			holder.addOneButton.setVisibility(View.GONE);
		} else {
			holder.addOneButton.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		Button removeLineButton;
		Button addOneButton;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.add_one_below) {
			if (mCount < 5) {
				mCount++;
				notifyDataSetChanged();
			}
		} else if (v.getId() == R.id.remove_line) {
			if (mCount > 1) {
				mCount--;
				notifyDataSetChanged();
			}
		}
	}
}
