CREATE TRIGGER IF NOT EXISTS Not_Enough_Money_To_Withdraw
BEFORE INSERT ON Withdraw_Transactions
FOR EACH ROW
BEGIN
    DECLARE cur_balance DECIMAL(18,3);
    SELECT balance INTO cur_balance FROM Market_Accounts WHERE aid = NEW.aid;
    IF ( cur_balance < NEW.amount) THEN
	    SIGNAL SQLSTATE '45000'
		    SET MESSAGE_TEXT = "Not enough money to withdraw";
    END IF;
END;
