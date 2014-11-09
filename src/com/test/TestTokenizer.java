package com.test;

import java.util.StringTokenizer;

/**
 * @author sjmei
 * @date   2012-11-5
 */
public class TestTokenizer {

	public static void main(String[] args) {

		StringTokenizer tokenizer = new StringTokenizer("my name is sj_mei");
		while(tokenizer.hasMoreTokens()){
			System.out.println(tokenizer.nextToken());
		}
	}

}
