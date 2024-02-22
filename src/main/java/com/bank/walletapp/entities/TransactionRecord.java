package com.bank.walletapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactionRecords")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne()
    private User sender;

    @ManyToOne()
    private User receiver;

    @CreatedDate
    private LocalDateTime timestamp;

    @AttributeOverrides({
            @AttributeOverride(name = "numericalValue", column = @Column(name = "transactedAmount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transactedCurrency"))
    })
    private Money amount;

    public TransactionRecord(User sender, User receiver, Money amount){
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
}
