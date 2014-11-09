package com.homework.index;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.homework.bean.Crawl;
import com.homework.dao.ManagerDao;
import com.homework.dao.ManagerDaoImpl;

public class IndexManager {
	
	public boolean createSearchIndex(String path){
		boolean flag = false;
		Directory dir;
		IndexWriter writer = null;
		try {
			dir = FSDirectory.open(new File(createFolder(path)));
			writer = new IndexWriter(dir, new PaodingAnalyzer(),true, IndexWriter.MaxFieldLength.UNLIMITED);
			
			ManagerDao dao = new ManagerDaoImpl();
			List<Crawl> list = dao.select();
			for(int i=0;i<list.size();i++){
				Crawl crawlDoc = list.get(i);
				Document doc = new Document();
				doc.add(new Field("title", crawlDoc.getCrawlTitle(),
						Field.Store.YES, Field.Index.ANALYZED,
						Field.TermVector.WITH_POSITIONS_OFFSETS));
				doc.add(new Field("content", crawlDoc.getCrawlContent(),
						Field.Store.YES, Field.Index.ANALYZED,
						Field.TermVector.WITH_POSITIONS_OFFSETS));
				doc.add(new Field("url", crawlDoc.getCrawlUrl(),
						Field.Store.YES, Field.Index.ANALYZED));
				
				writer.addDocument(doc);
			}
			writer.optimize();
			writer.commit();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(writer!=null){
					writer.close();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	// 创建搜索引擎的路径
	private String createFolder(String path) {
		String filepath = null;
		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}
		try {
			filepath = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filepath;
	}
	
	public static void main(String[] args) {
		
		IndexManager indexer = new IndexManager();
		boolean flag = indexer.createSearchIndex("WebRoot\\WEB-INF\\index");
		System.out.println(flag);
	}
}
