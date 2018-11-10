package com.licursi.rest.transferservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ToString
@NoArgsConstructor
@Entity
public class Transfer {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    @NotNull
    private Account source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    @NotNull
    private Account target;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Min(value = 0, message = "Transfer amount cannot be negative")
    private BigDecimal amount;
}
