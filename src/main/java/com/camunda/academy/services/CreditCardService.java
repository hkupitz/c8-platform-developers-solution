package com.camunda.academy.services;

import com.camunda.academy.services.exceptions.InvalidExpiryDateException;

public class CreditCardService {

	public void chargeAmount(String cardNumber, String cvc, String expiryDate, Double amount)
		throws InvalidExpiryDateException {
		if (isValidExpiryDate(expiryDate)) {
			System.out.printf("charging card %s that expires on %s and has cvc %s with amount of %f %s", cardNumber, expiryDate, cvc, amount, System.lineSeparator());
			System.out.println("payment completed");
		} else {
			throw new InvalidExpiryDateException(expiryDate);
		}
	}

	private boolean isValidExpiryDate(String expiryDate) {
		return expiryDate.length() == 5;
	}
}