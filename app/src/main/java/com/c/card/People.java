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
		result+="ID"+this.ID+"��";
		result+="�� ��:"+this.Name+"��";
		result+="ͷ ��:"+this.Title+"��";
		result+="�� ַ:"+this.Address+"��";
		result+="�� ��:"+this.Postcode+"��";
		result+="�� ��:"+this.Phone+"��";
		result+="�� ��:"+this.Mailbox+"��";
		result+="ǩ ��:"+this.Autograph+"��";
		result+="�� ҳ:"+this.Homepahe+"��";
		result+="logo:"+this.Logo+"��";
		result+="head:"+this.Head+"��";
		return result;
	}
	
}
