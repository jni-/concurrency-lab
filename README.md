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
 * Make `transferMoneyTo()` synchronized. (solution not provided... come on)
 * Try [ReentrantLock](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html) (hint: use a global lock). Use `lockInterruptibly()`, not `.lock()`. (branch: global_lock)
 * Try the `synchronized` keyword again, but not on the `transferMoneyTo()`method. Can you make it work? (branch: scoped_locks)
 * Do you know about [AtomicInteger](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/AtomicInteger.html)? (branch: atomicint)
 * Mix synchronized methods (or blocks) with `AtomicInteger`. Try not to over-lock. (branch: atomicint_sync)
 * Try [akka agents](http://doc.akka.io/docs/akka/snapshot/java/agents.html). You may need to change the `App.java` for this one and handle failures with [Futures](http://doc.akka.io/docs/akka/snapshot/java/agents.html) instead. (branch: akka_agents)
 * Shared mutable state is the root of all evils (in concurrency). Can you avoid the problem altogether? (solution not provided)
 * Can you map out an actor model for this problem? You can change anything in the code for this. (solution not provided)
 * If you want a fonctionnal approach to this problem using clojure, see [this repository](https://github.com/gariepyalex/bankaccount) from gariepyalex.

Explain :
 * How come making `transferMoneyTo()` synchronized is not working? Or rather, why is it only partially working.
 * Why is the global lock so slow?
 * AtomicInteger only fixes the issue with the balance, not the negative balance. Why is that?
 * Akka agents are overkill for this problem, but they have the advantage to be [location transparent](http://doc.akka.io/docs/akka/snapshot/general/remoting.html). This is out of scope, but you can try to make it work!
