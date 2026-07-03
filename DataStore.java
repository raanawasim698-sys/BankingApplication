package bank.util;

import bank.model.Bank;
import bank.model.Account;
import bank.model.Client;

import java.io.*;

/** Handles saving and loading Bank state to/from a binary file. */
public class DataStore {
    private static final String DATA_FILE = "bank_data.ser";

    public static void save(Bank bank) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            // Save counters alongside bank object
            oos.writeObject(bank);
            oos.writeInt(Account.getCounter());
            oos.writeInt(Client.getCounter());
            System.out.println("[DataStore] Data saved successfully.");
        } catch (IOException e) {
            System.err.println("[DataStore] Save error: " + e.getMessage());
        }
    }

    public static Bank load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) {
            System.out.println("[DataStore] No existing data. Starting fresh.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            Bank bank = (Bank) ois.readObject();
            Account.setCounter(ois.readInt());
            Client.setCounter(ois.readInt());
            System.out.println("[DataStore] Data loaded successfully.");
            return bank;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[DataStore] Load error: " + e.getMessage());
            return null;
        }
    }
}
