package com.challenge.aggregator.model;

import java.util.Locale;

public class CampaignStats {
    private final String campaignId;
    private long totalImpressions;
    private long totalClicks;
    private double totalSpend;
    private long totalConversions;

    public CampaignStats(String campaignId) {
        this.campaignId = campaignId;
    }

    public void accumulate(long impressions, long clicks, double spend, long conversions) {
        this.totalImpressions += impressions;
        this.totalClicks += clicks;
        this.totalSpend += spend;
        this.totalConversions += conversions;
    }

    public String getCampaignId() { return campaignId; }
    public long getTotalImpressions() { return totalImpressions; }
    public long getTotalClicks() { return totalClicks; }
    public double getTotalSpend() { return totalSpend; }
    public long getTotalConversions() { return totalConversions; }

    public double getCtr() {
        return totalImpressions == 0 ? 0.0 : (double) totalClicks / totalImpressions;
    }

    public Double getCpa() {
        return totalConversions == 0 ? null : totalSpend / totalConversions;
    }

    public String toCsvRow() {
        Double cpaVal = getCpa();
        String cpaStr = (cpaVal == null) ? "null" : String.format(Locale.US, "%.2f", cpaVal);
        return String.format(Locale.US, "%s,%d,%d,%.2f,%d,%.4f,%s",
                campaignId, totalImpressions, totalClicks, totalSpend, totalConversions, getCtr(), cpaStr);
    }
}