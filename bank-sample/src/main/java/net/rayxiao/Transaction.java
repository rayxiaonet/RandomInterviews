package net.rayxiao;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Created by rxiao on 10/31/16.
 * Data model represents a bank transaction in this sample project
 */
public class Transaction {
    public enum Operation {
        DEPOSIT,
        WITHDRAW,
        LOGIN,
        LOGOUT,
        QUERY
    }

    @Id
    private String id;

    private long timestamp;

    private Operation operation;

    private BigDecimal amount;
    private String notes;
    private LocalDate date;


    public Operation getOperation() {
        return this.operation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Transaction(Operation operation, BigDecimal amount, String notes) {

        this.timestamp = System.currentTimeMillis();
        this.date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        this.operation = operation;
        this.amount = amount;
        this.notes = notes;


    }
}
