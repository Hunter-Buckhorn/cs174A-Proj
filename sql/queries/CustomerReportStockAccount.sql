SELECT ISA.aid, ISA.sym, ISA.balance
FROM In_Stock_Acc ISA
INNER JOIN Stock_Accounts SA ON ISA.aid = SA.aid
INNER JOIN Customers C ON SA.taxid = C.taxid
WHERE C.uname = "%s";