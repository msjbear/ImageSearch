package com.homework.dao;


import java.util.List;

import com.homework.bean.Crawl;

public interface ManagerDao {

	public void insert(Crawl crawl);
	public boolean update(Crawl crawl);
	public List<Crawl> select();
	public String find(String file);
	public void delete();
	
}
