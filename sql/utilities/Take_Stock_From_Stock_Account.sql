CREATE PROCEDURE IF NOT EXISTS Take_Stock_From_Stock_Account(IN in_aid INT, IN in_sym CHAR(3), IN amt DECIMAL(18,3), IN b_pps DECIMAL(18,3))
    UPDATE In_Stock_Acc
    SET balance = balance - amt
    WHERE aid = in_aid
    AND sym = in_sym
    AND pps = b_pps;