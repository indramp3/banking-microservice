package com.gateway.microservice.model.monitoring;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class MonitoringDTOs {

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountMetrics {
        private long totalAccounts;
        private BigDecimal totalBalance;
        private boolean error;
        private String message;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TransactionMetrics {
        private long totalSuccessfulTransactions;
        private BigDecimal totalTransactionVolume;
        private boolean error;
        private String message;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CombinedMetricsResponse {
        private AccountMetrics accountMetrics;
        private TransactionMetrics transactionMetrics;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HealthPing {
        private long timestampSent;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HealthResponse {
        private String serviceName;
        private String status;
        private long timestampSent;
        private long timestampReceived;
        private boolean error;
        private String message;
        private long latencyMs;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CombinedHealthResponse {
        private HealthResponse accountServiceHealth;
        private HealthResponse transactionServiceHealth;
    }
}
