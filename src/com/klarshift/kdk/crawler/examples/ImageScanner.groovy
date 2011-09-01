package com.klarshift.kdk.crawler.examples

import com.klarshift.kdk.crawler.Crawler
import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.Resource
import com.klarshift.kdk.crawler.gui.CrawlerView
import com.klarshift.kdk.crawler.plugin.BasePlugin

class MyPlugin extends CrawlerPlugin{
	MyPlugin(){
		
	}
	
	boolean visit(Resource r){
		if(r.isRoot()){
			r.properties.ed = 0
			r.properties.id = 0
		}
		
		true
	}
	
	boolean follow(Resource r){			
		// take parents priority by default
		if(r.parent){
			r.priority = r.parent.priority
			
			r.properties.ed = r.parent.properties.ed
			r.properties.id = r.parent.properties.id
		}
						
		// external links
		if(r.isExernal()){
			r.priority *= 1.1
			r.properties.id = 0
			r.properties.ed++
		}
		
		// internal links
		if(r.isInternal()){
			r.priority *= 1.2
			r.properties.id++
		}
		
		// filter
		// ...

					
		return true
	}
}

class ImageScanner {
	static main(args) {
		// create
		def c = new Crawler()			
		
		// add plugins
		c.addPlugin(new BasePlugin())		
			.addPlugin(new MyPlugin())				
		
		// start view
		new CrawlerView(c)
			
		def r = new Resource(new URL("http://www.berlin.de/"))
		r.priority = 2
		c.queue.addResources([r])
	
		//c.start()
	}
}
	

