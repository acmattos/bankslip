package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Bank Slip data request whenever a client wants to create a new entity.
 * @author acmattos
 */
@ApiModel(
   value="NewBankSlipDTO",
   description="New bank slip's data")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
class NewBankSlipDTO extends AbstractBankSlipDTO {
   /** Status of this bank slip. */
   @ApiModelProperty(example="PENDING, PAID or CANCELED", position = 9,
      required = true, readOnly = true)
   @NonNull
   private BankSlipStatusEnum status;

   /**
    * Keep it private (Lombok Builder generation)!
    * @param dueDate
    * @param totalInCents
    * @param customer
    * @param status
    */
   @Builder
   private NewBankSlipDTO(Date dueDate, BigDecimal totalInCents,
                          String customer, BankSlipStatusEnum status){
      super(dueDate, totalInCents, customer);
      this.status = status;
   }

   /**
    * Validate data received before performs save action, in order to report
    * errors accordingly.
    *
    * @return Error Map.
    */
   Map<String, String> validateMe(){
      Map<String, String> errorMap = new HashMap<>();
      if(null == super.getDueDate()){
         errorMap.put("dueDate", "Can't be null!");
      }
      if(null == super.getTotalInCents()
          || BigDecimal.ZERO.compareTo(super.getTotalInCents()) >= 0){
         errorMap.put("totalInCents", "Can't be null or below zero!");
      }
      if(null == super.getCustomer() || super.getCustomer().isEmpty()){
         errorMap.put("customer", "Can't be null or empty!");
      }
      if(null == status){
         errorMap.put("status", "Can't be null!");
      }
      return errorMap;
   }

   /**
    * Converts this DTO to a correspondent entity.
    * @return Bank slip entity.
    */
   BankSlip toEntity(){
      return BankSlip.builder()
         .dueDate(this.getDueDate())
         .totalInCents(this.getTotalInCents())
         .customer(this.getCustomer())
         .status(this.getStatus())
         .build();
   }
}
