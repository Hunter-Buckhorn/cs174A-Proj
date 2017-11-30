CREATE PROCEDURE IF NOT EXISTS Take_Money_From_Market_Account(IN in_aid INT, IN amt DECIMAL)
    UPDATE Market_Accounts
    SET balance = balance - amt
    WHERE aid = in_aid;