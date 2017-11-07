package com.zopa.loan.utils;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.*;


public class LoanUtilsTest {

    private static final String MARKET_FILE_PATH = "src/main/resources/";
    private static final String MARKET_FILE = "market.csv";
    private Map<String, BigDecimal> marketMap;

    @Before
    public void setUp() {
        if (marketMap == null) {
            marketMap = LoanUtils.loadMarketFile(MARKET_FILE, MARKET_FILE_PATH);
        }
    }

    @Test
    public void getMinimumAverageInterest_ShouldReturn_0_072_When_2000() {
        BigDecimal expectedInterest = new BigDecimal("0.072");
        BigDecimal actualInterest = LoanUtils.getMinimumAverageInterest(marketMap, new BigDecimal("2000"));
        assertEquals(expectedInterest, actualInterest);
    }

    @Test
    public void calculateMonthlyRepayment_ShouldReturn_61_93_When_2000_And_0_072_Rate() {
        BigDecimal rate = new BigDecimal("0.072");
        BigDecimal expectedMonthlyRepayment = new BigDecimal("61.93");
        BigDecimal actualMonthlyRepayment = LoanUtils.calculateMonthlyRepayment(rate, new BigDecimal("2000"))
                .setScale(2, BigDecimal.ROUND_FLOOR);
        assertEquals(expectedMonthlyRepayment, actualMonthlyRepayment);
    }

    @Test
    public void hasMarketSufficientOffers_ShouldReturn_True() {
        assertTrue(LoanUtils.hasMarketSufficientOffers(marketMap, "2330"));
    }

    @Test
    public void hasMarketSufficientOffers_ShouldReturn_False() {
        assertFalse(LoanUtils.hasMarketSufficientOffers(marketMap, "2331"));
    }

    @Test
    public void isAValidAmount_ShouldReturn_True_WhenMinimumAmount() {
        assertTrue(LoanUtils.isAValidAmount("1000"));
    }

    @Test
    public void isAValidAmount_ShouldReturn_True_WhenMaximumAmount() {
        assertTrue(LoanUtils.isAValidAmount("15000"));
    }

    @Test
    public void isAValidAmount_ShouldReturn_True_When_1100() {
        assertTrue(LoanUtils.isAValidAmount("1100"));
    }

    @Test
    public void isAValidAmount_ShouldReturn_False_When_1115() {
        assertFalse(LoanUtils.isAValidAmount("1115"));
    }

    @Test
    public void isAValidAmount_ShouldReturn_False_WhenNotMinimumAmount() {
        assertFalse(LoanUtils.isAValidAmount("999"));
    }

    @Test
    public void isAValidAmount_ShouldReturn_False_WhenNotMaximumAmount() {
        assertFalse(LoanUtils.isAValidAmount("15001"));
    }
}
