
package com.klarshift.kdk.crawler

import java.sql.Timestamp;

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Resource  {
	// parent reference
	Resource parent

	// public properties
	private URL url
	double priority = 1
	Date lastVisit
	String sessionId

	private int status = -1	
	private HttpURLConnection connection
	private Document document
	private Crawler crawler
	private byte[] content
	
	Map properties = [:]
	
	static Resource get(Map p){
		if(p){
			Resource r = new Resource(new URL(p.url))
			r.priority = p.containsKey('priority') ? p.priority : r.priority		
			r.lastVisit = p.containsKey('lastVisit') ? p.lastVisit : r.lastVisit
			
			// set parent
			if(p.parent && p.parent instanceof String){
				r.parent = new Resource(new URL(p.parent))
			}
			
			// set additional properties
			if(p.properties){
				r.properties  = p.properties
			}
			
			r
		}
	}
	
	Resource(){
		
	}
	
	Resource(URL url, Resource parent = null){
		setUrl(url)
		this.parent = parent
	}
	
	void setUrl(URL url){
		if(url){
			this.url = url
			this.url.host = url.host.toLowerCase()
		}
	}
	
	URL getUrl(){
		url
	}


	public int getStatus(){
		try{
			return getConnection()?.getResponseCode()
		}catch(Exception e){
			println "RESOURCE ERROR: $this"
			//throw e
		}
		-1		
	}
	
	boolean isExernal(){
		if(isRoot())return false
		parent.url.host != url.host
	}
	
	boolean isInternal(){
		if(isRoot())return true
		parent.url.host == url.host
	}
	
	boolean isRoot(){
		parent == null
	}

	String getContentType(){
		getConnection()?.getContentType()
	}
	
	String getCharset(){
		String contentType = getContentType()
		if(contentType){
			def cs = contentType.split("=")
			if(cs.size() > 1){
				return cs[1]
			}
		}
		'ISO-8859-1'
	}

	String getContentEncoding(){
		getConnection()?.getContentEncoding()
	}
	
	File getDownloadFile(){
		String fileName = "downloads/" + url.host + url.path
		if(url.path.isEmpty())fileName += "/_index.html"
		else if(url.path == '/')fileName += "_index.html"
		new File(fileName)
	}
	

	String toString(){
		"($priority) $url | $properties"
	}



	String getTitle(){
		if(getContentType() && getContentType().startsWith('text/html')){
			return getDocument()?.getElementsByTag('title')?.text()
		}
	}

	

	Crawler getCrawler(){
		if(!crawler && parent){
			return parent.getCrawler()
		}
		crawler
	}

	HttpURLConnection getConnection(){
		if(!connection){
			// open connection
			connection = ConnectionHelper.open(this)					
		}
		connection
	}
	
	
	
	byte[] getContent(){
		if(!content && getConnection()){
			try{
				content = getConnection()?.inputStream?.getBytes()
			}catch(FileNotFoundException e){
				println "FILE NOT FOUND: $e.message -> $this"
			}catch(IOException e){
				println "IOError: $e.message -> $this"
			}		
		}
		
		content
	}

	Document getDocument(){
		if(!document){
			if(getContentType()?.startsWith("text/html") && getContent()){
				try{
					document = Jsoup.parse(new String(getContent(), getCharset()))
				}catch(UnsupportedEncodingException e){
					
					println "ERROR: Failed to decode $this :: $e.message (${getConnection().headerFields})"
				}
			}
		}
		document
	}

	List<Resource> getReferences(){
		// get document
		Document doc = getDocument()

		if(!doc || !getContentType() || !getContentType().startsWith('text/html'))return []

		// get anchors
		def resourceUris = []



		// anchors
		resourceUris.addAll(doc.getElementsByTag('a').collect{ Element a ->
			[uri: a.attr('href'), type: 'a']
		})

		// images
		resourceUris.addAll(doc.getElementsByTag('img').collect{ Element a ->
			[uri: a.attr('src'), type: 'img']
		})

		// video
		resourceUris.addAll(doc.getElementsByTag('src').collect{ Element a ->
			[uri: a.attr('src'), type: 'source']
		})


		// normalize uris
		def fullUris = resourceUris?.collect{
			def u = it.uri
			if(u.startsWith('http://') || u.startsWith('https://')){
				return it
			}else if(u.startsWith('/')){
				it.uri = url.protocol + "://" + url.host + u
				return it
			}else if(u.startsWith('#')){
				//println "ignore hash $it"
			}
			null
		}?.findAll{it}

		def references = fullUris?.collect{ fullUri ->
			def r = new Resource(new URL(fullUri.uri), this)
			//r.priority = priority
			r
		}

		references
	}
}
