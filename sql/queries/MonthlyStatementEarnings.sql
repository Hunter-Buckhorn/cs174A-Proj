SELECT TotSellEarnings.tot + TotInterestEarnings.tot
FROM
 (
     SELECT SUM(earnings) AS tot
     FROM Sell_Transactions
     WHERE m_aid = %s
 ) TotSellEarnings,
 (
     SELECT SUM(amount) AS tot
     FROM Accrue_Interest_Transactions
     WHERE aid = %s
 ) TotInterestEarnings;