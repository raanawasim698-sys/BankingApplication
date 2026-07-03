# VaultBank

A desktop banking management system built with **Java Swing**. VaultBank lets you manage clients, open different types of accounts, process transactions, and pay bills — all through a simple GUI, with data persisted to disk between sessions.

## Features

- **Client management** — store client/person details
- **Multiple account types**
  - Current Account
  - Savings Account
  - Loan Account
- **Bill payments** — support for different bill types
- **Data persistence** — account and client data is saved locally via Java serialization (`bank_data.ser`) so your data survives between runs
- **Swing GUI** — desktop interface for interacting with the bank system

## Tech Stack

- **Language:** Java
- **UI:** Java Swing
- **Persistence:** Java Object Serialization
- **Build/Project:** IntelliJ IDEA (module-based, no Maven/Gradle)

## Project Structure

```
VaultBank/
├── src/
│   └── bank/
│       ├── Main.java              # Application entry point
│       ├── gui/
│       │   └── BankGUI.java       # Swing GUI and event handling
│       ├── model/
│       │   ├── Account.java
│       │   ├── Bank.java
│       │   ├── BillPayment.java
│       │   ├── Client.java
│       │   ├── CurrentAccount.java
│       │   ├── LoanAccount.java
│       │   ├── Person.java
│       │   └── SavingsAccount.java
│       └── util/
│           └── DataStore.java     # Handles serialization/deserialization
├── bank_data.ser                  # Serialized bank data (sample/local data)
├── Bank.iml                       # IntelliJ module file
└── .gitignore
```

## Getting Started

### Prerequisites

- Java JDK 17+ (built with JDK 21)
- IntelliJ IDEA (recommended, project includes `.iml` module file) — or any Java IDE / the `javac`/`java` CLI

### Running from the pre-built JAR

```bash
java -jar VaultBank.jar
```

### Running from source

1. Clone the repo
   ```bash
   git clone https://github.com/<your-username>/VaultBank.git
   cd VaultBank
   ```
2. Open the project in IntelliJ IDEA (it will pick up `Bank.iml`), or compile manually:
   ```bash
   javac -d out $(find src -name "*.java")
   java -cp out bank.Main
   ```

## Data Storage

VaultBank persists its state to `bank_data.ser` using Java serialization, via the `bank.util.DataStore` class. This file is created/updated automatically as you use the app.

> **Note:** Since this uses Java's native serialization, the saved data is tied to matching class versions — changing the model classes can make old `.ser` files unreadable.

