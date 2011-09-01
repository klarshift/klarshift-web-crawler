package com.klarshift.kdk.crawler.plugin

import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.Resource
import com.klarshift.kdk.util.KeywordUtil

class EmailSpider extends CrawlerPlugin{
	List<String> addresses = []
	
	// host, url, title, keywords, dateFound
	
	void storeEmails(Resource r, List<String> emails, List<String> keywords){
		def sql = getSql()
		def context = keywords.join(' ')
		emails.each{ a ->
			a = a.toLowerCase()
			
			if(!sql.firstRow("SELECT id FROM emails WHERE address=? AND url=?", [a, r.url.toString()])){			
				sql.dataSet('emails').add([address: a, url: r.url.toString(), host: r.url.host, context: context, title: r.getTitle()])
			}
		}
	}
	
	boolean visit(Resource r){
		// todo: parse host part of email address to have a new seed for crawling
		
		//
		String t = r.getDocument()?.text()
		if(t){
			def m = t =~ /([a-zA-Z0-9-_\.]+@[a-zA-Z0-9-_]+\.[a-zA-Z]{1,4})/
			if(m){
				
								
				def addys = m.collect{it[1]}.unique().findAll{!addresses.contains(it)}
				addresses.addAll(addys)	
				if(addys){
					def keywords = KeywordUtil.keywords(t, 20)
					storeEmails(r, addys, keywords)
				}
			}
		}
		true
	}
	
	String getName(){
		"emailSpider"
	}
}
