package com.camunda.academy.services.exceptions;

public class InvalidExpiryDateException extends Throwable {

  public InvalidExpiryDateException(String expiryDate) {
    super("The expiry date of the credit card is invalid: " + expiryDate);
  }
}
