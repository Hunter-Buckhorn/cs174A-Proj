CREATE TABLE IF NOT EXISTS In_Stock_Acc (
	sym CHAR(3) NOT NULL,
	aid INT NOT NULL,
	balance DECIMAL(18,3),
	PRIMARY KEY (sym, aid),
	FOREIGN KEY (sym) REFERENCES Stock_Profiles(sym),
	FOREIGN KEY (aid) REFERENCES Stock_Accounts(aid)
)
