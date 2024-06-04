const express = require('express');
const router = express.Router();
const savingsHandler = require('../handler/savingHandler');
const transactionsHandler = require('../handler/transactionHandler');

router.post('/saving', savingsHandler.createSaving);
router.get('/savings', savingsHandler.getAllSavings);

router.post('/transaction', transactionsHandler.createTransactions);
router.get('/transactions', transactionsHandler.getAllTransactions);

router.get('/:credentials', savingsHandler.getSavingById);
router.put('/:id', savingsHandler.updateSaving);
router.delete('/:id', savingsHandler.deleteSaving);

module.exports = router;
