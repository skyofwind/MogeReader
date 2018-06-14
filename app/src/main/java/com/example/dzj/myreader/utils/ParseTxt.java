package com.example.dzj.myreader.utils;

import android.content.Context;
import android.net.Uri;

import com.example.dzj.myreader.modle.LineData;
import com.example.dzj.myreader.modle.Paragraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class ParseTxt {
	private String filePath;
	private String charset;
	private String encoding;
	
	public static String[] regex ={
			"^((\\s{0,})(第(\\s{0,})))[零一二三四五六七八九十百千壹贰叁肆伍陆柒捌玖拾佰仟0123456789]{1,7}章.{0,}(\\r\\n){0,1}",
			"^((\\s{0,})(正文{0,}(\\s{0,})))\\d{1,7}.{0,}(\\r\\n){0,1}",
			"^((\\s{0,})(.{0,}(\\s{0,})))\\d{1,7}章.{0,}(\\r\\n){0,1}"
	};

	public ParseTxt(String filePath){
		this.filePath=filePath;
		try {
			this.charset=getCharset(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.encoding=System.getProperty("file.encoding");  
	}
	public String getFilePath(){
		return this.filePath;
	}
	public String getCharset(){
		return this.charset;
	}
	public String getEncoding(){
		return this.encoding;
	}
	public String[] readToString(String filePath,String charset)
    {
        File file = new File(filePath);
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try
        {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        String[] fileContentArr = null;
		try {
			fileContentArr = new String(filecontent,charset).split("\r\n");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return fileContentArr;// 返回文件内容,默认编码
    }
	

	 private static String getCharset(String fileName) throws IOException{
		 System.out.println("getCharset开始：");
		 long st = System.currentTimeMillis();
         CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
         detector.add(new ParsingDetector(false));
         detector.add(JChardetFacade.getInstance());// 需要第三方JAR包:antlr.jar、chardet.jar.
         detector.add(ASCIIDetector.getInstance());
         detector.add(UnicodeDetector.getInstance());
         Charset charset = null;
         File file = new File(fileName);
         try { 
          charset = detector.detectCodepage(file.toURI().toURL());
         } catch (Exception e) { 
        	 e.printStackTrace();
         }
		 System.out.println("结束="+(System.currentTimeMillis()-st));
         /*String charsetName = Const.GBK; 
         if (charset != null) { 
          if (charset.name().equals("US-ASCII")) { 
           charsetName = Const.ISO_8859_1; 
          } else if (charset.name().startsWith("UTF")) { 
           charsetName = charset.name();// 例如:UTF-8,UTF-16BE. 
          } 
         } */
         return charset.name(); 
    }
	 public static String string2Unicode(String string) {
		 
		    StringBuffer unicode = new StringBuffer();
		 
		    for (int i = 0; i < string.length(); i++) {
		 
		        // 取出每一个字符
		        char c = string.charAt(i);
		 
		        // 转换为unicode
		        unicode.append("\\u" + Integer.toHexString(c));
		    }
		 
		    return unicode.toString();
		}
	 public static int getChineseLength(char[] c){
		 int count=0;
		 for (int i = 0; i < c.length; i++) {
			 String len = Integer.toBinaryString(c[i]);
			 if(len.length() > 8)
				 count ++;
		}
		 return count;
	 }
	 public static String hexString2binaryString(String hexString)
		{
			if (hexString == null || hexString.length() % 2 != 0)
				return null;
			String bString = "", tmp;
			for (int i = 0; i < hexString.length(); i++)
			{
				tmp = "0000"
						+ Integer.toBinaryString(Integer.parseInt(hexString
								.substring(i, i + 1), 16));
				bString += tmp.substring(tmp.length() - 4);
			}
			return bString;
		}
		public static String getEncoding(String str) {
			String encode[] = new String[]{
					"UTF-8",
					"ISO-8859-1",
					"GB2312",
					"GBK",
					"GB18030",
					"Big5",
					"Unicode",
					"ASCII"
			};
			for (int i = 0; i < encode.length; i++){
				try {
					if (str.equals(new String(str.getBytes(encode[i]), encode[i]))) {
						return encode[i];
					}
				} catch (Exception ex) {
				}
			}
			
			return "";
		}
		public static String getChapter(long start,long end,String path,String charset) throws IOException{
			File file=new File(path);
			
			RandomAccessFile randomFile=new RandomAccessFile(file, "rw");
			randomFile.seek(start);
			
			byte[] buf=new byte[(int) (end-start)];
			randomFile.read(buf);
			String str=new String(buf,charset);
			
			return str;
		}
		public static List<LineData> getChapterList(String path, String charset) throws IOException{
			long st = System.currentTimeMillis();
			File file=new File(path);
			List<LineData> list=new ArrayList<>();
		    long size=0;
		    int cout=0;
		    int length=0;
		    int mn=0;
		    int lastLength=0;
		    int chapterNum=0;
		    long lastSize=0;

			LineData lineData=new LineData(cout, lastSize,lastLength,-1);
			list.add(lineData);

		    BufferedReader bReader=new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			StringBuilder sb=new StringBuilder();
			while ((mn=bReader.read())!=-1) {
				if(mn=='\r'){
					length++;
					size++;
					continue;
				}
				if(mn=='\n'){
					length++;
					//i++;
					size++;
					String temp=sb.toString();
					size=size+temp.getBytes(charset).length;
					if(Pattern.matches(regex[0], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							list.add(lineData);
							//System.out.println(lastLength+" "+temp);
						}
						
					}else if(Pattern.matches(regex[1], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							list.add(lineData);
							//System.out.println(lastLength+" "+temp);
						}
						
					}else if(Pattern.matches(regex[2], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							list.add(lineData);
							//System.out.println(lastLength+" "+temp);
						}
						
					}
					sb=new StringBuilder();
					lastLength=length;
					lastSize=size;
					cout++;
					//System.out.println(lastLength+" "+temp);
					continue;
				}
				sb.append((char)mn);
				length++;
			}
			String temp=sb.toString();
			size=size+temp.getBytes(charset).length;
			lineData=new LineData(cout, size,length,chapterNum);
			list.add(lineData);
			bReader.close();
			/*for(LineData d:list){
				System.out.println(d.toString());
			}*/
			System.out.println("getChapterList="+(System.currentTimeMillis()-st));
			return list;
			
		}
		public static List<Paragraph> getParagraph(String str){
			List<Paragraph> paragraphs = new ArrayList<>();
			String[] strs = str.replace("\r", "").split("\n");
			for(int i = 0; i < strs.length; i++) {
			    //System.out.println(i+strs[i]);
                Paragraph paragraph = new Paragraph(strs[i]);
                paragraphs.add(paragraph);

			}
			return paragraphs;
		} 
}
