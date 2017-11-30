CREATE TABLE IF NOT EXISTS Customers(
  uname VARCHAR(255) NOT NULL,
  pwd VARCHAR(255),
  phone CHAR(10),
  email VARCHAR(255),
  name VARCHAR(255),
  taxid VARCHAR(255),
  state CHAR(2),
  PRIMARY KEY (uname)
);