package com.homework.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.homework.bean.Search;
import com.homework.bean.Searchlog;
import com.homework.utils.HibernateSessionFactory;

public class SearchDaoImpl implements SearchDao {

	@Override
	public Search find(String searchKey) {
		Session session = HibernateSessionFactory.getSession();
		try {
			Search search = (Search) session.createQuery("from Search where queryString=?").setParameter(0, searchKey).uniqueResult();
			if(search!=null){
				session.beginTransaction().begin();
				search.setSearchCount(search.getSearchCount()+1);
				session.save(search);
				session.beginTransaction().commit();
				return search;
			}else {
				session.beginTransaction().begin();
				Search search2 = new Search();
				search2.setQueryString(searchKey);
				search2.setSearchCount(1);
				session.save(search2);
				session.beginTransaction().commit();
				return search2;
			}
		} catch (Exception e) {
			session.beginTransaction().rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		return null;
	}

	@Override
	public void addLog(Searchlog log) {
		Session session = HibernateSessionFactory.getSession();
		try {
			session.beginTransaction().begin();
			session.save(log);
			session.beginTransaction().commit();
		} catch (Exception e) {
			session.beginTransaction().rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Search> findHotSearch() {
		Session session = HibernateSessionFactory.getSession();
		List<Search> list = new ArrayList<Search>();
		int count = 0;
		try {
			List<Search> hotSearchs = (List<Search>) session.createQuery("from Search order by searchCount desc").list();
			if(hotSearchs.size()>32){
				count = 32;
			}else {
				count = hotSearchs.size();
			}
			for(int i=0;i<count;i++){
				Search search = hotSearchs.get(i);
				list.add(search);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		SearchDao dao = new SearchDaoImpl();
		List<Search> searchs = dao.findHotSearch();
		for(int i=0;i<searchs.size();i++){
			Search search = searchs.get(i);
			System.out.println(search.getQueryString());
		}
		
	}
}
