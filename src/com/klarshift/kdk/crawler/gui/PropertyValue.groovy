package com.klarshift.kdk.crawler.gui

class PropertyValue<PropertyType> {
	PropertyType property
	
	PropertyValue(PropertyType property){
		set(property)
	}
	
	PropertyType get(){
		property
	}
	
	void set(PropertyType property){
		this.property = property
	}
}
