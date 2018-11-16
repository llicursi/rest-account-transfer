package com.licursi.rest.transferservice.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.licursi.rest.transferservice.model.serializer.MoneySerializer;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Digits(integer = 15, fraction = 2)
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal balance;


}
