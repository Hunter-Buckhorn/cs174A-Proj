CREATE TABLE IF NOT EXISTS Has_Market_Acc (
    uname VARCHAR(255),
	aid INT NOT NULL,
	PRIMARY KEY (uname, aid),
	FOREIGN KEY (uname) REFERENCES Customers(uname),
	FOREIGN KEY (aid) REFERENCES Market_Accounts(aid)
);