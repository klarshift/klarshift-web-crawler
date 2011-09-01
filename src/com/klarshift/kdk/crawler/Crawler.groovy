package com.klarshift.kdk.crawler

import groovy.lang.Closure

import java.util.ArrayList
import java.util.Map

import javax.net.ssl.SSLHandshakeException

import com.klarshift.kdk.crawler.gui.PropertyValue
import com.klarshift.kdk.crawler.indexer.DefaultIndexer
import com.klarshift.kdk.crawler.indexer.MongoIndexer
import com.klarshift.kdk.crawler.queue.MongoQueue

/**
 * crawler class
 * @author timo (timo@klarshift.de)
 *
 */
class Crawler {
	// some config
	static final int STAT_INTERVAL = 2000
	static final int THREAD_COUNT = 12
	
	def stopListener = []
	
	String sessionId

	// changeable properties
	String userAgent = "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11)"

	// queue & index
	final MongoIndexer indexer = new MongoIndexer()
	final MongoQueue queue = new MongoQueue()
	
	boolean follow = true

	// plugin holder
	private PluginHolder crawlerPlugins = new PluginHolder()

	final PropertyValue<Integer> maxWorkerCount = new PropertyValue<Integer>(3)

	// workers map
	Map workers = [:]

	// run flag
	private boolean running = false

	boolean isRunning(){running}

	Crawler addPlugin(CrawlerPlugin plugin){
		// set crawler
		plugin.crawler = this

		// add to plugins
		crawlerPlugins.addPlugin(plugin)

		// tell plugin
		plugin.onAdded()

		println "added $plugin"
		this
	}
	
	CrawlerPlugin getPlugin(String name){
		crawlerPlugins.getPlugin(name)
	}
	
	void stop(){
		println "stopping crawler ..."

		// stop all worker
		workers.each { it.value.stop() }

		// set running false
		running = false
		
		// inform listener
		stopListener.each{
			it()
		}
	}

	ArrayList<Resource> pop(CrawlerWorker worker, int popSize){

		// get all currently hosts
		synchronized(workers){
			def hosts = []
			workers.each{ hosts.addAll(it.value.getHosts() ?: [])}
			hosts = hosts.unique()

			// get resources by hosts
			int hostCount = 10
			int popPerHost = Math.max(1, (int)(popSize/hostCount))
			ArrayList<Resource> resources = queue.popHostNotContainedIn(hosts, popPerHost)
			for(int h=0; h<hostCount; h++){
				hosts.addAll(resources.collect{it.url.host}.unique())
				resources.addAll(queue.popHostNotContainedIn(hosts.unique(), popPerHost))
			}
			
			
			

			return resources
		}

	}

	/**
	 * start the crawler
	 * @param resource
	 */
	void start(Closure afterStart = null){
		// create session id
		sessionId = UUID.randomUUID().toString()


		// set crawler running
		running = true
		
		crawlerPlugins.getAll().each{
			it.onStarted()
		}

		// create workers
		for(final int t=0; t<THREAD_COUNT; t++){
			// create worker
			synchronized(workers){
				CrawlerWorker w = new CrawlerWorker(this, t+1)
				workers[t] = w

				Thread th = Thread.start{
					// start crawling
					try{
						w.crawl()
					}catch(UnknownHostException e){
						println "ERROR: Unknown Host :: $e.message"
					}catch(SocketTimeoutException e){
						println "ERROR: TimeOut :: $e.message"
					}catch(MalformedURLException e){
						println "ERROR: Malformed Url :: $e.message"
					}catch(SSLHandshakeException e){
						println "ERROR: SSL Handshake failed :: $e.message"
					}catch(SocketException e){
						println "ERROR: SocketException:: $e.message"
					}catch(Exception e){
						e.printStackTrace()
					}
				}
				th.setName("CrawlerWorker $t")
				
				Thread.sleep(250)
			}
		}

		// main loop
		Thread.start{
			// loop while the queue is not empty for given time
			while(running){
				Thread.sleep(5000)
				if(queue.count() == 0 && !workers.find{it.value.resources?.size() > 0}){
					stop()
				}
			}
		}


		if(afterStart){
			afterStart()
		}

		// stat loop

		while(running){
			int current = indexer.count()
			Thread.sleep(STAT_INTERVAL)

			long indexed = indexer.count()
			long queued = queue.count()

			//avgCapacity = (indexed-current)/(STAT_INTERVAL/1000)

			long resourcesTotal = indexed + queued
			double progress = (resourcesTotal) ? indexed / resourcesTotal : 0

			println "CRAWLER: [(${((int)(progress*10000))/100 } %] - [${indexer.getIndexRate()} per sec] - ${indexed} of $resourcesTotal"
		}



	}

	double getProgress(){
		long resourcesTotal = indexer.count() + queue.count() + (workers?.collect{it.value.resources?.size() ?: 0}.sum() ?: 0)
		double progress = (resourcesTotal) ? (indexer.count() / resourcesTotal) : 0
		progress
	}





	/**
	 * filter the follow resource
	 * @param r
	 * @return
	 */
	boolean visit(Resource r){
		CrawlerPlugin stop = crawlerPlugins.getAll().find{CrawlerPlugin p -> !p.visit(r)}
		if(stop){
			println "stopped with $stop $r"
			return false
		}

		true
	}

	/**
	 * get filtered urls
	 * @param urls
	 * @return
	 */
	ArrayList<Resource> filterResources(def resources){
		resources?.findAll{ u -> follow(u) }
	}

	/**
	 * filter single url
	 * @param url
	 * @return
	 */
	boolean follow(Resource r){
		if(!follow)return false
		
		if(crawlerPlugins.getAll().find{CrawlerPlugin p -> !p.follow(r)}){
			//println "stopped with follow : $r"
			return false
		}

		true
	}

	void println(Object message){
		System.out.println("Crawler :: $message")
	}

	boolean processResource(Resource resource){			
		// visit resource
		if(!visit(resource)){return false}
		
		// index resource
		indexer.index(resource)
			
		// get urls
		def resources = resource.references

		// filter urls to follow
		resources = filterResources(resources)

		// add all
		queue.addResources(resources)

		true
	}

	private Crawler(){

	}
}

