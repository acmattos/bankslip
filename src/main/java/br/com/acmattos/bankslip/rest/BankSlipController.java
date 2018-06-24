package br.com.acmattos.bankslip.rest;

import br.com.acmattos.bankslip.data.BankSlip;
import br.com.acmattos.bankslip.data.BankSlipRepository;
import br.com.acmattos.bankslip.data.BankSlipStatusEnum;
import io.swagger.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller that exposes all endpoints available for this API.
 * @author acmattos
 */
@RestController
@RequestMapping("/bankslips")
public class BankSlipController {
   private static final Log LOGGER =
      LogFactory.getLog(BankSlipController.class);
   private static final String UUID_PATTERN =
      "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
   private static final String PAY_OR_CANCEL_ISERROR_MESSAGE =
      "Could not pay or cancel this particular bank slip: ";
   private static final String ISERROR = "iserror";
   private static final String BANKSLIP_CANCELED = "200 : Bankslip canceled";
   private static final String BANKSLIP_PAID = "200 : Bankslip paid";
   private static final String BANKSLIP_NOT_FOUND_WITH_THE_SPECIFIED_ID =
      "404 : Bankslip not found with the specified id";
   private static final String INVALID_BANKSLIP_STATUS_PROVIDED =
      "422 : Invalid bankslip status provided (PENDING)";
   private static final String INVALID_ID_PROVIDED_IT_MUST_BE_A_VALID_UUID =
      "400 : Invalid id provided - it must be a valid UUID";
   private static final String FIND_ONE_ISERROR_MESSAGE =
      "Could not find this particular bank slip: ";
   private static final String FIND_ALL_ISERROR_MESSAGE =
      "Could not find all bank slips: ";
   private static final String CREATE_ISERROR_MESSAGE =
      "Could not create new bank slip: ";
   private static final String TICKET_FORMAT = "[Ticket-%s] - ";
   private static final String
      INVALID_BANKSLIP_PROVIDED_CHECK_HEADERS_FOR_MORE_INFORMATION =
      "422 : Invalid bankslip provided. Check HEADERS for more information!";
   private static final String BANKSLIP_CREATED = "201 : Bankslip created";
   private static final String REST_BANKSLIPS_PATH = "/rest/bankslips/";
   private static final String LOCATION = "Location";
   
   @Autowired
   private BankSlipRepository repository;

