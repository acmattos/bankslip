package br.com.acmattos.bankslip.data;

import br.com.acmattos.bankslip.util.UnitTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * BankSlip entity Unit Tests
 *
 *******************************************************************************
 *      IMPORTANT: Instead of being named BankSlipUT, this class is called     *
 *           BankSlipUTest to help Maven find all UT/IT test classes.          *
 *******************************************************************************
 *
 * @author acmattos
 */
public class BankSlipUTest extends UnitTest {
   
   private UUID uuid;
   
   @Before
   public void setUp() {
      this.uuid = UUID.randomUUID();
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void setStatus_NullStatus() {
      try{
         LocalDate today = getLocalDateBeforeToday(0);
         BankSlip entity = getBankSlip(today, BankSlipStatusEnum.PENDING);
         entity.setStatus(null);
         fail("IllegalArgumentException  must be thrown!");
      } catch (IllegalArgumentException e){
         assertEquals("status can't be null!", e.getMessage());
         throw e;
      }
   }

   @Test
   public void setStatus_ValidStatus() {
      try{
         LocalDate today = getLocalDateBeforeToday(0);
         BankSlip entity = getBankSlip(today, BankSlipStatusEnum.PENDING);
         assertEquals(BankSlipStatusEnum.PENDING, entity.getStatus());

         entity.setStatus(BankSlipStatusEnum.CANCELED);
         assertEquals(BankSlipStatusEnum.CANCELED, entity.getStatus());
      } catch (IllegalArgumentException e){
         fail("IllegalArgumentException must not be thrown!");
         throw e;
      }
   }

   @Test
   public void constructor_NullIdIsAllowed() {
      try {
         LocalDate moreThanTenDays = getLocalDateBeforeToday(11);

         BankSlip entity = new BankSlip(null,
            Date.from(moreThanTenDays.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            new BigDecimal("100000"),
            "Customer",
            BankSlipStatusEnum.PENDING);

      } catch (NullPointerException e){
         fail("NullPointerException must not be thrown!");
         throw e;
      }
   }

   @Test(expected = NullPointerException.class)
   public void constructor_NullDueDateIsNotAllowed() {
      try {
         LocalDate moreThanTenDays = getLocalDateBeforeToday(11);

         BankSlip entity = new BankSlip(UUID.randomUUID(),
            null,
            new BigDecimal("100000"),
            "Customer",
            BankSlipStatusEnum.PENDING);
         fail("NullPointerException must be thrown!");
      } catch (NullPointerException e){
         assertEquals("dueDate is marked @NonNull but is null",
            e.getMessage());
         throw e;
      }
   }

   @Test(expected = NullPointerException.class)
   public void constructor_NullTotalInCentsIsNotAllowed() {
      try {
         LocalDate moreThanTenDays = getLocalDateBeforeToday(11);

         BankSlip entity = new BankSlip(UUID.randomUUID(),
            Date.from(moreThanTenDays.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            null,
            "Customer",
            BankSlipStatusEnum.PENDING);
         fail("NullPointerException must be thrown!");
      } catch (NullPointerException e){
         assertEquals("totalInCents is marked @NonNull but is null",
            e.getMessage());
         throw e;
      }
   }

   @Test(expected = NullPointerException.class)
   public void constructor_NullCustomerIsNotAllowed() {
      try {
         LocalDate moreThanTenDays = getLocalDateBeforeToday(11);

         BankSlip entity = new BankSlip(UUID.randomUUID(),
            Date.from(moreThanTenDays.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            new BigDecimal("100000"),
            null,
            BankSlipStatusEnum.PENDING);
         fail("NullPointerException must be thrown!");
      } catch (NullPointerException e){
         assertEquals("customer is marked @NonNull but is null",
            e.getMessage());
         throw e;
      }
   }

   @Test(expected = NullPointerException.class)
   public void constructor_NullStatusIsNotAllowed() {
      try {
         LocalDate moreThanTenDays = getLocalDateBeforeToday(11);

         BankSlip entity = new BankSlip(UUID.randomUUID(),
            Date.from(moreThanTenDays.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            new BigDecimal("100000"),
            "Customer",
            null);
         fail("NullPointerException must be thrown!");
      } catch (NullPointerException e){
         assertEquals("status is marked @NonNull but is null",
            e.getMessage());
         throw e;
      }
   }

   @Test
   public void calculateFine_NoFineExpectedForPendingStatus() {
      LocalDate today = getLocalDateBeforeToday(0);
      BankSlip entity = getBankSlip(today, BankSlipStatusEnum.PENDING);

      assertEquals("Must be ZERO: no fine is expected",
         BigDecimal.ZERO, entity.calculateFine());
   }

   @Test
   public void calculateFine_500OfFineExpectedOn1stDayForPendingStatus() {
      LocalDate oneDay = getLocalDateBeforeToday(1);
      BankSlip entity = getBankSlip(oneDay, BankSlipStatusEnum.PENDING);

      assertEquals("Must be 100000",
         new BigDecimal("100000"), entity.getTotalInCents());
      assertEquals("Must be 500: must charge 0.5% of fine (between 1 - 10)",
         new BigDecimal("500"), entity.calculateFine());
      assertEquals("Must be 100000",
         new BigDecimal("100000"), entity.getTotalInCents());
   }

   @Test
   public void calculateFine_500OfFineExpectedOn10thDayForPendingStatus() {
      LocalDate tenDays = getLocalDateBeforeToday(10);
      BankSlip entity = getBankSlip(tenDays, BankSlipStatusEnum.PENDING);

      assertEquals("Must be 500: must charge 0.5% of fine (between 1 - 10)",
         new BigDecimal("500"), entity.calculateFine());
   }

   @Test
   public void calculateFine_1000OfFineExpectedOn11thPlusDayForPendingStatus() {
      LocalDate moreThanTenDays = getLocalDateBeforeToday(11);
      BankSlip entity = getBankSlip(moreThanTenDays, BankSlipStatusEnum.PENDING);

      assertEquals("Must be 100000",
         new BigDecimal("100000"), entity.getTotalInCents());
      assertEquals("Must be 1000: must charge 1% of fine (11 - ...)",
         new BigDecimal("1000"), entity.calculateFine());
      assertEquals("Must be 100000",
         new BigDecimal("100000"), entity.getTotalInCents());
   }

   @Test
   public void calculateFine_NoFineExpectedForPaidStatus() {
      LocalDate today = getLocalDateBeforeToday(0);
      BankSlip entity = getBankSlip(today, BankSlipStatusEnum.PAID);

      assertEquals("Must be ZERO: no fine is expected",
         BigDecimal.ZERO, entity.calculateFine());
   }

   @Test
   public void calculateFine_noFineExpectedOn1stDayForPaidStatus() {
      LocalDate oneDay = getLocalDateBeforeToday(1);
      BankSlip entity = getBankSlip(oneDay, BankSlipStatusEnum.PAID);

      assertEquals("Must be ZERO: no fine is expected",
         BigDecimal.ZERO, entity.calculateFine());
   }

   @Test
   public void calculateFine_NoFineExpectedForCanceledStatus() {
      LocalDate today = getLocalDateBeforeToday(0);
      BankSlip entity = getBankSlip(today, BankSlipStatusEnum.CANCELED);

      assertEquals("Must be ZERO: no fine is expected",
         BigDecimal.ZERO, entity.calculateFine());
   }

   @Test
   public void calculateFine_NoFineExpectedOn11thPlusDayForCanceledStatus() {
      LocalDate moreThanTenDays = getLocalDateBeforeToday(11);
      BankSlip entity = getBankSlip(moreThanTenDays, BankSlipStatusEnum.CANCELED);

      assertEquals("Must be ZERO: no fine is expected",
         BigDecimal.ZERO, entity.calculateFine());
   }

   @Test
   public void toStringTest() {
      LocalDate today = getLocalDateBeforeToday(0);
      BankSlip entity = getBankSlip(today, BankSlipStatusEnum.PAID);

      assertEquals("BankSlip(id=" + this.uuid.toString() + ", dueDate="
            + Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()).toString()
            + ", totalInCents=100000, customer=Customer, status=PAID)",
         entity.toString());
   }

   @Test
   public void equals() {
      LocalDate today = getLocalDateBeforeToday(0);
      BankSlip entity1 = getBankSlip(today, BankSlipStatusEnum.PAID);
      BankSlip entity2 = getBankSlip(today, BankSlipStatusEnum.CANCELED);

      assertTrue(entity1.getId().equals(entity2.getId()));
      assertTrue(entity1.getDueDate().equals(entity2.getDueDate()));
      assertTrue(entity1.getCustomer().equals(entity2.getCustomer()));
      assertTrue(entity1.getTotalInCents().equals(entity2.getTotalInCents()));
      assertFalse("They must be different: to reinforce ID equality only",
         entity1.getStatus().equals(entity2.getStatus()));
      assertTrue("Must be equal: ID tested based only!",
         entity1.equals(entity2));
   }

   @Test
   public void hashCodeTest() {
      LocalDate today = getLocalDateBeforeToday(0);
      BankSlip entity = getBankSlip(today, BankSlipStatusEnum.PAID);

      assertNotNull("Can't be null!", entity.hashCode());
   }

   private LocalDate getLocalDateBeforeToday(int numberOfDaysBefore) {
      LocalDate today = LocalDate.now();
      return LocalDate.of(
         today.getYear(), today.getMonth(),
         today.getDayOfMonth() - numberOfDaysBefore);
   }

   private BankSlip getBankSlip(LocalDate dueDate, BankSlipStatusEnum status) {
      return BankSlip.builder()
         .id(this.uuid)
         .dueDate(Date.from(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
         .customer("Customer")
         .status(status)
         .totalInCents(new BigDecimal("100000"))
         .build();
   }
}
