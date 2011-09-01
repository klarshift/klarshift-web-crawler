package com.klarshift.kdk.crawler.plugin

import java.util.regex.Pattern

import com.klarshift.kdk.crawler.Crawler
import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.Resource



/**
 * robots txt plugin
 * 
 * checks for robots.txt on server and filter out unallowed urls
 * @author timo
 *
 */
class RobotsPlugin extends CrawlerPlugin{
	boolean enabled = true
	
	HashMap<String,Map> robotsCache = new HashMap<String,Map>()
	HashSet<String> spiders = new HashSet<String>()

	String getName(){
		"robotsTxt"
	}

	/**
	 * check if a resource is accessible by robots.txt 
	 * @param r
	 * @return
	 */
	boolean isAllowed(Resource r, String userAgent = "*"){
		if(!crawler)println "MMMMMMMMMMH"
		// only check when enabled
		if(!enabled)return true
		
		// get robots data
		fetchRobotsInfo(r)			
		
		// check allowance
		String host = r.url.host	
		if(robotsCache.containsKey(host)){
			if(robotsCache[host].containsKey(userAgent)){
				// get rules for this user agent
				def rules = robotsCache[host][userAgent]


				def dissRule = rules.find{
					def resourceMatch = it.key
					resourceMatch = resourceMatch.replace('*', ".*")
					resourceMatch = Pattern.compile("^$resourceMatch")
					if(r.url.path =~ resourceMatch){
						return true
					}
					false
				}

				if(dissRule){
					if(dissRule.value){
						println "allowed by robots"
						return true
					}
					else return false
				}

			}

		}
		

		// allow without rule or robots.txt
		return true
	}

	boolean visit(Resource r){				
		if(!isAllowed(r) || !isAllowed(r, crawler?.userAgent)){
			return false
		}
		return true
	}
	
	boolean follow(Resource r){
		return true
		if(!isAllowed(r) || !isAllowed(r, crawler?.userAgent)){
			return false
		}
		return true
	}

	private void fetchRobotsInfo(Resource r){
		String host = r.url.host
		
		if(!robotsCache.containsKey(host)){
			// fetch robots.txt
			Map robotsContent = [:]
			String robotsUrl = r.url.protocol + "://" + r.url.host + "/robots.txt"
			try{
				URLConnection c = new URL(robotsUrl).openConnection()
				if(c.contentType?.startsWith("text/plain")){
					String userAgent
					c.inputStream.readLines().each{ String line ->
						if(!line.startsWith('#')){
							def lc = line =~ /(.+?):(.+)#+/
							if(lc){
								String command = lc[0][1].trim().toLowerCase()
								String value = lc[0][2].trim()
								

								if(command.equals('user-agent')){

									userAgent = value

									// new spider list
									robotsContent[userAgent] = [:]
									
									


									// add to spiders list
									if(!spiders.contains(userAgent)){
										println "new spider $userAgent"
										spiders.add(userAgent)
									}
								}else if(command == 'sitemap'){
									println "found sitemap $value "
								}else if(command == 'allow' && userAgent){
									robotsContent[userAgent][value] = true
								}else if(command == 'disallow' && userAgent){
									robotsContent[userAgent][value] = false
								}else{
									println "parsed unknown command: ua=$userAgent, $command = $value"
								}
							}
						}
					}

				}

			}catch(FileNotFoundException e){
				println e.message
			}catch(Exception e){
				println e.message
			}

			robotsCache.put(host, robotsContent)
		}
	}
}
