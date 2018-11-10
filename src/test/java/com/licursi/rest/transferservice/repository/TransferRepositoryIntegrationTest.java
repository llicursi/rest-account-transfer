package com.licursi.rest.transferservice.repository;

import com.licursi.rest.transferservice.model.Account;
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

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;

import static com.licursi.rest.transferservice.AccountUtils.generateAccountANullId;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransferRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransferRepository transferRepository;

    private final Account acc1 = generateAccountANullId("Account Person 1");
    private final Account acc2 = generateAccountANullId("Account Person 2");
    private final Account acc3 = generateAccountANullId("Account Person 3");

    @Before
    public void setup() {
        entityManager.persist(acc1);
        entityManager.persist(acc2);
        entityManager.flush();
    }

    @Test
    public void whenSaveTransfer_thenReturnTransfer() {


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
    public void whenSaveTransferWithoutTarget_throwException() {

        final Transfer transfer = new Transfer();
        transfer.setSource(acc1);
        transfer.setTarget(acc3);
        transfer.setAmount(new BigDecimal("1000.00"));

        final Transfer savedTransfer = transferRepository.save(transfer);
        Assert.assertTrue(false);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void whenSaveTransferWithoutSource_throwException() {

        final Transfer transfer = new Transfer();
        transfer.setSource(acc3);
        transfer.setTarget(acc2);
        transfer.setAmount(new BigDecimal("1000.00"));

        final Transfer savedTransfer = transferRepository.save(transfer);

        Assert.assertTrue(false);
    }

    @Test()
    public void whenSaveTransferNegativeAmmount_throwException() {

        final Transfer transfer = new Transfer();
        transfer.setSource(acc1);
        transfer.setTarget(acc2);
        transfer.setAmount(new BigDecimal("-1000.00"));

        try {
            final Transfer savedTransfer = transferRepository.save(transfer);
            Assert.assertTrue(false);
        } catch (ConstraintViolationException c) {
            assertThat(c.getConstraintViolations().size()).isGreaterThan(0);
            assertThat(c.getConstraintViolations().iterator().next().getPropertyPath().toString())
                    .isEqualTo("amount");
        }

    }
}