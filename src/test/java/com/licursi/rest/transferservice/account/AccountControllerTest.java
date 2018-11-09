package com.licursi.rest.transferservice.account;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        // We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
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
    public void whenPostAccount_thenStatusOkWithJsonResult() throws Exception {
        Account account = getArrayOfAccounts().get(0);
        when(accountService.save(account)).thenReturn(account);
        this.mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(account)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).save(account);
        verifyNoMoreInteractions(accountService);

    }

 /* Unnecessary stubbings detected in test class
    @Test
    public void whenPutAccount_thenStatusMethodNotAllowed() throws Exception {
        Account account = getArrayOfAccounts().get(0);
        when(accountService.save(account)).thenReturn(account);
        this.mockMvc.perform(put("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(account)))
                .andExpect(status().isMethodNotAllowed());
    }*/


    /**
     * Generates a list of accounts for testing, using game of thrones characters
     * @return List of Accounts
     */
    private List<Account> getArrayOfAccounts() {

        Account account1 = new Account();
        account1.setId(0);
        account1.setName("Jon Snow");
        account1.setBalance(new BigDecimal(0));


        Account account2  = new Account();
        account2.setId(1);
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