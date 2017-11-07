package com.zopa.loan;


import com.zopa.loan.utils.LoanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoanApplication.class, LoanUtils.class})
public class LoanApplicationTest {

    private static final String MARKET_FILE_PATH = "src/main/resources/";

    @Before
    public void setUp() {
        PowerMockito.mockStatic(LoanApplication.class);
        PowerMockito.spy(LoanUtils.class);
        PowerMockito.spy(LoanApplication.class);
    }

    @Test
    public void loanApplication_Should_PrintFormattedQuote() {
        PowerMockito.doReturn(MARKET_FILE_PATH).when(LoanUtils.class);
        LoanUtils.getApplicationPath();

        String[] args = {"market.csv", "1000"};
        LoanApplication.main(args);

        verifyStatic(Mockito.atLeastOnce());
        LoanUtils.hasMarketSufficientOffers(anyMap(), anyString());
        verifyStatic(Mockito.atLeastOnce());
        LoanUtils.isAValidAmount(anyString());
        verifyStatic(Mockito.atLeastOnce());
        LoanUtils.getMinimumAverageInterest(anyMap(), any(BigDecimal.class));
        verifyStatic(Mockito.atLeastOnce());
        LoanUtils.calculateMonthlyRepayment(any(BigDecimal.class), any(BigDecimal.class));
        verifyStatic(Mockito.atLeastOnce());
        LoanUtils.printFormattedQuote(any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class),
                any(BigDecimal.class));
    }
}
