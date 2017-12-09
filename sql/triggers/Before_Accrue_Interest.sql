CREATE TRIGGER IF NOT EXISTS Before_Accrue_Interest
BEFORE INSERT ON Accrue_Interest_Transactions
FOR EACH ROW
BEGIN
    DECLARE day_of_month_stored INT;
    SELECT day_of_the_month INTO day_of_month_stored FROM Market_Accounts WHERE aid = NEW.aid;
    IF day_of_month_stored > 0 THEN
        BEGIN
            DECLARE additional_money DECIMAL(18,3);
            SELECT (running_balance_sum / CAST(day_of_the_month AS DECIMAL(18,3))) * CAST(0.03 AS DECIMAL(18,3))
            INTO additional_money
            FROM Market_Accounts
            WHERE aid = NEW.aid;
            SET NEW.amount = additional_money;
        END;
    ELSE
        SET NEW.amount = 0.000;
    END IF;
END;