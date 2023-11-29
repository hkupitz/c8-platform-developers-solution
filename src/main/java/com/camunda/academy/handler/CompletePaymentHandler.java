package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.util.Map;
import java.util.Random;

public class CompletePaymentHandler implements JobHandler {
	private final ZeebeClient zeebeClient;

	public CompletePaymentHandler(ZeebeClient zeebeClient) {
		this.zeebeClient = zeebeClient;
	}

	@Override
	public void handle(JobClient client, ActivatedJob job) throws Exception {
		System.out.println("Job handled: " + job.getType());

		var orderId = (Integer) job.getVariable("orderId");

		zeebeClient.newPublishMessageCommand()
			.messageName("paymentCompletionMessage")
			.correlationKey(orderId.toString())
			.send().join();

		// Complete the Job
		client.newCompleteCommand(job.getKey()).send().join();
	}
}