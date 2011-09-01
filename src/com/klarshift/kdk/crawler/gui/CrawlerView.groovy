package com.klarshift.kdk.crawler.gui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JScrollPane

import com.klarshift.kdk.crawler.Crawler

class CrawlerView extends JFrame {
	Crawler crawler
	
	CrawlerStartPanel startPanel
	
	JPanel contentPane
	JPanel sidebar
	JPanel workerPanel
	
	JLabel capacityLabel
	
	JProgressBar progressBar = new JProgressBar()
	
	CrawlerView(Crawler crawler){
		super()
		
		// store crawler
		this.crawler = crawler
		
		// content pane
		contentPane = new JPanel(new BorderLayout())
		contentPane.setVisible(true)
		add(contentPane)
		
		// create sidebar
		sidebar = new JPanel(new FlowLayout())
		sidebar.setPreferredSize(new Dimension(350, 700))
		contentPane.add(sidebar, BorderLayout.WEST)
		
		capacityLabel = new JLabel("0")
		sidebar.add(capacityLabel)
		
		progressBar.setVisible(true)
		progressBar.setMaximum(100)
		contentPane.add(progressBar, BorderLayout.SOUTH)
		
		
		// add start panel
		startPanel = new CrawlerStartPanel(this)
		sidebar.add(startPanel, BorderLayout.WEST)
		
		// add crawler params
		sidebar.add(new PropertySlider("Worker", crawler.maxWorkerCount, 1, 50), BorderLayout.WEST)
		
		
		
		// create worker panel
		workerPanel = new JPanel(new FlowLayout())		
		contentPane.add(new JScrollPane(workerPanel), BorderLayout.CENTER)
		
		// default size
		setPreferredSize(new Dimension(1200, 600))
		
		// pack
		pack()
		setVisible(true)
		
		Thread.start{
			while(true){
				Thread.sleep(5000)
				//capacityLabel.setText("" + crawler.indexer.getIndexRate())
				
				progressBar.setValue((int)(crawler.getProgress()*progressBar.getMaximum()))
			}
		}
	}
}
