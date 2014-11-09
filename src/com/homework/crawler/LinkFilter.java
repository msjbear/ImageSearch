package com.homework.crawler;
/**
 * @author sjmei
 * @date   2012-11-3
 */
public interface LinkFilter {
	public boolean accept(String url);
}
