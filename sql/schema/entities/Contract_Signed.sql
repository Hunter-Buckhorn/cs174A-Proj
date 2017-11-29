CREATE TABLE IF NOT EXISTS Contract_Signed (
	sym CHAR(3) NOT NULL,
    movie VARCHAR(255),
    year YEAR,
	value INT,
	role VARCHAR(255),
	PRIMARY KEY (sym, movie, year),
	FOREIGN KEY (sym) REFERENCES Stock_Profiles(sym)
	  ON DELETE CASCADE
);
