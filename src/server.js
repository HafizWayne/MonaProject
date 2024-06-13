
const express = require('express');
const bodyParser = require('body-parser');
const router = require('./routes/router');

const app = express();
const port = 8080;

app.use(bodyParser.json());

app.use('/mona', router);

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}/`);
});
