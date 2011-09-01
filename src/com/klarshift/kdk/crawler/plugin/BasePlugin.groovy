package com.klarshift.kdk.crawler.plugin

class BasePlugin extends PluginQueue
{
	BasePlugin(){
		super("basePlugin")
		
		addPlugin(new CrawlPeriod())
		
		// add plugins
		addPlugin(new StatusCodeTracker())
		
		addPlugin(new DeadLinkChecker())		
		addPlugin(new RobotsPlugin())
		
		addPlugin(new HostTracker())
		//addPlugin(new ImageTracker())		
	}
}