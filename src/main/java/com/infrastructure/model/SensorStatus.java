package com.infrastructure.model;

public enum SensorStatus {
    UNINITIALIZED,
    CALIBRATING,
    ONLINE_NOMINAL,
    WARNING_ELEVATED,
    CRITICAL_BREACH,
    FAULT_OFFLINE
}
