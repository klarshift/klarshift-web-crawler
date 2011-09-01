package com.klarshift.kdk.crawler.queue

import groovy.sql.Sql

import java.util.List

import com.klarshift.kdk.crawler.Resource



class JDBCQueue implements QueueInterface {	
	// sql driver
	Sql sql = Sql.newInstance("jdbc:mysql://localhost:3306/queue", "USER", "PASSWORD", "com.mysql.jdbc.Driver")
		
	JDBCQueue(){
		
	}
	
	void addResources(List<Resource> resources){
		sql.withTransaction{
			resources.each{addResource(it)}
		}
	}
	
	private void addResource(Resource resource){
		if(!has(resource)){
			// queue
			try{
				sql.dataSet('resource').add([
					url: resource.url.toString(),
					dateQueued: new Date(),
					host: resource.url.host,
					depth: resource.depth,
					priority: resource.priority,
				])
			}catch(Exception e){
				e.printStackTrace()
			}
		}
		
	}
	
	void removeResources(List<Resource> resources){
		sql.withTransaction {  
			sql.withBatch("DELETE FROM resource WHERE url=?") { stmt ->				
				resources.each{ r ->
					stmt.addBatch([r.url.toString()])
				}
			}
		}
		
	}
	
	private void removeResource(Resource r){
		removeResources([r])
	}
	
	boolean has(Resource resource){
		def f = sql.firstRow("SELECT id FROM resource WHERE url=? AND depth>=?", [resource.url.toString(), resource.depth])
		f != null
	}
	
	long count(){
		sql.firstRow("SELECT COUNT(id) as queueSize FROM resource").queueSize
	}
	
	private List<Resource> popByQuery(String query, def args = null){
		List<Resource> resources = new ArrayList<Resource>()
		sql.withTransaction {  

			sql.rows(query, args).each {
				resources << Resource.get(it)
			}	
			
			// unqueue
			removeResources(resources)
		
		}
			
		resources
	}
		
	List<Resource >popHostNotContainedIn(def hosts, int num){
		def hostQ = hosts.collect { "(host!='$it')" }?.join(' AND ') ?: 1
		//popByQuery("SELECT * FROM resource WHERE ($hostQ) GROUP BY host ORDER BY priority DESC LIMIT 0,?", [num])
		popByQuery("SELECT * FROM resource WHERE ($hostQ) ORDER BY priority DESC LIMIT 0,?", [num])
	}
}