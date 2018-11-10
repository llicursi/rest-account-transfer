package com.licursi.rest.transferservice.repository;

import com.licursi.rest.transferservice.model.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends CrudRepository<Transfer, Integer> {
}
