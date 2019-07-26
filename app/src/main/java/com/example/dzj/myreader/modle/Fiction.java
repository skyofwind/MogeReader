package com.example.dzj.myreader.modle;

import com.example.dzj.myreader.utils.ParseTxt;
import java.io.IOException;
import java.util.List;

public class Fiction {
    private String name;
    private String filePath;
    private String charset;
    private List<LineData> lineDatas;
    private int maxChapter;
    private int sequence;
    private int hasForeword;

    public Fiction(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LineData> getLineDatas() {
        return lineDatas;
    }

    public void setLineDatas(List<LineData> lineDatas) {
        this.lineDatas = lineDatas;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getMaxChapter() {
        return maxChapter;
    }

    public void setMaxChapter(int maxChapter) {
        this.maxChapter = maxChapter;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getHasForeword() {
        return hasForeword;
    }

    public void setHasForeword(int hasForeword) {
        this.hasForeword = hasForeword;
    }

    public Chapter getChapter(int position) throws IOException {
        int target = 0;
        if (lineDatas.size() == 2) {
            target = 1;
            position = 0;
        } else {
            target = position + 1;
        }
        if (filePath != null && lineDatas!= null && charset != null){
            Chapter chapter = ParseTxt.getChapter(lineDatas.get(target).getSize(), lineDatas.get(position), filePath, charset);
            chapter.setChapterNum(position);

            return chapter;
        }
        return null;
    }

    public String toString(){
        return "name = "+name
                +"\n"+"charset = "+charset
                +"\n"+"maxChapter = "+maxChapter
                +"\n"+"lineDatas.size = "+lineDatas.size();
    }
}
