package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles all valid transfer operation
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private TransferRepository transferRepository;

    @Autowired
    public TransferService(final TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }


}
