package com.scooter.app.modules.riders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OnlineToggleRequest {
    @NotNull
    private Boolean online;
}
