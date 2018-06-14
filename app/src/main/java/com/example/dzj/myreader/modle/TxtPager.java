package com.example.dzj.myreader.modle;

import java.util.ArrayList;
import java.util.List;

public class TxtPager {
    private List<TxtLine> lines;

    public TxtPager(){
        lines = new ArrayList<>();
    }

    public List<TxtLine> getLines() {
        return lines;
    }

    public void setLines(List<TxtLine> lines) {
        this.lines = lines;
    }

    public void addTxtLine(TxtLine line){
        if(lines == null){
            lines = new ArrayList<>();
        }
        lines.add(line);
    }

    public int getSize(){
        return lines.size();
    }

    public TxtLine getLine(int position){
        return lines.get(position);
    }

}
