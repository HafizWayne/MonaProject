const express = require('express');
const router = express.Router();
const savingsHandler = require('../handler/savingHandler');
const transactionsHandler = require('../handler/transactionHandler');
const userHandler = require('../handler/userHandler');

// Savings Routes
router.post('/saving', savingsHandler.createSaving);
router.get('/savings', savingsHandler.getAllSavings);
router.get('/savings/:credentials', savingsHandler.getSavingById);
router.put('/savings/:id', savingsHandler.updateSaving);
router.delete('/savings/:id', savingsHandler.deleteSaving);

// Transactions Routes
router.post('/transaction', transactionsHandler.createTransactions);
router.get('/transactions', transactionsHandler.getAllTransactions);
router.get('/transactions/:credentials', transactionsHandler.getTransactionById);
router.put('/transactions/:id', transactionsHandler.updateTransaction);
router.delete('/transactions/:id', transactionsHandler.deleteTransaction);

router.post('/user', userHandler.createUsers);
router.get('/users', userHandler.getAllUsers);
router.get('/users/:credentials', userHandler.getUsersBycredentials);

module.exports = router;
