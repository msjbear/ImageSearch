package com.homework.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.homework.bean.Crawl;
import com.homework.utils.HibernateSessionFactory;

public class ManagerDaoImpl implements ManagerDao {

	@Override
	public void insert(Crawl crawl) {
		
		Crawl findCrawl = select(crawl.getCrawlFile());
		if(findCrawl==null){
			Session session = HibernateSessionFactory.getSession();
			try {
				session.beginTransaction().begin();
				session.save(crawl);
				session.beginTransaction().commit();
			} catch (Exception e) {
				session.beginTransaction().rollback();
			}finally{
				session.close();
			}
		}else {
			System.out.println("插入失败，数据已存在！");
		}
	}

	@Override
	public synchronized boolean update(Crawl crawl) {
		
		Crawl findCrawl = select(crawl.getCrawlFile());
		if(findCrawl!=null){
			Session session = HibernateSessionFactory.getSession();
			try {
				session.beginTransaction();
				crawl.setCrawlUrl(findCrawl.getCrawlUrl());
				crawl.setCrawlTime(findCrawl.getCrawlTime());
				session.update(crawl);
				System.out.println(crawl.getCrawlContent());
				session.beginTransaction().commit();
			} catch (Exception e) {
				session.flush();
				session.beginTransaction().rollback();
				return false;
			}finally{
				session.close();
			}
			return true;
		}else {
			return false;
		}
		
	}
	
	private synchronized Crawl select(String fileId) {
		
		Session session = HibernateSessionFactory.getSession();
		try {
			Crawl crawl = (Crawl) session.createQuery("from Crawl c where c.crawlFile=?").setParameter(0, fileId).uniqueResult();
			return crawl;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Crawl> select() {
		List<Crawl> lists = null;
		Session session = HibernateSessionFactory.getSession();
		try {
			lists = session.createQuery("from Crawl").list();
			return lists;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
		return null;
	}

	@Override
	public String find(String file) {
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void delete() {
		Session session = HibernateSessionFactory.getSession();
		List crawls = (List)session.createQuery("from Crawl c where c.crawlTitle='' or c.crawlContent='' or c.crawlTitle is null").list();
		if(crawls.size()>0){
			session.beginTransaction().begin();
			for(Iterator iter = crawls.iterator();iter.hasNext();){
				Crawl crawl2 = (Crawl)iter.next();
				session.delete(crawl2);
			}
			session.beginTransaction().commit();
		}
		session.close();
	}
}
