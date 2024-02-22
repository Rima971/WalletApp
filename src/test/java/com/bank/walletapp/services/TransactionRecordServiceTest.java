package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.repositories.TransactionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class TransactionRecordServiceTest {
    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @InjectMocks
    private TransactionRecordService transactionRecordService;
    @BeforeEach
    void setup(){
        openMocks(this);
    }
    @Test
    public void test_shouldAddATransactionRecord(){
        TransactionRecord dummyRecord = new TransactionRecord(new User(), new User(), new Money());
        assertDoesNotThrow(()->this.transactionRecordService.add(dummyRecord));
        verify(this.transactionRecordRepository).save(dummyRecord);
    }
    @Test
    public void test_shouldFetchListOfTransactionRecordsForAnAuthenticatedUserByUsername(){
        TransactionRecord dummyRecord = new TransactionRecord(new User(), new User(), new Money());
        List<TransactionRecord> log = new ArrayList<>(List.of(dummyRecord, dummyRecord));
        when(this.transactionRecordRepository.findBySenderUsername(TestConstants.USERNAME)).thenReturn(log);
        when(this.transactionRecordRepository.findByReceiverUsername(TestConstants.USERNAME)).thenReturn(log);

        assertDoesNotThrow(()-> {
            List<TransactionRecord> result = this.transactionRecordService.fetchAll(TestConstants.USERNAME);
            assertTrue(result.stream().allMatch(r->r==dummyRecord));
            assertEquals(log.size()*2, result.size());
        });
        verify(this.transactionRecordRepository).findBySenderUsername(TestConstants.USERNAME);
        verify(this.transactionRecordRepository).findByReceiverUsername(TestConstants.USERNAME);
    }
}
