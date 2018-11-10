package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    // Tests for :
    // public Account save(Account account) {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenSave_thenReturnAutoGeneratedId() {
        final Account account = generateAccountANullId();
        assertThat(account.getId()).isNull();

        when(accountRepository.save(any(Account.class))).thenReturn(generateAccountAWithId());

        Account accountSaved = accountService.save(account);
        assertThat(accountSaved.getId()).isNotNull();

        verify(accountRepository, times(1)).save(account);
        verifyNoMoreInteractions(accountRepository);
    }


    @Test(expected = BalanceConstraintViolationException.class)
    public void whenSaveNegativeBalance_throwException() {
        Account account = generateAccountANegative();
        accountService.save(account);

        verifyZeroInteractions(accountRepository);
    }


    /*
     * Auxiliar account type A generated without ID
     */
    private static Account generateAccountANullId() {
        // TODO Convert into a factory
        Account account = new Account();
        account.setName("Test Name");
        account.setBalance(new BigDecimal("1000.00"));
        return account;
    }

    /*
     * Auxiliar account type A generated without ID
     */
    private static Account generateAccountAWithId() {
        // TODO Convert into a factory
        Account account = generateAccountANullId();
        account.setId(6);
        return account;
    }

    /*
     * Auxiliar account type A generated without ID
     */
    private static Account generateAccountANegative() {
        // TODO Convert into a factory
        Account account = generateAccountANullId();
        account.setBalance(new BigDecimal("-1000.00"));
        return account;
    }


}