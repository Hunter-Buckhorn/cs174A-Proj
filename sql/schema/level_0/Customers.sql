CREATE TABLE IF NOT EXISTS Customers(
  uname VARCHAR(255) NOT NULL,
  pwd VARCHAR(255),
  phone CHAR(12),
  email VARCHAR(255),
  name VARCHAR(255),
  taxid VARCHAR(255) UNIQUE NOT NULL,
  state CHAR(2),
  is_admin BOOL NOT NULL,
  PRIMARY KEY (uname)
);