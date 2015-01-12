package com.minorityhobbies.util.ee;

import java.util.Date;
import java.util.List;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchStatusMailer implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final JobOperator op;
	private final long executionId;
	private final Session mailSession;
	private final BatchStatusEnabled mailer;
	
	public BatchStatusMailer(JobOperator op, long executionId,
			Session mailSession, BatchStatusEnabled mailer) {
		super();
		this.op = op;
		this.executionId = executionId;
		this.mailSession = mailSession;
		this.mailer = mailer;
	}
	
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			JobExecution exec = op.getJobExecution(executionId);
			List<StepExecution> steps = op.getStepExecutions(executionId);
			BatchStatus status = exec.getBatchStatus();
			switch (status) {
			case ABANDONED:
			case FAILED:
			case STOPPED:
				sendMail(mailer.onFailure(exec), exec, steps);
				return;
			case COMPLETED:
				sendMail(mailer.onSuccess(exec), exec, steps);				
				return;
			default:
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				break;
			}
		}
	}

	private void sendMail(BatchStatusMailTemplate mail, JobExecution execution, List<StepExecution> steps) {
		MimeMessage msg = new MimeMessage(mailSession);
		try {
			msg.setSubject(mail.getSubject());
			msg.setSender(new InternetAddress(mail.getFrom()));
			msg.setRecipient(RecipientType.TO, new InternetAddress(mail.getTo()));
			msg.setSentDate(new Date());
			msg.setText(listSteps(execution, steps));
			Transport.send(msg);
		} catch (MessagingException e) {
			logger.error("Failed to send batch status e-mail", e);
		}
	}

	private String listSteps(JobExecution execution, List<StepExecution> steps) {
		StringBuilder msgBody = new StringBuilder();
		msgBody.append(String.format("Execution report: %s%n", execution.getJobName()));
		msgBody.append(String.format("Job parameters: %s%n", execution.getJobParameters()));
		msgBody.append("\n");
		for (StepExecution step : steps) {
			msgBody.append(String.format("Step: %s%n", step.getStepName()));
			for (Metric metric : step.getMetrics()) {
				msgBody.append(String.format("  %s: %s%n", 
						metric.getType().toString(), metric.getValue()));
			}
			msgBody.append("\n");
		}
		return msgBody.toString();
	}
}
