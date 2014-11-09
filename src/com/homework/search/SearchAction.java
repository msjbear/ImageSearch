package com.homework.search;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.struts2.ServletActionContext;

import com.homework.bean.Pager;
import com.homework.bean.Search;
import com.homework.bean.Searchlog;
import com.homework.dao.SearchDao;
import com.homework.dao.SearchDaoImpl;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class SearchAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	private String searchKey;
	private Pager pager;
	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	List<String> snippets = new ArrayList<String>();
	List<Float> scores = new ArrayList<Float>();
	List<Document> docVec = new ArrayList<Document>();
	@Override
	public String execute() throws Exception {
		if(searchKey!=null&&!searchKey.equals("")){
			Long start = System.currentTimeMillis();
			
			HttpServletRequest request = ServletActionContext.getRequest();
			pager.setPerPageRows(3);
			int curPage = pager.getCurPage();//获取起始页
			int limit = pager.getPerPageRows();//获取页显示数
			request.setAttribute("searchKey", searchKey);
			
			String field[] = {"title", "content","url"};
			String quString[] = {searchKey, searchKey,searchKey};
			Query query = MultiFieldQueryParser.parse(Version.LUCENE_30,quString, field, new PaodingAnalyzer());
			@SuppressWarnings("deprecation")
			String filepath = request.getRealPath("\\WEB-INF\\index");
			Directory indexDir = SimpleFSDirectory.open(new File(filepath));
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(indexDir));
			
			FastVectorHighlighter highlighter = getHighlighter();
			FieldQuery fieldQuery = highlighter.getFieldQuery(query);
			
			TopDocs docs = searcher.search(query, 1000);
			
			for(ScoreDoc scoreDoc:docs.scoreDocs){
				String snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, "content", 200);
				if(snippet!=null){
					Document doc = searcher.doc(scoreDoc.doc);
					snippets.add(snippet);
					scores.add(scoreDoc.score);
					docVec.add(doc);
				}
			}
			
			List<String> retSnippets = new ArrayList<String>();
			List<Float> retScores = new ArrayList<Float>();
			List<Document> retDocVec = new ArrayList<Document>();
			//分页实现
			if (limit != 0) {
				String snipet1 = null;
				Float score1 = null;
				Document doc1 = null;
				if (curPage*limit < docVec.size()) {
					for (int i = (curPage-1)*limit; i <(curPage-1)*limit+limit; i++) {
						//返回指定的页
						snipet1 = snippets.get(i);
						retSnippets.add(snipet1);
						score1 = scores.get(i);
						retScores.add(score1);
						doc1 = docVec.get(i);
						retDocVec.add(doc1);
					}
				} else {//末页处理
					for (int i = (curPage-1)*limit;i<docVec.size();i++) {
						snipet1 = snippets.get(i);
						retSnippets.add(snipet1);
						score1 = scores.get(i);
						retScores.add(score1);
						doc1 = docVec.get(i);
						retDocVec.add(doc1);
					}
				}
			}
			request.setAttribute("snippets", retSnippets);
			request.setAttribute("scores", retScores);
			request.setAttribute("docs", retDocVec);
			request.setAttribute("pagesCount", docVec.size());
			pager.setRowCount(docVec.size());
			searcher.close();
			Long end = System.currentTimeMillis();
			Long spendTime = (end-start);
			request.getSession().setAttribute("spendTime", spendTime);
			SearchDao searchDao = new SearchDaoImpl();
			Search search = searchDao.find(searchKey);
			if(search!=null){
				Searchlog log = new Searchlog();
				log.setSearch(search);
				log.setResultNum(docVec.size());
				Date date=new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr = formatter.format(date);
				log.setSearchTime(dateStr);
				log.setSearchIp(request.getRemoteAddr());
				DecimalFormat df = new DecimalFormat("#0.000##");  
				String string = df.format((double)spendTime/1000);
				System.out.println(string);
				log.setSpendTime(string);
				searchDao.addLog(log);
			}
			return SUCCESS;
		}else {
			return INPUT;
		}
		
	}
	public static FastVectorHighlighter getHighlighter(){
		FragListBuilder builder = new SimpleFragListBuilder();
		FragmentsBuilder fragmentsBuilder = new ScoreOrderFragmentsBuilder(BaseFragmentsBuilder.COLORED_PRE_TAGS,BaseFragmentsBuilder.COLORED_POST_TAGS);
		
		return new FastVectorHighlighter(true, true,builder,fragmentsBuilder);
	}
}
