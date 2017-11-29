CREATE TABLE IF NOT EXISTS Accrue_Interest_Transactions (
	tid INT NOT NULL,
	aid INT NOT NULL,
	date DATE,
	PRIMARY KEY (tid),
	FOREIGN KEY (tid) REFERENCES Transactions(tid)
	    ON DELETE CASCADE
	    ON UPDATE CASCADE,
	FOREIGN KEY (aid) REFERENCES Market_Accounts(aid)
);
