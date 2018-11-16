package com.licursi.rest.transferservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.licursi.rest.transferservice.TransferServiceApplication;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.AccountBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TransferServiceApplication.class})
@WebAppConfiguration
public class AccountControllerIntegrationTest {


    private final static String ACCOUNT_CONTROLLER_ROUTE = "/account";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;


    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Sql("/simultaneous-transfer-data.sql")
    public void givenTransfer_whenFundsIsEnough_thenReturnSourceAccount() throws Exception {

        BigDecimal amount = new BigDecimal("2000");

        final Long tyrionLanisterId = 30L;
        final Long littleFingerId = 31L;

        this.mockMvc.perform(
                post(ACCOUNT_CONTROLLER_ROUTE + "/{sourceId}/transfer/{target}",
                        tyrionLanisterId, littleFingerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("" + amount))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.balance").value(new BigDecimal("6000.00")))
                .andReturn();
    }

    @Test
    @Sql("/simultaneous-transfer-data.sql")
    public void givenTransfer_whenNotEnoughFunds_thenReturnError() throws Exception {

        BigDecimal amount = new BigDecimal("2000");

        final Long tyrionLanisterId = 30L;
        final Long littleFingerId = 31L;

        this.mockMvc.perform(
                post(ACCOUNT_CONTROLLER_ROUTE + "/{sourceId}/transfer/{target}",
                        littleFingerId, tyrionLanisterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("" + amount))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
    }

    @Test
    public void givenTransfer_whenInvalidMethod_thenReturnMethodNotAllowed() throws Exception {

        BigDecimal amount = new BigDecimal("2000");

        final Long tyrionLanisterId = 30L;
        final Long littleFingerId = 31L;

        this.mockMvc.perform(
                get(ACCOUNT_CONTROLLER_ROUTE + "/{sourceId}/transfer/{target}",
                        littleFingerId, tyrionLanisterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("" + amount))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
    }

    @Test
    public void givenTransfer_whenBadContent_thenBadRequest() throws Exception {

        BigDecimal amount = new BigDecimal("2000");

        final Long tyrionLanisterId = 30L;
        final Long littleFingerId = 31L;

        this.mockMvc.perform(
                post(ACCOUNT_CONTROLLER_ROUTE + "/{sourceId}/transfer/{target}",
                        littleFingerId, tyrionLanisterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"test\" :" + amount + "}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Sql("/simultaneous-transfer-data.sql")
    public void whenTransfer_givenParallelTransfer_synchronouslyProcess() throws Exception {
        List<Integer> ammountsTransfer = Arrays.asList(
                1000, 1000, 1000, 1000, 1000, 1000,
                1000, 1000, 1000, 1000, 1000, 1000); // 12000

        // Image from simultaneous-transfer-data.sql
        final Account tyrionLanister = AccountBuilder.createGeneric("Tyrion Lanister").id(30L).balance("8000.00").build();
        final Account littleFinger = AccountBuilder.createGeneric("LittleFinger").id(31L).balance("100.00").build();

        final Long tyrionLanisterId = tyrionLanister.getId(); // balance 8000
        final Long littleFingerId = littleFinger.getId();     // balance 100

        final List<MvcResult> results = ammountsTransfer.stream().parallel().map((amount) -> {
            try {
                return this.mockMvc.perform(
                        post(ACCOUNT_CONTROLLER_ROUTE + "/{sourceId}/transfer/{target}", tyrionLanisterId, littleFingerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("" + amount))
                        .andDo(print())
                        .andReturn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());

        int failed = 0;
        int badRequest = 0;
        int ok = 0;

        for (MvcResult result : results) {
            if (result == null) {
                failed++;
            } else if (result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value()) {
                badRequest++;
            } else if (result.getResponse().getStatus() == HttpStatus.OK.value()) {
                ok++;
            } else {
                failed++;
            }
        }
        System.err.println("OK..: " + ok);
        System.err.println("FAIL: " + failed);
        System.err.println("BAD.: " + badRequest);

        assertThat(failed).isZero();
        assertThat(ok).isEqualTo(8);
        assertThat(badRequest).isEqualTo(4);

        this.mockMvc.perform(get(ACCOUNT_CONTROLLER_ROUTE + ""))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].balance").value(tyrionLanister.getBalance().subtract(BigDecimal.valueOf(8000))))
                .andExpect(jsonPath("$[1].balance").value(littleFinger.getBalance().add(BigDecimal.valueOf(8000))))
                .andReturn();

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}