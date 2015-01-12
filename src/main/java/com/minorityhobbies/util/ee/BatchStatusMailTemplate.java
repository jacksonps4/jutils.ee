package com.minorityhobbies.util.ee;

public class BatchStatusMailTemplate {
	private final String from;
	private final String to;
	private final String subject;
	
	public BatchStatusMailTemplate(String from, String to, String subject) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getSubject() {
		return subject;
	}
}
