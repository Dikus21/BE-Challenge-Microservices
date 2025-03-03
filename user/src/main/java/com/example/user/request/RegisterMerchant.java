package com.example.user.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Data
public class RegisterMerchant {
    @NotEmpty(message = "userId required")
    private Long userId;
    @NotEmpty(message = "merchantId required")
    private UUID merchantId;
}
