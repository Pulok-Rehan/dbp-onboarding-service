package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.Data;

@Data
public class PoAccessDto {
    private boolean deposit;
    private boolean withdraw;
    private boolean trade;
    private boolean infoChange;
}
