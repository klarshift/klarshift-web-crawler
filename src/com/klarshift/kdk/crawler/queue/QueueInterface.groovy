package com.klarshift.kdk.crawler.queue

import com.klarshift.kdk.crawler.Resource


/**
 * queue interface
 * @author timo
 *
 */
interface QueueInterface {
	void addResources(List<Resource> resources)
	void removeResources(List<Resource> resources)
	
	boolean has(Resource resource)	
	long count()
}
