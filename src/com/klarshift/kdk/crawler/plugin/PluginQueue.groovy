package com.klarshift.kdk.crawler.plugin

import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.PluginHolder
import com.klarshift.kdk.crawler.Resource


/**
 * plugin queue
 * 
 * the plugin queue can be used to combine several plugins
 * 
 * @author timo
 *
 */
class PluginQueue extends CrawlerPlugin {	
	private PluginHolder plugins = new PluginHolder()
		
	PluginQueue(){
		super("PluginQueue")
	}
	
	PluginQueue(String name){
		super(name)
	}
	
	PluginQueue addPlugin(CrawlerPlugin plugin){		
		plugins.addPlugin(plugin)		
		println "added $plugin"
		return this
	}
	
	void onAdded(){
		plugins.getAll().each{ CrawlerPlugin p ->
			p.crawler = crawler
			p.onAdded()
		}
	}
	
	int count(){ plugins.count() }
	
	CrawlerPlugin getPlugin(String name){
		plugins.getPlugin(name)
	}
	
	boolean hasPlugin(String name){
		plugins.hasPlugin(name)
	}
	
	

	boolean follow(Resource resource){		
		if(plugins.getAll().find{CrawlerPlugin p -> !p.follow(resource)}){
			return false
		}
		
		true		
	}

	boolean visit(Resource resource){
		if(plugins.getAll().find{CrawlerPlugin p -> !p.visit(resource)}){
			return false
		}
		
		true	
	}
}
