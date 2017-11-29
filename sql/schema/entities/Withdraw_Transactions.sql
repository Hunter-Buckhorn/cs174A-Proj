CREATE TABLE IF NOT EXISTS Withdraw_Transactions (
	tid INT NOT NULL,
	aid INT,
	date DATE,
	amount DECIMAL,
	PRIMARY KEY (tid),
	FOREIGN KEY (tid) REFERENCES Transactions(tid)
	  ON DELETE CASCADE
      ON UPDATE CASCADE,
	FOREIGN KEY (aid) REFERENCES Market_Accounts(aid)
);
