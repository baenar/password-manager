# PasswordManager

A secure, offline-first, terminal-based password manager written in Java 23. It provides a robust local encrypted vault, an interactive CLI, and advanced tools for analyzing the strength and health of your digital identity.

## Key Features

* **Offline-First & Zero-Knowledge:** Your data never leaves your device. The master password is only used in RAM to derive the decryption key and is never stored on disk.
* **Interactive CLI:** A fluid, raw-mode terminal interface built with JLine 3. Navigate with your keyboard, search in real-time, and manage entries without typing clunky commands.
* **Strong Cryptography:** Utilizes industry-standard AES-256 encryption and PBKDF2 key derivation to secure your database.
* **Advanced Security Auditing:** Built-in "Security Center" that calculates Shannon entropy, estimates brute-force time, and detects reused, similar, or outdated passwords.
* **Smart Clipboard:** Copy passwords directly to your OS clipboard. The app automatically securely clears the clipboard after 30 seconds.
* **Custom Password Generator:** Create strong passwords with customizable parameters (length, special characters).

## Tech Stack

* **Language:** Java 23
* **Libraries:** JLine 3 (Interactive CLI), Lombok
* **Cryptography:** Java Cryptography Architecture (JCA) - AES-256, SHA-256, PBKDF2
* **Build System:** Custom lightweight Bash script orchestrator (`run.sh`)

## Security Model

The database is a single, easily portable flat file. Security is guaranteed by:
1. **Key Derivation:** The encryption key is generated on-the-fly from your Master Password using PBKDF2 with a high iteration count. 
2. **Symmetric Encryption:** Every password entry is individually encrypted. Text fields (like usernames and notes) are Base64 encoded to prevent flat-file parsing errors.
3. **Safe I/O:** Uses temporary buffer files (`.tmp`) during write operations to prevent data corruption in case of unexpected system crashes.

## Getting Started

### Prerequisites
* **JDK 23** installed and added to your system's `PATH`.
* Bash environment (Native on Linux/macOS, Git Bash or WSL on Windows).
* *(Linux only)* `xclip` installed for clipboard support.

### Installation & Running

Forget heavy build tools like Maven or Gradle. This project uses a custom, self-contained build script for maximum simplicity.

1. Clone the repository:
   ```bash
   git clone [https://github.com/YOUR_USERNAME/PasswordManager.git](https://github.com/YOUR_USERNAME/PasswordManager.git)
   cd PasswordManager
   ```
2. (Windows only) In 'run.sh', update path to your JDK bin directory.
3. (Unix only) Make the script executable:
   ```bash
   chmod +x run.sh
   ```
4. Build the project:
   ```bash
   ./run.sh
   ```
5. The script will compile the code and link all required libraries. To run, copy and paste that command shown in the end into your terminal.
