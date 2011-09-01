package com.klarshift.kdk.crawler.plugin

import java.util.List;

import com.klarshift.kdk.crawler.CrawlerPlugin;
import com.klarshift.kdk.crawler.Resource

class DeadLinkChecker extends CrawlerPlugin{
	List<Resource> deadLinks = []
	boolean stopOnDeadLink = true
	
	List successCodes = [200, 301, 302, 304]
	
	boolean visit(Resource r){
		int code = r.getStatus()
		if(!successCodes.contains(code)){
			println "Deadlink ($code) -> $r"
			deadLinks << r
			
			// stop on dead link
			if(stopOnDeadLink){
				return false
			}
		}
		
		true		
	}
	
	String getName(){
		"deadLinkChecker"
	}
}
