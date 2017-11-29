CREATE TABLE IF NOT EXISTS Has_Stock_Acc (
	uname VARCHAR(255),
	aid INT,
	PRIMARY KEY (uname, aid),
	FOREIGN KEY (uname) REFERENCES Customers(uname),
	FOREIGN KEY (aid) REFERENCES Stock_Accounts(aid)
);
