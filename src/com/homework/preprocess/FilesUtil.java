package com.homework.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FilesUtil {

	private static List<File> lists = new ArrayList<File>();
	public static String readPage(File page) throws IOException {
		/*
		 * 注意编码格式
		 * 注意写入写出编码格式需对应，否则会出现乱码
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(new  FileInputStream(page),"UTF-8"));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line=reader.readLine())!=null){
			sb.append(line.trim()+" ");
		}
		reader.close();
		return  sb.toString();
	}

	public static List<File> readFile(String path) {
		
		File file = new File(path);
		
		if(!file.isDirectory()){
			lists.add(file);
		}
		else if(file.isDirectory()){
			
			File[] listFiles = file.listFiles();
			for(int i=0;i<listFiles.length;i++){
				
				if(listFiles[i].isFile()){
					lists.add(listFiles[i]);
					//System.out.println("File:"+(++k)+"   "+listFiles[i]);
				}else {
					readFile(listFiles[i].getPath());
				}
			}
		}
		return lists;
	}

	public FilesUtil() {
		super();
	}

}