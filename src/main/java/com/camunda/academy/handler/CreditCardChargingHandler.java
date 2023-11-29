package com.camunda.academy.handler;

import com.camunda.academy.services.CreditCardService;
import com.camunda.academy.services.CustomerService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.util.Map;

public class CreditCardChargingHandler implements JobHandler {

	@Override
	public void handle(JobClient client, ActivatedJob job) throws Exception {
		System.out.println("Job handled: " + job.getType());

		Map<String, Object> variables = job.getVariablesAsMap();
		var cvc = (String) variables.get("cvc");
		var cardNumber = (String) variables.get("cardNumber");
		var expiryDate = (String) variables.get("expiryDate");
		var openAmount = (Double) variables.get("openAmount");

		var creditCardService = new CreditCardService();
		creditCardService.chargeAmount(cardNumber, cvc, expiryDate, openAmount);

		// Complete the Job
		client.newCompleteCommand(job.getKey()).send().join();
	}
}