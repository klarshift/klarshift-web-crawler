package com.klarshift.kdk.crawler.plugin

import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.Resource

/**
 * host tracker plugin
 * simply tracks the hosts and the date visited
 * 
 * @author timo
 *
 */
class HostTracker extends CrawlerPlugin{
	HashMap<String,Date> hosts = new HashMap<String,Date>()
	
	boolean visit(Resource r){
		String host = r.url.host
		if(!hosts.containsKey(host)){
			hosts.put(host, new Date())
		}
		
		true
	}
	
	String getName(){"hostTracker"}	
}
