CREATE TABLE IF NOT EXISTS Market_Accounts (
	aid INT NOT NULL,
    balance DECIMAL,
    uname VARCHAR(255) NOT NULL,
    day_of_the_month INT,
    running_balance_sum DECIMAL,
    PRIMARY KEY (aid),
	FOREIGN KEY (aid) REFERENCES Accounts(aid)
	  ON DELETE CASCADE
	  ON UPDATE CASCADE,
	FOREIGN KEY (uname) REFERENCES Customers(uname)
)
