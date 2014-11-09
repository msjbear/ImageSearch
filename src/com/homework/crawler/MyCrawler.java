package com.homework.crawler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.homework.bean.Crawl;
import com.homework.dao.ManagerDao;
import com.homework.dao.ManagerDaoImpl;

/**
 * @author sjmei
 * @date 2012-11-3
 */
public class MyCrawler {

	private static int COUNT = 1;
	
	
	/**
	 * 使用种子初始化 URL 队列
	 * 
	 * @return
	 * @param seeds
	 *            种子 URL
	 */
	public static void initCrawlerWithSeeds(String[] seeds) {
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
		
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				/*
				 * 中文抓取CSDN网站的网页
				 * 英文抓取Apache网站的网页
				 */
				Pattern pattern = Pattern.compile("http://\\w{3,}.csdn.net/");
				Matcher matcher = pattern.matcher(url);
				while(matcher.find()){
					if (url.startsWith(matcher.group()))
						return true;
				}
				return false;
			}
		};
		
			// 循环条件：待抓取的链接不空且抓取的网页不多于 600
			while (!URLQueue.isEmpty() && URLQueue.getVisitedUrlNum() <= 2000) {
				// 队头 URL 出队列
				String visitUrl = (String) URLQueue.unvisitDeQueue();
				if (visitUrl == null||!isValid(visitUrl))
					continue;
				// 下载网页
				try {
					downloadFile(visitUrl);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 该 URL 放入已访问的 URL 中
				URLQueue.addvisitedUrl(visitUrl);
				// 提取出下载网页中的 URL
				Set<String> links = extracLinks(visitUrl, filter);
				// 新的未访问的 URL 入队
				for (String link : links) {
					URLQueue.addunvisitList(link);
				}
			}
	
	}
	public Set<String> extracLinks(String url, LinkFilter filter) {

		Set<String> links = new HashSet<String>();
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("utf-8");
			// 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性
			NodeFilter frameFilter = new NodeFilter() {
				private static final long serialVersionUID = 1L;
				@Override
				public boolean accept(Node node) {
					if (node.getText().startsWith("frame src=")) {
						return true;
					} else {
						return false;
					}
				}
			};
			// OrFilter 来设置过滤 <a> 标签和 <frame> 标签
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(
					LinkTag.class), frameFilter);
			// 得到所有经过过滤的标签
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag) {
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// URL
					if (filter.accept(linkUrl))
						links.add(linkUrl);
				} else {
					//提取 frame 里 src 属性的链接， 如 <frame src="test.html"/>
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
						end = frame.indexOf("/>");
					String frameUrl = frame.substring(5, end - 1);
					if (filter.accept(frameUrl))
						links.add(frameUrl);
				}
			}
		} catch (ParserException e) {
			System.err.println("Connection timed out");
		}
		return links;
	}
	private static boolean isValid(String urlStr){
		if (urlStr == null || urlStr.length() <= 0||urlStr.endsWith(".js")||urlStr.endsWith(".jpg")||urlStr.endsWith(".pdf")||urlStr.endsWith(".gif")) {                         
            return false;                   
        } 
		try{
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int status = connection.getResponseCode();
			if(status==200){
					return true;
			}
		}catch(IOException e){
			System.err.println(urlStr+" url地址有误");
			return false;
		}
		return false;
	}
	
	// 下载 URL 指向的网页
	public static void downloadFile(String url)throws IOException{
		
		/*生成 HttpClinet 对象并设置参数 */
		HttpClient httpClient = new HttpClient();
		// 设置 Http 连接超时 5s
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		boolean flag = isValid(url);
		GetMethod getMethod = null;
		if(!flag){
			return;
		}
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
			System.err.println("Please check your provided http address!");
		} catch (IOException e) {
			System.err.println("发生IO异常");
		}catch (Exception e) {
			System.err.println("出现未知错误");
		}finally {
			// 释放连接
			if(getMethod!=null){
				getMethod.releaseConnection();
			}
		}
	}
	
	private static synchronized void saveToLocal(String url,String string) throws IOException {
		
		int num = COUNT++;
		try {
			String fileName = "News_"+num+"_CN.html";
			byte[] data = string.getBytes();
			
			Crawl crawl = new Crawl();
			crawl.setCrawlFile(fileName.substring(0, fileName.indexOf(".html")));
			crawl.setCrawlUrl(url);
			crawl.setCrawlTime(new Date());
			
			//将数据保存到数据库中
			ManagerDao dao = new ManagerDaoImpl();
			dao.insert(crawl);

			File path = new File("pages/CN");
			if(!path.exists()){
				path.mkdir();
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("pages/CN/News_"+(num)+"_CN.html")));
			for (int i = 0; i < data.length; i++)
			{
				out.write(data[i]);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(url+"  News_"+num+"_CN.html  已抓取");
	}
	
}
