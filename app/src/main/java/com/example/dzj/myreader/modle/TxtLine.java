package com.example.dzj.myreader.modle;

public class TxtLine {
    private int position;//文章段数
    private int start;//段字符串中的开始位置
    private int end;//结束位置

    public TxtLine(){}

    public TxtLine(int start, int end){
        this.start = start;
        this.end = end;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
