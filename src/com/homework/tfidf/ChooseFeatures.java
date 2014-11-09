package com.homework.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/*
 *确定特征向量空间，降低维数，统一文本向量
 * 
 **/

public class ChooseFeatures {

	private ArrayList<FilesVector> dataInput;
	private TreeMap<String,Integer> primeDict = new TreeMap<String,Integer>();// 文档集合中的主词典(对应关系：单词-包含该单词的文档个数)
	@SuppressWarnings("unused")
	private int totalFiles = 0;
	//private static int CHOOSEFACTOR = 20;
	private static int CHOOSEFACTOR = 1;

	public ChooseFeatures(ArrayList<FilesVector> files) {
		dataInput = files;
		totalFiles = files.size();
	}

	public void addDictWords(String word) {
		if (primeDict.get(word) == null) {
			primeDict.put(word, new Integer(1));
		} else {
			int n = ((Integer) primeDict.get(word)).intValue();
			primeDict.put(word, new Integer(n + 1));
		}
	}

	public boolean initDict() {

		for (FilesVector fv : dataInput) {
			// 获取每篇文档的单词集合
			HashMap<String,Integer> hm = (HashMap<String,Integer>) fv.getFileWords();
			Set<String> word = hm.keySet();
			for (Iterator<String> it = word.iterator(); it.hasNext();) {
				String str = (String) it.next();
				addDictWords(str);// 将所有不同的单词加入到词典(primeDict)中
			}
		}

		System.out.println("文档集合共包含【" + dataInput.size() + "】篇文档");
		System.out.println("文档集合中包含单词总数为：" + primeDict.size());

		Set<String> keys = primeDict.keySet();

		/*
		 * 降维并统一向量空间，去除小于10的单词
		 */

		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			int value = ((Integer) primeDict.get(key)).intValue();

			if (value <= CHOOSEFACTOR) {//去除出现频度小于2的单词
				it.remove();
			}
		}
		System.out.println("文档集合的向量空间为：" + primeDict.size());
		return true;
	}

	public TreeMap<String,Integer> getDict() {
		return primeDict;
	}

}
