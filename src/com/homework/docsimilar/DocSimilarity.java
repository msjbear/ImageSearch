package com.homework.docsimilar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.homework.tfidf.FilesVector;
import com.homework.tfidf.TFIDF;

public class DocSimilarity {

	private static final String DOC_PATH1="output/CN/News_10_CN.txt";
	private static final String DOC_PATH2="output/CN/News_99_CN.txt";
	
	public static void main(String[] args) {
		
		ArrayList<FilesVector> files = new ArrayList<FilesVector>();// 所有的文档
		HashMap<FilesVector,HashMap<String, Double>> file_WordTFIDF = new HashMap<FilesVector,HashMap<String, Double>>();
		
		File doc1 = new File(DOC_PATH1);
		File doc2 = new File(DOC_PATH2);
		files.add(new FilesVector(doc1));
		files.add(new FilesVector(doc2));
		
		TFIDF tfidfCal = new TFIDF(files);
		file_WordTFIDF = tfidfCal.start();
		Set<FilesVector> filetf = file_WordTFIDF.keySet();
		
		for(Iterator<FilesVector> iter = filetf.iterator(); iter.hasNext();){
			FilesVector fv = iter.next();
			HashMap<String, Double> word = file_WordTFIDF.get(fv);
			System.out.println("文档【"+fv.getFileName()+"】feature tfidf vector： "+word);
		}
		/*
		 * 不能将两个相同文档进行相似度比较
		 */
		Double score = tfidfCal.calcDocSimilarity();
		System.err.println("【"+DOC_PATH1.substring(7)+"】  与   【"+DOC_PATH2.substring(7)+"】的文档相似度为："+score);
	}
}
