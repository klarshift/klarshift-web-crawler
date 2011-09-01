package com.klarshift.kdk.crawler.plugin

import groovy.json.JsonBuilder;

import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.Resource

class Host{
	String id
	ArrayList<Host> hosts = new ArrayList<Host>()
	JsonBuilder j = new JsonBuilder()
	int occurance = 1
	
	Host(String id){
		this.id = id
	}
	
	void add(Host h){
		Host da = hosts.find{it.id == h.id} 
		if(!da){
			hosts << h					
		}else{
			da.occurance ++	
		}
	}
	
	String toJSON(int num = 5){
		synchronized(hosts){
			def childs = hosts?.sort{a, b -> b.occurance <=> a.occurance}
			childs = (childs.size() > 0) ? childs[0..(Math.min(childs.size(), num)-1)] : []
			
			String childrenJson = "[" + childs.collect { it.toJSON() }.join(",\r\n") + "]"
return """
{
	id: '$id', name: '$id', data: {relation: ''},
	children: $childrenJson
}
	"""
		}
	}
	
	Host findById(String host){		
		if(!host)return null		
		if(id == host)return this
		
		// check child hosts
		if(hosts){
			def c =  hosts.find{it.findById(host) != null}
			if(c)return c.findById(host)			
		}		
	}
	
	String toString(){
		"$id (${hosts.size()})"
	}
}

class HostRelations extends CrawlerPlugin{
	Host root
	int childCount
	
	HostRelations(int childCount = 7){
		this.childCount = childCount
	}
	
	void onStarted(){
		Thread.start {
			while(crawler.running){
				println 'saving'
				save(new File('/home/timo/klarshift/Workspace/www.klarshift.de/html/pages/leistungen/vis/data.js'))
				Thread.sleep(5000)
			}
		}
	}
	
	boolean visit(Resource r){			
		if(!root){
			root = new Host(r.url.host)
		}
					
		String host = r.url.host
		String parentHost = r.parent?.url?.host
				
		if(!parentHost || (host == parentHost))return true
					
		// get host
		Host current = root.findById(parentHost)
		
		if(current){
			current.add(new Host(host))
		}else{
			println "NOT FOUND !! $parentHost -> $host"
		}
		
		true
	}
	
	boolean follow(Resource r){
		return true
	}
	
	String getName(){
		"hostRelations"
	}
	
	void save(File f){
		// build tree
		if(!root)return

		// save
		f.write("var json=" + root.toJSON(childCount) + ";")
	}
}
