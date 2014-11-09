package com.test;

import java.io.IOException;

import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestStringBean {

	public static void main(String[] args) throws ParserException, IOException {
		
		//String s = extractText("C:\\Users\\admin\\Desktop\\index_101.html");
		extractText2("http://www.sina.com.cn");
	}

	public static String extractText(String url) throws ParserException, IOException{
		
		 StringBean sbean = new StringBean();
		 sbean.setLinks (false);
		 sbean.setReplaceNonBreakingSpaces (true);
		 sbean.setCollapse (true);
	     sbean.setURL(url);
	     String s = sbean.getStrings();
	     byte[] bytes = s.getBytes();
	     String ss = new String(bytes, "utf-8");
	     System.out.println(ss);
	     return s;
		
	}
	
	public static void extractText2(String url) throws ParserException, IOException{
		
		Document doc = Jsoup.connect(url).get();   
		Elements links = doc.getElementsByTag("a");
		for(Element link : links) {
			if(!links.isEmpty()){
				String linkHref = link.absUrl("href"); 
				String linkText = link.text();
				if(linkHref!=null&&!linkHref.equals("")){
					System.out.println(linkHref+"   "+linkText);
				}
			}
		}	
	}
}
