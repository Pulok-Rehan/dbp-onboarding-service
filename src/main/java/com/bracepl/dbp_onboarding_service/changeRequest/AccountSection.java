package com.bracepl.dbp_onboarding_service.changeRequest;

public enum AccountSection {
    EKYC("E-KYC", 1),
    PERSONAL_DETAILS("Personal Details", 2),
    ADDRESS("Address", 3),
    BANK_DETAILS("Bank Details", 4),
    CLIENT_TYPE("Client Type", 5),
    DOCUMENTS("Documents", 6);

    private final String displayName;
    private final int stepNumber;

    AccountSection(String displayName, int stepNumber) {
        this.displayName = displayName;
        this.stepNumber = stepNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStepNumber() {
        return stepNumber;
    }
}