SELECT C.uname, TotSharesDeal.tot
FROM Customers C
INNER JOIN Stock_Accounts SA ON C.taxid = SA.taxid
INNER JOIN
(
    SELECT TotSharesBought.s_aid, (TotSharesBought.tot + TotSharesSold.tot) AS tot
    FROM
    (
        SELECT s_aid, SUM(amount) AS tot
        FROM Buy_Transactions
        WHERE MONTH(date) = MONTH("%s")
        GROUP BY s_aid
    ) TotSharesBought
    INNER JOIN
    (
        SELECT s_aid, SUM(amount) AS tot
        FROM Sell_Transactions
        WHERE MONTH(date) = MONTH("%s")
        GROUP BY s_aid
    ) TotSharesSold ON TotSharesBought.s_aid = TotSharesSold.s_aid
) TotSharesDeal ON SA.aid = TotSharesDeal.s_aid
WHERE TotSharesDeal.tot >= 1000;