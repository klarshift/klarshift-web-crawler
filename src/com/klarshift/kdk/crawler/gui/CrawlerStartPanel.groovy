package com.klarshift.kdk.crawler.gui

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

class CrawlerStartPanel extends JPanel implements ActionListener {
	CrawlerView view
	JButton startButton
	JButton stopButton
	JTextField entryText
	
	
	CrawlerStartPanel(CrawlerView view){
		this.view = view
		
		startButton = new JButton('Start')
		startButton.addActionListener(this)
		add(startButton)
		
		stopButton = new JButton('Stop')
		stopButton.addActionListener(this)
		add(stopButton)
		
		entryText = new JTextField('http://www.htw-berlin.de/')
		add(entryText)
	}
	
	void actionPerformed(ActionEvent e){
		JButton b = e.source
		if(b == startButton){
			Thread.start{
				view.crawler.start(){
					view.workerPanel.clear()
					view.crawler.workers.each{
						view.workerPanel.add(new WorkerPanel(it.value))
					}
					view.workerPanel.updateUI()
					view.pack()
					
				}				
			}													
		}else if(b == stopButton){
			if(view.crawler.running){
				view.crawler.stop()
			}
		}
	}
}
