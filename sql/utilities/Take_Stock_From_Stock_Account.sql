CREATE PROCEDURE IF NOT EXISTS Take_Stock_From_Stock_Account(IN in_aid INT, IN amt DECIMAL(18,3))
    UPDATE Stock_Accounts
    SET balance = balance - amt
    WHERE aid = in_aid;