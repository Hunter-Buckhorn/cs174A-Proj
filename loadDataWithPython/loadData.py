#Customer (Admin)
print('INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("admin","secret","(805)6374632","admin@stock.com","John Admin","1000","CA",TRUE);')

currDate = "2013-3-16"
symToPrice = {}

SQLCustomers = 'INSERT INTO Customers (uname,pwd,phone,email,name,taxid,state,is_admin) VALUES ("{}","{}","{}","{}","{}","{}","{}",{});'
with open("Customers.txt", "r") as customers:
    for line in customers:
        name,uname,pwd,_,state,phone,email,taxid, _ = line.strip().split(",")
        print(SQLCustomers.format(uname, pwd, phone, email, name, taxid, state, "FALSE"))

SQLStock_Profiles = 'INSERT INTO Stock_Profiles (sym,dcp,price,DOB,name) VALUES ("{}",{},{},"{}","{}");'

def getDateFormat(dmyInLetters):
    day, month, year = dmyInLetters.strip().split(" ");
    month = ({
        'January': '01',
        'February': '02',
        'March': '03',
        'April': '04',
        'May': '05',
        'June': '06',
        'July': '07',
        'August': '08',
        'September': '09',
        'October': '10',
        'November': '11',
        'December': '12'
    }).get(month)
    return "{}-{}-{}".format(year,month,day)

with open("Stock_Profiles.txt", "r") as stock_profiles:
    for line in stock_profiles:
        sym, price,name,dob,_,_,_,_ = line.strip().split(",")
        symToPrice[sym] = price
        print(SQLStock_Profiles.format(sym, price, price, getDateFormat(dob), name))

SQLContract_Signed = 'INSERT INTO Contract_Signed (sym,movie,year,value,role) VALUES ("{}","{}",{},{},"{}");'

with open("Stock_Profiles.txt", "r") as stock_profiles:
    for line in stock_profiles:
        sym,_,_,_,movie,role,year,value = line.strip().split(",")
        print(SQLContract_Signed.format(sym, movie, year, value, role))

SQLMarket_Accounts = 'INSERT INTO Market_Accounts (aid,balance,taxid,day_of_the_month, running_balance_sum) VALUES ("{}",{},"{}",{}, {});'

with open("Market.txt", "r") as market:
    for line in market:
        taxid,aid, balance = line.strip().split(",")
        print(SQLMarket_Accounts.format(aid, balance, taxid, 16, 16 * float(balance)))

SQLStockAccounts = 'INSERT INTO Stock_Accounts (taxid) VALUES("{}")'
SQLIn_Stock_Acc = 'INSERT INTO In_Stock_Acc (sym,aid,balance,pps) VALUES ("{}", {}, {}, {});'

with open("Stocks.txt", "r") as stocks:
    aid = 0
    for line in stocks:
        aid += 1
        taxid,balance,sym = line.strip().split(",")
        print(SQLStockAccounts.format(taxid))
        print(SQLIn_Stock_Acc.format(sym, aid, balance, symToPrice[sym]))