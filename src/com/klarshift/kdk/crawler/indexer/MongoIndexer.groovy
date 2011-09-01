package com.klarshift.kdk.crawler.indexer

import com.gmongo.GMongo
import com.klarshift.kdk.crawler.Resource
import com.klarshift.kdk.util.KeywordUtil
import com.mongodb.DB
import com.mongodb.DBCollection




class MongoIndexer{
	GMongo mongo = new GMongo("127.0.0.1", 27017)
	DB db
	DBCollection index

	MongoIndexer(){
		// get db & collection
		db = mongo.getDB("indexer")
		index = db.index
		
		index.remove([:])
		
		// index setup
		index.ensureIndex([url: 1], [unique: true, dropDups: true])
		index.ensureIndex([host: 1])
					
		println "having ${count()} indexed files"
	}

	
	boolean has(Resource resource){
		index.find([url: resource.url.toString()]) != null
	}

	Resource getResource(Resource resource){
		Resource.get(index.findOne([url: resource.url.toString()]))
	}

	void index(Resource resource){				
		// get resource
		def res = index.findOne([url: resource.url.toString()])

		if(res){
			// revisit resource
			println "revisiting $resource"
			res.visits++
			res.lastVisit = new Date()
								
		}else{
			// create entry
			String ct = resource.getContentType()
			String context = resource.getDocument() ? KeywordUtil.keywords(resource.getDocument()?.text()).join(' ') : null
			String content = resource.getDocument() ? resource.getDocument()?.html() : null
			
			res = [
				url: resource.url.toString(),
				visits: 1,
				lastVisit: new Date(),
				host: resource.url.host,
				contentType: resource.getContentType(),
				/*title: resource.getTitle() ?: null,
				context: context,
				content: content*/
			]
		}
		
		// save
		index.save(res)
		
	}

	void unindex(Resource resource){
		def r = index.find([url: resource.url.toString()])
		index.remove(r)
	}

	long count(){
		index.count()
	}
	
	double getIndexRate(){
		Date tenSecsAgo = new Date()
		tenSecsAgo.time -= 1000*10
		index.find([
			lastVisit: ['$gt' : tenSecsAgo]	
		]).count() / 10.0
	}
}
