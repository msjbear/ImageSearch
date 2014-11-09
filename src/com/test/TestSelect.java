package com.test;

import java.util.List;

import com.homework.bean.Crawl;
import com.homework.dao.ManagerDao;
import com.homework.dao.ManagerDaoImpl;

public class TestSelect {

	public static void main(String[] args) {
		ManagerDao dao = new ManagerDaoImpl();
		List<Crawl> list = dao.select();
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).getCrawlTitle());
		}
	}
}
