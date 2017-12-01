CREATE TABLE IF NOT EXISTS Withdraw_Transactions (
	tid INT NOT NULL AUTO_INCREMENT,
	aid INT NOT NULL,
	date DATE,
	amount DECIMAL,
	PRIMARY KEY (tid),
	FOREIGN KEY (aid) REFERENCES Market_Accounts(aid)
);
