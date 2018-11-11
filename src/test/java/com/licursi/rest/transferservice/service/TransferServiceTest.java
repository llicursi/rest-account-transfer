package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.AccountBuilder;
import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.NegativeConstraintViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.Transfer;
import com.licursi.rest.transferservice.repository.TransferRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferService transferService;

    private Account account1;
    private Account account2;

    private static final int SOURCE_ID = 1;
    private static final int TARGET_ID = 2;
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(100);

    @Before
    public void setUp() throws Exception {

        account1 = AccountBuilder.createGeneric("Source").id(SOURCE_ID).build();
        account2 = AccountBuilder.createGeneric("Target").id(SOURCE_ID).build();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void whenSaveTransfer_thenReturnsSourceAccount() throws NegativeConstraintViolationException, BalanceConstraintViolationException, AccountNotFoundException {


        when(accountService.findById(SOURCE_ID)).thenReturn(Optional.of(account1));
        when(accountService.findById(TARGET_ID)).thenReturn(Optional.of(account2));

        doNothing().when(spy(accountService)).withdraw(any(Account.class), any(BigDecimal.class));
        doNothing().when(spy(accountService)).deposit(any(Account.class), any(BigDecimal.class));

        final Transfer transfer = new Transfer();
        transfer.setSource(account1);
        transfer.setAmount(DEFAULT_AMOUNT);
        transfer.setTarget(account2);

        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);

        final Account account = transferService.processTransfer(SOURCE_ID, TARGET_ID, DEFAULT_AMOUNT);

        verify(accountService, times(2)).findById(any(Integer.class));
        verifyNoMoreInteractions(accountService);

        verify(transferRepository, times(1)).save(any(Transfer.class));
        verifyNoMoreInteractions(transferRepository);

    }

    @Test(expected = AccountNotFoundException.class)
    public void whenSaveTransferWithInvalidSource_throwAccountNotFoundException() throws NegativeConstraintViolationException, BalanceConstraintViolationException, AccountNotFoundException {


        when(accountService.findById(SOURCE_ID)).thenReturn(Optional.empty());
        when(accountService.findById(TARGET_ID)).thenReturn(Optional.of(account2));

        transferService.processTransfer(SOURCE_ID, TARGET_ID, DEFAULT_AMOUNT);

    }


    @Test(expected = AccountNotFoundException.class)
    public void whenSaveTransferWithInvalidTarget_throwAccountNotFoundException() throws NegativeConstraintViolationException, BalanceConstraintViolationException, AccountNotFoundException {

        when(accountService.findById(SOURCE_ID)).thenReturn(Optional.of(account1));
        when(accountService.findById(TARGET_ID)).thenReturn(Optional.empty());

        transferService.processTransfer(TARGET_ID, TARGET_ID, DEFAULT_AMOUNT);

    }

}