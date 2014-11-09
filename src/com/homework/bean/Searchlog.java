package com.homework.bean;

/**
 * Searchlog entity. @author MyEclipse Persistence Tools
 */

public class Searchlog implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 1L;
	private Integer logId;
	private Search search;
	private Integer resultNum;
	private String searchTime;
	private String searchIp;
	private String spendTime;

	// Constructors

	/** default constructor */
	public Searchlog() {
	}

	/** minimal constructor */
	public Searchlog(Search search, Integer resultNum, String searchTime) {
		this.search = search;
		this.resultNum = resultNum;
		this.searchTime = searchTime;
	}

	/** full constructor */
	public Searchlog(Search search, Integer resultNum, String searchTime,
			String searchIp, String spendTime) {
		this.search = search;
		this.resultNum = resultNum;
		this.searchTime = searchTime;
		this.searchIp = searchIp;
		this.spendTime = spendTime;
	}

	// Property accessors

	public Integer getLogId() {
		return this.logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Search getSearch() {
		return this.search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public Integer getResultNum() {
		return this.resultNum;
	}

	public void setResultNum(Integer resultNum) {
		this.resultNum = resultNum;
	}

	public String getSearchTime() {
		return this.searchTime;
	}

	public void setSearchTime(String searchTime) {
		this.searchTime = searchTime;
	}

	public String getSearchIp() {
		return this.searchIp;
	}

	public void setSearchIp(String searchIp) {
		this.searchIp = searchIp;
	}

	public String getSpendTime() {
		return this.spendTime;
	}

	public void setSpendTime(String spendTime) {
		this.spendTime = spendTime;
	}

}