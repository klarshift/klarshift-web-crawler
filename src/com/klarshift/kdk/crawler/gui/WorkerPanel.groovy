

package com.klarshift.kdk.crawler.gui

import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

import com.klarshift.kdk.crawler.CrawlerWorker

class WorkerPanel extends JPanel implements ActionListener, ChangeListener {
	CrawlerWorker worker
	JList hostList
	JList resourceList
	DefaultListModel hostModel, resourceModel

	JLabel pendingCount = new JLabel()
	
	WorkerPanel(CrawlerWorker worker){
		this.worker = worker
		
		JLabel title = new JLabel("Worker ${worker.id}")
		title.setPreferredSize(new Dimension(100, 20))
		add(title)
		
		// add slider		
		add(new PropertySlider("Delay", worker.delayTime, 10, 10000))
		add(new PropertySlider("Pop Size", worker.popSize, 1, 100))
		
		
		// host list
		def p
		hostList = new JList()		
		p = new JScrollPane(hostList)
		p.setPreferredSize(new Dimension(300, 100))
		add(p)
		
		// resource list
		resourceList = new JList()
		p = new JScrollPane(resourceList)
		p.setPreferredSize(new Dimension(300, 200))		
		add(p)
		
		Thread.start{
			while(worker.running){
				hostModel = new DefaultListModel()
				worker.resources?.clone().collect{it.url.host}?.unique()?.each { hostModel.addElement(it) }
				hostList.setModel(hostModel)
				
				resourceModel = new DefaultListModel()
				worker.resources?.clone().collect{"$it"}.each { resourceModel.addElement(it) }
				resourceList.setModel(resourceModel)
				
				//resourceList.parent.revalidate()
				
				Thread.sleep(500)
				
				
			}
		}
		
		setPreferredSize(new Dimension(320, 600))
		setVisible(true)
	}
	
	void actionPerformed(ActionEvent e){
		
	}
	
	void stateChanged(ChangeEvent e){
		
	}
}
