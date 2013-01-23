package com.heavenly.ticket.pojo;

public class UserDO {
	private String name;
	public UserDO(String name,String pass){
		this.name = name;
		this.pass = pass;
	}
	public UserDO(String name,String pass,String email){
		this(name,pass);
		this.email = email ;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private String pass;
	private String email;
}