   /**
    * Endpoint: POST http://address:port/rest/bankslips
    * Creates a valid bank slip.
    * Format:
    * {
    *    "due_date" : "2018-01-01" ,
    *    "total_in_cents" : "100000" ,
    *    "customer" : "ACME Company" ,
    *    "status" : "PENDING"
    * }
    * @param newBankSlipDTO New bank slip to be created.
    * @return See Swagger configuration bellow.
    */
   @ApiOperation(value = "Creates a valid bank slip.")
   @ApiResponses(value = {
      @ApiResponse(
         code = 201,
         message = BANKSLIP_CREATED,
         response = String.class,
         responseHeaders = @ResponseHeader(
            name = LOCATION,
            description = "/rest/bankslips/CREATED_BANK_SLIP_ID",
            response = String.class)
      ),
      @ApiResponse(
         code = 400,
         message = "400 : Bankslip not provided in the request body",
         response = String.class
      ),
      @ApiResponse(
         code = 422,
         message = INVALID_BANKSLIP_PROVIDED_CHECK_HEADERS_FOR_MORE_INFORMATION,
         response = String.class,
         responseHeaders = {
            @ResponseHeader(
               name = "dueDate",
               description = "Can't be null or empty!/Date format accepted: (yyyy-MM-dd)",
               response = String.class),
            @ResponseHeader(
               name = "totalInCents",
               description = "Can't be null or below zero!",
               response = String.class),
            @ResponseHeader(
               name = "customer",
               description = "Can't be null or empty!",
               response = String.class),
            @ResponseHeader(
               name = "status",
               description = "Can't be null!",
               response = String.class)
         }
      ),
      @ApiResponse(
         code = 500,
         message = "Internal Server Error",
         response = String.class,
         responseHeaders = {
            @ResponseHeader(
               name = ISERROR,
               description = "Could not create new bank slip: CAUSE_MESSAGE",
               response = String.class)
         }
      )
   })
   @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.TEXT_PLAIN_VALUE)
   @ResponseStatus(value = HttpStatus.CREATED)
   ResponseEntity create(
      @ApiParam(value = "Valid bank slip data.", required = true)
      @RequestBody
      NewBankSlipDTO newBankSlipDTO){
      LOGGER.info("New bank slip creation: " + newBankSlipDTO.toString());
      ResponseEntityBuilder<String> builder = ResponseEntityBuilder.builder();
      try {
         doCreate(newBankSlipDTO, builder);
      } catch (Exception e) {
         processErrorResponse(builder, e, CREATE_ISERROR_MESSAGE);
      }
      return builder.build();
   }
   
   /**
    * Creates a valid bank slip.
    * @param newBankSlipDTO New bank slip to be created.
    * @param builder Response entity builder.
    */
   private void doCreate(NewBankSlipDTO newBankSlipDTO,
                         ResponseEntityBuilder<String> builder) {
      Map<String, String> errorMap = newBankSlipDTO.validateMe();
      if(errorMap.isEmpty()){
         LOGGER.info("Response: CREATED");
         BankSlip entity = repository.save(newBankSlipDTO.toEntity());
         builder.key(LOCATION)
            .value(REST_BANKSLIPS_PATH + entity.getId())
            .body(BANKSLIP_CREATED)
            .CREATED();
      } else {
         LOGGER.info("Response: UNPROCESSABLE_ENTITY");
         for (String key : errorMap.keySet()) {
            builder.key(key).value(errorMap.get(key));
         }
         builder
            .body(INVALID_BANKSLIP_PROVIDED_CHECK_HEADERS_FOR_MORE_INFORMATION)
            .UNPROCESSABLE_ENTITY();
      }
   }
   
   /**
    * Endpoint: GET http://address:port/rest/bankslips
    * Lists all available bank slips created.
    *
    * @return See Swagger configuration bellow.
    */
   @ApiOperation(value = "Lists all available bank slips created.")
   @ApiResponses(value = {
      @ApiResponse(
         code = 200,
         message = "Ok",
         response = SavedBankSlipDTO.class,
         responseContainer = "List"
      ),
      @ApiResponse(
         code = 404,
         message = "No bank slip found",
         response = String.class
      ),
      @ApiResponse(
         code = 500,
         message = "Internal Server Error",
         response = String.class,
         responseHeaders = {
            @ResponseHeader(
               name = ISERROR,
               description = "Could not find all bank slips: CAUSE_MESSAGE",
               response = String.class)
         }
      )
   })
   @GetMapping(
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
   ResponseEntity findAll() {
      LOGGER.info("Find all bank slip requested" );
      ResponseEntityBuilder<List<SavedBankSlipDTO>> builder =
         ResponseEntityBuilder.builder();
      try{
         List<BankSlip> entities = repository.findAll();
         builder.body(SavedBankSlipDTO.toDTOs(entities));
      } catch (Exception e) {
         processErrorResponse(builder, e, FIND_ALL_ISERROR_MESSAGE);
      }
      return builder.build();
   }

   /**
    * Endpoint: GET http://address:port/rest/bankslips/{id}
    * Finds one bank slip defined by an ID and calculates fine in case of delays.
    *
    * @param id Bank slip identifier.
    * @return See Swagger configuration bellow.
    */
   @SuppressWarnings("unchecked")
   @ApiOperation(value = "Finds one bank slip defined by an ID and calculates fine in case of delays.")
   @ApiResponses(value = {
      @ApiResponse(
         code = 200,
         message = "Ok",
         response = DetailedBankSlipDTO.class
      ),
      @ApiResponse(
         code = 400,
         message = INVALID_ID_PROVIDED_IT_MUST_BE_A_VALID_UUID,
         response = String.class
      ),
      @ApiResponse(
         code = 404,
         message = BANKSLIP_NOT_FOUND_WITH_THE_SPECIFIED_ID,
         response = String.class
      ),
      @ApiResponse(
         code = 500,
         message = "Internal Server Error",
         response = String.class,
         responseHeaders = {
            @ResponseHeader(
               name = ISERROR,
               description = "Could not find this particular bank slip: CAUSE_MESSAGE",
               response = String.class)
         }
      )
   })
   @GetMapping(path = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
   ResponseEntity<DetailedBankSlipDTO> findById(
      @ApiParam(value = "Valid bank slip identifier.", required = true)
      @PathVariable UUID id) {
      LOGGER.info("Detailed bank slip requested: " + id);
      ResponseEntityBuilder builder = ResponseEntityBuilder.builder();
      try{
         doFindById(id, builder);
      } catch (Exception e) {
         processErrorResponse(builder, e, FIND_ONE_ISERROR_MESSAGE);
      }
      return builder.build();
   }
   
   /**
    * Finds one bank slip defined by an ID and calculates fine in case of delays.
    * @param id Bank slip identifier.
    * @param builder Response entity builder.
    */
   private void doFindById(UUID id, ResponseEntityBuilder builder) {
      if(!id.toString().matches(UUID_PATTERN)){
         LOGGER.info("Response: BAD_REQUEST");
         builder.body(INVALID_ID_PROVIDED_IT_MUST_BE_A_VALID_UUID)
            .BAD_REQUEST();
      } else {
         Optional<BankSlip> optional = repository.findById(id);
         if(optional.isPresent()){
            LOGGER.info("Response: OK");
            DetailedBankSlipDTO dto = DetailedBankSlipDTO.toDTO(optional.get());
            builder.body(dto);
         } else {
            LOGGER.info("Response: NOT_FOUND");
            builder.body(BANKSLIP_NOT_FOUND_WITH_THE_SPECIFIED_ID)
               .NOT_FOUND();
         }
      }
   }
   
   /**
    * Endpoint : PUT http://address:port/rest/bankslips/{id}
    * Pays or Cancels a bank slip defined by an ID.
    * Format:
    * {
    *    "status" : "PAID"
    * }
    * OR
    * {
    *    "status" : "CANCELED"
    * }
    *
    * @param id Bank slip identifier.
    * @param status Bank slip status.
    * @return See Swagger configuration bellow.
    */
   @ApiOperation(value = "Pays or Cancels a bank slip defined by an ID.")
   @ApiResponses(value = {
      @ApiResponse(
          code = 200,
          message = "200 : Bankslip paid | Bankslip canceled",
          response = String.class
      ),
      @ApiResponse(
         code = 404,
         message = BANKSLIP_NOT_FOUND_WITH_THE_SPECIFIED_ID,
         response = String.class,
         responseHeaders = {
            @ResponseHeader(
               name = "dueDate",
               description = "Can't be null or empty!",
               response = String.class),
         }
      ),
      @ApiResponse(
         code = 422,
         message = "422 : Invalid bankslip status provided (PENDING)",
         response = String.class
      ),
      @ApiResponse(
         code = 500,
         message = "Internal Server Error",
         response = String.class,
         responseHeaders = {
         @ResponseHeader(
            name = ISERROR,
            description = "Could not pay or cancel this particular bank slip: CAUSE_MESSAGE",
            response = String.class)
         }
      )
   })
   @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
   ResponseEntity payOrCancelOne(
      @ApiParam(value = "Valid bank slip identifier.", required = true)
      @PathVariable
      UUID id,
      @ApiParam(value = "Valid bank slip status type (PAID or CANCELED).",
         required = true)
      @RequestBody
      UpdatedBankSlipStatusDTO status) {
      LOGGER.info("Pay or cancel bank slip requested: " + id);
      ResponseEntityBuilder<String> builder = ResponseEntityBuilder.builder();
      try{
         doPayOrCancelOne(id, status, builder);
      } catch (Exception e) {
         processErrorResponse(builder, e, PAY_OR_CANCEL_ISERROR_MESSAGE);
      }
      return builder.build();
   }
   
   /**
    * Pays or Cancels a bank slip defined by an ID.
    * @param id Bank slip identifier.
    * @param status Bank slip status.
    * @param builder Response entity builder.
    */
   private void doPayOrCancelOne(UUID id,
                                 UpdatedBankSlipStatusDTO status,
                                 ResponseEntityBuilder<String> builder) {
      if(BankSlipStatusEnum.PENDING.equals(status.getStatus())){
         LOGGER.info("Response: UNPROCESSABLE_ENTITY");
         builder.body(INVALID_BANKSLIP_STATUS_PROVIDED)
            .UNPROCESSABLE_ENTITY();
      } else {
         Optional<BankSlip> optional = repository.findById(id);
         if (!optional.isPresent()) {
            LOGGER.info("Response: NOT_FOUND");
            builder.body(BANKSLIP_NOT_FOUND_WITH_THE_SPECIFIED_ID)
               .NOT_FOUND();
         } else {
            BankSlip entity = optional.get();
            entity.setStatus(status.getStatus());
            repository.save(entity);
            if(BankSlipStatusEnum.PAID.equals(status.getStatus())){
               LOGGER.info("Response: OK - PAID");
               builder.body(BANKSLIP_PAID);
            } else {
               LOGGER.info("Response: OK - CANCELED");
               builder.body(BANKSLIP_CANCELED);
            }
         }
      }
   }
   
   /**
    * Processes error responses.
    * @param builder Response entity builder.
    * @param e Exception occurred during execution.
    * @param errorMessage Error message to be sent to client.
    */
   private void processErrorResponse(ResponseEntityBuilder builder,
                                     Exception e,
                                     String errorMessage) {
      String ticket = generateErrorTicket();
      String message = errorMessage + ticket + e.getMessage();
      LOGGER.error(message, e);
      builder.key(ISERROR).value(message).INTERNAL_SERVER_ERROR();
   }
   
   /**
    * Generates error ticket.
    * @return Error ticket.
    */
   private String generateErrorTicket(){
      return String.format(TICKET_FORMAT, UUID.randomUUID().toString()) ;
   }
}
