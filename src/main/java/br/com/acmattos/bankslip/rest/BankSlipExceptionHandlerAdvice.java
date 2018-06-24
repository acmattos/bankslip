package br.com.acmattos.bankslip.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Detects exceptional conditions and prepare responses according to the case.
 * @author acmattos
 */
@ControllerAdvice
class BankSlipExceptionHandlerAdvice {
   private static final Log LOGGER =
      LogFactory.getLog(BankSlipExceptionHandlerAdvice.class);
   private static final String INVALID_ID_PROVIDED_IT_MUST_BE_A_VALID_UUID =
      "400 : Invalid id provided - it must be a valid UUID";
   private static final String BANKSLIP_NOT_PROVIDED_IN_THE_REQUEST_BODY =
      "400 : Bankslip not provided in the request body";
   private static final String
      INVALID_BANKSLIP_PROVIDED_CHECK_HEADERS_FOR_MORE_INFORMATION =
      "422 : Invalid bankslip provided. Check HEADERS for more information!";
   private static final String DATE_FORMAT_ACCEPTED_YYYY_MM_DD =
      "Date format accepted: (yyyy-MM-dd)";
   private static final String DUE_DATE = "dueDate";
   private static final String NEW_BANK_SLIP_DTO_DUE_DATE =
      "NewBankSlipDTO[\"dueDate\"]";
   
   /**
    * Handle HttpMessageNotReadableException.
    * @param ex HttpMessageNotReadableException
    * @return UNPROCESSABLE_ENTITY or BAD_REQUEST.
    */
   @ExceptionHandler({HttpMessageNotReadableException.class})
   public ResponseEntity handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
      ResponseEntityBuilder<String> builder = ResponseEntityBuilder.builder();
      
      boolean handleUnprocessableEntity =
         ex.getMessage().contains(NEW_BANK_SLIP_DTO_DUE_DATE);
      if(handleUnprocessableEntity){
         LOGGER.info("Response: UNPROCESSABLE_ENTITY");
         builder.key(DUE_DATE).value(DATE_FORMAT_ACCEPTED_YYYY_MM_DD);
         builder
            .body(INVALID_BANKSLIP_PROVIDED_CHECK_HEADERS_FOR_MORE_INFORMATION)
            .UNPROCESSABLE_ENTITY();
      } else {
         // if(ex.getMessage().contains("Required request body is missing"))
         LOGGER.info("Response: BAD_REQUEST");
         builder
            .body(BANKSLIP_NOT_PROVIDED_IN_THE_REQUEST_BODY)
            .BAD_REQUEST();
      }
      return builder.build();
   }
   
   /**
    * Handle MethodArgumentTypeMismatchException.
    * @param ex MethodArgumentTypeMismatchException
    * @return BAD_REQUEST.
    */
   @ExceptionHandler({MethodArgumentTypeMismatchException.class})
   public ResponseEntity handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
      LOGGER.info("Response: BAD_REQUEST");
      ResponseEntityBuilder<String> builder = ResponseEntityBuilder.builder();
      builder.body(INVALID_ID_PROVIDED_IT_MUST_BE_A_VALID_UUID).BAD_REQUEST();
      return builder.build();
   }
}
