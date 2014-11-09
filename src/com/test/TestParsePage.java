package com.test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;

import com.homework.preprocess.FilesUtil;

public class TestParsePage {

	public static void main(String[] args) throws IOException, ParserException {
		
		String content = FilesUtil.readPage(new File("C:\\Users\\admin\\Desktop\\index_101.html"));
		Parser parser = Parser.createParser(content, "utf-8");
		
		NodeFilter[] filters = new NodeFilter[3];
		filters[0] = new NodeClassFilter(TableTag.class);
		filters[1] = new NodeClassFilter(ParagraphTag.class);
		filters[2] = new NodeClassFilter(Div.class);
		
		NodeFilter filter = new OrFilter(filters);
		NodeList list = parser.extractAllNodesThatMatch(filter);
		
		for(int i=0;i<list.size();i++){
			Node node = list.elementAt(i);
			String str = node.toPlainTextString().trim()
					.replace("&nbsp;", "")
					.replace("&#160;", "")
					.replace("*", "")
					.replace("show_ads_zone", "")
					.replaceAll("\\(\\d{1,}\\);", "");
			
	        str = Jsoup.parse(str).text();
	        Matcher matcher = Pattern.compile("[\u4E00-\u9FA5]{2,}|[a-z]{4,}").matcher(str);
            
            while(matcher.find()){
            	System.out.println(matcher.group().trim());
            }
		}
	}
}
