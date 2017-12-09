SELECT TotSellEarnings.tot
FROM
 (
     SELECT m_aid, SUM(earnings) AS tot
     FROM Sell_Transactions
     GROUP BY m_aid
 ) TotSellEarnings
WHERE TotSellEarnings.m_aid = %s;