# 📝 PROMPTS.md

This document tracks the iterative design, profiling, and modernization engineering prompts used in collaboration with the AI assistant to craft this high-performance pipeline module.

## 🔍 Session 1: Requirements Breakdown & Technical Analysis
- **Prompt:** I need you to review the challenge in the attached file as a software engineer. Let's identify all the requirements, check if there's any hidden assumptions, ambiguities, performance expectation, and evaluation criteria. Let's not jump into the code just yet, give me the technical analysis and suggest an implementation approach first. Also, rank the most suitable programming languages for this problem in terms of large dataset processing. I'm most familiar with Java (Java 8 to be specific), where does it stand on the list?

## 🛠️ Session 2: Cardinality Verification & Initial Architectural Choices
- **Prompt:** 
1. To answer the hidden nuances you brought up:
    + The number of distinct campaign_id is uncertain, to see if it's manageable enough to hold the aggregated results in an in-memory hash map, you can first provide me with a program or method to check for the cardinality in this file
    + If two campaigns have the exact same CTR or CPA, sort them ascendingly by campaign_id
    + As described in the expected output format, CTR can be rounded to 4 decimal points, and CPA can be rounded to 2 decimal points
2. My questions:
    + Besides the cases mentioned, what other edge cases are there and what is your intended approach?
    + If I were to use Java 8, should I use BufferedReader or the CSV library? Should parallel processing be implemented?
    + For this type of problem and workload, does the bottleneck usually lie at the CPU, RAM or disk I/O?
    + The challenge mentioned a benchmark to measure peak memory usage which you somehow magically skipped. Which approach do we have here? For an instance, if I have multiple Java processes running on my devices, how do I isolate and determine that the benchmark I'm seeing belongs to the application we're discussing? For this dataset, what is the acceptable processing time?

- **Prompt:** 
1. I ran the snippet you provided, we're dealing with 50 unique campaign_id here, so the HashMap approach should be fine
2. Regarding the method to isolate this app from other Java processes, let's go for the JVM Internal Way. I want the metrics and the benchmark method to be independent of the host OS
3. The nuances have been cleared for now. Now write me the code that consists of all the things we mentioned above, and is compatible with Java 8 and Maven

## 📐 Session 3: SOLID Architecture Refactoring & Telemetry Tracking
- **Prompt:** You even nested the components inside that pom.xml file incorrectly??
Anyway, I fixed it and changed the artifact-id to ad-performance-aggregator. The code ran smoothly, producing the output csv files as described in the challenge. However, there are some problems with this code:
1. Stuffing everything inside the Main class is unacceptable as it is violating every single one of the SOLID principles. As much as its straightforward to read and comprehend, and probably most suitable when it comes to solving these kinds of challenges, but I'm more familiar with enterprise/production systems, so  help me rewrite it according to the below checklist:
    + Must comply to the SOLID principles (separate the CSV reader and the aggregation logic from the main class)
    + Must be testable and implement unit testing. I'm using JUnit 4.13.2 (remember to comment clearly on the test case choices)
2. The console output is neat, but it lacks transparency.<br>
=========================================<br>
Processing completed in: 4.71 seconds<br>
Peak Isolated JVM Heap Allocated: 181.00 MB<br>
=========================================<br>
The processing time is within the ideal range, but for how many records processed? We have the peak heap allocation, but what about the minimum? And this can't be considered a log. A log should record the benchmark between intervals, for example, every second of the total process, every n rows out of the total number of records

## 📈 Session 4: Performance Variance Evaluation & Different Approaches Comparison
- **Prompt:** The refactored code produces the following metrics:<br>
================ PERFORMANCE EXECUTION DASHBOARD ================<br>
Total Valid Records Aggregated : 26,843,544 rows<br>
Total Malformed Rows Dropped   : 0 rows<br>
Total Execution Elapsed Time   : 4.67 seconds<br>
Baseline Memory Footprint      : 1.48 MB<br>
Peak Isolated Peak Heap usage  : 217.00 MB<br>
=================================================================<br>
The Peak Heap usage jumped from 181.00 MB to 217.00 MB, which is not very significant. But I want to know what caused this. 

- **Prompt:** I see. But will this be a problem when the system is scaled up, for instance, if the file size increased from 1GB to 50GB, will the current design work? If this module is integrated into an existing system, what do I need to notice here? 

- **Prompt:** Right, that's a lot to take in right now. I'm curious about the Rust solution. I want to see how better it is compared to our current Java solution. Provide me with the Rust solution and explain the choices made. I have never worked with Rust, so you need to provide me with an A-Z solution from installation to execution (Windows)

- **Prompt:** The peak memory from this solution is 3MB... This is far superior from the Java solution, then we just have no reason to use Java here. But again, when the system is scaled up, will this design work? Is this code easier to expand than Java (for example: changing the output to JSON format, adding new metrics or aggregation methods?), easier to test or integrate with a CI/CD pipeline, the structure isn't broken when multiple people work on it at the same time, or easier to fix the bugs? 

- **Prompt:** I will go for the Java solution for now as it is a familiar option. The Rust solution can be the plan B. Now help me cover the remaining categories:
    + README.md file (check the requirement again for the reference)
    + Dockerfile
    + Benchmark logs (mentioned in the original requirement, but we have included the log inside our application, so you can decide whether it's necessary to provide an extra log file)
    + PROMPTS.md (only the formatting)
Provide me with a file (or open a console), not the message as it usually encounters formatting error that does not produce the file as a whole copy pastable chunk 
After the above has been covered, push it to GitHub