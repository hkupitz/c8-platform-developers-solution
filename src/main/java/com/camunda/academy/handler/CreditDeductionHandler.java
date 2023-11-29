package com.camunda.academy.handler;

import com.camunda.academy.services.CustomerService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

public class CreditDeductionHandler implements JobHandler {

	@Override
	public void handle(JobClient client, ActivatedJob job) throws Exception {
		System.out.println("Job handled: " + job.getType());

		var customerId = (String) job.getVariable("customerId");
		var orderTotal = (Double) job.getVariable("orderTotal");

		var customerService = new CustomerService();
		Double openAmount = customerService.deductCredit(customerId, orderTotal);

		// Complete the Job
		client.newCompleteCommand(job.getKey()).variable("openAmount", openAmount).send().join();
	}
}