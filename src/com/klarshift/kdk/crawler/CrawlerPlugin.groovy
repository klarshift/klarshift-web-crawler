package com.klarshift.kdk.crawler

import groovy.sql.Sql;

/**
 * abstract crawler plugin
 * @author timo
 *
 */
abstract class CrawlerPlugin {
	// crawler holder
	// injected by crawler itself
	Crawler crawler	
	
	private String name = this.class.name
	
	private Sql sql
	Sql getSql(){
		if(!sql){
			sql = Sql.newInstance("jdbc:mysql://localhost:3306/plugins", "root", "t!87fs?81f", "com.mysql.jdbc.Driver")
		}
		sql
	}
	
	 
	
	/**
	 * plugin constructor
	 */
	CrawlerPlugin(){
		
	}
	
	/**
	 * 
	 * @param name
	 */
	CrawlerPlugin(String name){
		this.name = name
	}
	
	
	
	/**
	 * visit resource
	 * @param resource
	 * @return true | false
	 */
	boolean visit(Resource resource){
		true	
	}
	
	/**
	 * follow resource
	 * @param resource
	 * @return true | false
	 */
	boolean follow(Resource resource){
		true
	}
	
	/**
	 * onAdded callback
	 */
	void onAdded(){
		
	}
	
	void onStarted(){
		
	}
	
	String getName(){
		return name
	}
	
	String toString(){
		"Plugin `${getName()}`"
	}
	
	void println(Object message){
		System.out.println("Plugin ${getName()} :: $message")
	}
	
	void download(Resource resource){
		
		File downloadFile = resource.getDownloadFile()
		
	
		
		// create dirs
		File folder = new File(downloadFile.getParent())
		folder.mkdirs()
		
		// open file
		try{
			
			// get content
			String content = null
			if(resource.getDocument()){
				content = resource.getDocument().html()
				downloadFile.write(content)
			}else{	
				FileOutputStream fout = new FileOutputStream(downloadFile)
				fout << resource.getConnection().inputStream
				fout.close()
			}
		}catch(Exception e){
			println "FileNotFound $downloadFile"
		}
	}
}
