package com.example.UBXUtilityOne.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestResponseEntity {
	
	private Map<String,Object> metaData= new LinkedHashMap<>();
	private Object result;
	
	@JsonAnySetter
    public void add(String key, Object value) {
        metaData.put(key, value);
    }
	
	  @JsonAnyGetter
	    public Map<String,Object> getMetaData() {
	        return metaData;
	    }
	  
	  public void setResult(Object obj)
	  {
		  result=obj;
	  }
	  public Object getResult()
	  {
		  return result;
	  }

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("RestResponse{");
        sb.append(", meta data=").append(metaData);
        sb.append(", results=").append(result);
        sb.append('}');
		return sb.toString();
	}
}
