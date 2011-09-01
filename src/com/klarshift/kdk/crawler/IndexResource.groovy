package com.klarshift.kdk.crawler

class IndexResource {
	Resource resource
	Date lastFetched
	
	IndexResource(Resource resource, Date lastFetched = new Date()){
		this.resource = resource
		this.lastFetched = lastFetched
	}
	
	String toString(){
		"$lastFetched / $resource"
	}
}
