SELECT MA.aid, MA.balance
FROM Market_Accounts MA
INNER JOIN Customers C ON MA.taxid = C.taxid
WHERE C.uname = "%s";