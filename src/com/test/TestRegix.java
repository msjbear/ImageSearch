package com.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.homework.preprocess.FilesUtil;

/**
 * @author sjmei
 * @date   2012-10-29
 */
public class TestRegix {

	public static void main(String[] args) throws IOException {

        List<File> list = FilesUtil.readFile("C:\\Users\\sjmei\\Desktop\\News_0_EN.html");
        for(int i=0;i<list.size();i++){
        	String listStr = FilesUtil.readPage(new File(list.get(i).toString()));
            Matcher matcher = Pattern.compile("[\\u0061-\\u007a|\\u0041-\\u005A]+").matcher(listStr);
            while(matcher.find()){
            	System.out.println(matcher.group());
            }
        }
	}

}
