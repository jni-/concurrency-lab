package ca.ulaval.glo4003.concurrence_akka;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
	private final static int NUMBER_OF_ACCOUNTS = 100;
	private final static int NUMBER_OF_MONEY_TRANSFERS = 1000;
	private final static int NUMBER_OF_ACCOUNTS_PER_MONEY_TRANSFER = 5;

	private static Random random = new Random(12478L);
	private static ExecutorService transferExecutor = Executors.newFixedThreadPool(100);

	public static void main(String[] args) throws InterruptedException {
		Account[] accounts = createAccounts();

		time(() -> transferMoneyRandomly(accounts));
		payEveryoneWhoCouldNotBePaidTheFirstTime(accounts);

		checkThatAllAccountsAreBackToTheirInitialBalance(accounts);
	}

	private static Account[] createAccounts() {
		Account[] accounts = new Account[NUMBER_OF_ACCOUNTS];
		for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
			accounts[i] = new Account(random.nextInt(10000), i);
		}
		return accounts;
	}

	private static void transferMoneyRandomly(Account[] accounts) {
		for (int i = 0; i < NUMBER_OF_MONEY_TRANSFERS; i++) {
			final Account fromAccount = accounts[random.nextInt(NUMBER_OF_ACCOUNTS)];
			for (int j = 0; j < NUMBER_OF_ACCOUNTS_PER_MONEY_TRANSFER; j++) {
				final Account toAccount = accounts[random.nextInt(NUMBER_OF_ACCOUNTS)];
				final int amount = random.nextInt(100);
				transferExecutor.execute(createTransactionRunnable(fromAccount, toAccount, amount));
				transferExecutor.execute(createTransactionRunnable(toAccount, fromAccount, amount));
			}
		}

		transferExecutor.shutdown();
		try {
			transferExecutor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("Could not shut down executor");
		}
	}

	private static Runnable createTransactionRunnable(final Account fromAccount, final Account toAccount, final int amount) {
		return () -> {
			try {
				fromAccount.transferMoneyTo(toAccount, amount);
			} catch (OutOfMoneyForNowException e) {
				System.out.println(
						"Cannot transfer " + amount + "$ from account #" + fromAccount.accountNumber + " to account #" + toAccount.accountNumber + ".");
			} catch (NegativeBalanceException e) {
				System.err.println("Balance of account #" + fromAccount.accountNumber + " became negative!!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	private static void payEveryoneWhoCouldNotBePaidTheFirstTime(Account[] accounts) {
		System.out.print("Paying everyone back...");
		boolean someOneNeedsToBeRepaid = true;
		while (someOneNeedsToBeRepaid) {
			someOneNeedsToBeRepaid = false;
			for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
				try {
					accounts[i].fullfilDelayedPayments();
				} catch (OutOfMoneyForNowException e) {
					someOneNeedsToBeRepaid = true;
				}
			}
			System.out.print(".");
		}
		System.out.println(" done");
	}

	private static void checkThatAllAccountsAreBackToTheirInitialBalance(Account[] accounts) {
		System.out.print("Verifying account consistency...");
		for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
			if (accounts[i].balance != accounts[i].initialBalance) {
				System.err.println("Account #" + i + " should have a balance of " + accounts[i].initialBalance + "$, but has " + accounts[i].balance + "$.");
			}
		}
		System.out.println(" done");
	}

	private static void time(Runnable runnable) {
		long start = System.currentTimeMillis();
		runnable.run();
		long stop = System.currentTimeMillis();

		System.out.println("Executed in " + (stop - start) + "ms.");
	}

}
