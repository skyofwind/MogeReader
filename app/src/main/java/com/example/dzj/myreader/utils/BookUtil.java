package com.example.dzj.myreader.utils;

/**
 * Created by dzj on 2017/8/20.
 */

public class BookUtil {
    //封面宽度
    private int width;
    //高度
    private int height;
    //左右边距
    private int hPadding;
    //上下边距
    private int vPadding;

    public BookUtil(double width){
        this.width=(int)width;
        setValue();
    }
    private void setValue(){
        double mheight=width/0.8;
        height=(int)mheight;
        double mhPadding=width*0.16;
        hPadding=(int)mhPadding;
        double mvPadding=mheight*0.10;
        vPadding=(int)mvPadding;
    }
    public int getHeight() {
        return height;
    }

    public int gethPadding() {
        return hPadding;
    }

    public int getvPadding() {
        return vPadding;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void sethPadding(int hPadding) {
        this.hPadding = hPadding;
    }

    public void setvPadding(int vPadding) {
        this.vPadding = vPadding;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public String toString(){
        return "width="+width+"\nheight="+height+"\nleftPadding="+hPadding+"\ntopPadding="+vPadding;
    }
}
