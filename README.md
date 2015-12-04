Concurrency by trial and error
==============================

_Note_: This project is used for a workshop. If it makes no sense to you as-is, it's normal.

The goal of this project is to figure out ways to deal with concurrency.

The problem
===========

The current code (in `master`) does this :

 * Create 100 accounts
 * Randomly move money between these accounts.
   * Find a random account and transfer money to 5 other accounts (maybe yourself).
   * Generate the opposite transaction (payback)
   * It is possible for an account to be out of money. In this case transactions are stacked and payed  synchronously at the end.
   * Verifying the balance and paying the other account (in `Account.java`) incures a delay. This is to simulate real transactions.
 * Pay everyone back (delayed payments), until no money is owed anymore.
 * Verify that all accounts are back to their initial balance.

As it stands, the current code will not bring all accounts back to their initial balance.

Your job
========

Try different methods to make sure money flows properly! That means that balance never becomes negative and all accounts come back to their initial balance.

You cannot change `App.java`. You cannot remove the 1ms delay in the account. Everything else, you can change.

Here are a few ideas :
 * Make `transferMoneyTo()` synchronized.
 * Try [ReentrantLock](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html) (hint: use a global lock). Use `lockInterruptibly()`, not `.lock()`.
 * Try the `synchronized` keyword again, but not on the `transferMoneyTo()`method. Can you make it work?
 * Do you know about [AtomicInteger](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/AtomicInteger.html)?

Explain :
 * How come making `transferMoneyTo()` synchronized is not working? Or rather, why is it only partially working.
 * Why is the global lock so slow?
 * AtomicInteger only fixes the issue with the balance, not the negative balance. Why is that?

Also try to fix :
 * Locking on `transferMoneyTo()` is not necessary and locks more than is needed. How can you make locks more specific?
