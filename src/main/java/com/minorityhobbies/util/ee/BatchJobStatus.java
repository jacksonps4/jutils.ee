package com.minorityhobbies.util.ee;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BatchJobStatus {
	private long executionId;
	private String name;
	private Map<String, String> properties = new HashMap<>();
	private Date started;
	private Date finished;
	private String status;
	
	public BatchJobStatus() {
		super();
	}

	public BatchJobStatus(long executionId, String name, Date started, Date finished,
			String status) {
		super();
		this.executionId = executionId;
		this.name = name;
		this.started = started;
		this.finished = finished;
		this.status = status;
	}

	public long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(long executionId) {
		this.executionId = executionId;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = new HashMap<>(properties);
	}
	
	public void setProperties(Properties properties) {
		Enumeration<?> names = properties.propertyNames();
		while (names.hasMoreElements()) {
			String key = (String) names.nextElement();
			String value = properties.getProperty(key);
			this.properties.put(key, value);
		}
	}
}
