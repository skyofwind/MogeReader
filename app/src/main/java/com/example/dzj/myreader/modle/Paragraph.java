package com.example.dzj.myreader.modle;

public class Paragraph {
	private String strParagraph;
	private int startPage;
	private int startLine;
	private int endPage;
	private int endLine;
	
	public Paragraph(String str) {
		this.strParagraph = str;
	}
	
	public String getStrParagraph() {
		return strParagraph;
	}
	public void setStrParagraph(String strParagraph) {
		this.strParagraph = strParagraph;
	}
	public int getStartPage() {
		return startPage;
	}
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndPage() {
		return endPage;
	}
	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public String toString() {
		return strParagraph;
	}
}
