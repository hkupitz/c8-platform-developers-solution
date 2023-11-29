package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.util.Map;
import java.util.Random;

public class InvokePaymentHandler implements JobHandler {
	private final ZeebeClient zeebeClient;

	public InvokePaymentHandler(ZeebeClient zeebeClient) {
		this.zeebeClient = zeebeClient;
	}

	@Override
	public void handle(JobClient client, ActivatedJob job) throws Exception {
		System.out.println("Job handled: " + job.getType());

		Map<String, Object> variables = job.getVariablesAsMap();
		var orderId = new Random().nextInt();
		variables.put("orderId", orderId);

		zeebeClient.newPublishMessageCommand()
			.messageName("paymentRequestMessage")
			.correlationKey("")
			.variables(variables)
			.send().join();

		// Complete the Job
		client.newCompleteCommand(job.getKey()).variable("orderId", orderId).send().join();
	}
}