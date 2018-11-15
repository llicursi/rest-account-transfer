package com.licursi.rest.transferservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.service.AccountService;
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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}