DROP TABLE IF EXISTS Sell_Transactions;
DROP TABLE IF EXISTS Accrue_Interest_Transactions;
DROP TABLE IF EXISTS Buy_Transactions;
DROP TABLE IF EXISTS Deposit_Transactions;
DROP TABLE IF EXISTS In_Stock_Acc;
DROP TABLE IF EXISTS Withdraw_Transactions;
DROP TABLE IF EXISTS Contract_Signed;
DROP TABLE IF EXISTS Market_Accounts;
DROP TABLE IF EXISTS Stock_Accounts;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Stock_Profiles;
DROP TRIGGER IF EXISTS Accrue_Interest_Into_Market_Account;
DROP TRIGGER IF EXISTS After_Stock_Purcahse;
DROP TRIGGER IF EXISTS After_Stock_Sale;
DROP TRIGGER IF EXISTS Before_Accrue_Interest;
DROP TRIGGER IF EXISTS Not_Enough_Stock_To_Sell;
DROP TRIGGER IF EXISTS Deposit_Into_Market_Account;
DROP TRIGGER IF EXISTS New_Market_Account_Balance_Under_1000;
DROP TRIGGER IF EXISTS Not_Enough_Money_To_Buy;
DROP TRIGGER IF EXISTS Not_Enough_Money_To_Withdraw;
DROP TRIGGER IF EXISTS Withdraw_Money_From_Market_Account;
DROP PROCEDURE IF EXISTS Add_Money_To_Market_Account;
DROP PROCEDURE IF EXISTS Add_Stock_To_Stock_Account;
DROP PROCEDURE IF EXISTS Take_Money_From_Market_Account;
DROP PROCEDURE IF EXISTS Take_Stock_From_Stock_Account;