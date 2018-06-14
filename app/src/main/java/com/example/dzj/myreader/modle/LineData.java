package com.example.dzj.myreader.modle;


public class LineData {
	private int lineNum;//行数
	private int chapterNum;//段落字数
	private long size;//段落长度
	private int length;
	 
	public LineData(int lineNum,long size,int length,int chapterNum){
		this.lineNum=lineNum;
		this.size=size;
		this.length=length;
		this.chapterNum=chapterNum;
	}
	public LineData() {
		
	}
	public int getLiteNum(){
		return this.lineNum;
	}
	public int getChapterNum(){
		return this.chapterNum;
	}
	public long getSize(){
		return this.size;
	}
	public int getLength(){
		return this.length;
	}
	public void setLiteNum(int lineNum){
		this.lineNum=lineNum;
	}
	public void setChapterNum(int chapterNum){
		this.chapterNum=chapterNum;
	}
	public void setSize(long size){
		this.size=size;
	}
	public void setLength(int length){
		this.length=length;
	}
	public String toString(){
		return "lineNum="+lineNum+" size="+size+" length="+length+" chapterNum="+chapterNum;
	}
}
