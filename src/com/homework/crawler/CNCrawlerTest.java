package com.homework.crawler;


/**
 * @author sjmei
 * @date   2012-11-4
 */
public class CNCrawlerTest extends Thread{

	@Override
	public void run() {
		MyCrawler crawler = new MyCrawler();
		//正则表达式过滤
		crawler.crawling();
	}
	public static void main() {
		/*
		 * 中文抓取CSDN网站的网页
		 */
		MyCrawler.initCrawlerWithSeeds(new String[] { "http://www.csdn.net" });
		for(int i=0;i<10;i++){
			CNCrawlerTest thread = new CNCrawlerTest();
			try {
				Thread.sleep(2000);
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
