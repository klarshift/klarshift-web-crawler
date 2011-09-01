package com.klarshift.kdk.crawler

import javax.annotation.Resources;

import groovy.time.TimeCategory;

import com.klarshift.kdk.crawler.gui.PropertyValue

/**
 * crawler worker
 * @author timo
 *
 */
class CrawlerWorker {
	// crawler reference
	Crawler crawler 
	
	long id
	ArrayList<Resource> resources = []
	
	// worker state
	boolean running = false
	private boolean idle = true	
	boolean getIdle(){idle}
	
	// changeable properties
	PropertyValue<Integer> delayTime = new PropertyValue<Integer>(1500)
	PropertyValue<Integer> popSize = new PropertyValue<Integer>(20)
	
	HashMap<String,Date> hostRequests = new HashMap<String,Date>()
	
	/**
	 * create worker
	 * @param crawler
	 * @param id
	 */
	CrawlerWorker(Crawler crawler, int id){
		this.id = id
		this.crawler = crawler
	}
	
	void println(Object s){
		System.out.println("CW $id] (${resources?.size()}) :: $s")
	}
	
	void stop(){
		running = false
	}
	
	// sort by access time
	Date getAccessTime(Resource r){
		Date access
		if(hostRequests.containsKey(r.url.host)){
			access = hostRequests[r.url.host]
		}else{
			access = new Date()
			use(TimeCategory) {
				access = access - delayTime.get().milliseconds
			}
			
			
		}
		
		access		
	}
	
	def getHosts(){
		if(resources){
			synchronized(resources){
				return resources?.collect { it.url.host }?.unique()
			}
		}
	}
	
	Resource getNext(){

		// request n resources from crawler		
		if(!resources || resources.size() == 0){
			// get new resources
			resources = crawler.pop(this, popSize.get())
			
			// clear host requests
			hostRequests.clear()
		}
			
		if(resources){
			// sort by access time			
			resources = resources.sort{a, b -> getAccessTime(a) <=> getAccessTime(b)}			
			
			// get first one and remove
			Resource next = resources[0]
			resources.remove(0)
			return next
		}
		
	}
	
	void crawl(){
		running = true
		
		println "starting worker ..."
		
		Resource next		
		while(running){
			// get next resource
			next = getNext()
			
			if(next){
				// set idle to false
				idle = false
				
				// wait difference (now-visit) + d = delayTime
				int waitTime = Math.max(0, delayTime.get() - (new Date().time - getAccessTime(next).time))
				Thread.sleep(waitTime)
				
				// store request time
				hostRequests[next.url.host] = new Date()
								
				// process resource
				if(crawler.processResource(next)){
					
				}

			}else{
				// idle
				idle = true
				Thread.sleep(1000)				
			}
		}
		
		println "end worker ..."
	}
}
