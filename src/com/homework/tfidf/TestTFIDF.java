package com.homework.tfidf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class TestTFIDF {
	public static void main(String[] args) throws IOException {

		TFIDF cal = new TFIDF();
		cal.readFile("output/EN");
		TFIDF cal2 = new TFIDF(cal.getFiles());
		cal2.start();
		Map<String, String> worddict = cal2.printTFIDf();
		Set<String> wordSet = worddict.keySet();
		
		FileWriter writer = new FileWriter(new File("tfidf/tfidf_en.txt"));
		for(Iterator<String> iter = wordSet.iterator();iter.hasNext();){
			String fileName = iter.next();
			String vector = worddict.get(fileName);
			writer.write(fileName+"\t"+vector);
		}
		writer.close();
	}
}