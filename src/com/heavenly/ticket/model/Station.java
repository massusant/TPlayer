package com.heavenly.ticket.model;

import android.text.TextUtils;

public class Station {

	// bjb|北京北|VAP|0
	
	public static Station create(String in) {
		Station st = null;
		if (!TextUtils.isEmpty(in)) {
			String[] values = in.split("\\|");
			if (values != null && values.length == 4) {
				st = new Station();
				st.spell = values[0];
				st.name = values[1];
				st.code = values[2];
				try {
					st.seq = Integer.parseInt(values[3]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		return st;
	}
	
	private int seq;
	private String name;
	private String spell;
	private String code;
	public int getSeq() {
		return seq;
	}
	public String getName() {
		return name;
	}
	public String getSpell() {
		return spell;
	}
	public String getCode() {
		return code;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSpell(String spell) {
		this.spell = spell;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return name;
	}
}