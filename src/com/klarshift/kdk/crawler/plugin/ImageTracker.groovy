package com.klarshift.kdk.crawler.plugin

import java.awt.Graphics
import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import javax.swing.JFrame

import com.klarshift.kdk.crawler.CrawlerPlugin
import com.klarshift.kdk.crawler.Resource

/**
 * host tracker plugin
 * simply tracks the hosts and the date visited
 * 
 * @author timo
 *
 */
class ImageTracker extends CrawlerPlugin{
	ImageTrackerFrame frame = new ImageTrackerFrame()
	
	boolean visit(Resource r){
		try{		
			if(r.contentType?.startsWith("image")){				
				def image = ImageIO.read(new ByteArrayInputStream(r.getContent()))		
				if(image){
					frame.setSize(image.width, image.height)
					frame.image = image
					frame.update(frame.graphics)
				}
			}
		}catch(Exception e){
			e.printStackTrace()
		}
		
		true
	}	
	
	String getName(){
		"imageTracker"
	}
}

class ImageTrackerFrame extends JFrame{
	BufferedImage image
	
	ImageTrackerFrame(){
		super()
		title = "Image Tracker"
			
		setSize(100, 100)
		setVisible(true)
	}
	
	public void paint(Graphics g) {
		if(image)
			g.drawImage(image, 0, 0, null);
	}
}