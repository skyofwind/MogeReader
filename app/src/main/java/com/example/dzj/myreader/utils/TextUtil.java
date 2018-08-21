package com.example.dzj.myreader.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;

import com.example.dzj.myreader.modle.Chapter;
import com.example.dzj.myreader.modle.Paragraph;
import com.example.dzj.myreader.modle.TxtLine;
import com.example.dzj.myreader.modle.TxtPager;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    private static String TAG = "TextUtil";
    private static TextUtil textUtil;
    private int minNum;
    private int maxNum;
    private int rows;
    private float rowHeight;
    //private int columns;
    private int maxHeight;
    private int maxWidth;
    private int lineSpacing;
    private TextPaint textPaint;

    private float nomalSize;

    private TextUtil(){ }

    public static TextUtil getInstance(){
        if(textUtil == null){
            textUtil = new TextUtil();
            //init(paint);
        }
        return textUtil;
    }

    public void init(TextPaint paint){
        textPaint = paint;
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        rowHeight = fontMetrics.descent- fontMetrics.ascent;
        lineSpacing = (int)(rowHeight*0.8);

        float tWidth = paint.measureText("是");
        nomalSize = tWidth;

        minNum = (int)(maxWidth/tWidth);
        tWidth = paint.measureText("a");
        maxNum = (int)(maxWidth/tWidth);
        rows = (int)(maxHeight/(rowHeight+lineSpacing)-1);
    }

    int p ;
    int c ;
    TxtPager pager;
    int myValue = 0;
    public void dealChpter(Chapter chapter){
        p = 1;
        c = 0;
        List<Paragraph> paragraphs = chapter.getParagraphs();
        List<TxtPager> pagers = new ArrayList<>();
        boolean isFinish = false;
        pager = new TxtPager();
        //log("paragraphs.size() = "+paragraphs.size());
        int i = 0;
        if(paragraphs.size() > 1){
            i = 1;
        }
        for(; i < paragraphs.size(); i++){//第一段默认是标题，跳过
            String str = paragraphs.get(i).getStrParagraph();
            if(textPaint.measureText(str) <= maxWidth){
                if(i == paragraphs.size()-1){
                    isFinish = true;
                }
                addColumns(pagers, i, 0, str.length(), isFinish);
            }else {
                int length = str.length();
                int mStart = 0;
                int end = (minNum+maxNum)/2+2;
                while (length >= minNum){
                    calCutOffPosition(str, mStart, mStart+minNum, 0);
                    length = length-myValue;
                    if(i == paragraphs.size()-1){
                        if(length == 0){
                            isFinish = true;
                        }
                    }
                    addColumns(pagers, i, mStart, mStart+myValue, isFinish);
                    mStart = mStart+myValue;
                    myValue = 0;
                }
                if(length > 0){
                    if(i == paragraphs.size()-1){
                        isFinish = true;
                    }
                    addColumns(pagers, i, mStart, str.length(), isFinish);
                }
                isFinish = false;
            }
        }
        chapter.setPagers(pagers);
        for(int j = 0; j < paragraphs.size(); j++){
            String temp = paragraphs.get(j).getStrParagraph();
            //log(j+" : "+temp.length()+" "+temp);
        }
    }
    private void calCutOffPosition(String str, int start, int end, float lastLength){
        float tLength;
        if(end > str.length() ){
            tLength = textPaint.measureText(str.substring(start, str.length()));
            if(tLength <= maxWidth){
                myValue = str.length()-start;
                return ;
            }else {
                calCutOffPosition(str, start, str.length(), tLength);
                return;
            }
        }
        tLength = textPaint.measureText(str.substring(start, end));
        //log("tLength = "+tLength);
        //log("str = "+str+" lastLength = "+lastLength+" start = "+start+" end = "+end);
        if(tLength == maxWidth){
            myValue = end-start;
            return;
        }

        if(maxWidth>tLength && maxWidth-tLength <= 150){
            int mEnd= end;
            while(tLength <= maxWidth){
                mEnd++;
                if(mEnd <= str.length()){
                    tLength = textPaint.measureText(str.substring(start, mEnd));
                }else{
                    break;
                }
            }
            myValue = mEnd-1-start;
            return;
        }


        if(tLength > maxWidth){
            calCutOffPosition(str, start, (start+minNum+end)/2, tLength);
        }else if(tLength < maxWidth){
            calCutOffPosition(str, start, (start+maxNum+end)/2, tLength);
        }
        return;
    }
    private void addColumns(List<TxtPager> pagers, int position, int start, int end, boolean isFinish){
        TxtLine txtLine = new TxtLine();
        if(c == rows){
            pagers.add(pager);
            pager = new TxtPager();
            p++;
            c = 1;
            txtLine.setPosition(position);
            txtLine.setStart(start);
            txtLine.setEnd(end);
            pager.addTxtLine(txtLine);
        }else {
            c++;
            txtLine.setPosition(position);
            txtLine.setStart(start);
            txtLine.setEnd(end);
            pager.addTxtLine(txtLine);
        }
        if(isFinish){
            pagers.add(pager);
        }
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public static void log(String str){
        Log.d(TAG, str);
    }

    public void setWidthAndHeight(int width, int height){
        setMaxWidth(width);
        setMaxHeight(height);
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(float rowHeight) {
        this.rowHeight = rowHeight;
    }

    public TextPaint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(TextPaint textPaint) {
        this.textPaint = textPaint;
    }

    public String toString(){
        return "\nminNum="+minNum+"\nmaxNum="+maxNum+"\nrows="+rows+"\nmaxWidth="+maxWidth+"\nmaxHeight="+maxHeight;
    }
}
