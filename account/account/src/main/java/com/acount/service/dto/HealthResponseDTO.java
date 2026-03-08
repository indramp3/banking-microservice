package com.acount.service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponseDTO {

    private String serviceName;
    private String status;
    private long timestampSent;
    private long timestampReceived;
    private boolean error;
    private String message;
}
