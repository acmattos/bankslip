package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Bank Slip data view whenever a client requests for a specific entity.
 * @author acmattos
 */
@ApiModel(
   value="DetailedBankSlipDTO",
   description="Detailed bank slip's data")
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
class DetailedBankSlipDTO extends AbstractBankSlipDTO {
   /** Identifier of this bank slip. */
   @ApiModelProperty(example="84e8adbf-1a14-403b-ad73-d78ae19b59bf",
      position = 1, required = true, readOnly = true)
   @NonNull
   private final UUID id;
   /** Fine of this bank slip. */
   @ApiModelProperty(example="1000", position = 8, required = true,
      readOnly = true)
   @NonNull
   private final BigDecimal fine;
   /** Status of this bank slip. */
   @ApiModelProperty(example="PENDING, PAID or CANCELED", position = 9,
      required = true, readOnly = true)
   @NonNull
   private final BankSlipStatusEnum status;
   
   /**
    * Keep it private (Lombok Builder generation)!
    * @param id
    * @param dueDate
    * @param totalInCents
    * @param customer
    * @param fine
    * @param status
    */
   @Builder
   private DetailedBankSlipDTO(UUID id, Date dueDate, BigDecimal totalInCents,
                               String customer, BigDecimal fine,
                               BankSlipStatusEnum status){
      super(dueDate, totalInCents, customer);
      this.id = id;
      this.fine = fine;
      this.status = status;
   }

   /**
    * Converts an entity into a DTO, calculating fine.
    *
    * @param entity Bank slip.
    * @return A DTO equivalent to the given entity.
    */
   static DetailedBankSlipDTO toDTO(BankSlip entity){
      if(null == entity){
         return null;
      }

      return DetailedBankSlipDTO.builder()
         .id(entity.getId())
         .dueDate(entity.getDueDate())
         .totalInCents(entity.getTotalInCents())
         .customer(entity.getCustomer())
         .fine(entity.calculateFine())
         .status(entity.getStatus())
         .build();
   }
}
