package com.klarshift.kdk.crawler.plugin

import com.klarshift.kdk.crawler.CrawlerPlugin;
import com.klarshift.kdk.crawler.Resource

class PriorityPusher extends CrawlerPlugin{
	def pushs = []
	

	PriorityPusher(){
		
	}
	
	
	String getName(){
		"priorityPusher"
	}
	
	PriorityPusher push(Map push){
		pushs << push
		this
	}
	
	boolean follow(Resource resource){
		true
	}
	
	boolean visit(Resource r){
		pushs.each{ push ->
			if(push.containsKey('title')){
				if(r.getDocument()?.title() =~ push.title.match){
					r.priority *= push.title.push
					
					//println "pushed $r.title with ${push.title.push}"
				}
			}
			
			if(push.containsKey('url')){
				if(r.url.toString() =~ push.url.match){
					r.priority *= push.url.push
					
					//println "pushed $r.url with ${push.url.push}"
				}
			}
		}
		
		true
	}
}
