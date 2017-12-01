CREATE TRIGGER IF NOT EXISTS After_Stock_Sale
AFTER INSERT ON Sell_Transactions
FOR EACH ROW
BEGIN
    CALL Add_Money_To_Market_Account(NEW.m_aid, (NEW.amount * NEW.pps - 20) );
    CALL Take_Stock_From_Stock_Account(NEW.s_aid, NEW.sym, NEW.amount);
END;