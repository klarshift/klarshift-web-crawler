package com.klarshift.kdk.crawler.indexer

import com.klarshift.kdk.crawler.Resource

interface IndexerInterface {
	boolean has(URL url)
	boolean has(Resource resource)
	
	void index(Resource resource)
	void unindex(Resource resource)
	
	long count()
}
