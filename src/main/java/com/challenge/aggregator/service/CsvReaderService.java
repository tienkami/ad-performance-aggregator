package com.challenge.aggregator.service;

import com.challenge.aggregator.model.CampaignStats;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CsvReaderService {
    private static final int LOG_INTERVAL_ROWS = 1_000_000; // Log every 1M rows
    private long totalRecordsProcessed = 0;
    private long malformedRecordsCount = 0;

    public Map<String, CampaignStats> readAndAggregate(File file) throws IOException {
        Map<String, CampaignStats> metricsMap = new HashMap<>(128);

        try (BufferedReader br = new BufferedReader(new FileReader(file), 64 * 1024)) {
            String header = br.readLine(); // Consume schema header

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                totalRecordsProcessed++;

                parseLine(line, metricsMap);

                // Interval-based logging trigger
                if (totalRecordsProcessed % LOG_INTERVAL_ROWS == 0) {
                    logProgress();
                }
            }
        }
        return metricsMap;
    }

    private void parseLine(String line, Map<String, CampaignStats> metricsMap) {
        try {
            int p1 = line.indexOf(',');
            int p2 = line.indexOf(',', p1 + 1);
            int p3 = line.indexOf(',', p2 + 1);
            int p4 = line.indexOf(',', p3 + 1);
            int p5 = line.indexOf(',', p4 + 1);

            if (p1 == -1 || p2 == -1 || p3 == -1 || p4 == -1 || p5 == -1) {
                malformedRecordsCount++;
                return;
            }

            String campaignId = line.substring(0, p1).trim();
            long impressions = Long.parseLong(line.substring(p2 + 1, p3).trim());
            long clicks = Long.parseLong(line.substring(p3 + 1, p4).trim());
            double spend = Double.parseDouble(line.substring(p4 + 1, p5).trim());
            long conversions = Long.parseLong(line.substring(p5 + 1).trim());

            if (impressions < 0 || clicks < 0 || spend < 0 || conversions < 0) {
                malformedRecordsCount++;
                return;
            }

            metricsMap.computeIfAbsent(campaignId, CampaignStats::new)
                    .accumulate(impressions, clicks, spend, conversions);

        } catch (NumberFormatException e) {
            malformedRecordsCount++;
        }
    }

    private void logProgress() {
        double currentHeap = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0);
        System.out.printf("[PROGRESS LOG] Processed %d records... Current Active Heap: %.2f MB%n",
                totalRecordsProcessed, currentHeap);
    }

    public long getTotalRecordsProcessed() { return totalRecordsProcessed; }
    public long getMalformedRecordsCount() { return malformedRecordsCount; }
}