/**
 * Package for fraud detection of credit card transactions
 *
 * A credit card transaction is comprised of the following elements;
 *
 * 1. hashed credit card number
 * 2. timestamp - of format 'year-month-dayThour:minute:second'
 * 3. price - of format 'dollars.cents'
 *
 * Transactions are to be received as a comma separated string of elements
 * eg. '10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00'
 *
 * A credit card will be identified as fraudulent if the sum of prices for a unique hashed credit card number,
 * for a given day, exceeds the price threshold T.
 */
package com.touchcorp;
