package com.homework.crawler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.homework.bean.Crawl;
import com.homework.dao.ManagerDao;
import com.homework.dao.ManagerDaoImpl;

/**
 * @author sjmei
 * @date 2012-11-3
 */
public class ENCrawler_Beta {

	private static int i = 1;
	
	/**
	 * 使用种子初始化 URL 队列
	 * 
	 * @return
	 * @param seeds
	 *            种子 URL
	 */
	public void initCrawlerWithSeeds(String[] seeds) {
		for (int i = 0; i < seeds.length; i++)
			URLQueue.addunvisitList(seeds[i]);
	}
	/**
	 * 抓取过程
	 * 
	 * @return
	 * @param seeds
	 * @throws IOException 
	 */
	public void crawling(){
		
		/*
		 * 英文抓取washingtonpost网站的新闻
		 */
		String visitUrl = null;
		try{
			// 循环条件：待抓取的链接不空且抓取的网页不多于 1000
			while (!URLQueue.isEmpty() && URLQueue.getVisitedUrlNum() <= 1200) {
				// 队头 URL 出队列
				visitUrl = (String) URLQueue.unvisitDeQueue();
				// 提取出下载网页中的 URL
				Set<String> links = extracLinks(visitUrl);
				// 该 URL 放入已访问的 URL 中
				URLQueue.addvisitedUrl(visitUrl);
				// 新的未访问的 URL 入队
				if(links!=null){
					for (String link : links) {
						URLQueue.addunvisitList(link);
					}
				}
				if(!visitUrl.equals("http://www.apache.org")){
					if (!isValid(visitUrl)){
						continue;
					}
				}
				// 下载网页
				downloadFile(visitUrl);
			}
		}catch (Exception e) {
			System.err.println(visitUrl+"抓取失败...");
		}
	}
	
	public Set<String> extracLinks(String url) {

		Set<String> urlLinks = new HashSet<String>();
		try {
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.getElementsByTag("a");
			if(!links.isEmpty()){
				for(Element link : links) {
					String linkHref = link.absUrl("href"); 
					if(linkHref!=null&&!linkHref.equals("")&&linkHref.startsWith("http")){
						urlLinks.add(linkHref);
					}
				}
				return urlLinks;
			}
		}catch (IOException e) {
			System.err.println(url+"页面提取url出错...");
		} 
		return null;
	}
	
	private static boolean isValid(String urlStr){
		if (urlStr == null||urlStr.trim().equals("")||urlStr.trim().endsWith(".zip")||urlStr.trim().endsWith(".gz")||urlStr.trim().endsWith(".jpg")||urlStr.trim().endsWith(".gif")) { 
            return false;                   
        } 
		//if(urlStr.trim().endsWith(".html")||urlStr.trim().endsWith(".htm")||urlStr.trim().endsWith(".shtml")){
			Pattern pattern = Pattern.compile("http://\\w{3,}.washingtonpost.com/");
			Matcher matcher = pattern.matcher(urlStr);
			while(matcher.find()){
				if (urlStr.startsWith(matcher.group())){
					URL url;
					try {
						url = new URL(urlStr);
						HttpURLConnection connection = null;
						try {
							connection = (HttpURLConnection) url.openConnection();
							int status = connection.getResponseCode();
							if(status==200){
									return true;
							}
						} catch (IOException e) {
							System.err.println("....IO异常....");
						}
					} catch (MalformedURLException e) {
							System.err.println("....URL格式错误....");
					}
				}
			}
		//}
		return false;
	}
	
	// 下载 URL 指向的网页
	public static void downloadFile(String url)throws IOException{
		
		/*生成 HttpClinet 对象并设置参数 */
		HttpClient httpClient = new HttpClient();
		// 设置 Http 连接超时 5s
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		GetMethod getMethod = null;
		try {
			/*生成 GetMethod 对象并设置参数 */
			getMethod = new GetMethod(url);
			// 设置 get 请求超时 5s
			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
			// 设置请求重试处理
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
			/*执行 HTTP GET 请求 */
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode == HttpStatus.SC_OK) {
				URL pageUrl = new URL(url);
				Scanner scanner = new Scanner(new InputStreamReader(pageUrl.openStream(),"utf-8"));
				scanner.useDelimiter("\\z");
				StringBuilder sBuilder = new StringBuilder();
				while(scanner.hasNext()){
					sBuilder.append(scanner.next());
				}
				
				saveToLocal(url,sBuilder.toString());
				scanner.close();
			}
		}catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.err.println("Http协议异常!");
		} catch (IOException e) {
			System.err.println("发生IO异常");
		} catch (Exception e) {
			System.err.println("...404,请求资源不存在...");
		}finally {
			// 释放连接
			if(getMethod!=null){
				getMethod.releaseConnection();
			}
		}
	}
	
	private static synchronized void saveToLocal(String url,String string) throws IOException {
		try {
			String fileName = "News_"+(i++)+"_EN.html";
			byte[] data = string.getBytes();
			Crawl crawl = new Crawl();
			crawl.setCrawlFile(fileName.substring(0, fileName.indexOf(".html")));
			crawl.setCrawlUrl(url);
			crawl.setCrawlTime(new Date());
			
			ManagerDao dao = new ManagerDaoImpl();
			dao.insert(crawl);
			
			/*
			 *中文输出目录格式为：pages/CN/News_i__CN.html
			 */
			File path = new File("pages/EN");
			if(!path.exists()){
				path.mkdir();
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(path+"/"+fileName)));
			for (int i = 0; i < data.length; i++)
			{
				out.write(data[i]);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("  News_"+i+"_EN.html  已抓取");
	}
}
