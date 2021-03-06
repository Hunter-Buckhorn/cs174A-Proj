CREATE TABLE IF NOT EXISTS Deposit_Transactions (
	tid INT NOT NULL AUTO_INCREMENT,
	aid INT NOT NULL,
	date DATE,
	amount DECIMAL(18,3),
	PRIMARY KEY (tid),
	FOREIGN KEY (aid) REFERENCES Market_Accounts(aid)
);
