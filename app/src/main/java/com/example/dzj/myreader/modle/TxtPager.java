package com.example.dzj.myreader.modle;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class TxtPager implements Cloneable{
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

    @Override
    public TxtPager clone() {
        TxtPager txtPager = null;
        try {
            txtPager = (TxtPager)super.clone();
            txtPager.setLines(Lists.newArrayList(lines));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return txtPager;
    }
}
