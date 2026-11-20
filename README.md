## Group Members
* **OBENTULU GODFREY 2025-08-44517 BIT** 
* **AKAO GLORIA 2025-04-37272 BIT** 
* **RASHIM QURAISH NSANMBU 2025-08-44333 BIT** 
*  **SHIVAN OWOMUGISHA 2028-08-43099 BIT** 
* **SSERUNKUMA BRUNO 2025-08-43107 BIT** 
* **KAYEMBA SHAFIK WASSAJJA 2024-08-44333 DIT** 
* 



# School Hearing & Vision Screening System

A Java and MySQL desktop application designed to record, manage, and track pupils' hearing and vision screening results in schools.

## Features
* **Pupil Registration:** Add and manage student profiles and basic demographic details.
* **Screening Records:** Log hearing and vision test outcomes for individual pupils.
* **Search & Filter:** Quickly look up past screening records by pupil name or ID.
* **Status Tracking:** Identify pupils who require follow-up medical attention or referrals.

## Technologies Used
* **Programming Language:** Java (JDK 8 or higher)
* **Database:** MySQL
* **Database Connectivity:** JDBC (MySQL Connector/J)

## How to Set Up and Run

### 1. Database Setup
1. Ensure **MySQL Server** is installed and running on your machine.
2. Open your MySQL client (such as MySQL Workbench or phpMyAdmin).
3. Create a new database (e.g., `screening_db`) and import the provided `.sql` script from this repository to generate the required tables.

### 2. Project Configuration
1. Clone or download this repository to your computer.
2. Open the project in your Java IDE (NetBeans, IntelliJ IDEA, or Eclipse).
3. Ensure the **MySQL Connector/J** `.jar` library is added to your project's classpath.
4. Locate the database connection class in the source files and update the credentials to match your local setup:
   ```java
   String url = "jdbc:mysql://localhost:3306/screening_db";
   String user = "root";
   String password = "your_mysql_password";
   ```

### 3. Run the Application
* Locate the main class containing the `public static void main(String[] args)` method and run the project.

## Contributors
* **Obentulu Godfery** (2025-08-44517)
* *[Add other group member names and registration numbers here]*
