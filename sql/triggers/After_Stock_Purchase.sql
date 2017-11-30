CREATE TRIGGER IF NOT EXISTS After_Stock_Purcahse
AFTER INSERT ON Buy_Transactions
FOR EACH ROW
BEGIN
    CALL Take_Money_From_Market_Account(NEW.m_aid, (NEW.amount * NEW.pps + 20) );
    CALL Add_Stock_To_Stock_Account(NEW.s_aid, NEW.amount);
END;