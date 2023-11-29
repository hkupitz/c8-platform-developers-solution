package com.camunda.academy.handler;

import com.camunda.academy.services.CreditCardService;
import com.camunda.academy.services.CustomerService;
import com.camunda.academy.services.exceptions.InvalidExpiryDateException;
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

		try {
			creditCardService.chargeAmount(cardNumber, cvc, expiryDate, openAmount);
			client.newCompleteCommand(job.getKey()).send().join();
		} catch (InvalidExpiryDateException e) {
			System.out.println(e.getMessage());

			client.newThrowErrorCommand(job.getKey())
				.errorCode("creditCardChargeError")
				.errorMessage(e.getMessage())
				.send().join();
		} catch (Exception e) {
			e.printStackTrace();

			client.newFailCommand(job.getKey())
				.retries(0)
				.errorMessage(e.getMessage())
				.send().join();
		}
	}
}