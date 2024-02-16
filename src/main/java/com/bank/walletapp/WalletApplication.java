package com.bank.walletapp;

import com.bank.walletapp.models.User;
import com.bank.walletapp.models.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * ability to create new wallets for same single user,
 * deposit and withdraw from any of these wallets,
 * list balance amounts in all the wallets.
 * Users should be able to register themselves with a username and pwd
 * Registered users should have their own wallet
 * Registered users should be able to deposit and withdraw from their wallet
 * Registered users should be not be able to deposit and withdraw from other’s wallet
 * Endpoint to delete user along with the wallet
 * todo:
 * handle exceptions
 * response and request models
 * authorization
 * test controller
 * implement equals in money
 * **/

@SpringBootApplication
public class WalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}
}
