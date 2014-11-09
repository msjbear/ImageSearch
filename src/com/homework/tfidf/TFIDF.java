package com.homework.tfidf;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TFIDF {
	private ArrayList<FilesVector> files = new ArrayList<FilesVector>();// 所有的文档

	private HashMap<FilesVector,HashMap<String, Double>> file_WordTFIDF = new HashMap<FilesVector,HashMap<String, Double>>();
	private TreeMap<String,Integer> dict;
	private TreeMap<String, ArrayList<DocRow>> dimensionReduceDict = new TreeMap<String, ArrayList<DocRow>>();

	private int totalFiles = 0;// 待计算TF和IDF文档总数

	public ArrayList<FilesVector> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<FilesVector> files) {
		this.files = files;
	}

	public TFIDF() {
	}

	// 初始化构造函数
	public TFIDF(ArrayList<FilesVector> af) {
		files = af;
		totalFiles = files.size();
	}

	// 计算文档集中存在该单词的文档数
	public int wordExistsFilesNum(String word) {

		int num = 0;
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).wordExists(word)) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 开始计算每个文档的TFIDF，返回全局的（file,TFIDF）表
	 * 
	 **/
	public HashMap<FilesVector,HashMap<String, Double>> start() {

		ChooseFeatures cf = new ChooseFeatures(files);
		boolean flag = cf.initDict();//初始化词典
		if(flag){
			dict = cf.getDict();//获取词典
		}
		//计算tf-idf
		for (int i = 0; i < files.size(); i++) {
			FilesVector fv = files.get(i);
			Set<String> keys = fv.getFileWords().keySet();//获取各篇文章的单词集合
			HashMap<String,Double> word_TFIDF = new HashMap<String,Double>();

			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				String word = (String) it.next();
				// 不是对于每篇文档中的单词计算TFIDF值，只是计算包含dict中单词的TFIDF
				if (dict.get(word) != null) {
					int num = ((Integer) fv.getFileWords().get(word)).intValue();
					int fileTotalWordsNum = fv.getWordsNum();
					// dict中包含有每个单词在多少文档中出现过
					int wefn = ((Integer) dict.get(word)).intValue();
					//System.out.println("wefn: "+wefn);
					double tf = (double) num / fileTotalWordsNum;
					double idf = (double) totalFiles / wefn;
					//double idf = Math.log(df);
					double tfidf = tf * idf;
					String tfidfStr = new DecimalFormat("#.#####").format(tfidf);
					//各个单词的tf-idf
					word_TFIDF.put(word, new Double(tfidfStr));
				}
			}
			//各篇文章的tf-idf
			file_WordTFIDF.put(fv, word_TFIDF);
		}
		return file_WordTFIDF;
	}

	public Map<String, String> printTFIDf() {
		Map<String, String> dicts = new HashMap<String,String>();
		Set<FilesVector> keys = file_WordTFIDF.keySet();//key:filevector
		//for file level
		for (Iterator<FilesVector> it = keys.iterator(); it.hasNext();) {
			FilesVector fv = (FilesVector) it.next();
			HashMap<String, Double> hm = (HashMap<String, Double>) file_WordTFIDF.get(fv);//word_tfidf
			ArrayList<DocRow> array = new ArrayList<DocRow>();
			Set<String> hmkeys = hm.keySet();//key:word
			//for word level
			for (Iterator<String> it1 = hmkeys.iterator(); it1.hasNext();) {
				String word = (String) it1.next();
				double tfidf = ((Double) hm.get(word)).doubleValue();//value:tfidf
				//求单词在字典中的顺序
				int index = dictIndex(word);
				DocRow dr = new DocRow();
				dr.tfidf = tfidf;
				dr.index = index;
				array.add(dr);
			}
			// 对tfidf按照同数据集合的主词典的顺序排序
			Collections.sort(array, new MyComparator());
			//key:filename,value:list<DocRow>
			dimensionReduceDict.put(fv.getFileName(), array);
			System.out.println("文件" + fv.getFileName() + " 非零向量空间长度：" + hm.size());
		}
		// 将tfidf数据写入labData文件中
		Set<String> drs = dimensionReduceDict.keySet();
		for (Iterator<String> it = drs.iterator(); it.hasNext();) {
			String filename = (String) it.next();
			String vector = buildVector((ArrayList<DocRow>) dimensionReduceDict.get(filename));
			dicts.put(filename,vector);
		}
		return dicts;
	}
	
	public Double calcDocSimilarity() {

		Set<FilesVector> keys = file_WordTFIDF.keySet();//key:filevector
		//for file level
		for (Iterator<FilesVector> it = keys.iterator(); it.hasNext();) {
			FilesVector fv = (FilesVector) it.next();
			HashMap<String, Double> hm = (HashMap<String, Double>) file_WordTFIDF.get(fv);
			ArrayList<DocRow> array = new ArrayList<DocRow>();
			Set<String> hmkeys = hm.keySet();//key:word
			//for word level
			for (Iterator<String> it1 = hmkeys.iterator(); it1.hasNext();) {
				String word = (String) it1.next();
				double tfidf = ((Double) hm.get(word)).doubleValue();
				int index = dictIndex(word);
				DocRow dr = new DocRow();
				dr.tfidf = tfidf;
				dr.index = index;
				array.add(dr);
			}
			// 对tfidf按照同数据集合的主词典的顺序排序
			Collections.sort(array, new MyComparator());
			//filename:arraylist<docrow>
			dimensionReduceDict.put(fv.getFileName(), array);
			System.out.println("文件：" + fv.getFileName() + " 非零向量长度：" + hm.size());
		}

		// 将tfidf数据写入labData文件中
		Set<String> drs = dimensionReduceDict.keySet();//【获取各个文档的特征集】key：filename
		ArrayList<String> lists = new ArrayList<String>();
		for (Iterator<String> it = drs.iterator(); it.hasNext();) {
			String filename = (String) it.next();//filename
			String vec = buildVector((ArrayList<DocRow>) dimensionReduceDict.get(filename));
			lists.add(vec);
		}
		Double score = calSimi(lists.get(0), lists.get(1));
		return score;
	}
	
	private Double calSimi(String vec1,String vec2){
		Double score = 0.0;
		String[] veca = vec1.trim().split(" ");
		String[] vecb = vec2.trim().split(" ");
		if(veca.length!=vecb.length){
			System.err.println("两个文档向量维数不一致！"+veca.length+"   "+vecb.length);
			System.exit(-1);
		}
		if(veca.length!=0&&vecb.length!=0){
			Double vecBoost = 0.0;
			Double vecaBoost = 0.0;
			Double vecbBoost = 0.0;
			
			for(int i=0;i<veca.length;i++){
				vecBoost +=Double.valueOf(veca[i].trim())*Double.valueOf(vecb[i].trim());
				vecaBoost += Double.valueOf(veca[i].trim())*Double.valueOf(veca[i].trim());
				vecbBoost += Double.valueOf(vecb[i].trim())*Double.valueOf(vecb[i].trim());
			}
			score = vecBoost/(Math.sqrt(vecaBoost)*Math.sqrt(vecbBoost));
		}
		return score;
	}
	private String buildVector(ArrayList<DocRow> arrayList) {

		int current = 0;
		StringBuffer sb = new StringBuffer();
		if(arrayList!=null){
			for (DocRow dr : arrayList) {//arraylist:文档中的特征集合
				for (int i = current; i < dr.index; i++) {
					sb.append("0 ");
				}
				if(current==dict.size()-1){
					sb.append(String.valueOf((dr.tfidf)));
					current = dr.index+1;
				}else {
					sb.append(String.valueOf((dr.tfidf)+" "));
					current = dr.index+1;
				}
			}
		}
		for (int j=current; j<dict.size(); j++) {
			if(j==dict.size()-1){
				sb.append("0");
			}else {
				sb.append("0 ");
			}
		}
		sb.append("\r\n");
		return sb.toString();
	}
	//求单词在字典中的顺序
	public int dictIndex(String word) {

		int count = 0;
		Set<String> keys = dict.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String str = (String) it.next();
			if (str.equals(word)) {
				return count;
			}
			count++;
		}
		return -1;
	}

	
	//递归读取文件
	public void readFile(String path) {

		File file = new File(path);

		if (!file.isDirectory()) {
			files.add(new FilesVector(file));
		} else if (file.isDirectory()) {

			File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; i++) {

				if (listFiles[i].isFile()) {
					files.add(new FilesVector(listFiles[i]));
					//System.out.println(listFiles[i]);
				} else {
					readFile(listFiles[i].getPath());
				}
			}
		}
	}
}

class DocRow {
	public int index;
	public double tfidf;
}

class MyComparator implements Comparator<DocRow> {
	@Override
	public int compare(DocRow d1, DocRow d2) {
		if (d1.index > d2.index)
			return 1;
		else if (d1.index < d2.index)
			return -1;
		else
			return 0;
	}

}