package com.licursi.rest.transferservice.service;

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

import static com.licursi.rest.transferservice.AccountUtils.generateAccountAWithId;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void whenSaveTransfer_thenReturnsSourceAccount() {
        int sourceId = 1;
        int targetId = 2;
        BigDecimal amount = new BigDecimal(10);

        final Account account1 = generateAccountAWithId(sourceId);
        final Account account2 = generateAccountAWithId(targetId);

        when(accountService.findById(sourceId)).thenReturn(Optional.of(account1));
        when(accountService.findById(targetId)).thenReturn(Optional.of(account2));

        final Transfer transfer = new Transfer();
        transfer.setSource(account1);
        transfer.setAmount(amount);
        transfer.setTarget(account2);

        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);

        final Account account = transferService.processTransfer(sourceId, targetId, amount);

        verify(accountService, times(2)).findById(any(Integer.class));
        verifyNoMoreInteractions(accountService);

        verify(transferRepository, times(1)).save(any(Transfer.class));
        verifyNoMoreInteractions(transferRepository);

    }


}