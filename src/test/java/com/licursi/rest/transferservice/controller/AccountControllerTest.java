package com.licursi.rest.transferservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.Transfer;
import com.licursi.rest.transferservice.service.AccountService;
import com.licursi.rest.transferservice.service.TransferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;
    @Mock
    private TransferService transferService;

    @InjectMocks
    private AccountController accountController;


    @Before
    public void setup() {
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
        // MockMvc standalone approach
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void whenGetAccount_thenStatusOkWithJsonResult() throws Exception {

        when(accountService.findAll()).thenReturn(getArrayOfAccounts()).getMock();
        this.mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is("Jon Snow")))
                .andExpect(jsonPath("$[0].balance", is(0)))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[1].balance", is(109999000.20)));

        verify(accountService, times(1)).findAll();
        verifyNoMoreInteractions(accountService);

    }


    @Test
    public void whenGetAccountNoData_thenStatusOkAndEmptyJsonResult() throws Exception {

        when(accountService.findAll()).thenReturn(new ArrayList<>()).getMock();
        this.mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(accountService, times(1)).findAll();
        verifyNoMoreInteractions(accountService);

    }

    @Test
    public void whenPostAccount_thenStatusCreatedWithJsonResult() throws Exception {
        Account account = getArrayOfAccounts().get(0);
        when(accountService.save(account)).thenReturn(account);
        this.mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(account)))
                .andExpect(status().isCreated());

        verify(accountService, times(1)).save(account);
        verifyNoMoreInteractions(accountService);

    }

    @Test
    public void whenGetOutgoingByExistentAccountId_thenStatusOkWithJsonResult() throws Exception {

        when(transferService.findAllOutgoing(1l)).thenReturn(getArrayOfTransfers()).getMock();
        this.mockMvc.perform(get("/account/1/outgoing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].source.name", is("Jon Snow")))
                .andExpect(jsonPath("$[0].target.name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[0].amount", is(500)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].source.name", is("Jon Snow")))
                .andExpect(jsonPath("$[1].target.name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[1].amount", is(750)))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].source.name", is("Jon Snow")))
                .andExpect(jsonPath("$[2].target.name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[2].amount", is(1400)));

        verify(transferService, times(1)).findAllOutgoing(1l);
        verifyNoMoreInteractions(transferService);

    }

    @Test
    public void whenGetOutgoingAccountWithNoData_thenStatusOkAndEmptyJsonResult() throws Exception {

        when(transferService.findAllOutgoing(2l)).thenReturn(new ArrayList<>()).getMock();
        this.mockMvc.perform(get("/account/2/outgoing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(transferService, times(1)).findAllOutgoing(2l);
        verifyNoMoreInteractions(transferService);

    }

    @Test
    public void whenGetOutgoingByNonExistentAccountId_thenThrowException() throws Exception {

        when(transferService.findAllOutgoing(40l)).thenThrow(AccountNotFoundException.class).getMock();
        this.mockMvc.perform(get("/account/40/outgoing"))
                .andExpect(status().isBadRequest());

        verify(transferService, times(1)).findAllOutgoing(40l);
        verifyNoMoreInteractions(transferService);
    }

    @Test
    public void whenGetIncomingByExistentAccountId_thenStatusOkWithJsonResult() throws Exception {

        when(transferService.findAllIncoming(2l)).thenReturn(getArrayOfTransfers()).getMock();
        this.mockMvc.perform(get("/account/2/incoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].source.name", is("Jon Snow")))
                .andExpect(jsonPath("$[0].target.name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[0].amount", is(500)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].source.name", is("Jon Snow")))
                .andExpect(jsonPath("$[1].target.name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[1].amount", is(750)))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].source.name", is("Jon Snow")))
                .andExpect(jsonPath("$[2].target.name", is("Cersei Lanister")))
                .andExpect(jsonPath("$[2].amount", is(1400)));

        verify(transferService, times(1)).findAllIncoming(2l);
        verifyNoMoreInteractions(transferService);

    }

    @Test
    public void whenGetIncomingAccountWithNoData_thenStatusOkAndEmptyJsonResult() throws Exception {

        when(transferService.findAllIncoming(1l)).thenReturn(new ArrayList<>()).getMock();
        this.mockMvc.perform(get("/account/1/incoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(transferService, times(1)).findAllIncoming(1l);
        verifyNoMoreInteractions(transferService);

    }

    @Test
    public void whenGetIncomingByNonExistentAccountId_thenThrowException() throws Exception {

        when(transferService.findAllIncoming(50l)).thenThrow(AccountNotFoundException.class).getMock();
        this.mockMvc.perform(get("/account/50/incoming"))
                .andExpect(status().isBadRequest());

        verify(transferService, times(1)).findAllIncoming(50l);
        verifyNoMoreInteractions(transferService);
    }

    /**
     * Generates a list of accounts for testing, using game of thrones characters
     * @return List of Accounts
     */
    private List<Account> getArrayOfAccounts() {

        Account account1 = new Account();
        account1.setId(0l);
        account1.setName("Jon Snow");
        account1.setBalance(new BigDecimal(0));


        Account account2  = new Account();
        account2.setId(1l);
        account2.setName("Cersei Lanister");
        account2.setBalance(new BigDecimal("109999000.20"));

        return Arrays.asList(account1, account2);
    }

    /**
     * Generates a list of transfer for testing
     * @return List of Transfer
     */
    private List<Transfer> getArrayOfTransfers() {

        List<Account> accountList = getArrayOfAccounts();

        Transfer transfer1 = new Transfer();
        transfer1.setId(1l);
        transfer1.setSource(accountList.get(0));
        transfer1.setTarget(accountList.get(1));
        transfer1.setAmount(new BigDecimal(500));

        Transfer transfer2 = new Transfer();
        transfer2.setId(2l);
        transfer2.setSource(accountList.get(0));
        transfer2.setTarget(accountList.get(1));
        transfer2.setAmount(new BigDecimal(750));

        Transfer transfer3 = new Transfer();
        transfer3.setId(3l);
        transfer3.setSource(accountList.get(0));
        transfer3.setTarget(accountList.get(1));
        transfer3.setAmount(new BigDecimal(1400));

        return Arrays.asList(transfer1, transfer2, transfer3);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}