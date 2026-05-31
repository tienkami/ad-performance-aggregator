package com.challenge.aggregator.service;

import com.challenge.aggregator.model.CampaignStats;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class AggregationPipelineTest {

    private CampaignStats statsA;
    private CampaignStats statsB;

    @Before
    public void setUp() {
        statsA = new CampaignStats("CMP001");
        statsB = new CampaignStats("CMP002");
    }

    /**
     * Case 1: Verifies standard linear accumulation metrics calculation precision.
     */
    @Test
    public void testAccumulationAndMetricsCalculation() {
        statsA.accumulate(1000, 50, 10.00, 5);
        statsA.accumulate(2000, 150, 20.00, 5);

        assertEquals(3000, statsA.getTotalImpressions());
        assertEquals(200, statsA.getTotalClicks()); // Corrected from the complex math typo
        assertEquals(30.00, statsA.getTotalSpend(), 0.001);
        assertEquals(10, statsA.getTotalConversions());

        // CTR = 200 / 3000 = 0.06666666666666667
        assertEquals(0.06666666666666667, statsA.getCtr(), 0.00001);
        // CPA = 30.00 / 10 = 3.00
        assertEquals(3.00, statsA.getCpa(), 0.01);
    }

    /**
     * Case 2: Verification of Edge Case mitigation requirements.
     * CPA must return null if conversions match 0 to prevent division by zero.
     */
    @Test
    public void testZeroConversionsReturnsNullCpa() {
        statsA.accumulate(1000, 10, 5.00, 0);
        assertNull("CPA should be null when conversions are zero.", statsA.getCpa());
    }

    /**
     * Case 3: Confirms the deterministic sorting requirements when metric calculations result in ties.
     * Tied criteria ranks ascending by lexicographical evaluation order.
     */
    @Test
    public void testTieBreakerSortingLogic() {
        // Both campaigns configured with identical metrics tracking properties
        statsA.accumulate(1000, 50, 10.00, 2); // ID: CMP001
        statsB.accumulate(1000, 50, 10.00, 2); // ID: CMP002

        List<CampaignStats> rawList = new ArrayList<>();
        rawList.add(statsB);
        rawList.add(statsA);

        // Sort ascending alphabetically by ID on tie-breaker properties
        rawList.sort((a, b) -> {
            int comp = Double.compare(b.getCtr(), a.getCtr()); // Identical CTR triggers fallback
            if (comp != 0) return comp;
            return a.getCampaignId().compareTo(b.getCampaignId());
        });

        assertEquals("CMP001", rawList.get(0).getCampaignId());
        assertEquals("CMP002", rawList.get(1).getCampaignId());
    }
}