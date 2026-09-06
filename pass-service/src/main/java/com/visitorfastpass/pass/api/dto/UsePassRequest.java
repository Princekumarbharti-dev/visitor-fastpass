package com.visitorfastpass.pass.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsePassRequest(@NotBlank @Size(max = 512) String token) {
}
