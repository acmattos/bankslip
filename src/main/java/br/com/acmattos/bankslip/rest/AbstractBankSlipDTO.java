package br.com.acmattos.bankslip.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Base class for BankSlip DTO.
 * @author acmattos
 */
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
abstract class AbstractBankSlipDTO implements Serializable {
   /** Due date of this bank slip. */
   @ApiModelProperty(example="Date (format: yyyy-MM-dd)", position = 2,
      required = true, readOnly = true)
   @NonNull
   @JsonFormat(pattern = "yyyy-MM-dd")
   private Date dueDate ;

   /** Total in cents of this bank slip. */
   @ApiModelProperty(example="100000", position = 3, required = true,
      readOnly = true)
   @NonNull
   private BigDecimal totalInCents;

   /** Customer's name of this bank slip. */
   @ApiModelProperty(example="ACME Company", position = 4, required = true,
      readOnly = true)
   @NonNull
   private String customer;
}
