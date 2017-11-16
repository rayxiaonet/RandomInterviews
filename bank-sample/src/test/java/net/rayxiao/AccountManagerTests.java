package net.rayxiao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Created by rxiao on 10/31/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)


public class AccountManagerTests {
    @Mock
    private AccountRepository repository;
    private Account accountData = new Account("123", "Ray Xiao", new BigDecimal(10000.23), new ArrayList<Transaction>());
    @InjectMocks
    private AccountManager accountManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        //this.mockMvc = webAppContextSetup(webApplicationContext).build();
        when(repository.save(any(Account.class))).thenAnswer(new Answer<Account>() {
            @Override
            public Account answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                accountData = (Account) args[0];
                return accountData;
            }
        });

        when(repository.findOne(eq("123"))).thenAnswer(new Answer<Account>() {
            @Override
            public Account answer(InvocationOnMock invocation) throws Throwable {
                return accountData;
            }
        });

    }

    private void resetTransactions(Account accountData) {
        if (accountData != null) {
            accountData.transactions.clear();
        }
    }

    @Test
    public void balance() throws Exception {

        Account result;
        result = accountManager.getBalance("123");
        assertEquals(result.getBalance(), new BigDecimal(10000.23));
        assertEquals(result.getTransactions().size(), 0);
    }

    @Test
    public void deposit() throws Exception {
        resetTransactions(accountData);
        Account result = accountManager.deposit("123", "Ray Xiao1", new BigDecimal(39952.92), "test 1");
        assertEquals(result.getBalance(), new BigDecimal(39952.92).add(new BigDecimal(10000.23)));
        assertEquals(result.getTransactions().size(), 1);
        assertEquals(result.getId(), "123");

    }

    @Test
    public void depositToNewAccount() throws Exception {
        resetTransactions(accountData);
        Account result = accountManager.deposit("456", "Testa", new BigDecimal(4424.92), "test 1");
        assertEquals(result.getBalance(), new BigDecimal(4424.92));
        assertEquals(result.getTransactions().size(), 1);
        assertEquals(result.getId(), "456");

    }

    @Test(expected = AccountOperationException.class)
    public void depositTooLarge() throws Exception {
        resetTransactions(accountData);

        Account before = accountManager.getBalance("123");
        accountManager.deposit("123", "Ray Xiao1", AccountManager.SINGLE_DEPOSIT_MAX.add(BigDecimal.ONE), "test 1");

    }

    @Test(expected = AccountOperationException.class)
    public void depositTooManyTimes() throws Exception {
        resetTransactions(accountData);

        Account before = accountManager.getBalance("123");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(1000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(1000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(1000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(1000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(1000), "test 1");
    }

    @Test(expected = AccountOperationException.class)
    public void depositDailyLimitExceed() throws Exception {
        resetTransactions(accountData);

        Account before = accountManager.getBalance("123");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(40000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(40000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(40000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(40000), "test 1");
    }


    @Test
    public void withdraw() throws Exception {
        resetTransactions(accountData);
        Account result = accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(9952.92), "test 1");
        assertEquals(result.getBalance(), new BigDecimal(10000.23).subtract(new BigDecimal(9952.92)));
        assertEquals(result.getTransactions().size(), 1);

    }

    @Test(expected = AccountOperationException.class)
    public void withdrawFromInvalidAccount() throws Exception {
        resetTransactions(accountData);
        Account result = accountManager.withdraw("456", "Ray Xiao", new BigDecimal(9952.92), "test 1");
    }

    @Test(expected = AccountOperationException.class)
    public void withdrawNegativeAmount() throws Exception {
        resetTransactions(accountData);
        Account result = accountManager.withdraw("123", "Ray Xiao", new BigDecimal(-9952.92), "test 1");

    }


    @Test(expected = AccountOperationException.class)
    public void withdrawTooLarge() throws Exception {
        resetTransactions(accountData);

        Account before = accountManager.getBalance("123");
        accountManager.withdraw("123", "Ray Xiao1", AccountManager.SINGLE_WITHDRAWAL_MAX.add(BigDecimal.ONE), "test 1");

    }

    @Test(expected = AccountOperationException.class)
    public void withdrawNotEnoughBalance() throws Exception {
        resetTransactions(accountData);

        Account before = accountManager.getBalance("123");
        accountManager.withdraw("123", "Ray Xiao1", AccountManager.SINGLE_WITHDRAWAL_MAX.subtract(BigDecimal.ONE), "test 1");

    }

    @Test(expected = AccountOperationException.class)
    public void withdrawTooManyTimes() throws Exception {
        resetTransactions(accountData);

        Account before = accountManager.getBalance("123");
        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(500), "test 1");
        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(500), "test 1");
        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(500), "test 1");
        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(500), "test 1");
    }

    @Test(expected = AccountOperationException.class)
    public void withdrawDailyLimitExceed() throws Exception {
        resetTransactions(accountData);
        Account before = accountManager.getBalance("123");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(40000), "test 1");
        accountManager.deposit("123", "Ray Xiao1", new BigDecimal(40000), "test 1");

        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(20000), "test 1");
        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(20000), "test 1");
        accountManager.withdraw("123", "Ray Xiao1", new BigDecimal(10001), "test 1");
    }

}
