package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Bank Slip Status view whenever a client requests for a pay or cancel
 * operation.
 * @author acmattos
 */
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UpdatedBankSlipStatusDTO implements Serializable{
    /** Status of this bank slip. */
   @ApiModelProperty(example="PAID or CANCELED", position = 1, required = true)
   @NonNull
   private BankSlipStatusEnum status;
}