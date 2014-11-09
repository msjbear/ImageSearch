package com.homework.crawler;


/**
 * @author sjmei
 * @date   2012-11-4
 */
public class ENCrawlerTest extends Thread{

	@Override
	public void run() {
		MyENCrawler crawler = new MyENCrawler();
		//正则表达式过滤
		crawler.crawling();
	}
	public static void main(String[] args) {
		/*
		 * 中文抓取CSDN网站的网页
		 * 英文抓取Apache网站的网页
		 */
		MyENCrawler.initCrawlerWithSeeds(new String[] { "http://www.washingtonpost.com/" });
		for(int i=0;i<10;i++){
			ENCrawlerTest thread = new ENCrawlerTest();
			try {
				Thread.sleep(2000);
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}
