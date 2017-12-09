CREATE TRIGGER Accrue_Interest_Into_Market_Account
AFTER INSERT ON Accrue_Interest_Transactions
FOR EACH ROW
BEGIN
    CALL Add_Money_To_Market_Account(NEW.aid, NEW.amount);
END;