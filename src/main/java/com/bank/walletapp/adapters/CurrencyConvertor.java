package com.bank.walletapp.adapters;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.UnsuccessfulCurrencyConversion;
import currencyConvertor.currencyConvertorRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import currencyConvertor.currencyConvertorServiceGrpc;
import org.springframework.stereotype.Component;

@Component
public class CurrencyConvertor {
        private static final int CURRENCY_CONVERTOR_SERVICE_PORT = 8090;
        private static final String CURRENCY_CONVERTOR_SERVICE_HOST = "localhost";

        private static final ManagedChannel CHANNEL = ManagedChannelBuilder
                .forAddress(CURRENCY_CONVERTOR_SERVICE_HOST, CURRENCY_CONVERTOR_SERVICE_PORT)
                .usePlaintext()
                .build();

        private static final currencyConvertorServiceGrpc.currencyConvertorServiceBlockingStub CLIENT = currencyConvertorServiceGrpc.newBlockingStub(CHANNEL);

    public CurrencyConvertor() {
    }


    public Money convertCurrency(double value, Currency fromCurrency, Currency toCurrency) throws UnsuccessfulCurrencyConversion {
                try{
                        currencyConvertor.Money money = currencyConvertor.Money.newBuilder().setCurrency(fromCurrency.name()).setValue(value).build();
                        currencyConvertorRequest request = currencyConvertorRequest.newBuilder().setMoney(money).setTargetCurrency(toCurrency.name()).build();
                        currencyConvertor.Money convertedMoney = CLIENT.convert(request);
                        return new Money(convertedMoney.getValue(), Currency.valueOf(convertedMoney.getCurrency()));
                } catch (Exception e){
                        throw new UnsuccessfulCurrencyConversion(e.getLocalizedMessage());
                }

        }
}
