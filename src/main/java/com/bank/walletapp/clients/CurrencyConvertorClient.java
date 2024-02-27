package com.bank.walletapp.clients;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import currencyConvertor.currencyConvertorServiceGrpc;

public class CurrencyConvertorClient {
        private static final int CURRENCY_CONVERTOR_SERVICE_PORT = 8090;
        private static final String CURRENCY_CONVERTOR_SERVICE_HOST = "localhost";

        private static final ManagedChannel CHANNEL = ManagedChannelBuilder
                .forAddress(CURRENCY_CONVERTOR_SERVICE_HOST, CURRENCY_CONVERTOR_SERVICE_PORT)
                .usePlaintext()
                .build();

        public static final currencyConvertorServiceGrpc.currencyConvertorServiceBlockingStub CLIENT = currencyConvertorServiceGrpc.newBlockingStub(CHANNEL);
}
