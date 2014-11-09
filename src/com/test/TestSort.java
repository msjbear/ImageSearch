package com.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestSort {

	public static void main(String[] args) {
		List<FileSocre> list = new ArrayList<FileSocre>();
		FileSocre fileSocre1 = new FileSocre();
		fileSocre1.filename = "News_1049_CN.txt";
		fileSocre1.socre = 1.6491686884493977E-6;
		
		FileSocre fileSocre2 = new FileSocre();
		fileSocre2.filename = "News_124_CN.txt";
		fileSocre2.socre = 3.186685574152527E-4;
		
		FileSocre fileSocre3 = new FileSocre();
		fileSocre3.filename = "News_806_CN.txt";
		fileSocre3.socre = 1.2336835593597184E-5;
		list.add(0, fileSocre1);
		list.add(1, fileSocre2);
		list.add(2, fileSocre3);
		Collections.sort(list,new ScoreComparator());
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).socre);
		}
	}
}
class FileSocre{
	public String filename;
	public double socre;
}
class ScoreComparator implements Comparator<FileSocre> {
	@Override
	public int compare(FileSocre d1, FileSocre d2) {
		
		if (d1.socre > d2.socre)
			return -1;
		else if (d1.socre < d2.socre)
			return 1;
		else
			return 0;
	}
}
