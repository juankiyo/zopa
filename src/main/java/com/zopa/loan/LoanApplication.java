package com.zopa.loan;

import com.zopa.loan.utils.LoanUtils;

import java.math.BigDecimal;
import java.util.Map;

import static com.zopa.loan.utils.LoanUtils.MONTHLY_LOAN_PERIOD;

public class LoanApplication {

    private static final int PARAMETER_MARKET_FILE = 0;
    private static final int PARAMETER_REQUESTED_AMOUNT = 1;

    public static void main(String[] args) {
        checkArguments(args);
        String marketFileParameter = args[PARAMETER_MARKET_FILE];
        String loanAmountParameter = args[PARAMETER_REQUESTED_AMOUNT];

        // Load market file
        Map<String, BigDecimal> marketMap = LoanUtils.loadMarketFile(marketFileParameter,
                LoanUtils.getApplicationPath());

        if (LoanUtils.hasMarketSufficientOffers(marketMap, loanAmountParameter) &&
                LoanUtils.isAValidAmount(loanAmountParameter)) {
            // Loan rates calculations
            BigDecimal loanAmount = new BigDecimal(loanAmountParameter);
            BigDecimal minimumInterest = LoanUtils.getMinimumAverageInterest(marketMap, loanAmount);
            BigDecimal monthlyRepayment = LoanUtils.calculateMonthlyRepayment(minimumInterest, loanAmount);
            BigDecimal totalRepayment = monthlyRepayment.multiply(MONTHLY_LOAN_PERIOD)
                    .setScale(2, BigDecimal.ROUND_FLOOR);

            // Print quote
            LoanUtils.printFormattedQuote(loanAmount, minimumInterest, monthlyRepayment, totalRepayment);
        } else {
            System.out.println("Unfortunately we cannot provide a quote at this moment");
        }


    }

    private static void checkArguments(String args[]) {
        if (args.length < 2) {
            System.out.println("ERROR: Invalid arguments");
            System.exit(1);
        }
    }


}
