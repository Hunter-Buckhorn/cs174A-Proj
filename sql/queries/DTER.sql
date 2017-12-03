SELECT C.uname, C.state
FROM Customers C
INNER JOIN Market_Accounts MA ON C.taxid = MA.taxid
INNER JOIN
(
    SELECT m_aid, SUM(earnings) AS tot
    FROM Sell_Transactions
    GROUP BY m_aid
) TotSellEarnings ON TotSellEarnings.m_aid = MA.aid
INNER JOIN
(
    SELECT aid, SUM(amount) AS tot
    FROM Accrue_Interest_Transactions
    GROUP BY aid
) TotInterestEarnings ON TotInterestEarnings.aid = MA.aid
WHERE TotSellEarnings.tot + TotInterestEarnings.tot > 10000;