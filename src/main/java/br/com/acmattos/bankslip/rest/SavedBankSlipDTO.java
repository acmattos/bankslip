package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Bank Slip data view whenever a client requests for a list of entities.
 * @author acmattos
 */
@ApiModel(
   value="SavedBankSlipDTO",
      description="Saved bank slip's data")
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
class SavedBankSlipDTO extends AbstractBankSlipDTO {
   /** Identifier of this bank slip. */
   @ApiModelProperty(example="84e8adbf-1a14-403b-ad73-d78ae19b59bf",
      position = 1, required = true, readOnly = true)
   @NonNull
   private final UUID id;

   /**
    * Keep it private (Lombok Builder generation)!
    * @param id
    * @param dueDate
    * @param totalInCents
    * @param customer
    */
   @Builder
   private SavedBankSlipDTO(UUID id, Date dueDate, BigDecimal totalInCents,
                            String customer){
      super(dueDate, totalInCents, customer);
      this.id = id;
   }

   /**
    * Converts a list of entities into a list of DTOs.
    *
    * @param entities Bank slips.
    * @return A list of VOs equivalent to the given entities.
    */
   static List<SavedBankSlipDTO> toDTOs(List<BankSlip> entities){
      Assert.notNull(entities, "entities can't be null!");

      List<SavedBankSlipDTO> dtos = new ArrayList<>();
      entities.stream().forEach(entity -> dtos.add(toDTO(entity)));

      return dtos;
   }

   /**
    * Converts an entity into a DTO.
    *
    * @param entity Bank slip.
    * @return A DTO equivalent to the given entity.
    */
   private static SavedBankSlipDTO toDTO(BankSlip entity){
      Assert.notNull(entity, "entity can't be null!");

      return SavedBankSlipDTO.builder()
         .id(entity.getId())
         .dueDate(entity.getDueDate())
         .totalInCents(entity.getTotalInCents())
         .customer(entity.getCustomer())
         .build();
   }
}
