package com.klarshift.kdk.crawler.plugin

import com.klarshift.kdk.crawler.CrawlerPlugin;
import com.klarshift.kdk.crawler.Resource

class CrawlPeriod extends CrawlerPlugin{
	int period = 60*60*8 // 8 hours
	boolean enabled = true
	
	CrawlPeriod(){
		
	}
	
	CrawlPeriod(int period){
		this.period = period
	}
	
	long getLastVisit(Resource r){
		
		Resource v = crawler.indexer.getResource(r)
		if(v){			
			return (new Date().time - v.lastVisit.time) /1000
		}
		-1
	}
	
	boolean visit(Resource r){
		if(!enabled)return true
		
		long dif = getLastVisit(r)
		
		if(dif > 0){			
			if(dif > period){
				return true
			}			
			return false
		}
		true
	}
	
	boolean follow(Resource r){
		if(!enabled)return true
		
		long dif = getLastVisit(r)		
		if(dif > 0){
			if(dif > period){
				return true
			}
			//println "dont queue $r"
			return false
		}
		true
	}
	
	String getName(){
		"crawlPeriod"
	}
}
