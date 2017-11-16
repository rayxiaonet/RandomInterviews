package net.rayxiao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxiao on 10/31/16.
 * <p>
 * Main business logic class to deal with Balance/Deposit/Withdraw operation
 */
@Component

public class AccountManager {
    static final public BigDecimal DAILY_DEPOSIT_MAX = new BigDecimal(150000);
    static final public BigDecimal SINGLE_DEPOSIT_MAX = new BigDecimal(40000);
    static final public int DAILY_DEPOSIT_FREQUENCY = 4;

    static final public BigDecimal DAILY_WITHDRAWAL_MAX = new BigDecimal(50000);
    static final public BigDecimal SINGLE_WITHDRAWAL_MAX = new BigDecimal(20000);
    static final public int DAILY_WITHDRAWAL_FREQUENCY = 3;


    @Autowired
    private AccountRepository repository;


    public Account getBalance(String id) {
        Account account = repository.findOne(id);
        if (account == null) {
            return new Account(id, "", new BigDecimal(0), new ArrayList<Transaction>());
        } else {
            return account;
        }
    }

    public Account withdraw(String id, String name, BigDecimal amount, String notes) {

        Account account = repository.findOne(id);
        if (account == null) {
            throw new AccountOperationException("Account " + id + " have no record, cannot proceed with withdraw");
        }
        if (amount.subtract(SINGLE_WITHDRAWAL_MAX).compareTo(BigDecimal.ZERO) > 0) {
            throw new AccountOperationException("exceed daily withdrawal limit(" + SINGLE_WITHDRAWAL_MAX + ")");
        }
        if (amount.intValue() <= 0) {
            throw new AccountOperationException("invalid amount for withdrawal, must > 0");
        }
        BigDecimal newBalance = account.getBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountOperationException("insufficient funds(" + account.getBalance() + ")");
        }
        List<Transaction> transactions = account.getTransactions();

        if (transactions.stream().
                filter(
                        t -> Transaction.Operation.WITHDRAW.equals(t.getOperation()) && t.getDate().equals(LocalDate.now()))
                .count() >= DAILY_WITHDRAWAL_FREQUENCY) {
            throw new AccountOperationException("too many withdraw made today (" + DAILY_WITHDRAWAL_FREQUENCY + ")");
        }
        BigDecimal totalWithdrawed = transactions.stream().
                filter(
                        t -> Transaction.Operation.WITHDRAW.equals(t.getOperation()) && t.getDate().equals(LocalDate.now()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWithdrawed.add(amount).compareTo(DAILY_WITHDRAWAL_MAX) > 0) {
            throw new AccountOperationException("exceed daily withdraw limit(" + DAILY_WITHDRAWAL_MAX + ")");
        }

        transactions.add(new Transaction(Transaction.Operation.WITHDRAW, amount, notes));
        Account newAccount = new Account(id, account.getName(), newBalance, transactions);

        return repository.save(newAccount);

    }

    public Account deposit(String id, String name, BigDecimal amount, String notes) {

        Account account = repository.findOne(id);
        BigDecimal newBalance = account != null ? account.getBalance().add(amount) : amount;
        if (amount.subtract(SINGLE_DEPOSIT_MAX).compareTo(BigDecimal.ZERO) > 0) {
            throw new AccountOperationException("exceed single deposit limit(" + SINGLE_DEPOSIT_MAX + ")");
        }
        List<Transaction> transactions = account != null ? account.getTransactions() : new ArrayList<Transaction>();

        if (transactions.stream().
                filter(
                        t -> Transaction.Operation.DEPOSIT.equals(t.getOperation()) && t.getDate().equals(LocalDate.now()))
                .count() >= DAILY_DEPOSIT_FREQUENCY) {
            throw new AccountOperationException("too many deposit made today (" + DAILY_DEPOSIT_FREQUENCY + ")");
        }

        BigDecimal totalDeposited = transactions.stream().
                filter(
                        t -> Transaction.Operation.DEPOSIT.equals(t.getOperation()) && t.getDate().equals(LocalDate.now()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDeposited.add(amount).compareTo(DAILY_DEPOSIT_MAX) > 0) {
            throw new AccountOperationException("exceed daily deposit limit(" + DAILY_DEPOSIT_MAX + ")");
        }

        transactions.add(new Transaction(Transaction.Operation.DEPOSIT, amount, notes));
        Account newAccount = new Account(id, name, newBalance, transactions);

        return repository.save(newAccount);

    }
}
