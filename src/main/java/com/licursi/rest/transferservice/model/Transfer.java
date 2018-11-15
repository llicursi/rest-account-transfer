package com.licursi.rest.transferservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@Entity
public class Transfer {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnoreProperties({"balance"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    @NotNull
    private Account source;

    @JsonIgnoreProperties({"balance"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_id")
    @NotNull
    private Account target;


    @NotNull
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;
}
