CREATE TRIGGER IF NOT EXISTS Deposit_Into_Market_Account
AFTER INSERT ON Deposit_Transactions
FOR EACH ROW
    CALL Add_Money_To_Market_Account(NEW.aid, NEW.amount);