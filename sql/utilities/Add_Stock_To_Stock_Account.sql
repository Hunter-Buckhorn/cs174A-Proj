CREATE PROCEDURE IF NOT EXISTS Add_Stock_To_Stock_Account (IN in_aid INT, IN in_sym CHAR(3), IN amt DECIMAL(18,3))
BEGIN
    DECLARE existed BOOL;
    SET existed = EXISTS(SELECT * FROM IN_STOCK_ACC WHERE aid = in_aid AND sym = in_sym);
    IF existed THEN
        UPDATE In_Stock_Acc
        SET balance = balance + amt
        WHERE aid = in_aid
        AND sym = in_sym;
    ELSE
        INSERT INTO In_Stock_Acc
        VALUES (in_sym, in_aid, amt);
    END IF;
END;