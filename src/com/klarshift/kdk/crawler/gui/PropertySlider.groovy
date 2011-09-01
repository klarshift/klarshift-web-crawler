package com.klarshift.kdk.crawler.gui

import java.awt.Dimension

import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class PropertySlider extends JPanel implements ChangeListener{
	PropertyValue property
	String name
	
	JLabel nameLabel
	JLabel valueLabel
	JSlider slider
	
	PropertySlider(String name, PropertyValue property, int min, int max){
		// store property
		this.property = property
		
		// add label
		nameLabel = new JLabel(name)
		add(nameLabel)
		
		// create slider		
		slider = new JSlider(min, max, property.get())
		slider.addChangeListener(this)
		add(slider)
		
		// value label
		valueLabel = new JLabel("${property.get()}")
		add(valueLabel)
		
		//setPreferredSize(new Dimension(200, 100))
		
		setVisible(true)
	}
	
	void stateChanged(ChangeEvent e){
		property.set((e.source as JSlider).value)
		valueLabel.text = "${property.get()}"
	}
}
