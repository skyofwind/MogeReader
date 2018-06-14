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

    public Chapter getChapter(int position) throws IOException {
        if (filePath != null && lineDatas!= null && charset != null){
            String str = ParseTxt.getChapter(lineDatas.get(position-1).getSize(), lineDatas.get(position).getSize(), filePath, charset);
            Chapter chapter = new Chapter(str, position);
            return chapter;
        }
        return null;
    }
}
