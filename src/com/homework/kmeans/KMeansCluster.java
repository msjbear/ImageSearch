package com.homework.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class KMeansCluster {
	
	private static final String PATH = "tfidf/tfidf_en.txt";
	private static final String CLUTSER = "clusters";
	private static final Double Delta = 0.0001;
	private static final int ITERATION = 3;
	private static Map<String, String> dict = new HashMap<String, String>();
	private int COLS = 0;
	public KMeansCluster() {
		dict = readDict(PATH);
		File file = new File(CLUTSER);
		if(!file.exists()){
			file.mkdir();
		}
	}
	//读取文档向量
	private Map<String, String> readDict(String path) {
		Map<String, String> dict = new HashMap<String, String>();
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			try {
				while((line=reader.readLine())!=null){
					String[] vecs = line.split("\t");
					dict.put(vecs[0], vecs[1]);//文档名,文档向量表示
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return dict;
		} catch (FileNotFoundException e) {
			System.err.println("文件不存在");
		}
		return null;
	}
	//初始化聚类中心（随机选择k个聚类中心）
	public ArrayList<String> initialCenter(int k){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> centers = new ArrayList<String>();
		Set<String> keys = dict.keySet();
		for(Iterator<String> iter=keys.iterator();iter.hasNext();){
			String filename = iter.next();
			list.add(dict.get(filename));
			String[] vec = dict.get(filename).split(" ");
			if(vec.length!=19298){
				//System.err.println(vec.length);
			}
			COLS = vec.length;
		}
		int scope = list.size();
		Random random = new Random(System.currentTimeMillis());
		int i = 0;
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(CLUTSER+"/initCenter.txt"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (i < k) {
			int seed = Math.abs(random.nextInt() % scope);
			if (!centers.contains(list.get(seed))) {
				centers.add(list.get(seed));
				try {
					//System.out.println(list.get(seed));
					writer.append(list.get(seed)+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return centers;
	}
	
	//kmeans聚类实现
	public void kmeans(Map<String, String> dict,ArrayList<String> centers){
		long start = System.currentTimeMillis();
		for(int k=1;k<=ITERATION;k++){
			//将各文档分配到最邻近聚类中心
			Map<String, Set<HashMap<String, String>>> cluster = classify(dict,centers);
			//更新聚类中心
			ArrayList<String> newCenter = updateCenter(centers, cluster);
			//判断是否收敛
			boolean flag = isConverge(newCenter, centers, Delta);
			if(!flag&&k<ITERATION){
				System.out.println("第"+k+"次迭代结束");
				centers = newCenter;
			}else {
				System.out.println("迭代结束");
				long end = System.currentTimeMillis();
				printResult(cluster);
				System.out.println("聚类结束，总共花费："+(end-start)+"ms");
				return;
			}
		}
	}
	//将属于不同聚类的文档分别输出到不同的文件中
	private void printResult(Map<String, Set<HashMap<String, String>>> cluster) {
		
		int classNo = 1;
		Set<String> clusterKeySet = cluster.keySet();
		for(Iterator<String> iter=clusterKeySet.iterator();iter.hasNext();){
			String oldCenter = iter.next();//key:center
			Set<HashMap<String, String>> maps = cluster.get(oldCenter);//value:cluster[i]-set
			System.err.println(oldCenter);
			if(maps!=null){
				FileWriter writer = null;
				File file = new File(CLUTSER+"/cluster-"+(classNo++)+".txt");
				if(file.exists()){
					file.delete();
				}
				try {
					file.createNewFile();
					writer = new FileWriter(file);
					writer.append("聚类中心："+oldCenter+"\n");
					writer.append("该类有"+maps.size()+"个文档\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				List<FileSocre> fileList = new ArrayList<FileSocre>();
				//cluster[i]-set-key
				for(Iterator<HashMap<String, String>> iter2=maps.iterator();iter2.hasNext();){
					HashMap<String, String> fileVec = iter2.next();//file-vector
					
					Set<String> keySet = fileVec.keySet();
					for(Iterator<String> iter3=keySet.iterator();iter3.hasNext();){
						String filename = iter3.next();
						String vectorStr = fileVec.get(filename);//vector
						try {
							double score = 1.0-calcDis(oldCenter, vectorStr);
							String content = filename+"\t"+score;
							writer.append(content+"\n");
							FileSocre fileSocre = new FileSocre();
							fileSocre.file_name = filename;
							fileSocre.file_socre = score;
							fileList.add(fileSocre);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					writer.append("******************************top three similar doc******************************\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				topFive(writer,fileList);
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				File file = new File(CLUTSER+"/cluster-"+(classNo++)+".txt");
				if(file.exists()){
					file.delete();
				}
				try {
					file.createNewFile();
					FileWriter writer = new FileWriter(file);
					writer.append("聚类中心："+oldCenter+"\n");
					writer.append("没有属于该类的文档\n");
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//计算每个聚类中最相似的5个文档
	private void topFive(FileWriter writer, List<FileSocre> fileList) {
		
		Collections.sort(fileList,new ScoreComparator());
		int count = 5;
		if(count>fileList.size()){
			count = fileList.size();
		}
		for(int i=0;i<count;i++){
			try {
				FileSocre fileSocre = fileList.get(i);
				writer.append(fileSocre.file_name+"\t"+fileSocre.file_socre+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//更新聚类中心
	private ArrayList<String> updateCenter(ArrayList<String> centers,
			Map<String, Set<HashMap<String, String>>> cluster) {
		ArrayList<String> newCenter = new ArrayList<String>();
		//center-key
		Set<String> clusterKeySet = cluster.keySet();
		System.err.println("centersize:"+clusterKeySet.size());
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(CLUTSER+"/newCenter.txt"));
		} catch (IOException e1) {
			e1.printStackTrace();
		};
		for(Iterator<String> iter=clusterKeySet.iterator();iter.hasNext();){
			String oldCenter = iter.next();//key:center
			Set<HashMap<String, String>> maps = cluster.get(oldCenter);//value:cluster[i]-set
			
			double[] vec = new double[COLS];
			int count = 1;
			if(maps!=null){
				//cluster[i]-set-key
				for(Iterator<HashMap<String, String>> iter2=maps.iterator();iter2.hasNext();){
					HashMap<String, String> fileVec = iter2.next();//file-vector
					
					Set<String> keySet = fileVec.keySet();
					for(Iterator<String> iter3=keySet.iterator();iter3.hasNext();){
						String filename = iter3.next();
						String vectorStr = fileVec.get(filename);//vector
						String[] vectors = vectorStr.split(" ");
						for(int i=0;i<vectors.length;i++){
							vec[i] += Double.parseDouble(vectors[i]);
						}
						count++;
					}
				}
			}else {
				String[] vectors = oldCenter.split(" ");
				for(int i=0;i<vectors.length;i++){
					vec[i] = Double.parseDouble(vectors[i]);
				}
			}
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<COLS;j++){
				vec[j]=vec[j]/count;
				String vecStr = new DecimalFormat("#.#####").format(vec[j]);
				if(j<COLS-1){
					sb.append(vecStr+" ");
				}else {
					sb.append(vec[j]);
				}
			}
			try {
				writer.append(sb.toString()+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			newCenter.add(sb.toString());
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newCenter;
	}
	//将各文档分配到最邻近聚类中心
	private Map<String, Set<HashMap<String, String>>> classify(
			Map<String, String> dict, ArrayList<String> centers) {
		Set<String> keys = dict.keySet();
		Map<String,Set<HashMap<String, String>>> cluster = new HashMap<String, Set<HashMap<String,String>>>();
		for(Iterator<String> iter=keys.iterator();iter.hasNext();){
			String filename = iter.next();
			String center = new String();
			double minDis = Double.MAX_VALUE;
			
			Set<HashMap<String, String>> eachSet = new HashSet<HashMap<String,String>>();
			for (int i = 0; i < centers.size(); i++) {
				double tmpDis = calcDis(centers.get(i), dict.get(filename));// 数据和类i间的距离
				//System.out.println(tmpDis);
				if (tmpDis < minDis) {
					minDis = tmpDis;
					center = centers.get(i);
				}
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(filename, dict.get(filename));
			eachSet.add(map);
			if(cluster.containsKey(center)){
				cluster.get(center).add(map);
			}else {
				cluster.put(center, eachSet);
			}
		}
		//处理聚类为空的簇集
		for(int i = 0; i < centers.size(); i++){
			if(!cluster.containsKey(centers.get(i))){
				cluster.put(centers.get(i), null);
			}
		}
		return cluster;
	}
	private static Double calcDis(String vec1,String vec2){
		String[] veca = vec1.trim().split(" ");
		String[] vecb = vec2.trim().split(" ");
		if(veca.length!=vecb.length){
			System.err.println();
			System.err.println("两个文档向量维数不一致！"+veca.length+"   "+vecb.length);
			System.exit(-1);
		}
		Double vecBoost = 0.0;
		Double vecaBoost = 0.0;
		Double vecbBoost = 0.0;
		Double dis = 0.0;
		
		for(int i=0;i<veca.length;i++){
			vecBoost +=Double.valueOf(veca[i].trim())*Double.valueOf(vecb[i].trim());
			vecaBoost += Double.valueOf(veca[i].trim())*Double.valueOf(veca[i].trim());
			vecbBoost += Double.valueOf(vecb[i].trim())*Double.valueOf(vecb[i].trim());
		}
		dis = 1.0-Math.abs(vecBoost/(Math.sqrt(vecaBoost)*Math.sqrt(vecbBoost)));
		if(dis<0){
			dis = 1.0;
		}
		return dis;
	}
	
	/*
	 * @功能 判断迭代是否收敛
	 */
	private static boolean isConverge(ArrayList<String> centers,
			ArrayList<String> newCenters, double convergeData) {
		if (centers.size() != newCenters.size()) {
			System.err.println("聚类中心维数不一致");
			return false;
		}
		double stopDis = Double.MIN_VALUE;
		for (int i = 0; i < centers.size(); i++) {
			double dis = calcDis(centers.get(i), newCenters.get(i));
			if (dis > stopDis) {
				stopDis = dis;
			}
		}
		if (stopDis < convergeData) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		
		KMeansCluster kmeans = new KMeansCluster();
		ArrayList<String> centers = kmeans.initialCenter(20);
		kmeans.kmeans(dict, centers);
	}
}

class FileSocre{
	public String file_name;
	public double file_socre;
}
class ScoreComparator implements Comparator<FileSocre> {
	@Override
	public int compare(FileSocre d1, FileSocre d2) {
		
		if (d1.file_socre < d2.file_socre)
			return 1;
		else if (d1.file_socre > d2.file_socre)
			return -1;
		else
			return 0;
	}
}
