package br.com.acmattos.bankslip.data;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * BeforeConvert's BankSlip entity listener.
 * @author acmattos
 */
@Component
class BankSlipUUIDGeneratorEventListener
   extends AbstractMongoEventListener<BankSlip> {
   
   /**
    * Captures {@link  }, and sets an UUID for new entites.
    * @param event BeforeConvertEvent that holds an entity instance.
    */
   @Override
   public void onBeforeConvert(BeforeConvertEvent<BankSlip> event) {
      BankSlip entity = event.getSource();
      if (entity.isNew()) {
         entity.setId(UUID.randomUUID());
      }
   }
}