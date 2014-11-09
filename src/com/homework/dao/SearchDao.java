package com.homework.dao;

import java.util.List;

import com.homework.bean.Search;
import com.homework.bean.Searchlog;


public interface SearchDao {

	public Search find(String searchKey);
	public void addLog(Searchlog log);
	public List<Search> findHotSearch();
}
