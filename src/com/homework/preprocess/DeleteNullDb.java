package com.homework.preprocess;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.homework.dao.ManagerDao;
import com.homework.dao.ManagerDaoImpl;

/**
 * @author sjmei
 * @date   2012-10-28
 */
public class DeleteNullDb extends FilesUtil {
	
	public static void main(String[] args) throws ParserException, IOException{
		
		long start = System.currentTimeMillis();
        parse("pages/CN/News_110_CN.html");
        long end = System.currentTimeMillis();
        System.out.println("文本提取完毕,用时："+(end-start)+"ms");
        
        deleteNull();
	}
	
	public static void parse(String file) throws ParserException, IOException, IOException{
		
		String content = readPage(new File(file));
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
        Document doc = Jsoup.parse(content);
        sb.append(doc.getElementsByTag("title").text()+"\n");
        Elements elements = doc.select("meta");
        for(Element ele:elements){
        	sb.append(ele.attr("content"));
        }
        sb.append(doc.getElementsByTag("h1").text()+"\n");
        sb.append(doc.getElementsByTag("h4").text()+"\n");
        sb.append(doc.getElementsByTag("span").text()+"\n");
        sb.append(doc.getElementsByTag("p").text()+"\n");
        sb.append(doc.getElementsByTag("dd").text()+"\n");
        
        sb.append(doc.select("div.post_body").text().trim());
        Matcher matcher = Pattern.compile("[\u4E00-\u9FA5]{2,}|[a-z|A-Z]{3,}").matcher(sb.toString());
        while(matcher.find()){
        	sb2.append(matcher.group()+" ");
        }
        System.out.println(sb2.toString());
       
	}

	private static void deleteNull() {
		ManagerDao dao = new ManagerDaoImpl();
        dao.delete();
	}
}
