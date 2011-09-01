package com.klarshift.kdk.crawler.indexer
import groovy.sql.DataSet
import groovy.sql.Sql

import java.net.URL

import com.klarshift.kdk.crawler.Resource
import com.klarshift.kdk.util.KeywordUtil;



class DefaultIndexer implements IndexerInterface {
	// sql driver
	Sql sql = Sql.newInstance("jdbc:mysql://localhost:3306/indexer", "USERNAME", "PASSWORD", "com.mysql.jdbc.Driver")

	/**
	 * get resource dataset
	 * @return
	 */
	private DataSet getDataSet(){
		sql.dataSet("resource")
	}

	DefaultIndexer(){
		println "having ${count()} indexed files"
	}

	boolean has(URL url){
		return has(url.toString())
	}
	boolean has(Resource resource){
		return has(resource.url)
	}

	boolean has(String url){
		sql.firstRow("SELECT id FROM resource WHERE url=?", [url]) != null
	}
	
	Resource getResource(Resource resource){
		Resource.get(sql.firstRow("SELECT * FROM resource WHERE url=?", [resource.url.toString()]))
	}

	void index(Resource resource){
		// get resource
		def res = sql.firstRow("SELECT * FROM resource WHERE url=?", [resource.url.toString()])

		if(res){
			sql.executeUpdate("UPDATE resource SET visits=?, lastVisit=? WHERE id=?", [
				res.visits+1,
				new Date(),
				res.id
			])
		}else{
			String ct = resource.getContentType()
			String context = resource.getDocument() ? KeywordUtil.keywords(resource.getDocument()?.text()).join(' ') : null
			String content = resource.getDocument() ? resource.getDocument()?.html() : null
			dataSet.add([
				url: resource.url.toString(), 
				visits: 1, 
				lastVisit: new Date(), 
				host: resource.url.host,
				contentType: resource.getContentType(),
				title: resource.getTitle() ?: null,
				context: context,
				content: content
			])
		}
		
	}

	void unindex(Resource resource){
		sql.execute("DELETE FROM resource WHERE url=?", [resource.url.toString()])
		index.remove(resource.url.toString())
	}

	long count(){
		dataSet.firstRow("SELECT COUNT(id) num FROM resource").num
	}
	
	double getIndexRate(){
		int visited = sql.firstRow("SELECT COUNT(*) as visitedResources FROM resource WHERE lastVisit > NOW()-10").visitedResources
		return visited / 10.0
	}
}
