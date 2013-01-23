package com.heavenly.ticket.pojo;

public class SkuDO {
	/**
	 * <option value="1">硬座</option>
	 * <option value="2"></option>
	 * <option value="3">硬卧</option>
	 * <option value="4">软卧</option>
	 */
	private String seat;   /*1--硬座,3--硬卧,4--软卧*///硬卧
	/**
	 * <option value="0">随机</option>
	 * <option value="3">上铺</option>
	 * <option value="2">中铺</option>
	 * <option value="1">下铺</option>
	*/
	
	private String seatDetailSelect    ;/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
	/**
	* <option value="1">成人票</option>
	  <option value="2">儿童票</option>
	  <option value="3">学生票</option>
	  <option value="4">残军票</option>
     */
	private String type = "1"  ;//默认都是成人票
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getSeatDetailSelect() {
		return seatDetailSelect;
	}
	public void setSeatDetailSelect(String seatDetailSelect) {
		this.seatDetailSelect = seatDetailSelect;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public  SkuDO(String seat ,String seatDetailSelect ){
		this.seat =seat;
		this.seatDetailSelect = seatDetailSelect;
	}
	//硬卧
	public static SkuDO creatSKUfoHardSleep(){
		SkuDO sku = new SkuDO("3","1");
		return sku;
	}
}
