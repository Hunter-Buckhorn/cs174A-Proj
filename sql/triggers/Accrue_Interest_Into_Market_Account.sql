CREATE TRIGGER IF NOT EXISTS Accrue_Interest_Into_Market_Account
AFTER INSERT ON Accrue_Interest_Transactions
FOR EACH ROW
BEGIN
    DECLARE additional_money DECIMAL;
    SELECT (running_balance_sum / day_of_the_month) * 0.03
    INTO additional_money
    FROM Market_Accounts
    WHERE aid = NEW.aid;
    CALL Add_Money_To_Market_Account(NEW.aid, additional_money);
END;