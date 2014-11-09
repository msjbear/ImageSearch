package com.homework.preprocess;

import java.io.IOException;

import org.htmlparser.util.ParserException;

/**
 * @author sjmei
 * @date   2012-10-28
 */
public class ParsingPagesMain {
	
	public static void main(String[] args){
		
		try {
			//ParsingCNPages.main();
			ParsingENPages.main();
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
