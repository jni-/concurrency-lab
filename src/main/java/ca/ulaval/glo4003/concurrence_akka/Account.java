package ca.ulaval.glo4003.concurrence_akka;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

	public final int initialBalance;

	private static ReentrantLock lock = new ReentrantLock();

	public int balance;
	public int accountNumber;
	public Map<Account, Integer> delayedPayments = new ConcurrentHashMap<>();

	public Account(int balance, int accountNumber) {
		this.balance = balance;
		this.initialBalance = balance;
		this.accountNumber = accountNumber;
	}

	public void transferMoneyTo(Account toAccount, int amount) {
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		delayPaymentIfFundsNotAvaialble(toAccount, amount);

		balance -= amount;
		simulateDelay(); // You cannot remove this.
		toAccount.balance += amount;

		if (balance < 0) {
			lock.unlock();
			throw new NegativeBalanceException();
		}

		lock.unlock();

	}

	private void delayPaymentIfFundsNotAvaialble(Account otherAccount, int amount) {
		if (balance - amount < 0) {
			if (delayedPayments.containsKey(otherAccount)) {
				delayedPayments.put(otherAccount, delayedPayments.get(otherAccount) + amount);
			} else {
				delayedPayments.put(otherAccount, amount);
			}
			lock.unlock();
			throw new OutOfMoneyForNowException();
		}
		simulateDelay(); // You cannot remove this.
	}

	private void simulateDelay() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void fullfilDelayedPayments() {
		for (Entry<Account, Integer> entry : delayedPayments.entrySet()) {
			this.transferMoneyTo(entry.getKey(), entry.getValue());
			delayedPayments.remove(entry.getKey());
		}
	}

}
