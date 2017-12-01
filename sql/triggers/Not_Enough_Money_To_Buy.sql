CREATE TRIGGER IF NOT EXISTS Not_Enough_Money_To_Buy
BEFORE INSERT ON Buy_Transactions
FOR EACH ROW
BEGIN
	DECLARE cur_acc_bal DECIMAL(18,3);
	SELECT balance INTO cur_acc_bal
    FROM Market_Accounts
    WHERE aid = NEW.m_aid;
    IF cur_acc_bal < (NEW.amount * NEW.pps + 20) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = "Not enough money to buy";
END IF;
END;
