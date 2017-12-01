CREATE TRIGGER IF NOT EXISTS Not_Enough_Stock_To_Sell
BEFORE INSERT ON Sell_Transactions
FOR EACH ROW
BEGIN
	DECLARE current_stock_amount DECIMAL(18,3);
	SET current_stock_amount = -1.000;
	SELECT balance INTO current_stock_amount
    FROM In_Stock_Acc
    WHERE sym = NEW.sym
    AND aid = NEW.s_aid;
    IF current_stock_amount < NEW.amount THEN
        SIGNAL SQLSTATE '45000'
		    SET MESSAGE_TEXT = "Not enough stocks to sell";
END IF;
END;
