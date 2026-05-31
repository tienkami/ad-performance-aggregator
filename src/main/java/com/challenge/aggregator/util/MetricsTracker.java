package com.challenge.aggregator.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

public class MetricsTracker {
    private final long startTime;
    private final double initialMemoryMb;

    public MetricsTracker() {
        this.startTime = System.currentTimeMillis();
        // Capture baseline memory after startup initialization hooks
        this.initialMemoryMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0);
    }

    public void printFinalDashboard(long processedRows, long skippedRows) {
        long totalTimeMs = System.currentTimeMillis() - startTime;
        double peakMemoryMb = 0;

        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP) {
                long peakUsage = pool.getPeakUsage().getUsed();
                double peakMb = peakUsage / (1024.0 * 1024.0);
                if (peakMb > peakMemoryMb) {
                    peakMemoryMb = peakMb;
                }
            }
        }

        System.out.println("\n================ PERFORMANCE EXECUTION DASHBOARD ================");
        System.out.printf("Total Valid Records Aggregated : %,d rows%n", processedRows - skippedRows);
        System.out.printf("Total Malformed Rows Dropped   : %,d rows%n", skippedRows);
        System.out.printf("Total Execution Elapsed Time   : %.2f seconds%n", totalTimeMs / 1000.0);
        System.out.printf("Baseline Memory Footprint      : %.2f MB%n", initialMemoryMb);
        System.out.printf("Peak Isolated Peak Heap usage  : %.2f MB%n", peakMemoryMb);
        System.out.println("=================================================================\n");
    }
}