# Backend-Mona-App

http://35.240.207.177:8080/mona

## Add the following paths for user data:
- **/user**: To perform a POST request for users data.
- **/users**: To perform a GET request to retrieve the previously entered data.
- **/users/credentials (/users/yWwBA7zB9jXcpPD3Wx23I6EgGiG3)**: To perform a GET request to retrieve the previously entered data by credentials.

### Use the following format for testing in Postman in the body section:
```json
{
    "credentials": "yWwBA7zB9jXcpPD3Wx23I6EgGiG3",
    "nama": "Faishal",
    "total_balance": 0,
    "total_expense": 0,
    "total_emergency": 0,
    "dana_maksimal": 50000,
    "total_makan": 2
}
```


## Add the following paths for transaction data:
- **/transaction**: To perform a POST request for saving income and expense data.
- **/transactions**: To perform a GET request to retrieve the previously entered data.
- **/transactions/credentials (/transactions/yWwBA7zB9jXcpPD3Wx23I6EgGiG3)**: To perform a GET request to retrieve the previously entered data by credentials.

### Use the following format for testing in Postman in the body section:
```json
{
   "date": "2024-06-11",
   "credentials": "yWwBA7zB9jXcpPD3Wx23I6EgGiG3",
   "category": "makanan",
   "amount": 50000,
   "titles": "Coba Post",
   "action": "income",
   "food_category": "fast food"
}
```

## Add the following paths for saving data:
- **/saving**: To perform a POST request for saving emergency fund data.
- **/savings**: To perform a GET request to retrieve the previously entered data.
- **/savings/credentials (/savings/yWwBA7zB9jXcpPD3Wx23I6EgGiG3)**: To perform a GET request to retrieve the previously entered data by credentials.

### Use the following format for testing in Postman in the body section:
```json
{
   "date": "2024-06-11",
   "credentials": "yWwBA7zB9jXcpPD3Wx23I6EgGiG3",
   "amount": 50000,
   "title": "Coba Post"
}
```
