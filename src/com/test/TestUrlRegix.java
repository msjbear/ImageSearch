package com.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sjmei
 * @date   2012-11-4
 */
public class TestUrlRegix {

	public static void main(String[] args) {

		Pattern pattern = Pattern.compile("http://\\w{2,}.csdn.net/");
		Matcher matcher = pattern.matcher("http://my.csdn.net/");
		while(matcher.find()){
			System.out.println(matcher.group());
		}
	}

}
