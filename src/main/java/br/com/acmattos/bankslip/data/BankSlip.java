package br.com.acmattos.bankslip.data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * MongoDB Entity for Bank Slip Document manipulation.
 * @author acmattos
 */
@ToString
@EqualsAndHashCode(of = "id")
@Getter
@Builder
@Document(collection = "bankslip")
public class BankSlip implements Serializable {
   private static final int FIRST_DAY = 1;
   private static final int LAST_DAY = 10;
   private static final String FIRST_PERCENTAGE = "0.005";
   private static final String SECOND_PERCENTAGE = "0.01";
   
   /** Identifier of this bank slip. */
   @Id
   private UUID id;

   /** Due date of this bank slip. */
   @NonNull
   @Field("due_date")
   private Date dueDate ;

   /** Total in cents of this bank slip. */
   @NonNull
   @Field("total_in_cents")
   private BigDecimal totalInCents;

   /** Customer's name of this bank slip. */
   @NonNull
   private String customer;

   /** Status of this bank slip. */
   @NonNull
   private BankSlipStatusEnum status;
   
   /**
    * Changes status for this entity.
    * @param status Status of this bank slip.
    */
   public void setStatus(BankSlipStatusEnum status){
      Assert.notNull(status, "status can't be null!");
      this.status = status;
   }
   
   /**
    * Sets an UUID for this entity.
    * @param uuid UUID.
    */
   void setId(UUID uuid){
      this.id = uuid;
   }
   
   /**
    * Verifies if this entity is new (non-persisted).
    * @return true if has no id, or false otherwise.
    */
   boolean isNew() {
      return (null == this.id);
   }
   
   /**
    * Calculates a fine for this entity (if it applies).
    * Rule:
    * Up to 10 days: 0.5%
    * After 11th day: 1%
    *
    * @return The fine.
    */
   public BigDecimal calculateFine(){
      BigDecimal fine = BigDecimal.ZERO;
      if(BankSlipStatusEnum.PENDING.equals(this.getStatus())) {
         BigDecimal percentage = getFinePercentage();
         fine = this.getTotalInCents().multiply(percentage)
            .setScale(0, BigDecimal.ROUND_HALF_EVEN);
      }
      return fine;
   }
   
   /**
    * Gets the fine percentage.
    * @return Fine percentage.
    */
   private BigDecimal getFinePercentage() {
      BigDecimal percentage = BigDecimal.ZERO;
      long daysBetween = getDaysBetweenDueDateAndToday();
      if(daysBetween >= FIRST_DAY && daysBetween <= LAST_DAY){
         percentage = new BigDecimal(FIRST_PERCENTAGE);
      } else if(daysBetween > LAST_DAY){
         percentage = new BigDecimal(SECOND_PERCENTAGE);
      }
      return percentage;
   }
   
   /**
    * Get number of days between due date and current date (today).
    * @return Number of day between.
    */
   private long getDaysBetweenDueDateAndToday() {
      LocalDate now = LocalDate.now();
      return DAYS.between(
         this.dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
         now);
   }
}