CREATE TABLE IF NOT EXISTS Accrue_Interest_Transactions (
	tid INT NOT NULL AUTO_INCREMENT,
	aid INT NOT NULL,
	date DATE,
	PRIMARY KEY (tid),
	FOREIGN KEY (aid) REFERENCES Market_Accounts(aid)
);
