package com.example.dzj.myreader.modle;

import android.util.Log;

import com.example.dzj.myreader.utils.ParseTxt;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
	private String strChapter;
	private List<Paragraph> paragraphs;
	private List<TxtPager> pagers;
	private int chapterNum;

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

	public Chapter(String str, int num) {
		this.strChapter = str;
		this.chapterNum = num;
		paragraphs = ParseTxt.getParagraph(str);
		pagers = new ArrayList<>();
	}

	public Chapter(){}

	public String getStrChapter() {
		return strChapter;
	}

	public void setStrChapter(String strChapter) {
		this.strChapter = strChapter;
	}

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public String toString() {
		return strChapter;
	}

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
}
