CREATE TRIGGER IF NOT EXISTS New_Market_Account_Balance_Under_1000
BEFORE INSERT ON Market_Accounts
FOR EACH ROW
BEGIN
    IF NEW.balance < 1000.00 THEN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Inserting new Market_Account with balance < 1000";
    END IF;
END;