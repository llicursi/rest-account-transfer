package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.repository.TransferRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class TransferServiceIntegrationTest {

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void whenSaveTransfer_thenReturnsTrue() {


    }

}