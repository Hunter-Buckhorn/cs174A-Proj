SELECT TotInterestEarnings.tot
FROM
 (
     SELECT aid, SUM(amount) AS tot
     FROM Accrue_Interest_Transactions
     GROUP BY aid
 ) TotInterestEarnings
WHERE TotInterestEarnings.aid = %s;