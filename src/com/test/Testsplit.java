package com.test;

public class Testsplit {

	public static void main(String[] args) {
		String path = "index\\path\\file";
		String[] files = path.split("\\\\");
		System.out.println(files[2]);
	}
}
