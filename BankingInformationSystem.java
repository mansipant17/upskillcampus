import java.io.*;
import java.util.*;

class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}

class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private int accountNumber;
    private String name;
    private String email;
    private String phone;
    private String password;
    private double balance;
    private List<Transaction> transactions;

    public Account(int accountNumber, String name, String email, String phone, String password, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void deposit(double amount) {
        balance += amount;
        transactions.add(new Transaction("Deposit", amount));
    }

    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            transactions.add(new Transaction("Withdrawal", amount));
            return true;
        } else {
            return false;
        }
    }

    public void updateInfo(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}

public class BankingSystem {
    private static HashMap<Integer, Account> accounts = new HashMap<>();
    private static int nextAccountNumber = 1001;
    private static Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "accounts.dat";

    @SuppressWarnings("unchecked")
    private static void loadAccounts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // file nahi mili, naya start karo
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            accounts = (HashMap<Integer, Account>) ois.readObject();
            if (!accounts.isEmpty()) {
                nextAccountNumber = Collections.max(accounts.keySet()) + 1;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void registerUser() {
        System.out.println("\n--- Register User ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Create a password: ");
        String password = scanner.nextLine();

        Account account = new Account(nextAccountNumber, name, email, phone, password, 0.0);
        accounts.put(nextAccountNumber, account);
        System.out.println("Registration successful! Your account number is: " + nextAccountNumber);
        nextAccountNumber++;
        saveAccounts();
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter account number: ");
        int accNum = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Account account = accounts.get(accNum);
        if (account != null && account.getPassword().equals(password)) {
            System.out.println("Login successful! Welcome " + account.getName());
            userMenu(account);
            saveAccounts();
        } else {
            System.out.println("Invalid account number or password.");
        }
    }

    private static void userMenu(Account account) {
        boolean logout = false;
        while (!logout) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Deposit Money");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Fund Transfer");
            System.out.println("4. View Transaction History");
            System.out.println("5. Update Account Info");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    depositMoney(account);
                    saveAccounts();
                    break;
                case 2:
                    withdrawMoney(account);
                    saveAccounts();
                    break;
                case 3:
                    fundTransfer(account);
                    saveAccounts();
                    break;
                case 4:
                    viewTransactionHistory(account);
                    break;
                case 5:
                    updateAccountInfo(account);
                    saveAccounts();
                    break;
                case 6:
                    changePassword(account);
                    saveAccounts();
                    break;
                case 7:
                    logout = true;
                    saveAccounts();
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    private static void depositMoney(Account account) {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        if (amount > 0) {
            account.deposit(amount);
            System.out.println("Amount deposited successfully. New balance: " + account.getBalance());
        } else {
            System.out.println("Invalid amount.");
        }
    }

    private static void withdrawMoney(Account account) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        if (amount > 0) {
            if (account.withdraw(amount)) {
                System.out.println("Amount withdrawn successfully. New balance: " + account.getBalance());
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Invalid amount.");
        }
    }

    private static void fundTransfer(Account sender) {
        System.out.print("Enter recipient account number: ");
        int recipientAccNum = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        Account recipient = accounts.get(recipientAccNum);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (sender.withdraw(amount)) {
            recipient.deposit(amount);
            System.out.println("Fund transfer successful. Your new balance: " + sender.getBalance());
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    private static void viewTransactionHistory(Account account) {
        System.out.println("\n--- Transaction History ---");
        List<Transaction> transactions = account.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t.getType() + ": " + t.getAmount());
            }
        }
    }

    private static void updateAccountInfo(Account account) {
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        System.out.print("Enter new phone number: ");
        String phone = scanner.nextLine();
        account.updateInfo(name, email, phone);
        System.out.println("Account information updated successfully.");
    }

    private static void changePassword(Account account) {
        System.out.print("Enter current password: ");
        String currentPass = scanner.nextLine();
        if (!account.getPassword().equals(currentPass)) {
            System.out.println("Incorrect password.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = scanner.nextLine();
        account.changePassword(newPass);
        System.out.println("Password changed successfully.");
    }

    public static void main(String[] args) {
        loadAccounts();

        boolean exit = false;

        while (!exit) {
            System.out.println("\n*** Banking System Menu ***");
            System.out.println("1. Register User");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    saveAccounts();
                    exit = true;
                    System.out.println("Thank you for using the Banking System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        scanner.close();
    }
}
