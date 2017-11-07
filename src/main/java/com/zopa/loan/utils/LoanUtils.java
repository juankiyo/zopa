package com.zopa.loan.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LoanUtils {

    private static final String CSV_SEPARATOR = ",";
    private static final String HEADERS_CONTENT = "Lender";
    private static final int FIELD_RATE = 1;
    private static final int FIELD_AVAILABLE = 2;
    private static final int DECIMAL_PRECISION = 5;
    public static final BigDecimal MONTHLY_LOAN_PERIOD = new BigDecimal(36);
    private static final BigDecimal TWELVE_MONTHS = new BigDecimal(12);
    private static final int REQUESTED_AMOUNT_MINIMUM = 1000;
    private static final int REQUESTED_AMOUNT_MAXIMUM = 15000;


    public static Map<String, BigDecimal> loadMarketFile(String marketFile, String filePath) {
        Map<String, BigDecimal> rateAvailableMap = new HashMap<>();
        try {
            Files.lines(Paths.get(filePath.concat(marketFile))).filter(line -> !line.contains(HEADERS_CONTENT))
                    .forEach(line ->
                    {
                        String rate = line.split(CSV_SEPARATOR)[FIELD_RATE];
                        BigDecimal available = new BigDecimal(line.split(CSV_SEPARATOR)[FIELD_AVAILABLE]);
                        if (rateAvailableMap.containsKey(rate)) {
                            available = available.add(rateAvailableMap.get(rate));
                        }
                        rateAvailableMap.put(rate, available);
                    });
        } catch (IOException ioEx) {
            System.out.println("Error loading markets: " + ioEx.getMessage());
        }

        return rateAvailableMap;
    }

    /**
     * Loop through all lenders available until we get the requested loan amount, and returns the interest rate that
     * applies to that amount. Returned rate can be either one or the average interest between two rates.
     * Example: For a loan amount of 1000 we might get:
     * - 6.9% if available >= 1000
     * - 7.0% if available-6.8% = 480 and available-7.1% = 520 (total 1000 - average interest 7.0%)
     *
     * @param marketMap  Map containing rates and total amount available from lenders
     * @param loanAmount Requested loan amount
     * @return Returns the minimum average interest as BigDecimal
     */
    public static BigDecimal getMinimumAverageInterest(Map<String, BigDecimal> marketMap, BigDecimal loanAmount) {
        BigDecimal minimumAverageInterest = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_FLOOR);
        Map<String, BigDecimal> sortedInterestMap = getInterestMapSortedByRate(marketMap);

        BigDecimal remainingLoan = loanAmount;
        for (Map.Entry<String, BigDecimal> entry : sortedInterestMap.entrySet()) {
            BigDecimal interestRate = new BigDecimal(entry.getKey());
            BigDecimal maxLenderAvailable = entry.getValue();
            if (isAvailableEqualsOrGreaterThanRemainingLoan(maxLenderAvailable, remainingLoan)) {
                // Requested amount is covered
                minimumAverageInterest = minimumAverageInterest.add(interestRate.multiply(remainingLoan));
                break;
            } else {
                minimumAverageInterest = minimumAverageInterest.add(interestRate.multiply(maxLenderAvailable));
            }
            remainingLoan = remainingLoan.subtract(maxLenderAvailable);
        }
        return minimumAverageInterest.divide(loanAmount, BigDecimal.ROUND_FLOOR);


    }

    /**
     * Calculates monthly repayment based on the formula: Loan Payment = Amount / Discount Factor
     * Discount Factor (D) = {[(1 + i) ^n] - 1} / [i(1 + i)^n]
     * - i = rate/12
     * - n = 36 (loan period 36 months)
     *
     * @param rate       Interest rate
     * @param loanAmount Requested loan amount
     * @return Returns monthly repayment as BigDecimal
     */
    public static BigDecimal calculateMonthlyRepayment(BigDecimal rate, BigDecimal loanAmount) {
        BigDecimal interest = rate.divide(TWELVE_MONTHS, DECIMAL_PRECISION, BigDecimal.ROUND_CEILING);
        BigDecimal interestPowerOfLoanPeriod = interest.add(BigDecimal.ONE).pow(MONTHLY_LOAN_PERIOD.intValue());

        BigDecimal discountFactor = interestPowerOfLoanPeriod.subtract(BigDecimal.ONE)
                .divide(interest.multiply(interestPowerOfLoanPeriod), DECIMAL_PRECISION, BigDecimal.ROUND_CEILING);


        return loanAmount.divide(discountFactor, DECIMAL_PRECISION, BigDecimal.ROUND_CEILING);
    }

    private static boolean isAvailableEqualsOrGreaterThanRemainingLoan(BigDecimal available, BigDecimal remainingLoan) {
        return available.compareTo(remainingLoan) == 1 || available.compareTo(remainingLoan) == 0;
    }

    private static Map<String, BigDecimal> getInterestMapSortedByRate(Map<String, BigDecimal> rateAvailableMap) {
        return rateAvailableMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static boolean hasMarketSufficientOffers(Map<String, BigDecimal> marketMap, String requestedAmount) {
        BigDecimal totalAvailable = BigDecimal.ZERO;
        BigDecimal amount = new BigDecimal(requestedAmount);
        for (Map.Entry<String, BigDecimal> market : marketMap.entrySet()) {
            totalAvailable = totalAvailable.add(market.getValue());
        }

        // Is total available equals or greater than requested mount
        return totalAvailable.compareTo(amount) == 0 || totalAvailable.compareTo(amount) == 1;
    }

    public static boolean isAValidAmount(String requestedAmount) {
        int amount = Integer.valueOf(requestedAmount);
        // Is amount between 1000 and 15000 and increments of 100
        return amount % 100 == 0 && amount >= REQUESTED_AMOUNT_MINIMUM && amount <= REQUESTED_AMOUNT_MAXIMUM;
    }

    public static String getApplicationPath() {
        return new File("").getAbsolutePath() + "\\";
    }

    public static void printFormattedQuote(BigDecimal amount, BigDecimal rate, BigDecimal monthlyRepayment,
                                            BigDecimal totalRepayment) {
        StringBuilder formattedOutput = new StringBuilder();

        formattedOutput.append("Requested amount: £").append(amount);
        formattedOutput.append(System.lineSeparator());
        formattedOutput.append("Rate: ").append(rate.multiply(new BigDecimal(100))
                .setScale(1, BigDecimal.ROUND_FLOOR)).append("%");
        formattedOutput.append(System.lineSeparator());
        formattedOutput.append("Monthly repayment: £").append(monthlyRepayment.setScale(2, BigDecimal.ROUND_FLOOR));
        formattedOutput.append(System.lineSeparator());
        formattedOutput.append("Total repayment: £").append(totalRepayment.setScale(2, BigDecimal.ROUND_FLOOR));

        System.out.println(formattedOutput);
    }
}
