package com.test;
/**
 * @author sjmei
 * @date   2012-11-4
 */
public class TestIndexOf {

	public static void main(String[] args) {

		String frame = new String("<a href=\"test.html\"/>");
		System.out.println(frame.indexOf("href="));
		int start = frame.indexOf("href=");
		frame = frame.substring(start);
		int end = frame.indexOf(" ");
		if (end == -1)
			end = frame.indexOf("/>");
		String frameUrl = frame.substring(6, end - 1);
		System.out.println("http://www.csdn.net/"+frameUrl);
	}

}
