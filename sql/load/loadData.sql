INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("admin","secret","(805)6374632","admin@stock.com","John Admin","1000","CA",TRUE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("alfred","hi","(805)2574499","alfred@hotmail.com","Alfred Hitchcock","1022","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("billy","cl","(805)5629999","billy@yahoo.com","Billy Clinton","3045","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("cindy","la","(805)6930011","cindy@hotmail.com","Cindy Laugher","2034","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("david","co","(805)8240011","david@yahoo.com","David Copperfill","4093","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("sailor","sa","(805)1234567","sailor@hotmail.com","Elizabeth Sailor","1234","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("brush","br","(805)1357999","george@hotmail.com","George Brush","8956","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("ivan","st","(805)3223243","ivan@yahoo.com","Ivan Stock","2341","NJ",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("joe","pe","(805)5668123","pepsi@pepsi.com","Joe Pepsi","0456","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("magic","jo","(805)4535539","jordon@jordon.org","Magic Jordon","3455","NJ",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("olive","st","(805)2574499","olive@yahoo.com","Olive Stoner","1123","CA",FALSE);
INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("frank","ol","(805)3456789","frank@gmail.com","Frank Olson","3306","CA",FALSE);
INSERT INTO Stock_Profiles (sym,dcp,price,DOB,name) VALUES ("SKB",40.00,40.00,"1958-12-8","Kim Basinger");
INSERT INTO Stock_Profiles (sym,dcp,price,DOB,name) VALUES ("SMD",71.00,71.00,"1944-09-25","Michael Douglas");
INSERT INTO Stock_Profiles (sym,dcp,price,DOB,name) VALUES ("STC",32.50,32.50,"1962-07-3","Tom Cruise");
INSERT INTO Contract_Signed (sym,movie,year,value,role) VALUES ("SKB","L.A. Confidential",1997,5000000,"Actor");
INSERT INTO Contract_Signed (sym,movie,year,value,role) VALUES ("SMD","A Perfect Murder",1998,10000000,"Actor");
INSERT INTO Contract_Signed (sym,movie,year,value,role) VALUES ("STC","Jerry Maguire",1996,5000000,"Actor");
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("001",10000,"1022",15, 150000.0, 10000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("002",100000,"3045",15, 1500000.0, 100000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("003",50000,"2034",15, 750000.0, 50000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("004",45000,"4093",15, 675000.0, 45000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("005",200000,"1234",15, 3000000.0, 200000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("006",5000,"8956",15, 75000.0, 5000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("007",2000,"2341",15, 30000.0, 2000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("008",10000,"0456",15, 150000.0, 10000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("009",130200,"3455",15, 1953000.0, 130200);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("010",35000,"1123",15, 525000.0, 35000);
INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum, initial_balance) VALUES ("011",30500,"3306",15, 457500.0, 30500);
INSERT INTO Stock_Accounts (taxid) VALUES("1022")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 1, 100, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("3045")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 2, 500, 71.00);
INSERT INTO Stock_Accounts (taxid) VALUES("3045")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("STC", 3, 100, 32.50);
INSERT INTO Stock_Accounts (taxid) VALUES("2034")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("STC", 4, 250, 32.50);
INSERT INTO Stock_Accounts (taxid) VALUES("4093")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 5, 100, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("4093")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 6, 500, 71.00);
INSERT INTO Stock_Accounts (taxid) VALUES("4093")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("STC", 7, 50, 32.50);
INSERT INTO Stock_Accounts (taxid) VALUES("1234")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 8, 1000, 71.00);
INSERT INTO Stock_Accounts (taxid) VALUES("8956")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 9, 100, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("2341")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 10, 300, 71.00);
INSERT INTO Stock_Accounts (taxid) VALUES("0456")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 11, 500, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("0456")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("STC", 12, 100, 32.50);
INSERT INTO Stock_Accounts (taxid) VALUES("0456")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 13, 200, 71.00);
INSERT INTO Stock_Accounts (taxid) VALUES("3455")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 14, 1000, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("1123")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 15, 100, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("1123")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 16, 100, 71.00);
INSERT INTO Stock_Accounts (taxid) VALUES("1123")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("STC", 17, 100, 32.50);
INSERT INTO Stock_Accounts (taxid) VALUES("3306")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SKB", 18, 100, 40.00);
INSERT INTO Stock_Accounts (taxid) VALUES("3306")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("STC", 19, 200, 32.50);
INSERT INTO Stock_Accounts (taxid) VALUES("3306")
INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("SMD", 20, 100, 71.00);