package com.camunda.academy;

import com.camunda.academy.handler.CompletePaymentHandler;
import com.camunda.academy.handler.CreditCardChargingHandler;
import com.camunda.academy.handler.CreditDeductionHandler;
import com.camunda.academy.handler.InvokePaymentHandler;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import java.util.Scanner;

public class PaymentApplication {

  // Zeebe Client Credentials
  private static final String ZEEBE_ADDRESS = "a591c20a-a7b3-48f1-8acc-b19cf3864eca.bru-2.zeebe.camunda.io";
  private static final String ZEEBE_CLIENT_ID = "JZmuhQfT9V0DOoxgCxMkvagyEWWeBeyG";
  private static final String ZEEBE_CLIENT_SECRET = "GNb-Uy6Xl8RZBco4FwJuMQuFX4ztCU5ThVlk7dF06LMVuQJUCa8wjVVZSo0tbV~4";
  private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
  private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";

  public static void main(String[] args) {

    final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
      .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
      .audience(ZEEBE_TOKEN_AUDIENCE)
      .clientId(ZEEBE_CLIENT_ID)
      .clientSecret(ZEEBE_CLIENT_SECRET)
      .build();

    try (final ZeebeClient client = ZeebeClient.newClientBuilder()
      .gatewayAddress(ZEEBE_ADDRESS)
      .credentialsProvider(credentialsProvider)
      .build()) {

      // Request the Cluster Topology
      System.out.println("Connected to: " + client.newTopologyRequest().send().join());

      // Start a Job Worker
      final JobWorker creditDeductionWorker = client.newWorker()
        .jobType("credit-deduction")
        .handler(new CreditDeductionHandler())
        .open();

      final JobWorker creditCardWorker = client.newWorker()
        .jobType("credit-card-charging")
        .handler(new CreditCardChargingHandler())
        .open();

      final JobWorker paymentInvocationWorker = client.newWorker()
        .jobType("payment-invocation")
        .handler(new InvokePaymentHandler(client))
        .open();

      final JobWorker paymentCompletionHandler = client.newWorker()
        .jobType("payment-completion")
        .handler(new CompletePaymentHandler(client))
        .open();

      // Terminate the worker with an Integer input
      Scanner sc = new Scanner(System.in);
      sc.nextInt();
      sc.close();
      creditCardWorker.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
