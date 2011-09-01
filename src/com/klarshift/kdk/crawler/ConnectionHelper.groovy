package com.klarshift.kdk.crawler

import javax.net.ssl.SSLHandshakeException;

class ConnectionHelper {
	static final String UA_MOZILLA = "Mozilla/5.0 (X11; Linux i686; rv:6.0) Gecko/20100101 Firefox/6.0"
	
	static HttpURLConnection open(Resource resource, int timeOut = 15000){
		HttpURLConnection connection
		
		try{					
			// prepare connection
			connection = (HttpURLConnection)resource.url.openConnection()
			connection.setRequestProperty("User-Agent", UA_MOZILLA);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.connectTimeout = timeOut					
			connection.readTimeout = timeOut

			// connect
			connection.connect()											
		}catch(Exception e){
			//throw e
		}finally{
			/*if(connection.connected){
				((HttpURLConnection)connection).disconnect()
			}*/
		}
		
		connection
	}
}
