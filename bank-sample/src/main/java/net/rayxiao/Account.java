package net.rayxiao;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxiao on 10/31/16.
 * <p>
 * Data model represents a user accounts in this sample project
 */
public class Account {


    @Id
    private String id;

    private String name;
    private BigDecimal balance;

    List<Transaction> transactions;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    ;

    public Account() {
        this.transactions = new ArrayList<Transaction>();
    }

    public Account(String id, String name, BigDecimal balance, List<Transaction> transactions) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.transactions = transactions;
    }
}
