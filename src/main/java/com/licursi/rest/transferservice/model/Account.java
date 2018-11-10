package com.licursi.rest.transferservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Min(value = 0, message = "Negative balance not allowed")
    private BigDecimal balance = BigDecimal.ZERO;

}
