package com.heavenly.ticket.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.heavenly.ticket.constants.TicketInfoConstants;
import com.heavenly.ticket.pojo.OrderDO;
import com.heavenly.ticket.pojo.PassengerDO;
import com.heavenly.ticket.pojo.TicketDO;
import com.heavenly.ticket.pojo.UserDO;
import com.heavenly.ticket.util.HttpUtils;
import com.heavenly.ticket.util.JavaUtils;
import com.heavenly.ticket.util.TrainDataUtils;

/**
 * 在线自动抢订火车票-控制台版本
 */
public class OrderMain {
	
	private static String queryUrlTemp = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=queryLeftTicket&orderRequest.train_date=${train_date}&orderRequest.from_station_telecode=${from_station_telecode}&orderRequest.to_station_telecode=${to_station_telecode}&orderRequest.train_no=&trainPassType=QB&trainClass=QB%23D%23Z%23T%23K%23QT%23&includeStudent=00&seatTypeAndNum=&orderRequest.start_time_str=00%3A00--24%3A00";
	private static String erro_code_yanzhi = "请输入正确的验证码";
	private UserDO user ;//登录的用户
	private List<OrderDO> orders = new ArrayList<OrderDO>();//要买哪些票
	public List<OrderDO> getOrders() {
		return orders;
	}
	public void setOrders(List<OrderDO> orders) {
		this.orders = orders;
	}
	public OrderMain(UserDO user,List<OrderDO> orders){
		this.user = user;
		this.orders =orders;
	}
	public OrderMain(){
		
	}
	/**
	 * 查询满足条件的车票
	 * @return
	 */
	private TicketDO getTicket(String queryUrl,OrderDO order){
		while (true) {
			String ticketMessage = getTicketMessage(queryUrl, order.getStart(),
					order.getEnd(), order.getTrainDate());
			if (ticketMessage.contains("起售")) {
				String[] ms = ticketMessage.split("'>");
				System.err.println(order.getTicketMes() + "--"
						+ ms[ms.length - 1] + "--10S后重试");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			List<TicketDO> tickets = transformTicketDO(ticketMessage);
			String trainName = "";
			for (TicketDO train : tickets) {
				trainName += train.getNum() + ";";
				if (order.getTrainNum().equalsIgnoreCase(train.getNum())) {
					int num = getNumByName(train, order.getSeat());
					if (num == -1) {
						System.err.println(String.format(
								"你要购买的车次【%s】座位类型【%s】 尚未开盘,继续重试",
								order.getTrainNum(), order.getSeat()));
						continue;
					}
					if (num == 0) {
						System.err.println(String.format(
								"你要购买的车次【%s】座位类型【%s】已经没有", order.getTrainNum(),
								order.getSeat()));
						return null;
					}
					return train;
				}
			}
			System.err.println(String.format("你要购买的车次【%s】不存在[" + trainName
					+ "]   :", order.getTrainNum()));
			return null;
		}
			
	}
	/**
	 * 系统忙 或者用户过多会尝试100，依然失败的话 就买另外一张票
	 * @param OrderDO
	 * @param ticket
	 * @return
	 */
	private boolean  getOrderDetail(OrderDO OrderDO,TicketDO ticket){
		int count = 1000;
		while(count>0){
			try{
			//获取令牌
			String body = HttpUtils.doPostBody("https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=submutOrderRequest", JavaUtils
					.toPostParam(submitOrderParams(ticket,OrderDO.getTrainDate())), null, "UTF-8", true,user.getName());
			if(body.contains("目前您还有未处理的订单")){
				System.out.println("你已经有订单未付款，请先付款");
				return true;
			}
				body = submitForOrder(OrderDO, ticket, body);
				if (body.contains("Y")) {
					return true;
				}
				if (body.contains("没有足够的票")) {
					System.err.println("--没有足够的票,重新搜索--!");
					return false;
				}
				if (body.contains("验证码")) {
					System.out.println("下订单 验证码错误。。。");
					checkOrderInfo("");
					continue;
				}
				if (body.contains("系统忙！")) {
					System.err.println("--请求失败:系统忙!");
					count--;
				}
				if (body.indexOf("当前提交订单用户过多！") != -1) {
					System.err.println("--当前提交订单用户过多!");
					count--;
				} else {
					// System.out.println(body);
					checkOrderInfo("");
					count--;
				}
				Thread.sleep(300);// 3s后继续下单
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			}
		return false ;
	}
	private String submitForOrder(OrderDO order,TicketDO ticket,String body) throws UnsupportedEncodingException{
		String paras = "";
		Map<String, String> postOrderParam = new HashMap<String, String>();
		postOrderParam.putAll(getOrderParams(ticket,order));
		for(int x=0; x<order.getPassengerDO().size();x++){
			Map<String,String> submitOrderParams= new HashMap<String, String>();
			PassengerDO passer = order.getPassengerDO().get(x);
			int y = x+1;
			String passenger = "passenger_"+y+"_";
			String checkbox = "checkbox"+x;
			submitOrderParams.put(checkbox,""+x);
			submitOrderParams.put("oldPassengers",passer.getName()+",1,"+passer.getCardid());
			submitOrderParams.put("passengerTickets", TrainDataUtils.seatNameValueMap.get(order.getSeat())+",1,"+
					TrainDataUtils.seatNameValueMap.get(order.getSeatDetai())+","+passer.getName()+",1,"+passer.getCardid()+","+passer.getMobile()+",Y");
			submitOrderParams.put(passenger+"seat", TrainDataUtils.seatNameValueMap.get(order.getSeat())/*1--硬座,3--硬卧,4--软卧*/);//硬卧
			submitOrderParams.put(passenger+"seat_detail", "1"/*0-随机,3-上铺,2-中铺,1--下铺*/);//下铺
			submitOrderParams.put(passenger+"seat_detail_select", TrainDataUtils.seatNameValueMap.get(order.getSeatDetai())/*0-随机,3-上铺,2-中铺,1--下铺*/);//下铺
			submitOrderParams.put(passenger+"ticket", "1");//成人票  /* 1-成人 2-儿童 3-学生 4-残军*/
			submitOrderParams.put(passenger+"name", passer.getName());//姓名
			submitOrderParams.put(passenger+"cardtype", passer.getCardtype()/*二代身份证*/);//证件类型
			submitOrderParams.put(passenger+"cardno", passer.getCardid());//证件号码
			submitOrderParams.put(passenger+"mobileno", passer.getMobile());//手机号
			paras+=JavaUtils.toPostParam(submitOrderParams)+"&";
		}
		postOrderParam.put("org.apache.struts.taglib.html.TOKEN",getTOKEN(body));
		postOrderParam.put("leftTicketStr",getLeftTicketStr(body));
		postOrderParam.put("textfield","中文或拼音首字母");
		postOrderParam.put("randCode",submitCode);
		paras+=JavaUtils.toPostParam(postOrderParam);
//		System.out.println(paras);
		body = HttpUtils.doPostForOrder(paras,user.getName());
		
		return body; 
	}
	public static void main(String[] args) throws Exception {
		String userName = readString("请输入用户名");
		String passWd = readString("请输入密码");
		
		for(OrderDO OrderDO : TicketInfoConstants.orderList){
			System.out.println(OrderDO.getTrainNum());
		}
		OrderMain orderMain = new OrderMain();
		orderMain.setUser(new UserDO(userName,passWd));
		orderMain.setOrders(TicketInfoConstants.orderList);
		orderMain.login();
		while(true){
			orderMain.beginBuyTicket();
		}
		//买票失败 自动一直尝试买票
	}
	private void beginBuyTicket(){
		try{
			if(orders==null||orders.isEmpty()){
				throw new Exception("orders为空，无票需要买");
			}
			for(OrderDO order:orders){
				String queryUrl = queryUrlTemp
						.replace("${train_date}", order.getTrainDate())
						.replace("${from_station_telecode}",TrainDataUtils.stationNames.get(order.getStart()))
						.replace("${to_station_telecode}", TrainDataUtils.stationNames.get(order.getEnd()));
					TicketDO ticket =getTicket(queryUrl,order);
					if(ticket == null) {
						continue ;
					}
					//输入订单校验码
//					checkOrderInfo("");
					if(getOrderDetail(order, ticket)){
						System.out.println("订单成功：去付款吧");
						break;
					}else{
						System.out.println(order.getTicketMes()+"下单失败，尝试另外一个订单");
					}
			}
		}catch(Exception ex){
			//如果出现未知异常 那就重来一把
			beginBuyTicket();
		}
	}
	@SuppressWarnings("deprecation")
	private String getTicketMessage(String queryUrl,String from_station_telecode,String to_station_telecode,String trainDate){
		String queryBody = "";
		int waitTile = 0 ;
		while (true) {
			waitTile ++ ;
			queryBody = HttpUtils.doGetBody(queryUrl, null,user.getName());
			try {
			if (StringUtils.isEmpty(queryBody)) {
				System.out.println(String.format(
						"查询结果为空，查询条件[start=%s,end=%s,date=%s]",
						from_station_telecode, to_station_telecode,
						trainDate));
				Date now =new Date();
				if(now.getHours()<7 || now.getHours()>=23){
					System.out.println("12306例行维护时间，等待早上7点后开始抢票");
//					if(now.getHours())
					Thread.sleep(300000);
				}
				Thread.sleep(300);
				
				continue;
			}
			if(queryBody.contains("-10")){
				System.out.println("查询异常 ："+queryBody+"url:"+queryUrl);
//				Thread.sleep(000);
				if(waitTile==30){
					waitTile = 0 ;
					login();
				}
				continue;
			}
			if (queryBody.indexOf("系统维护中") != -1) {
				System.out.println("--系统维护中, 一分钟再重新搜索--");
				Thread.sleep(60000);
				continue;
			}

			if (queryBody.indexOf("登录名：") != -1) {
				login();
				continue;
			}
			break;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return  queryBody;
	}
	private boolean checkOrderInfo(String body) throws Exception{
		while(true){
			body = HttpUtils.doPostBody(
							"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=checkOrderInfo&tFlag=dc&rand="+submitCode,
							"", null, "UTF-8", true ,user.getName());
			if (body.contains("验证码")) {
					File file = HttpUtils
							.doGetFile(
									"https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp&"+Math.random(),
									TrainDataUtils.saveSumbitCodePath,user.getName());
					
					File codeFile = new File(TrainDataUtils.saveSumbitCodePath);
					if (!codeFile.exists())
						codeFile.createNewFile();
					//FileUtils.copyFile(file, codeFile);
//					submitCode = ImageDemo.getTextFromImage(file,"jpg");
					submitCode = getCode(file,"订单验证码");
					if(submitCode == null||submitCode.isEmpty()||submitCode.length()!=4){
						submitCode = "reld";
						Thread.sleep(100);
					}
					System.out.println("订单验证码：" + submitCode);
			}else{
				System.out.println(body);
				break;
			}
		}
		 System.out.println("校验验证码正确");
		 return true;
	}
	private static int getNumByName(TicketDO train, String name) {
		if (name.equals("硬座"))
			return train.getHardSite();
		if (name.equals("无座"))
			return train.getNoneSite();
		if (name.equals("软座"))
			return train.getSofeSite();
		if (name.equals("硬卧"))
			return train.getHardSleeprt();
		if (name.equals("软卧"))
			return train.getSoftSleeper();
		if (name.equals("特等座位"))
			return train.getSpecial();
		if (name.equals("一等座位"))
			return train.getFirst();
		if (name.equals("二等座位"))
			return train.getSecond();
		return 0;
	}

	private static List<TicketDO> transformTicketDO(String queryBody) {
		List<TicketDO> ts = new ArrayList<TicketDO>();
		for (String tic : StringUtils.split(queryBody, "预定")) {
			tic = tic.replaceAll("&nbsp;", "");
			String[] text = StringUtils.split(tic, ",");
			if (text.length != 17) {
				continue;
			}
			TicketDO ticket = new TicketDO();
			ticket.setNum(text[1].split(">")[1].split("<")[0]);

			ticket.setStart(text[2].split("<br>")[0].split(">").length > 1 ? text[2]
					.split("<br>")[0].split(">")[1] : text[2].split("<br>")[0]);
			ticket.setStartTime(text[2].split("<br>")[1]);

			ticket.setEnd(text[3].split("<br>")[0].split(">").length > 1 ? text[3]
					.split("<br>")[0].split(">")[1] : text[3].split("<br>")[0]);
			ticket.setEndTime(text[3].split("<br>")[1]);
			ticket.setUseTime(text[4]);
			ticket.setBusiness(getCount(text[5]));
			ticket.setSpecial(getCount(text[6]));
			ticket.setFirst(getCount(text[7]));
			ticket.setSecond(getCount(text[8]));
			ticket.setSuperSoft(getCount(text[9]));
			ticket.setSoftSleeper(getCount(text[10]));
			ticket.setHardSleeprt(getCount(text[11]));
			ticket.setSofeSite(getCount(text[12]));
			ticket.setHardSite(getCount(text[13]));
			ticket.setNoneSite(getCount(text[14]));
			ticket.setOther(getCount(text[15]));
			ticket.setBtn(text[16]);
			ts.add(ticket);

		}
		return ts;
	}

	private static int getCount(String text) {
		if (text.contains("无") || text.contains("-"))
			return 0;
		else if (text.contains("有"))
			return 200;
		else if (text.contains("*"))
			return -1;
		else
			return Integer.valueOf(text);
	}

	private static String submitCode = "reload";
	

	/**
	 * 登录系统，在多线程中，只需要有一个线程来请求登录就可以
	 * @throws InterruptedException 
	 * 
	 * @throws Exception
	 */
	private void login() throws InterruptedException{
		//每次登陆清除 缓存的 cook;
		HttpUtils.shutdownHttpClient(user.getName());
		if(user == null){
			throw new IllegalArgumentException("登录用户不能设置为空");
		}
			System.out.println(user.getName()+"--开始登录--");
			String params = "";
			params += "&loginUser.user_name=" + user.getName();
			params += "&user.password=" + user.getPass();
			params += "&refundLogin=N";
			String body = erro_code_yanzhi;
			String code = "";
			do {
				try{
				if (body.contains(erro_code_yanzhi)) {
					File file = HttpUtils.doGetFile(TrainDataUtils.logCodeUrd,TrainDataUtils.saveLoginCodePath,user.getName());
					if(file == null){
						System.err.println("获取登录验证码错误，继续获取验证码");
						Thread.sleep(3000);//停3s继续
						continue;
					}
					code = getCode(file,"登录验证码");
					if(code == null||code.isEmpty()||code.length()!=4){
						continue;
					}
					params += "&randCode=" + code;
					Thread.sleep(1000);
				}
				body = HttpUtils.doPostBody(
								"https://dynamic.12306.cn/otsweb/loginAction.do?method=loginAysnSuggest",
								params, null, null, false,user.getName());
				if(StringUtils.isEmpty(body)){
					System.err.println("登录获取随机数失败 重试...");
					Thread.sleep(1000);
					login();
					break;
				}
				String loginRand = body.split(",")[0].split(":")[1];
				params += "&loginRand=" + loginRand.split("\"")[1];
				body = HttpUtils.doPostBody(
								"https://dynamic.12306.cn/otsweb/loginAction.do?method=login",
								params, null, null, false,user.getName());
				if (StringUtils.isEmpty(body)) {
					login();
					break;
				}
				if (body.contains("当前访问用户过多，请稍后重试")) {
					System.err.println("登录失败：当前访问用户过多！");
				}
				}catch(Exception ex){
					login();
					break;
				}
			} while (body.contains("请输入正确的验证码")
					|| body.contains("当前访问用户过多，请稍后重试"));
			if(!(body==null||body.contains("请输入正确的验证码"))){
				System.out.println("--登录成功--");
				System.out.println("识别验证码为：" + code);
			}
			//登陆成功等候3s  不然总报错
//			Thread.sleep(3000);
	}

	/**
	 * 多控制台读取验证码
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private static String readString(String msg) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(System.in));
		try {
			System.out.print(msg + ": ");
			return bufferedReader.readLine();
		} catch (Exception e) {
		}
		return "1245";
	}
	private static String getTOKEN(String body){
		String[] token=JavaUtils.match(body, "org.apache.struts.taglib.html.TOKEN[\\w\\W]*</div>");
		return token[0].split("value=\"")[1].split("\"><")[0];
	}
	private static String getLeftTicketStr(String body){
		try{
			String[] token=JavaUtils.match(body, "leftTicketStr[\\w\\W]*>");
			return token[0].split("value=\"")[1].split("/>")[0].split("\"")[0];
		}catch(Exception ex){
			System.out.println("解析出错:"+body);
			throw new IllegalArgumentException(ex);
		}
	}
/**
 * 请求订单参数
 */
private static Map<String,String> submitOrderParams(TicketDO ticket,String orderDate){
		Map<String,String> queryOrderParams= new HashMap<String, String>();
		queryOrderParams.put("station_train_code",ticket.getNum());
		queryOrderParams.put("train_date",orderDate);
		queryOrderParams.put("seattype_num","");
		queryOrderParams.put("from_station_telecode",TrainDataUtils.stationNames.get(ticket.getStart()));
		queryOrderParams.put("to_station_telecode",TrainDataUtils.stationNames.get(ticket.getEnd()));
		queryOrderParams.put("include_student","00");
		queryOrderParams.put("from_station_telecode_name",ticket.getStart());
		queryOrderParams.put("to_station_telecode_name",ticket.getEnd());
		queryOrderParams.put("round_train_date",orderDate);
		queryOrderParams.put("round_start_time_str","00:00--24:00");
		queryOrderParams.put("start_time_str","00:00--24:00");
		queryOrderParams.put("lishi",ticket.getUseTime());
		queryOrderParams.put("train_start_time",ticket.getStartTime());
		queryOrderParams.put("trainno4",ticket.getMeg()[3]);
		queryOrderParams.put("arrive_time",ticket.getEndTime());
		queryOrderParams.put("from_station_name",ticket.getStart());
		queryOrderParams.put("to_station_name",ticket.getEnd());
		queryOrderParams.put("from_station_no",ticket.getMeg()[9]);
		queryOrderParams.put("to_station_no",ticket.getMeg()[10]);
		queryOrderParams.put("ypInfoDetail",ticket.getMeg()[11]);
		queryOrderParams.put("mmStr",ticket.getMeg()[12]);
		queryOrderParams.put("locationCode",ticket.getMeg()[13]);
		return queryOrderParams;
}
/**
 * 提交订单参数
 */
private static Map<String,String> getOrderParams(TicketDO ticket,OrderDO order){
		Map<String,String> submitOrderParams= new HashMap<String, String>();
		submitOrderParams.put("orderRequest.train_date",order.getTrainDate());
		submitOrderParams.put("orderRequest.train_no",ticket.getMeg()[3]);
		submitOrderParams.put("orderRequest.station_train_code",ticket.getNum());
		submitOrderParams.put("orderRequest.from_station_telecode",TrainDataUtils.stationNames.get(ticket.getStart()));
		submitOrderParams.put("orderRequest.to_station_telecode",TrainDataUtils.stationNames.get(ticket.getEnd()));
		submitOrderParams.put("orderRequest.seat_type_code","");
		submitOrderParams.put("orderRequest.seat_detail_type_code","");
		submitOrderParams.put("orderRequest.ticket_type_order_num","");
		submitOrderParams.put("orderRequest.bed_level_order_num","000000000000000000000000000000");

		submitOrderParams.put("orderRequest.start_time",ticket.getStartTime());

		submitOrderParams.put("orderRequest.end_time",ticket.getEndTime());
		submitOrderParams.put("orderRequest.from_station_name",ticket.getStart());
		submitOrderParams.put("orderRequest.to_station_name",ticket.getEnd());
		submitOrderParams.put("orderRequest.cancel_flag","1");
		submitOrderParams.put("orderRequest.id_mode","Y");
		submitOrderParams.put("checkbox9", "Y");
		submitOrderParams.put("orderRequest.reserve_flag", "A");//支付方式
		submitOrderParams.put("oldPassengers", "");
		return submitOrderParams;
}
public UserDO getUser() {
	return user;
}
public void setUser(UserDO user) {
	this.user = user;
}
public void addOrder(OrderDO order){
	this.orders.add(order);
}
private String getCode(File file ,String name) throws Exception{
	return readString(name);//自动识别
}
}