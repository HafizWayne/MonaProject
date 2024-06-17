# Backend-Mona-App

http://35.240.207.177:8080/mona

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
