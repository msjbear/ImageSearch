package com.homework.crawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author sjmei
 * @date   2012-11-3
 */
public class URLQueue {

	private static Set<String> visitedSet = new HashSet<String>();
	private static LinkedList<String> unvisitList = new LinkedList<String>();
	
	//获得URL队列
	public static List<String> getvnVisitList(){
		return unvisitList;
	}
	//添加到访问过的 URL队列中
	public synchronized static void addvisitedUrl(String url){
		visitedSet.add(url);
	}
	
	//移除访问过的 URL
	public static void removeVisitedUrl(String url){
		visitedSet.remove(url);
	}
	
	//未访问的 URL出队列
	public synchronized static String unvisitDeQueue(){
		return unvisitList.removeFirst();
	}
	
	public synchronized static void addunvisitList(String url){
		if(url!=null&&!url.trim().equals("")&&!visitedSet.contains(url)&&!unvisitList.contains(url)){
			unvisitList.addLast(url);
		}
	}
	//判断未访问的 URL 队列中是否为空
	public static boolean isEmpty(){
		return unvisitList.isEmpty();
	}
	//获得已经访问的 URL 数目
	public static int getVisitedUrlNum() {
		return visitedSet.size();
	}
}
