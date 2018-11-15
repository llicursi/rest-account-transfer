package com.licursi.rest.transferservice.repository;

import com.licursi.rest.transferservice.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
}
