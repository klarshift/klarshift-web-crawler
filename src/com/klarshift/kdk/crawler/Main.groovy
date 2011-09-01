package com.klarshift.kdk.crawler

import com.klarshift.kdk.crawler.gui.CrawlerView
import com.klarshift.kdk.crawler.plugin.BasePlugin
import com.klarshift.kdk.crawler.plugin.HostRelations

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
		// spam filters
		if(['lsf', 'spurl', '.gov', '.mil', 'facebook', 'twitter', 'banner', 'wikipedia', '.gif', 'gnolia', 'hamburg.de', '?', 'arbeitsagentur', 'weier'].find{r.url.toString().contains(it)})return false		
		//if(!['/', '.de', '.com', '.html', '.htm', '.php', '.asp', '.aspx'].find{r.url.toString().endsWith(it)})return false
		
		if(['.png', '.gif', '.jpg', '.pdf'].find{r.url.toString().endsWith(it)})return false
		
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
		if(r.properties.ed > 0 && r.properties.id > 0){
			return false
		}
		
		
		if(r.properties.ed > 2 || r.properties.id > 1){
			return false
		}

					
		return true
	}
}



class Main {
	static main(args) {
		// create
		def c = new Crawler()
		
		c.stopListener << {
			HostRelations hr = c.getPlugin('hostRelations')			
			hr.save(new File('data.js'))
		}
		
		// add plugins
		c.addPlugin(new BasePlugin())
			//.addPlugin(new EmailSpider())
			.addPlugin(new MyPlugin())	
			.addPlugin(new HostRelations(10))
			
		//def pusher = new PriorityPusher()
		//pusher.minPriority(0.7)
		/*pusher.push(title: [match: /student/, push: 5.0], url: [match: /student/, push: 1])
			.push(url: [match : /\.(jpg|jpeg|png|avi|mpg|mp3|m4|flv)/, push: 1.1])
			.push(url: [title: /(usa|obama|öl|irak|bush)/, push: 2.0])
		c.addPlugin(pusher)*/
		
		// start view
		new CrawlerView(c)
			
		def r = new Resource(new URL("http://www.htw-berlin.de/"))
		r.priority = 2
		c.queue.addResources([r])
	
		//c.start()
<<<<<<< HEAD
=======

>>>>>>> 6751064... source
	}
}
