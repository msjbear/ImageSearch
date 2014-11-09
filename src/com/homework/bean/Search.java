package com.homework.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * Search entity. @author MyEclipse Persistence Tools
 */

public class Search implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 1L;
	private Integer searchId;
	private String queryString;
	private Integer searchCount;
	@SuppressWarnings("rawtypes")
	private Set searchlogs = new HashSet(0);

	// Constructors

	/** default constructor */
	public Search() {
	}

	/** minimal constructor */
	public Search(String queryString, Integer searchCount) {
		this.queryString = queryString;
		this.searchCount = searchCount;
	}

	/** full constructor */
	@SuppressWarnings("rawtypes")
	public Search(String queryString, Integer searchCount, Set searchlogs) {
		this.queryString = queryString;
		this.searchCount = searchCount;
		this.searchlogs = searchlogs;
	}

	// Property accessors

	public Integer getSearchId() {
		return this.searchId;
	}

	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public Integer getSearchCount() {
		return this.searchCount;
	}

	public void setSearchCount(Integer searchCount) {
		this.searchCount = searchCount;
	}

	@SuppressWarnings("rawtypes")
	public Set getSearchlogs() {
		return this.searchlogs;
	}

	@SuppressWarnings("rawtypes")
	public void setSearchlogs(Set searchlogs) {
		this.searchlogs = searchlogs;
	}

}