CREATE PROCEDURE Take_Money_From_Market_Account(IN in_aid INT, IN amt DECIMAL(18,3))
    UPDATE Market_Accounts
    SET balance = balance - amt
    WHERE aid = in_aid;