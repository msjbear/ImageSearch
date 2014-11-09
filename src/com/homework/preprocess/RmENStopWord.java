package com.homework.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sjmei
 * @date   2012-10-29
 */
public class RmENStopWord {

	private static Set<String> stopSet = new HashSet<String>();
	private static StringBuilder sBuilder;
	
	public static void main(String[] args) throws Exception {
		
		String dirpath = "preoutput/EN";
		String stoppath = "stopwords/英文停用词词表.txt";
		long start = System.currentTimeMillis();
		
			
		List<File> lists = FilesUtil.readFile(dirpath);
		for(Iterator<File> iter = lists.iterator();iter.hasNext();){
			
			File file = (File)iter.next();
			removeStopWord(file, stoppath);
		}
	
		long end = System.currentTimeMillis();
		System.out.println("停用词过滤完毕,用时："+(end-start)+"ms");
	}
	/**
	 *@param file 待处理文件
	 *@param stopfile  停用词表
	 *@param mode 模式1:表示处理中文停用词，其它模式表示处理英文停用词
	 */
	public static void removeStopWord(File file,String stopFile) throws Exception{
		
		sBuilder = new StringBuilder();
		String pageStr = FilesUtil.readPage(file);
		
		Set<String> stopSets = buildStopWords(stopFile);
		StringTokenizer tokenizer = new StringTokenizer(pageStr);
		while(tokenizer.hasMoreTokens()){
			String eachStr = tokenizer.nextToken();
			Matcher matcher = Pattern.compile("[A-Za-z]{2,20}+").matcher(eachStr);
		    
		    while(matcher.find()){
		    	String word = matcher.group();
		    	if(!stopSets.contains(word.trim().toLowerCase())){
		    		System.out.println(word);
			    	sBuilder.append(word+"\t");
		    	}
		    }
		}
		File path = new File("output/EN");
		if(!path.exists()){
			path.mkdir();
		}
	    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path+"/"+file.getName())),"UTF-8"));
		writer.write(sBuilder.toString());
		writer.close();
	}
	/**
	 * 构建停用词表
	 * 
	 * @param 停用词表文件
	 */
	private static Set<String> buildStopWords(String stopFile) {
		
		String stopwords;
		try {
			stopwords = FilesUtil.readPage(new File(stopFile));
			StringTokenizer tokenizer = new StringTokenizer(stopwords);
			
			while(tokenizer.hasMoreTokens()){
				String stopword = tokenizer.nextToken();
				stopSet.add(stopword);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stopSet;
	}
}
