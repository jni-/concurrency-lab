package ca.ulaval.glo4003.concurrence_akka;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Account {

	public final int initialBalance;

	public int balance;
	public int accountNumber;
	public Map<Account, Integer> delayedPayments = new ConcurrentHashMap<>();

	public Account(int balance, int accountNumber) {
		this.balance = balance;
		this.initialBalance = balance;
		this.accountNumber = accountNumber;
	}

	public void transferMoneyTo(Account toAccount, int amount) {
		delayPaymentIfFundsNotAvaialble(toAccount, amount);

		changeBalance(-amount);
		simulateDelay(); // You cannot remove this.
		toAccount.changeBalance(amount);

		if (balance < 0) {
			throw new NegativeBalanceException();
		}

	}

	private synchronized void delayPaymentIfFundsNotAvaialble(Account otherAccount, int amount) {
		if (balance - amount < 0) {
			if (delayedPayments.containsKey(otherAccount)) {
				delayedPayments.put(otherAccount, delayedPayments.get(otherAccount) + amount);
			} else {
				delayedPayments.put(otherAccount, amount);
			}
			throw new OutOfMoneyForNowException();
		}
		simulateDelay(); // You cannot remove this.
	}

	private synchronized void changeBalance(int delta) {
		balance += delta;
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
