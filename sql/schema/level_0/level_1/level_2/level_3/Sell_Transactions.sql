CREATE TABLE IF NOT EXISTS Sell_Transactions (
	tid INT NOT NULL AUTO_INCREMENT,
	m_aid INT NOT NULL,
	s_aid INT NOT NULL,
	sym CHAR(3) NOT NULL,
	b_pps DECIMAL (18,3) NOT NULL,
	amount DECIMAL(18,3) NOT NULL,
	s_pps DECIMAL(18,3) NOT NULL,
	earnings DECIMAL(18,3),
    date DATE,
	PRIMARY KEY (tid),
    FOREIGN KEY (m_aid) REFERENCES Market_Accounts(aid),
    FOREIGN KEY (s_aid) REFERENCES Stock_Accounts(aid),
    FOREIGN KEY (s_aid, sym, b_pps) REFERENCES In_Stock_Acc(aid, sym, pps),
    FOREIGN KEY (sym) REFERENCES Stock_Profiles(sym)
);