const express = require('express');
const router = express.Router();
const savingsHandler = require('../handler/savingHandler');
const transactionsHandler = require('../handler/transactionHandler');
const userHandler = require('../handler/userHandler');

// Savings Routes
router.post('/saving', savingsHandler.createSaving);
router.get('/savings', savingsHandler.getAllSavings);
router.get('/saving/:credentials', savingsHandler.getSavingById);
router.put('/saving/:id', savingsHandler.updateSaving);
router.delete('/saving/:id', savingsHandler.deleteSaving);

// Transactions Routes
router.post('/transaction', transactionsHandler.createTransactions);
router.get('/transactions', transactionsHandler.getAllTransactions);

router.post('/user', userHandler.createUsers);
router.get('/users', userHandler.getAllUsers);
router.get('/users/:id', userHandler.getUsersById);

module.exports = router;
