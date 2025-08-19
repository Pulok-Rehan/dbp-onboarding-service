package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.BankEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.BankRepository;
import com.bracepl.dbp_onboarding_service.domain.interfaces.BankDomain;
import com.bracepl.dbp_onboarding_service.domain.models.BankDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BankAdapter implements BankDomain {
    private final BankRepository bankRepository;

    public BankAdapter(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public BankDetails findByBankRoutingNumber(String routingNumber) {
        Optional<BankEntity> optionalBankEntity = bankRepository.findByRoutingNumber(routingNumber);
        return optionalBankEntity.map(this::populateToBankDetails).orElse(null);
    }

    @Override
    public BankDetails findBankByBranchName(String branchName) {
        Optional<BankEntity> optionalBankEntity = bankRepository.findByBranchName(branchName);
        return optionalBankEntity.map(this::populateToBankDetails).orElse(null);
    }

    @Override
    public BankDetails save(BankDetails bankDetails) {
        BankEntity bankEntity = this.populateToBankEntity(bankDetails);
        BankEntity savedBankEntity = bankRepository.save(bankEntity);
        return this.populateToBankDetails(savedBankEntity);
    }

    private BankDetails populateToBankDetails(BankEntity bankEntity){
        return BankDetails.builder()
                .id(bankEntity.getId())
                .bankName(bankEntity.getBankName())
                .branchName(bankEntity.getBranchName())
                .routingNumber(bankEntity.getRoutingNumber())
                .build();
    }
    private BankEntity populateToBankEntity(BankDetails bankDetails){
        return BankEntity.builder()
                .bankName(bankDetails.getBankName())
                .branchName(bankDetails.getBranchName())
                .routingNumber(bankDetails.getRoutingNumber()).build();
    }
}
