package com.licursi.rest.transferservice.repository;

import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.AccountBuilder;
import com.licursi.rest.transferservice.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransferRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransferRepository transferRepository;

    private Account acc1;
    private Account acc2;
    private Account acc3;

    @Before
    public void setup() {

        acc1 = AccountBuilder.createGeneric("Gray Worm").noId().build();
        acc2 = AccountBuilder.createGeneric("Sor Jorah Mormont").noId().build();
        acc3 = AccountBuilder.createGeneric("Sor Barristan Selmy").noId().build();
        entityManager.persist(acc1);
        entityManager.persist(acc2);
        entityManager.flush();
    }

    @Test
    public void givenSave_whenTransfer_thenReturnTransfer() {


        final Transfer transfer = new Transfer();
        transfer.setSource(acc1);
        transfer.setTarget(acc2);
        transfer.setAmount(new BigDecimal("1000.00"));

        final Transfer savedTransfer = transferRepository.save(transfer);
        assertThat(savedTransfer.getId()).isNotNull();
        assertThat(savedTransfer.getSource().getId()).isEqualTo(acc1.getId());
        assertThat(savedTransfer.getTarget().getId()).isEqualTo(acc2.getId());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void givenSave_whenTransferWithoutTarget_throwException() {

        final Transfer transfer = new Transfer();
        transfer.setSource(acc1);
        transfer.setTarget(acc3);
        transfer.setAmount(new BigDecimal("1000.00"));

        final Transfer savedTransfer = transferRepository.save(transfer);
        Assert.assertTrue(false);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void givenSave_whenTransferWithoutSource_throwException() {

        final Transfer transfer = new Transfer();
        transfer.setSource(acc3);
        transfer.setTarget(acc2);
        transfer.setAmount(new BigDecimal("1000.00"));

        final Transfer savedTransfer = transferRepository.save(transfer);

        Assert.assertTrue(false);
    }

}