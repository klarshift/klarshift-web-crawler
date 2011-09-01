package com.klarshift.kdk.crawler.plugin

import com.klarshift.kdk.crawler.CrawlerPlugin;
import com.klarshift.kdk.crawler.Resource

class StatusCodeTracker extends CrawlerPlugin{
	Map codes = [total:0]
	boolean stopOnError = true
	
	List successCodes = [200, 301, 302, 304]
	
	boolean visit(Resource r){
		
		// get status code
		int code = r.getStatus()			
		
		// store code
		if(!codes.containsKey(code)){
			codes[code] = 1
		}else{
			codes[code] ++
		}					
		codes.total++
		
		// stop on error
		if(stopOnError && !successCodes.contains(code)){
			println "stop due status [$code] and stopOnError=true ($r)" 
			return false
		}
	
		true
	}
	
	String getName(){
		"statusCodeTracker"
	}
}
