package com.homework.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.homework.bean.Crawl;
import com.homework.dao.ManagerDao;
import com.homework.dao.ManagerDaoImpl;

/**
 * @author sjmei
 * @date   2012-10-28
 */
public class ParsingCNPages extends FilesUtil {

	private static int num = 1;
	
	public static void main() throws ParserException, IOException {
		
		long start = System.currentTimeMillis();
		List<File> list = ParsingCNPages.readFile("pages/CN");
        System.out.println("提取的网页数量："+list.size());
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).toString());
            parse(list.get(i).toString());
        }
        long end = System.currentTimeMillis();
        System.out.println("文本提取完毕,用时："+(end-start)+"ms");
	}
	
	public static synchronized void parse(String file) throws ParserException, IOException, IOException{
		
		String content = FilesUtil.readPage(new File(file));
		File path = new File("preoutput/CN");
		if(!path.exists()){
			path.mkdir();
		}
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path+"/News_"+(num++)+"_CN.txt"))));
		StringBuilder sb = new StringBuilder();
		
        Document doc = Jsoup.parse(content);
        sb.append(doc.getElementsByTag("h1").text());
        sb.append(doc.getElementsByTag("span").text());
        sb.append(doc.getElementsByTag("p").text());
        sb.append(doc.getElementsByTag("dd").text());
        
        sb.append(doc.select("div.post_body").text().trim());
		Crawl crawl = new Crawl();
		String[] splits = file.split("\\\\");
		System.out.println("split:"+splits[2]);
        crawl.setCrawlFile(splits[2].substring(0, splits[2].indexOf(".html")));
        String title = doc.title();
        crawl.setCrawlTitle(title);
        crawl.setCrawlContent(sb.toString());
        
        ManagerDao dao = new ManagerDaoImpl();
        dao.update(crawl);
        
        writer.write(sb.toString());
        writer.flush();
		writer.close();
	}
}
