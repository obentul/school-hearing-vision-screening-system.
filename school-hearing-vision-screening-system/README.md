# School Hearing and Vision Screening Assistant

A Java Swing + MySQL desktop application for organizing pupil registration, hearing and vision screening records, and referral follow-up at the proposed Kansanga Primary School case study.

## Features

- Secure sign-in with password hashes (PBKDF2-HMAC-SHA256).
- Register and view pupils.
- Record hearing and vision screening outcomes (`PASS`, `REFER`, `INCONCLUSIVE`).
- Record referrals and update follow-up status.
- Dashboard counts for pupils, screening records and open referrals.
- Export pupil, screening and referral tables to CSV.
- MySQL foreign keys and basic input validation.

**Clinical limitation:** This is a record-management and follow-up tool. It does not diagnose hearing or vision conditions or replace assessment by qualified health professionals.

## Technology

- Java 17+
- Apache Maven
- Java Swing
- MySQL 8+
- MySQL Connector/J (managed by Maven)

## 1. Create the database

1. Start MySQL Server.
2. Open MySQL Workbench.
3. Open `database/schema.sql`.
4. Execute the complete script.

This creates the `school_screening` database and its tables.

## 2. Configure the database connection

The application reads these environment variables:

- `SCHOOL_DB_URL` (optional; default: `jdbc:mysql://localhost:3306/school_screening?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `SCHOOL_DB_USER` (optional; default: `root`)
- `SCHOOL_DB_PASSWORD` (recommended; set this to your local MySQL password)

Do not commit your database password to GitHub.

### Windows PowerShell example

```powershell
$env:SCHOOL_DB_USER="root"
$env:SCHOOL_DB_PASSWORD="your-local-mysql-password"
```

Set the values in the terminal before running Maven. Replace the example password with your own local password.

## 3. Create the first administrator

From the project root, run:

```bash
mvn -q -DskipTests package
mvn -q exec:java -Dexec.mainClass=ug.schoolscreening.SetupAdmin
```

Enter an administrator username, full name and a strong password of at least 10 characters. The password is stored as a salted PBKDF2 hash, not plain text.

## 4. Run the application

```bash
mvn clean compile
mvn exec:java
```

Or open the folder in Apache NetBeans as an existing Maven project and run `ug.schoolscreening.App`.

## Database tables

- `app_users`: authorized user accounts and roles.
- `pupils`: minimum pupil identification and contact fields.
- `screenings`: dated hearing or vision screening records.
- `referrals`: referral reasons, destination and follow-up status.

## Important privacy and deployment guidance

This repository is intended as an academic demonstration. Do not upload real pupil names, dates of birth, guardian contacts, screening outcomes, passwords, database dumps, or identifiable screenshots to a public repository. Use synthetic test data only.

Before any real school deployment:
- obtain school authorization and appropriate parent/guardian permissions;
- confirm the applicable privacy and data-protection requirements;
- restrict database and application access to approved staff;
- use secure backups and a tested recovery procedure;
- configure encrypted connections and appropriate server security;
- validate the screening workflow with qualified health personnel;
- test with anonymized data before any real pupil records are entered.

## Current project status

This is an initial working implementation of the core record-management workflow. It should be tested in the group's own Java/MySQL environment and reviewed with the school before being described as approved for operational use. More advanced functions such as editing/deleting pupil records, detailed report filters, audit logs and role-specific permissions are future improvements.
