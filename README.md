# Zopa exercise
* After running "maven package" a jar file will be generated.
* Application will start by running the following command in the console: java -jar loan-1.0.jar market.csv 1000
* Market.csv file must be located in the same directoy than the actual jar file.
* We assume that available field on Market.csv file is the maximum available amount from each lender.
* Following formula has been used for monthly repayment calculations: 
     * Loan Payment = Amount / Discount Factor
     * Discount Factor (D) = {[(1 + i) ^n] - 1} / [i(1 + i)^n]
     * i = rate/12
     * n = 36 (loan period 36 months)
