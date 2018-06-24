package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import br.com.acmattos.bankslip.util.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * DetailedBankSlipDTO Unit Tests.
 * @author acmattos
 */
public class DetailedBankSlipDTOUT extends UnitTest {
   private UUID uuid;
   private Date date;
   private DetailedBankSlipDTO dto;
   
   @Before
   public void setUp() throws Exception {
      this.uuid = UUID.randomUUID();
      this.date = new Date();
      this.dto = DetailedBankSlipDTO.builder()
         .id(uuid)
         .dueDate(this.date)
         .totalInCents(BigDecimal.TEN)
         .customer("Customer")
         .fine(BigDecimal.ONE)
         .status(BankSlipStatusEnum.PENDING)
         .build();
   }
   
   @Test
   public void toDTO_nullEntity() {
      assertNull("Must be null!", DetailedBankSlipDTO.toDTO(null));
   }
   
   @Test
   public void toDTO_validEntity() {
      BankSlip entity = BankSlip.builder()
         .id(uuid)
         .dueDate(this.date)
         .totalInCents(BigDecimal.TEN)
         .customer("Customer")
         .status(BankSlipStatusEnum.PENDING)
         .build();
      DetailedBankSlipDTO dto = DetailedBankSlipDTO.toDTO(entity);
      
      assertNotNull("Can't be null!", dto);
      assertEquals(this.uuid, entity.getId());
      assertEquals(dto.getDueDate(), entity.getDueDate());
      assertEquals(dto.getTotalInCents(), entity.getTotalInCents());
      assertEquals(dto.getFine(), BigDecimal.ZERO);
      assertEquals(dto.getCustomer(), entity.getCustomer());
      assertEquals(dto.getStatus(), entity.getStatus());
   }
   
   @Test
   public void toStringTest() {
      assertEquals("DetailedBankSlipDTO(super=AbstractBankSlipDTO(dueDate="
         + this.date
         + ", totalInCents=10, customer=Customer), id="
         + this.uuid
         + ", fine=1, status=PENDING)", this.dto.toString());
   }
}