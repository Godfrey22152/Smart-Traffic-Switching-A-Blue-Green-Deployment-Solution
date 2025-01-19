# MongoDB Atlas Setup with `mongodb_data.js`

This README provides detailed instructions for setting up a MongoDB Atlas database using the `mongodb_data.js` file. The file contains the necessary commands to create collections and insert initial data into the MongoDB database.

## Prerequisites

1. **MongoDB Atlas Account**: You must have a MongoDB Atlas account. If you don't have one, sign up [here](https://www.mongodb.com/cloud/atlas).

2. **MongoDB Atlas Cluster**: Create a cluster in your MongoDB Atlas account. Follow the instructions [here](https://docs.atlas.mongodb.com/tutorial/deploy-free-tier-cluster/) to create a free-tier cluster.

3. **MongoDB Shell**: Ensure you have MongoDB Shell (mongosh) installed on your local machine. You can download it [here](https://www.mongodb.com/try/download/shell).

## Setup Instructions

### Step 1: Create a Database User

1. Go to your MongoDB Atlas dashboard.
2. Select your cluster.
3. Click on "Database Access" under the "Security" section.
4. Click on "Add New Database User".
5. Fill in the username and password. Remember these credentials as they will be used to connect to the database.
6. Assign the user the "Read and Write to any database" role.

### Step 2: Whitelist Your IP Address

1. Go to the "Network Access" section under "Security".
2. Click on "Add IP Address".
3. Select "Allow Access from Anywhere" or add your local IP address.
4. Save the changes.

### Step 3: Prepare the `mongodb_data.js` File

Ensure the `mongodb_data.js` file contains the following content:

```javascript
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
```

### Step 4: Connect to MongoDB Atlas and Execute the Script

#### 1. Obtain the Connection String:

1. Go to your cluster in MongoDB Atlas.
2. Click on "Connect".
3. Choose "Connect with MongoDB Shell".
4. Copy the connection string. It should look like this:

```bash
mongo "mongodb+srv://<username>:<password>@<cluster-name>.mongodb.net/"

```
#### 2. Run the Script:

1. Open your terminal or command prompt.
2. Navigate to the directory containing the mongodb_data.js file.
3. Run the following command, replacing <username>, <password>, and <cluster-name> with your actual credentials:

```bash
mongo "mongodb+srv://<username>:<password>@<cluster-name>.mongodb.net/" mongodb_data.js
```

### **Verification**
To verify that the data has been inserted correctly, you can use the MongoDB Atlas UI or run the following commands in the MongoDB Shell:

```sh
use reservation;

db.customer.find().pretty();
db.admin.find().pretty();
db.train.find().pretty();
db.history.find().pretty();
```
This should display the inserted documents for each collection.

### **Conclusion**
You have now successfully set up your MongoDB Atlas database and inserted initial data using the mongodb_data.js file. You can now proceed to use this database for your application.
For any further assistance, refer to the MongoDB Atlas Documentation.