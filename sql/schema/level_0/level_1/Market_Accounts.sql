CREATE TABLE IF NOT EXISTS Market_Accounts (
	aid INT NOT NULL AUTO_INCREMENT,
    balance DECIMAL(18,3),
    initial_balance DECIMAL(18,3),
    taxid VARCHAR(255) NOT NULL,
    day_of_the_month INT DEFAULT 0,
    running_balance_sum DECIMAL(18,3) DEFAULT 0.000,
    tot_commission DECIMAL(18,3) DEFAULT 0.000,
    PRIMARY KEY (aid),
	FOREIGN KEY (taxid) REFERENCES Customers(taxid)
)
