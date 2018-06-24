package br.com.acmattos.bankslip.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

/**
 * Interface that exposes Bank Slip repository features.
 * @author acmattos
 */
public interface BankSlipRepository extends MongoRepository<BankSlip, UUID>{
}
