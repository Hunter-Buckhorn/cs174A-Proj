SELECT C.uname, COALESCE(TotSharesBought.tot,0) + COALESCE(TotSharesSold.tot,0) AS totSharesDealt
FROM Customers C
INNER JOIN Stock_Accounts SA ON C.taxid = SA.taxid
LEFT JOIN
    (
    SELECT s_aid, SUM(amount) AS tot
        FROM Buy_Transactions
        GROUP BY s_aid
    ) TotSharesBought ON SA.aid = TotSharesBought.s_aid
LEFT JOIN
    (
        SELECT s_aid, SUM(amount) AS tot
        FROM Sell_Transactions
        GROUP BY s_aid
    ) TotSharesSold ON SA.aid = TotSharesSold.s_aid
WHERE COALESCE(TotSharesBought.tot,0) + COALESCE(TotSharesSold.tot,0) >= 1000;