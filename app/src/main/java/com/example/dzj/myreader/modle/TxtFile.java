package com.example.dzj.myreader.modle;

import android.os.Parcel;
import android.os.Parcelable;

public class TxtFile implements Parcelable{
    private String name;
    private String path;
    private String size;
    private long lastModified;
    private boolean isDirectory;

    private int id;
    private String charset;
    private int chapter;
    private int page;
    private int chapterNum;

    public TxtFile(){

    }

    public TxtFile(String name, String path, long lastModified, String size, boolean isDirectory){
        this.name=name;
        this.path=path;
        this.lastModified= lastModified;
        this.size=size;
        this.isDirectory = isDirectory;
    }

    protected TxtFile(Parcel in) {
        name = in.readString();
        path = in.readString();
        size = in.readString();
        lastModified = in.readLong();
        isDirectory = in.readByte() != 0;
        id = in.readInt();
        charset = in.readString();
        chapter = in.readInt();
        page = in.readInt();
    }

    public static final Creator<TxtFile> CREATOR = new Creator<TxtFile>() {
        @Override
        public TxtFile createFromParcel(Parcel in) {
            return new TxtFile(in);
        }

        @Override
        public TxtFile[] newArray(int size) {
            return new TxtFile[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public long getLastModified(){
        return this.lastModified;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
    public String getSize(){
        return this.size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(size);
        dest.writeLong(lastModified);
        dest.writeByte((byte) (isDirectory ? 1 : 0));
        dest.writeInt(id);
        dest.writeString(charset);
        dest.writeInt(chapter);
        dest.writeInt(page);
    }

    public String toString(){
        return "name="+name+"\npath="+path+"\ncharset="+charset+"\nchapter="+chapter+"\npage="+page;
    }
}
