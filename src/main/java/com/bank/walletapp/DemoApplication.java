package com.bank.walletapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ability to create new wallets for same single user,
 * deposit and withdraw from any of these wallets,
 * list balance amounts in all the wallets.
 * Users should be able to register themselves with a username and pwd
 * Registered users should have their own wallet
 * Registered users should be able to deposit and withdraw from their wallet
 * Registered users should be not be able to deposit and withdraw from other’s wallet
 * todo:
 * handle exceptions
 * response and request models
 * authorization
 * test controller
 * **/

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
