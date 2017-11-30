CREATE TABLE IF NOT EXISTS Sell_Transactions (
	tid INT NOT NULL,
	m_aid INT NOT NULL,
	s_aid INT NOT NULL,
	sym CHAR(3) NOT NULL,
	amount DECIMAL(18,3),
	pps DECIMAL,
    date DATE,
	PRIMARY KEY (tid),
	FOREIGN KEY (tid) REFERENCES Transactions(tid)
	    ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (m_aid) REFERENCES Market_Accounts(aid),
    FOREIGN KEY (s_aid) REFERENCES Stock_Accounts(aid),
    FOREIGN KEY (sym) REFERENCES Stock_Profiles(sym)
);