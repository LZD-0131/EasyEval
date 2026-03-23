package com.dong.easyeval.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class SnowflakeIdGenerator {
    private final long datacenterId;
    private final long machineId;
    private final long epoch = 1609459200000L; // 2023-01-01

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final long MAX_MACHINE_ID = 31L;
    private static final long MAX_DATA_CENTER_ID = 31L;
    private static final long MAX_SEQUENCE = 4095L;

    public SnowflakeIdGenerator() {
        this.datacenterId = 1L;
        this.machineId = 1L;
    }

    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATA_CENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        }

        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than " + MAX_MACHINE_ID + " or less than 0");
        }

        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public  synchronized  long generateId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << 22) |
                (datacenterId << 17) |
                (machineId << 12) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
