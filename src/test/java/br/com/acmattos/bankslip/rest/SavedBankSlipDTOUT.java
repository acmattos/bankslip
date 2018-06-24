package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import br.com.acmattos.bankslip.util.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * SavedBankSlipDTO Unit Tests.
 * @author acmattos
 */
public class SavedBankSlipDTOUT extends UnitTest {
   private UUID uuid;
   private Date date;
   private SavedBankSlipDTO dto;
   
   @Before
   public void setUp() throws Exception {
      this.uuid = UUID.randomUUID();
      this.date = new Date();
      this.dto = SavedBankSlipDTO.builder()
         .id(uuid)
         .dueDate(this.date)
         .totalInCents(BigDecimal.TEN)
         .customer("Customer")
         .build();
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void toDTOs_nullListOfEntity() {
      try {
         SavedBankSlipDTO.toDTOs(null);
         fail("IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("entities can't be null!", e.getMessage());
         throw e;
      }
   }
   
   @Test
   public void toDTOs_emptyListOfEntity() {
      try {
         List<SavedBankSlipDTO> dtos = SavedBankSlipDTO.toDTOs(new ArrayList<>());
         assertNotNull("Must not be null!", dtos);
         assertTrue("must be empty!", dtos.isEmpty());
      } catch (IllegalArgumentException e){
         fail("IllegalArgumentException must not be thrown!");
      }
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void toDTOs_listOfNullEntity() {
      try {
         List<BankSlip> entities = new ArrayList<BankSlip>(){{add(null);}};
         SavedBankSlipDTO.toDTOs(entities);
         fail("IllegalArgumentException must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("entity can't be null!", e.getMessage());
         throw e;
      }
   }
   
   @Test
   public void toDTOs_listOflEntity() {
      try {
         BankSlip entity = BankSlip.builder()
            .id(uuid)
            .dueDate(this.date)
            .totalInCents(BigDecimal.TEN)
            .customer("Customer")
            .status(BankSlipStatusEnum.PENDING)
            .build();
         List<BankSlip> entities = new ArrayList<BankSlip>(){{add(entity);}};
         List<SavedBankSlipDTO> dtos = SavedBankSlipDTO.toDTOs(entities);
         assertNotNull("Can't be null!", dtos);
         assertFalse("Can't be empty!", dtos.isEmpty());
      } catch (IllegalArgumentException e){
         fail("IllegalArgumentException must not be thrown!");
      }
   }
   
   @Test
   public void toStringTest() {
      assertEquals("SavedBankSlipDTO(super=AbstractBankSlipDTO(dueDate="
         + this.date
         + ", totalInCents=10, customer=Customer), id="
         + this.uuid
         + ")", this.dto.toString());
   }
}