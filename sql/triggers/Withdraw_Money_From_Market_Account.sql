CREATE TRIGGER Withdraw_Money_From_Market_Account
AFTER INSERT ON Withdraw_Transactions
FOR EACH ROW
    CALL Take_Money_From_Market_Account(NEW.aid, NEW.amount);