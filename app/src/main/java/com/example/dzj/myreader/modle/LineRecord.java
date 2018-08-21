package com.example.dzj.myreader.modle;

public class LineRecord {
    public long size;
    public int count;
    public int length;
    public int chapterNum;
    public StringBuilder sb;

    public LineRecord() {
        size = 0;
        count = 0;
        length = 0;
        chapterNum = 0;
        sb = new StringBuilder();
    }
}
