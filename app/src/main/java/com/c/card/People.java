package com.c.card;

public class People {
	public int ID=-1;
	public String Name;
	public String Title;
	public String Address;
	public String Postcode;
	public String Phone;
	public String Mailbox;
	public String Autograph;
	public String Homepahe;
	public String Logo;
	public String Head;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result="";
		result+="ID"+this.ID+"，";
		result+="姓 名:"+this.Name+"，";
		result+="头 衔:"+this.Title+"，";
		result+="地 址:"+this.Address+"，";
		result+="邮 编:"+this.Postcode+"，";
		result+="电 话:"+this.Phone+"，";
		result+="邮 箱:"+this.Mailbox+"，";
		result+="签 名:"+this.Autograph+"，";
		result+="主 页:"+this.Homepahe+"，";
		result+="logo:"+this.Logo+"，";
		result+="head:"+this.Head+"，";
		return result;
	}
	
}
