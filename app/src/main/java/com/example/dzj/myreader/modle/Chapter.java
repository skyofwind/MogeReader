package com.example.dzj.myreader.modle;

import android.util.Log;

import com.example.dzj.myreader.utils.ParseTxt;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements Cloneable{
	//private String strChapter;
	private List<Paragraph> paragraphs;
	private List<TxtPager> pagers;
	private int chapterNum;
	private String title;
	private int isRead;
	private int id;


	public Chapter(String str, int num) {
		//this.strChapter = str;
		this.chapterNum = num;
		paragraphs = ParseTxt.getParagraph(str);
		pagers = new ArrayList<>();
	}

	public Chapter(String str){
		//this.strChapter = str;
		paragraphs = ParseTxt.getParagraph(str);
		pagers = new ArrayList<>();
	}

	public Chapter(){}

//	public String getStrChapter() {
//		return strChapter;
//	}

//	public void setStrChapter(String strChapter) {
//		this.strChapter = strChapter;
//	}

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

//	public String toString() {
//		return strChapter;
//	}

	public void log(){

		for(int i = 0; i < pagers.size(); i++){
			TxtPager pager = pagers.get(i);
			List<TxtLine> lines = pager.getLines();
			for(int j = 0; j < lines.size(); j++){
				TxtLine line = lines.get(j);
				Log.d("pager", "第"+i+"页"+j+"行="+getString(line.getPosition(), line.getStart(), line.getEnd()));
			}
		}
	}
	public String getString(int position, int start, int end){
		String p = paragraphs.get(position).getStrParagraph();
		return p.substring(start, end);
	}
	public int getChapterNum() {
		return chapterNum;
	}

	public void setChapterNum(int chapterNum) {
		this.chapterNum = chapterNum;
	}

	public List<TxtPager> getPagers() {
		return pagers;
	}

	public void setPagers(List<TxtPager> pagers) {
		this.pagers = pagers;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Chapter clone() {
		Chapter chapter = null;
		try {
			chapter = (Chapter)super.clone();
			chapter.setParagraphs(Lists.newArrayList(paragraphs));
			chapter.setPagers(Lists.newArrayList(pagers));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return chapter;
	}
}
