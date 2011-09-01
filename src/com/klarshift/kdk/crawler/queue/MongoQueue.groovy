package com.klarshift.kdk.crawler.queue

import java.util.List

import com.gmongo.GMongo
import com.klarshift.kdk.crawler.Resource
import com.mongodb.DB
import com.mongodb.DBCollection



class MongoQueue implements QueueInterface {	
	GMongo mongo = new GMongo("127.0.0.1", 27017)
	DB db
	DBCollection queue
			
	MongoQueue(){
		// get db & collection
		db = mongo.getDB("queue")
		queue = db.queue
		
		queue.remove([:])
		
		// index setup		
		queue.ensureIndex([url: 1], [unique: true, dropDups: true])
		queue.ensureIndex([host: 1])
		queue.ensureIndex([priority: -1])
				
		println "having ${count()} queued items"	
	}
	
	void addResources(List<Resource> resources){
		resources.each{addResource(it)}		
	}
	
	private void addResource(Resource resource){
		
		if(!has(resource)){
			queue.insert([
				url: resource.url.toString(),
				dateQueued: new Date(),				
				priority: resource.priority,
				host: resource.url.host,
				parent: resource.parent?.url?.toString(),
				properties: resource.properties
			])		
			//println "queued $resource"	
		}else{
			//println "$resource still queued"
		}
	}
	
	void removeResources(List<Resource> resources){
		
	}
	
	boolean has(Resource resource){
		queue.findOne([url: resource.url.toString()]) != null
	}
	
	long count(){
		queue.count()
	}
	
	List<Resource >popHostNotContainedIn(def hosts, int num){		
		def query = hosts ? [
			host: ['$nin' : hosts]
		] : [:]
		
		def rs = queue.find(query).sort([priority: -1]).limit(num)
		
		List<Resource> result = []
		rs.each{ r ->
			result << Resource.get(r)
			queue.remove(r)
		}
		
		return result
	}
}