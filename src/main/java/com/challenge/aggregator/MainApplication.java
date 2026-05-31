package com.challenge.aggregator;

import com.challenge.aggregator.model.CampaignStats;
import com.challenge.aggregator.service.CsvReaderService;
import com.challenge.aggregator.service.ReportGeneratorService;
import com.challenge.aggregator.util.MetricsTracker;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class MainApplication {
    public static void main(String[] args) {
        MetricsTracker tracker = new MetricsTracker(); // Tracks baseline metrics immediately

        String inputPath = null;
        String outputDir = null;

        for (int i = 0; i < args.length; i++) {
            if ("--input".equals(args[i]) && i + 1 < args.length) inputPath = args[++i];
            else if ("--output".equals(args[i]) && i + 1 < args.length) outputDir = args[++i];
        }

        if (inputPath == null || outputDir == null) {
            System.err.println("Usage: java -jar app.jar --input <file.csv> --output <dir/>");
            System.exit(1);
        }

        try {
            Files.createDirectories(Paths.get(outputDir));

            CsvReaderService readerService = new CsvReaderService();
            ReportGeneratorService reportService = new ReportGeneratorService();

            // Execute pipeline operations sequentially
            Map<String, CampaignStats> aggregates = readerService.readAndAggregate(new File(inputPath));
            reportService.generateTopCtrReport(aggregates.values(), outputDir);
            reportService.generateLowestCpaReport(aggregates.values(), outputDir);

            // Final print out detailing exact total row computations
            tracker.printFinalDashboard(readerService.getTotalRecordsProcessed(), readerService.getMalformedRecordsCount());

        } catch (Exception e) {
            System.err.println("Execution pipeline terminated abnormally:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}