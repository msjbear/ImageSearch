package com.homework.tfidf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FilesVector {

	private File file;
	private HashMap<String,Integer> words = new HashMap<String,Integer>();// 文档单词集合
	private int wordsNum = 0;// 文件中含有单词的总数

	public FilesVector(File f) {
		file = f;
		BufferedReader reader = null;
		StringBuffer sBuffer = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				sBuffer.append(tempString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String[] words = sBuffer.toString().split("\\t");
		for (int i = 0; i < words.length; i++) {
			addWords(words[i]);
		}
	}

	private void addWords(String word) {
		if (words.get(word) == null) {
			words.put(word, new Integer(1));
		} else {
			int num = ((Integer) words.get(word)).intValue();
			words.put(word, new Integer(num + 1));
		}
		wordsNum++;
	}

	public int getNum(String word) {
		if (words.get(word) == null) {
			return 0;
		}
		return ((Integer) words.get(word)).intValue();
	}

	public String getFileName() {
		return file.getName();
	}

	public String getFilePath() {
		return file.getPath();
	}

	public Map<String,Integer> getFileWords() {
		return words;// 文档单词集合
	}

	public int getWordsNum() {
		return wordsNum;
	}

	public boolean wordExists(String word) {
		if (words.get(word) != null) {
			return true;
		} else
			return false;
	}

	public String toString() {
		Set<String> keys = words.keySet();
		String temp = "";
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String str = (String) iterator.next();
			int num = ((Integer) words.get(str)).intValue();
			temp += str;
			temp += ":" + num;
			temp += "\n";
		}
		return temp;
	}
}