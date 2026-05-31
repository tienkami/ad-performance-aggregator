package com.challenge.aggregator.service;

import com.challenge.aggregator.model.CampaignStats;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ReportGeneratorService {

    public void generateTopCtrReport(Collection<CampaignStats> stats, String outputDir) throws IOException {
        PriorityQueue<CampaignStats> ctrHeap = new PriorityQueue<>(11, (a, b) -> {
            int comp = Double.compare(a.getCtr(), b.getCtr());
            if (comp != 0) return comp;
            return b.getCampaignId().compareTo(a.getCampaignId()); // Alpha tie-breaker fallback
        });

        for (CampaignStats stat : stats) {
            ctrHeap.offer(stat);
            if (ctrHeap.size() > 10) ctrHeap.poll();
        }

        List<CampaignStats> sortedList = new ArrayList<>(ctrHeap);
        sortedList.sort((a, b) -> {
            int comp = Double.compare(b.getCtr(), a.getCtr());
            if (comp != 0) return comp;
            return a.getCampaignId().compareTo(b.getCampaignId());
        });

        writeCsv(sortedList, outputDir, "top10_ctr.csv");
    }

    public void generateLowestCpaReport(Collection<CampaignStats> stats, String outputDir) throws IOException {
        PriorityQueue<CampaignStats> cpaHeap = new PriorityQueue<>(11, (a, b) -> {
            int comp = Double.compare(b.getCpa(), a.getCpa());
            if (comp != 0) return comp;
            return b.getCampaignId().compareTo(a.getCampaignId());
        });

        for (CampaignStats stat : stats) {
            if (stat.getTotalConversions() == 0) continue;
            cpaHeap.offer(stat);
            if (cpaHeap.size() > 10) cpaHeap.poll();
        }

        List<CampaignStats> sortedList = new ArrayList<>(cpaHeap);
        sortedList.sort((a, b) -> {
            int comp = Double.compare(a.getCpa(), b.getCpa());
            if (comp != 0) return comp;
            return a.getCampaignId().compareTo(b.getCampaignId());
        });

        writeCsv(sortedList, outputDir, "top10_cpa.csv");
    }

    private void writeCsv(List<CampaignStats> data, String outputDir, String fileName) throws IOException {
        File file = new File(outputDir, fileName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA");
            bw.newLine();
            for (CampaignStats stat : data) {
                bw.write(stat.toCsvRow());
                bw.newLine();
            }
        }
    }
}