const cron = require('node-cron');
const db = require('../db/database');

const resetDailyFields = () => {
  const query = 'UPDATE users SET dana_maksimal = 0, total_makan = 0';
  
  db.query(query, (err, results) => {
    if (err) {
      console.error('Error resetting daily fields:', err);
    } else {
      console.log('Successfully reset daily fields for all users');
    }
  });
};

// Schedule the task to run every minute
cron.schedule('0 0 * * *', () => {
  console.log('Running reset job every day');
  resetDailyFields();
});
