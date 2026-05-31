# Ad Performance Aggregator

A command-line application written in Java 8 for processing large advertising datasets and generating campaign performance reports.

The application is designed to handle large CSV datasets efficiently while maintaining predictable memory usage and providing benchmark information required by the challenge.

## Performance

### Dataset

* File Size: ~1 GB
* Total Records Processed: 26,843,544

### Results

| Metric                    | Value        |
| ------------------------- | ------------ |
| Valid Records Aggregated  | 26,843,544   |
| Malformed Records Dropped | 0            |
| Total Execution Time      | 4.67 seconds |
| Baseline Heap Usage       | 1.48 MB      |
| Peak Heap Usage           | 217.00 MB    |

## Design Decisions

### Streaming File Processing

The application processes the CSV file sequentially using `BufferedReader`.

The entire dataset is never loaded into memory at once. Records are read, parsed, and aggregated as they are encountered.

This allows the application to process large datasets while keeping memory usage bounded.

### Aggregation Strategy

Campaign statistics are accumulated in memory using a `HashMap` keyed by `campaign_id`.

Memory consumption therefore depends primarily on the number of unique campaigns rather than the total number of records.

### CSV Parsing

Instead of relying on `String.split()`, parsing is performed using index-based delimiter detection.

This reduces temporary object creation and lowers garbage collection pressure during large-scale processing.

### Ranking

Top 10 CTR and CPA reports are generated from the aggregated campaign statistics after processing completes.

### Benchmark Tracking

Execution time is measured throughout the processing pipeline.

Heap usage statistics are collected using JVM memory management APIs to provide application-specific memory measurements.

### Error Handling

The application validates incoming records before aggregation.

Malformed records can be skipped and counted separately without interrupting the entire processing run.

## Project Structure

```text
ad-performance-aggregator/
├── src/
│   ├── main/java/com/challenge/aggregator/
│   │   ├── model/CampaignStats.java
│   │   ├── service/CsvReaderService.java
│   │   ├── service/ReportGeneratorService.java
│   │   ├── util/MetricsTracker.java
│   │   └── MainApplication.java
│   │
│   └── test/java/com/challenge/aggregator/
│       └── service/AggregationPipelineTest.java
│
├── Dockerfile
├── benchmark.log
├── PROMPTS.md
├── pom.xml
└── README.md
```

## Prerequisites

* Java 8 or higher
* Maven 3.6+

### Libraries

Runtime:

* No external runtime dependencies

Testing:

* JUnit 4.13.2

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/ad-performance-aggregator-1.0-SNAPSHOT.jar \
  --input path/to/ad_data.csv \
  --output results/
```

Example:

```bash
java -jar target/ad-performance-aggregator-1.0-SNAPSHOT.jar \
  --input D:\ad_data.csv \
  --output results/
```

## Output

The application generates:

```text
results/
├── top10_ctr.csv
└── top10_cpa.csv
```

## Running Tests

```bash
mvn test
```

## Running with Docker

### Clone Repository

```bash
git clone https://github.com/tienkami/ad-performance-aggregator.git
cd ad-performance-aggregator
```

### Build Docker Image

```bash
docker build -t ad-aggregator .
```

### Run Container

```bash
docker run --rm \
  -v /path/to/input:/data \
  -v /path/to/output:/results \
  ad-aggregator \
  --input /data/ad_data.csv \
  --output /results/
```

## Benchmark Artifacts

Additional benchmark details are available in:

```text
benchmark.log
```

## AI Usage Disclosure

This submission was developed with assistance from AI coding tools.

Design discussions, performance investigations, architecture decisions, and implementation prompts used during development are documented in:

```text
PROMPTS.md
```
