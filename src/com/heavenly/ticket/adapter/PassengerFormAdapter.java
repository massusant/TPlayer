package com.heavenly.ticket.adapter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.heavenly.ticket.R;
import com.heavenly.ticket.view.PassengerFormItemView;

public class PassengerFormAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
//	private int mCount = 1;
//	private List<String> mData;
	
	private List<PassengerFormItemView> mFormList;
	private LinkedList<PassengerFormItemView> removedForms;
	
	public PassengerFormAdapter(Context context) {
		mContext = context;
		removedForms = new LinkedList<PassengerFormItemView>();
		mFormList = new LinkedList<PassengerFormItemView>();
		mFormList.add(obtainFormView(0));
	}
	
	@Override
	public int getCount() {
		return mFormList.size();
	}

	@Override
	public PassengerFormItemView getItem(int position) {
		return mFormList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder holder;
//		if (convertView == null) {
//			convertView = View.inflate(mContext,
//					R.layout.item_passenger_info_form, null);
//			holder = new ViewHolder();
//			holder.addOneButton = (Button) convertView.findViewById(R.id.add_one_below);
//			holder.removeLineButton = (Button) convertView.findViewById(R.id.remove_line);
//			holder.addOneButton.setOnClickListener(this);
//			holder.removeLineButton.setOnClickListener(this);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//		if (position < mCount - 1 || position == 4) {
//			holder.addOneButton.setVisibility(View.GONE);
//		} else {
//			holder.addOneButton.setVisibility(View.VISIBLE);
//		}
//		holder.removeLineButton.setTag(position);
		PassengerFormItemView itemView = getItem(position);
		boolean hideAddButton = position < mFormList.size() - 1 || position == 4;
		itemView.showAddButton(!hideAddButton);
		itemView.setPosition(position);
		return itemView;
	}

	
	private PassengerFormItemView obtainFormView(int position) {
		PassengerFormItemView item;
		if (!removedForms.isEmpty()) {
			item = removedForms.removeFirst();
		} else {
			item = new PassengerFormItemView(mContext);
			item.setOnClickListener(this);
		}
//		item.setPosition(position);
		return item;
	}

	@Override
	public void onClick(View v) {
		if (isOperate.get()) {
			return;
		}
		isOperate.set(true);
		new FormShowTask(v).execute(v);
	}
	
	class FormShowTask extends AsyncTask<View, Void, Void> {
		View view;
		PassengerFormItemView itemView;
		int pos;
		FormShowTask(View v) {
			if (!isOperate.get()) {
				isOperate.set(true);
			}
			view = v;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (view.getId() == R.id.add_one_below) {
				if (mFormList.size() < 5) {
					itemView = obtainFormView(mFormList.size());
				}
			} else if (view.getId() == R.id.remove_line) {
				if (mFormList.size() > 1) {
					pos = (Integer) view.getTag();
				}
			}
		}

		@Override
		protected Void doInBackground(View... params) {
			if (view.getId() == R.id.add_one_below) {
				if (mFormList.size() < 5) {
					mFormList.add(itemView);
				}
			} else if (view.getId() == R.id.remove_line) {
				if (mFormList.size() > 1) {
					itemView = mFormList.remove(pos);
					removedForms.addLast(itemView);
				}
			}
			SystemClock.sleep(300);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			notifyDataSetChanged();
			isOperate.set(false);
		}
		
	}
	
	private AtomicBoolean isOperate = new AtomicBoolean(false);
	
}