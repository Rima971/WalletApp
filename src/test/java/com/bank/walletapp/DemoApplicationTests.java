package com.bank.walletapp;

import com.bank.walletapp.controllers.WalletController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private WalletController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
