package com.test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.homework.preprocess.FilesUtil;

/**
 * @author sjmei
 * @date   2012-11-5
 */
public class ReadFile {

	public static void main(String[] args) {

		List<File> lists = FilesUtil.readFile("pages");
		for(Iterator<File> iter = lists.iterator();iter.hasNext();){
			System.out.println(iter.next().toString());
		}
		
	}

}
