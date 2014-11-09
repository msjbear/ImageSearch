package com.homework.bean;

import java.util.Date;

/**
 * Crawl entity. @author MyEclipse Persistence Tools
 */

public class Crawl implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 1L;
	private String crawlFile;
	private String crawlUrl;
	private String crawlTitle;
	private String crawlContent;
	private Date crawlTime;

	// Constructors

	/** default constructor */
	public Crawl() {
	}

	/** minimal constructor */
	public Crawl(String crawlUrl, Date crawlTime) {
		this.crawlUrl = crawlUrl;
		this.crawlTime = crawlTime;
	}

	/** full constructor */
	public Crawl(String crawlUrl, String crawlTitle, String crawlContent,
			Date crawlTime) {
		this.crawlUrl = crawlUrl;
		this.crawlTitle = crawlTitle;
		this.crawlContent = crawlContent;
		this.crawlTime = crawlTime;
	}

	// Property accessors

	public String getCrawlFile() {
		return this.crawlFile;
	}

	public void setCrawlFile(String crawlFile) {
		this.crawlFile = crawlFile;
	}

	public String getCrawlUrl() {
		return this.crawlUrl;
	}

	public void setCrawlUrl(String crawlUrl) {
		this.crawlUrl = crawlUrl;
	}

	public String getCrawlTitle() {
		return this.crawlTitle;
	}

	public void setCrawlTitle(String crawlTitle) {
		this.crawlTitle = crawlTitle;
	}

	public String getCrawlContent() {
		return this.crawlContent;
	}

	public void setCrawlContent(String crawlContent) {
		this.crawlContent = crawlContent;
	}

	public Date getCrawlTime() {
		return this.crawlTime;
	}

	public void setCrawlTime(Date crawlTime) {
		this.crawlTime = crawlTime;
	}

}