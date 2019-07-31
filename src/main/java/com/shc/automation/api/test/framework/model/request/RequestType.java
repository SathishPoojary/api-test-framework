package com.shc.automation.api.test.framework.model.request;

public enum RequestType {
	get, put, post, delete;

	public static RequestType getRequestType(String name){
		for(RequestType type : values()){
			if(type.name().equalsIgnoreCase(name)){
				return type;
			}
		}
		return RequestType.get;
	}
}
