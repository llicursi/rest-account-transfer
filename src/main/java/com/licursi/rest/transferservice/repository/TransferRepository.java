package com.licursi.rest.transferservice.repository;

import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends CrudRepository<Transfer, Long> {
    Iterable<Transfer> findAllBySource(Account source);

    Iterable<Transfer> findAllByTarget(Account source);

}
