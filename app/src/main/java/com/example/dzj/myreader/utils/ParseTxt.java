package com.example.dzj.myreader.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.dzj.myreader.modle.Chapter;
import com.example.dzj.myreader.modle.LineData;
import com.example.dzj.myreader.modle.LineRecord;
import com.example.dzj.myreader.modle.Paragraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class ParseTxt {
	public static int PARAGRAPH_MAX_LENGTH = 1000;//限定文字段落字符串的最大长度
	public static int SENTENCE_MAX_LENGTH = 700;//段落过大需要分割时，限定新分割段落最大不超过700个字符串

	private String filePath;
	private String charset;
	private String encoding;
	
	public static String[] regex ={
			"^((\\s{0,})(第(\\s{0,})))[零一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟萬0123456789]{1,7}章.{0,}(\\r\\n){0,1}",
			"^((\\s{0,})(正文{0,}(\\s{0,})))\\d{1,7}.{0,}(\\r\\n){0,1}",
			"^((\\s{0,})(.{0,}(\\s{0,})))\\d{1,7}章.{0,}(\\r\\n){0,1}",
			"^((\\s{0,})(【{1,}.{1,}】{1,})(.{0,})[零一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟萬0123456789]{1,7}(.{0,})).{0,}(\\r\\n){0,}"	};

	public ParseTxt(String filePath){
		this.filePath = filePath;
		try {
			this.charset = resolveCode(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.encoding = System.getProperty("file.encoding");
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

	public static String resolveCode(String path) throws Exception {
		long last = System.currentTimeMillis();
		InputStream inputStream = new FileInputStream(path);
		byte[] head = new byte[3];
		inputStream.read(head);
		String code = "gb2312";  //或GBK
		if (head[0] == -1 && head[1] == -2 )
			code = "UTF-16";
		else if (head[0] == -2 && head[1] == -1 )
			code = "Unicode";
		else if(head[0]==-17 && head[1]==-69 && head[2] ==-65)
			code = "UTF-8";

		inputStream.close();

		System.out.println(code+" "+(System.currentTimeMillis()-last));
		return code;
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
		//根据位置读取txt中的章节
		public static Chapter getChapter(long end, LineData lineData, String path, String charset) throws IOException{
			File file=new File(path);
			RandomAccessFile randomFile=new RandomAccessFile(file, "rw");
			randomFile.seek(lineData.getSize());
			byte[] buf=new byte[(int) (end-lineData.getSize())];
			randomFile.read(buf);
			randomFile.close();
			Chapter chapter = new Chapter(new String(buf,charset));
			chapter.setId(lineData.getId());
			chapter.setTitle(lineData.getChapterTitle());
			chapter.setIsRead(lineData.getIsRead());
			return chapter;
		}
        //读取章节中的第一行
        public static String getChapterTitle(long start, String path, String charset) throws IOException{
            File file=new File(path);
            RandomAccessFile randomFile=new RandomAccessFile(file, "rw");
            randomFile.seek(start);
            String temp = randomFile.readLine();
            randomFile.close();
			Log.d("getChapterTitle",""+start+" "+path+" "+charset);
            return new String(temp.getBytes("ISO-8859-1"), charset);
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

			LineData lineData=new LineData(cout, lastSize,lastLength,0);
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
					size++;
					String temp=sb.toString();
					size=size+temp.getBytes(charset).length;


					if(Pattern.matches(regex[0], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							lineData.setChapterTitle(temp);
							list.add(lineData);
							Log.d("mytitleofme1", temp);
							//System.out.println(lastLength+" "+temp);
						}
						
					}else if(Pattern.matches(regex[1], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							lineData.setChapterTitle(temp);
							list.add(lineData);
							Log.d("mytitleofme2", temp);
							//System.out.println(lastLength+" "+temp);
						}
						
					}else if(Pattern.matches(regex[2], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							lineData.setChapterTitle(temp);
							list.add(lineData);
							Log.d("mytitleofme3", temp);
							//System.out.println(lastLength+" "+temp);
						}
						
					} else if(Pattern.matches(regex[3], temp)){
						if(temp.length()<30){
							chapterNum++;
							lineData=new LineData(cout, lastSize,lastLength,chapterNum);
							lineData.setChapterTitle(temp);
							list.add(lineData);
							Log.d("mytitleofme4", temp);
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
	public static List<LineData> getChapterListByReadLine(String path, String charset) throws IOException{
		long st = System.currentTimeMillis();
		File file=new File(path);
		List<LineData> list=new ArrayList<>();
		LineRecord record = new LineRecord();

		LineData lineData=new LineData(record.count, record.size, record.length, record.chapterNum);
		list.add(lineData);

		BufferedReader bReader=new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		String line = "";
		while((line = bReader.readLine()) != null) {
			if(record.count == 0){
				record.sb.append(line);
			}
			if(Pattern.matches(regex[0], line)){
				addChapterRecordOfText(record, list, charset, line);
			}else if(Pattern.matches(regex[1], line)){
				addChapterRecordOfText(record, list, charset, line);
			}else if(Pattern.matches(regex[2], line)){
				addChapterRecordOfText(record, list, charset, line);
			}else if(Pattern.matches(regex[3], line)){
				addChapterRecordOfText(record, list, charset, line);
			}
			if(record.count > 0){
				record.sb.append(line);
			}
			record.count++;
			record.size += 2;


		}
		if(record.sb.toString() != null){
			addChapterRecord(record, list, charset, line);
		}

		bReader.close();
		return list;
	}

	private static void addChapterRecordOfText(LineRecord record, List<LineData> list, String charset, String line) throws UnsupportedEncodingException {
		if(line.length()<30){
//			String temp = record.sb.toString();
//			if(temp.length() <= 250){
//				temp = temp.replaceAll(" ", "");
//				temp = temp.replaceAll("\r\n", "");
//				if(temp.length() <= 100){
//					return;
//				}
//			}
			addChapterRecord(record, list, charset, line);
		}
	}

	private static void addChapterRecord(LineRecord record, List<LineData> list, String charset, String line) throws UnsupportedEncodingException {
		record.chapterNum++;
		String temp = record.sb.toString();
		record.size += temp.getBytes(charset).length;
		record.length += temp.length();
		if(line != null){
			Log.e("parseTxt", line);
		}
		LineData lineData = new LineData(record.count, record.size, record.length, record.chapterNum);
		lineData.setChapterTitle(line);
		list.add(lineData);
		record.sb = new StringBuilder();

	}

	public static List<Paragraph> getParagraph(String str){
		List<Paragraph> paragraphs = new ArrayList<>();
		String[] strs = str.replace("\r", "").split("\n");
		int spaceCount = 0;
		for(int i = 0; i < strs.length; i++) {
			//System.out.println(i+strs[i]);
			if(strs[i].length() == 0){
				continue;
			}
			if(strs[i].length() > PARAGRAPH_MAX_LENGTH){
				List<Paragraph> paragraphList = dealParagraph(strs[i]);
				for(Paragraph p : paragraphList){
					paragraphs.add(p);
				}
			}else {
				paragraphs.add(new Paragraph(strs[i]));
//				if(spaceCount < 1){
//					paragraphs.add(new Paragraph(strs[i]));
//					System.out.println(i+" "+strs[i]);
//				}
//				if(strs[i].length() == 0){
//					spaceCount++;
//				}else {
//					spaceCount = 0;
//				}
			}
		}
		return paragraphs;
	}

	public static List<Paragraph> dealParagraph(String s){
		int i = s.length()/700;
		List<Paragraph> paragraphs = new ArrayList<>(i+1);
		int last = 0;
		for(int j = 1; j <= i; j++){
			int posistion = s.lastIndexOf("。", SENTENCE_MAX_LENGTH*j);
			paragraphs.add(new Paragraph(s.substring(last, posistion+1)));
			last = posistion+1;
		}
		paragraphs.add(new Paragraph(s.substring(last, s.length())));
		return paragraphs;
	}

	//保留分隔符的split方法
	public static String[] splitString(String str, String regex, int limit) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        /* fastpath if the regex is a
         (1)one-char String and this character is not one of the
            RegEx's meta characters ".$|()[{^?*+\\", or
         (2)two-char String and the first char is the backslash and
            the second is not the ascii digit or ascii letter.
         */
		char[] regexValue = regex.toCharArray();
		char[] value = str.toCharArray();

		char ch = 0;
		if (((regexValue.length == 1 &&
				".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) ||
				(regex.length() == 2 &&
						regex.charAt(0) == '\\' &&
						(((ch = regex.charAt(1))-'0')|('9'-ch)) < 0 &&
						((ch-'a')|('z'-ch)) < 0 &&
						((ch-'A')|('Z'-ch)) < 0)) &&
				(ch < Character.MIN_HIGH_SURROGATE ||
						ch > Character.MAX_LOW_SURROGATE))
		{
			System.out.println("splitString");
			int off = 0;
			int next = 0;
			boolean limited = limit > 0;
			ArrayList<String> list = new ArrayList<>();
			while ((next = str.indexOf(ch, off)) != -1) {
				if (!limited || list.size() < limit - 1) {
					list.add(str.substring(off, next+1));
					off = next + 1;
				} else {    // last one
					//assert (list.size() == limit - 1);
					list.add(str.substring(off, value.length));
					off = value.length;
					break;
				}
			}
			// If no match was found, return this
			if (off == 0)
				return new String[]{str};

			// Add remaining segment
			if (!limited || list.size() < limit)
				list.add(str.substring(off, value.length));

			// Construct result
			int resultSize = list.size();
			if (limit == 0) {
				while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
					resultSize--;
				}
			}
			String[] result = new String[resultSize];
			return list.subList(0, resultSize).toArray(result);
		}
		return splitPattern(str, regex, limit);
	}

	public static String[] splitPattern(CharSequence input, String regex, int limit) {
		Pattern pattern = Pattern.compile(regex);
		int index = 0;
		boolean matchLimited = limit > 0;
		ArrayList<String> matchList = new ArrayList<>();
		Matcher m = pattern.matcher(input);
		System.out.println("splitPattern");
		// Add segments before each match found
		while(m.find()) {
			if (!matchLimited || matchList.size() < limit - 1) {
				if (index == 0 && index == m.start() && m.start() == m.end()) {
					// no empty leading substring included for zero-width match
					// at the beginning of the input char sequence.
					continue;
				}
				String match = input.subSequence(index, m.start()+1).toString();
				if(match.length() > 1) {
					matchList.add(match);
				}
				index = m.end();
			} else if (matchList.size() == limit - 1) { // last one
				String match = input.subSequence(index,
						input.length()).toString();
				matchList.add(match);
				index = m.end();
			}
		}

		// If no match was found, return this
		if (index == 0) {
			return new String[] {input.toString()};
		}


		// Add remaining segment
		if (!matchLimited || matchList.size() < limit) {
			matchList.add(input.subSequence(index, input.length()).toString());
		}

		// Construct result
		int resultSize = matchList.size();
		if (limit == 0) {
			while (resultSize > 0 && matchList.get(resultSize-1).equals(""))
				resultSize--;
		}

		String[] result = new String[resultSize];
		return matchList.subList(0, resultSize).toArray(result);
	}
}
