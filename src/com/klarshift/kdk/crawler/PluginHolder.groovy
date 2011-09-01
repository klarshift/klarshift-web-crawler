package com.klarshift.kdk.crawler

class PluginHolder {
	private LinkedList<CrawlerPlugin> plugins = new LinkedList<CrawlerPlugin>()
	
	int count(){
		plugins.size()
	}
	
	void addPlugin(CrawlerPlugin plugin){
		plugins << plugin
	}
	
	boolean hasPlugin(String name){
		getPlugin(name) != null	
	}
	
	CrawlerPlugin getPlugin(String name){
		plugins.find{it.getName() == name}
	}
	
	LinkedList<CrawlerPlugin> getAll(){
		plugins
	}
}
