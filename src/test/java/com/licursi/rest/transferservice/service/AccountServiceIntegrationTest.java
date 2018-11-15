package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.AccountBuilder;
import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.NegativeConstraintViolationException;
import com.licursi.rest.transferservice.model.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    private Account accountJorahMormont;

    @Before
    public void setup() {

        accountJorahMormont = AccountBuilder.createGeneric("Jorah Mormont").id(7L).balance("100000.00").build();
    }

    @Test
    public void givenSave_when_thenReturnAutoGeneratedId() throws BalanceConstraintViolationException {
        final Account account = AccountBuilder.createGeneric("King Joffrey").noId().build();

        Account accountSaved = accountService.save(account);
        assertThat(accountSaved.getId()).isNotZero();

    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void givenSave_whenNullName_throwException() throws BalanceConstraintViolationException {
        final Account account = AccountBuilder.createGeneric().noId().noName().build();

        accountService.save(account);
    }

    @Test(expected = org.springframework.transaction.TransactionSystemException.class)
    public void givenSave_whenWithId_throwException() throws BalanceConstraintViolationException {
        final Account account = AccountBuilder.createGeneric().noName().build();

        accountService.save(account);
    }

    @Test()
    public void givenSave_whenNullName_throwExceptionWithPropertyPath() {
        final Account account = AccountBuilder.createGeneric().noId().noName().build();

        try {
            accountService.save(account);
        } catch (javax.validation.ConstraintViolationException c){
            assertThat(c.getConstraintViolations().size()).isGreaterThan(0);
            assertThat( c.getConstraintViolations().iterator().next().getPropertyPath().toString()).isEqualTo("name");
        } catch (BalanceConstraintViolationException e) {
            e.printStackTrace();
        }
    }

    @Test()
    public void givenSave_whenInvalidBalance_throwExceptionWithPropertyPath() throws BalanceConstraintViolationException {
        final Account account = AccountBuilder.createGeneric("Little Finger").noId().balance(new BigDecimal("0.0031987238")).build();

        try {
            accountService.save(account);
        } catch (javax.validation.ConstraintViolationException c) {
            assertThat(c.getConstraintViolations().size()).isGreaterThan(0);
            assertThat(c.getConstraintViolations().iterator().next().getPropertyPath().toString()).isEqualTo("balance");
        }
    }

    @Test(expected = BalanceConstraintViolationException.class)
    public void givenSave_whenNegativeBalance_throwException() throws BalanceConstraintViolationException {
        final Account sansaStark = AccountBuilder.createGeneric("Sansa Stark").noId().balance(new BigDecimal(-100)).build();

        accountService.save(sansaStark);
        assertThat(true).isTrue();

    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void givenSave_whenNullBalance_throwException() throws BalanceConstraintViolationException {
        final Account account = AccountBuilder.createGeneric("Tormund").noId().noBalance().build();
        accountService.save(account);
    }


    @Test(expected = NegativeConstraintViolationException.class)
    public void givenDeposit_whenNegativeAmount_throwException() throws BalanceConstraintViolationException, NegativeConstraintViolationException {
        accountService.deposit(accountJorahMormont, new BigDecimal(-100));

    }

    @Test(expected = NegativeConstraintViolationException.class)
    public void givenDeposit_wheneZero_throwException() throws BalanceConstraintViolationException, NegativeConstraintViolationException {
        accountService.deposit(accountJorahMormont, new BigDecimal(0));
    }

    @Test
    public void givenDeposit_when100_thenAccountRiseBy100() throws BalanceConstraintViolationException, NegativeConstraintViolationException, AccountNotFoundException {
        int finalValue = 100;
        BigDecimal raiseByValue = new BigDecimal("100");

        Account accountSandorClegane = accountService.save(AccountBuilder.createGeneric("Sandor Clegane").balance("0").build());
        accountService.deposit(accountSandorClegane, raiseByValue);
        final Account account = accountService.findById(accountSandorClegane.getId());


        assertThat(account.getBalance().setScale(2)).isEqualTo(new BigDecimal(finalValue).setScale(2));
        assertThat(accountSandorClegane.getBalance().setScale(2)).isEqualTo(new BigDecimal(finalValue).setScale(2));
    }


    @Test
    public void givenDeposit_whenFloatingValue_thenAccountRiseByFloatingValue() throws BalanceConstraintViolationException, NegativeConstraintViolationException, AccountNotFoundException {
        BigDecimal raiseByValue = new BigDecimal("7.33");
        BigDecimal finalValue = new BigDecimal("17.99");

        Account accountAyraStark = accountService.save(AccountBuilder.createGeneric("Arya Stark").balance("10.66").build());
        accountService.deposit(accountAyraStark, raiseByValue);
        final Account account = accountService.findById(accountAyraStark.getId());


        assertThat(account.getBalance()).isEqualTo(finalValue);
        assertThat(accountAyraStark.getBalance()).isEqualTo(finalValue);
    }

    @Test
    public void givenDeposit_whenHugeValue_thenAccountRiseByHugeValue() throws BalanceConstraintViolationException, NegativeConstraintViolationException, AccountNotFoundException {
        BigDecimal raiseByValue = new BigDecimal("999999999989.33");
        BigDecimal finalValue = new BigDecimal("999999999999.99");

        Account accountRobyStark = accountService.save(AccountBuilder.createGeneric("Roby Stark").balance("10.66").build());
        accountService.deposit(accountRobyStark, raiseByValue);
        final Account account = accountService.findById(accountRobyStark.getId());

        assertThat(account.getBalance()).isEqualTo(finalValue);
        assertThat(accountRobyStark.getBalance()).isEqualTo(finalValue);
    }

    @Test(expected = NegativeConstraintViolationException.class)
    public void givenWithdraw_whenNegativeAmount_throwException() throws BalanceConstraintViolationException, NegativeConstraintViolationException {
        accountService.withdraw(accountJorahMormont, new BigDecimal(-100));
    }

    @Test(expected = NegativeConstraintViolationException.class)
    public void givenWithdraw_whenZero_throwException() throws BalanceConstraintViolationException, NegativeConstraintViolationException {
        accountService.withdraw(accountJorahMormont, new BigDecimal(0));
    }

    @Test
    public void givenWithdraw_when100_thenBalanceReduceBy100() throws BalanceConstraintViolationException, NegativeConstraintViolationException, AccountNotFoundException {
        BigDecimal reducedByValue = new BigDecimal("100");
        BigDecimal finalValue = new BigDecimal("0.00");

        Account accountSansaStark = accountService.save(AccountBuilder.createGeneric("Sansa Stark").balance("100").build());
        accountService.withdraw(accountSansaStark, reducedByValue);
        final Account account = accountService.findById(accountSansaStark.getId());

        assertThat(account.getBalance().setScale(2)).isEqualTo(finalValue.setScale(2));
        assertThat(accountSansaStark.getBalance().setScale(2)).isEqualTo(finalValue.setScale(2));
    }


    @Test
    public void givenWithdraw_whenFloatingValue_thenBalanceReduceByFloatingValueKeepingPrecision() throws BalanceConstraintViolationException, NegativeConstraintViolationException, AccountNotFoundException {
        BigDecimal initialValue = new BigDecimal("10.00");
        BigDecimal reducedByValue = new BigDecimal("9.99");
        BigDecimal finalValue = new BigDecimal("00.01");

        Account accountViserysTargaryen = accountService.save(AccountBuilder.createGeneric("Viserys Targaryen").balance(initialValue).build());
        accountService.withdraw(accountViserysTargaryen, reducedByValue);
        final Account account = accountService.findById(accountViserysTargaryen.getId());

        assertThat(account.getBalance().setScale(2)).isEqualTo(finalValue.setScale(2));
        assertThat(accountViserysTargaryen.getBalance().setScale(2)).isEqualTo(finalValue.setScale(2));

    }

    @Test
    public void givenWithdraw_whenHugeValue_thenBalanceReduceByHugeValueKeepingPrecision() throws BalanceConstraintViolationException, NegativeConstraintViolationException, AccountNotFoundException {
        BigDecimal finalValue = new BigDecimal("10.66");
        BigDecimal reducedByValue = new BigDecimal("999999999989.33");
        BigDecimal initialValue = new BigDecimal("999999999999.99");
        Account accountCerseiLannister = accountService.save(AccountBuilder.createGeneric("Cersei Lannister").balance(initialValue).build());
        accountService.withdraw(accountCerseiLannister, reducedByValue);
        final Account account = accountService.findById(accountCerseiLannister.getId());

        assertThat(account.getBalance()).isEqualTo(finalValue);
        assertThat(accountCerseiLannister.getBalance()).isEqualTo(finalValue);

    }

    @Test(expected = BalanceConstraintViolationException.class)
    public void givenWithdraw_whenMoreThanAvailable_throwError() throws BalanceConstraintViolationException, NegativeConstraintViolationException {
        Account accountBranStark = accountService.save(AccountBuilder.createGeneric("Bran Stark").balance("2").build());
        accountService.withdraw(accountBranStark, new BigDecimal(3));
    }


}