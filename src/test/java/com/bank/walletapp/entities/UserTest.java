package com.bank.walletapp.entities;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.exceptions.WalletNotFound;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    @Test
    public void test_shouldThrowWalletNotFoundWhenTryingToGetWalletButItIsNull(){
        User user = new User(TestConstants.USER_ID, TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA, null);
        assertThrows(WalletNotFound.class, user::getWallet);
    }

    @Test
    public void test_shouldCreateWalletWithCurrencyInAccordanceToUserLocation() throws WalletNotFound {
        User asianUser = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
        assertEquals(Country.INDIA.currency, asianUser.getWallet().getBalance().getCurrency());

        User americanUser = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.USA);
        assertEquals(Country.USA.currency, americanUser.getWallet().getBalance().getCurrency());

        User europeanUser = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.UK);
        assertEquals(Country.UK.currency, europeanUser.getWallet().getBalance().getCurrency());
    }
}
