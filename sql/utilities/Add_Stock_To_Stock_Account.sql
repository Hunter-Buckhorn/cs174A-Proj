CREATE PROCEDURE Add_Stock_To_Stock_Account (IN in_aid INT, IN in_sym CHAR(3), IN amt DECIMAL(18,3), IN b_pps DECIMAL(18,3))
BEGIN
    DECLARE existed BOOL;
    SET existed = EXISTS(SELECT * FROM In_Stock_Acc WHERE aid = in_aid AND sym = in_sym AND pps = b_pps);
    IF existed THEN
        UPDATE In_Stock_Acc
        SET balance = balance + amt
        WHERE aid = in_aid
        AND sym = in_sym
        AND pps = b_pps;
    ELSE
        INSERT INTO In_Stock_Acc (aid, sym, balance, pps)
        VALUES (in_aid, in_sym, amt, b_pps);
    END IF;
END;