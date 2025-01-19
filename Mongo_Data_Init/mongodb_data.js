db = db.getSiblingDB('reservation');

db.createCollection("customer");
db.createCollection("admin");
db.createCollection("train");
db.createCollection("history");

// Insert Admin
db.admin.insertOne({
  mailid: 'admin@demo.com',
  pword: 'admin',
  fname: 'System',
  lname: 'Admin',
  addr: 'Demo Address 123 colony',
  phno: NumberLong(9874561230)  // Long value
});

// Insert Customers
db.customer.insertOne({
  mailid: 'godfrey@demo.com',
  pword: '1234',
  fname: 'Godfrey',
  lname: 'Odo',
  addr: 'Kubwa, Abuja',
  phno: NumberLong(8012345678)  // Long value
});

// Insert Trains
db.train.insertMany([
  { tr_no: NumberLong(10001), tr_name: 'ABUJA METRO EXP', from_stn: 'ABUJA', to_stn: 'KADUNA TERMINUS STATION', seats: NumberInt(50), fare: 3490.50 },
  { tr_no: NumberLong(10002), tr_name: 'ABUJA LIGHT EXP', from_stn: 'ABUJA', to_stn: 'LAGOS TERMINUS STATION', seats: NumberInt(4), fare: 7550.50 },
  { tr_no: NumberLong(10003), tr_name: 'IBADAN LIGHT EXP', from_stn: 'IBADAN', to_stn: 'IKEJA LAGOS SUBURBAN STATION', seats: NumberInt(20), fare: 2075 },
  { tr_no: NumberLong(10004), tr_name: 'KADUNA METRO EXP', from_stn: 'KADUNA', to_stn: 'KANO TERMINUS STATION', seats: NumberInt(82), fare: 6550 },
  { tr_no: NumberLong(10005), tr_name: 'LAGOS-CITY METRO EXP', from_stn: 'YABA SUBURBAN STATION', to_stn: 'ABEOKUTA TERMINUS', seats: NumberInt(150), fare: 1945 },
  { tr_no: NumberLong(10006), tr_name: 'EAST-SIDE SPEED TAIN EXP', from_stn: 'ABA', to_stn: 'ENUGU TERMINUS', seats: NumberInt(140), fare: 1450.75 }
]);

// Insert History
db.history.insertMany([
  { transid: 'BBC374-NSDF-4673', mailid: 'godfrey@demo.com', tr_no: '10001', date: '2024-02-02T00:00:00Z', from_stn: 'ABUJA', to_stn: 'KADUNA TERMINUS STATION', seats: NumberInt(10), amount: 3500.00 },
  { transid: 'BBC375-NSDF-4675', mailid: 'godfrey@demo.com', tr_no: '10009', date: '2024-01-12T00:00:00Z', from_stn: 'KADUNA', to_stn: 'PORT HARCOURT TERMINUS STATION', seats: NumberInt(49), amount: 10550.00 },
  { transid: 'BBC373-NSDF-4674', mailid: 'godfrey@demo.com', tr_no: '10006', date: '2024-07-22T00:00:00Z', from_stn: 'ABA', to_stn: 'ENUGU TERMINUS', seats: NumberInt(3), amount: 1652.25 }
]);