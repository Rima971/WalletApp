package com.bank.walletapp;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.repositories.TransactionRecordRepository;
import com.bank.walletapp.services.TransactionRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionRecordServiceTest {
    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @InjectMocks
    private TransactionRecordService transactionRecordService;
    @Test
    public void test_shouldAddATransactionRecord(){
        TransactionRecord dummyRecord = new TransactionRecord(new User(), new User(), new Money());
        assertDoesNotThrow(()->this.transactionRecordService.add(dummyRecord));
        verify(this.transactionRecordRepository).save(dummyRecord);
    }
    @Test
    public void test_shouldFetchListOfTransactionRecords(){
        TransactionRecord dummyRecord = new TransactionRecord(new User(), new User(), new Money());
        List<TransactionRecord> log = new ArrayList<>(List.of(dummyRecord, dummyRecord));
        when(this.transactionRecordRepository.findAll()).thenReturn(log);

        assertDoesNotThrow(()-> {
            Object result = this.transactionRecordService.fetchAll();
            assertEquals(log, result);
        });
        verify(this.transactionRecordRepository).findAll();
    }
}
