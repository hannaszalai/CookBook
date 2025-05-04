# 🍲 CookBook: Java Edition  
**Your personal chef — now with SQL-powered taste buds 👨‍🍳💾**

A robust recipe management system built in Java with MySQL and JavaFX GUI. Fueled by scrum, caffeine, and that one person who insisted on using interfaces.

---

## 📦 Releases

- Final project release includes:
  - Complete GUI application
  - Source code with modular structure
  - SQL schema for database setup
  - Gradle build scripts
  - Secret sauce (not really, just configs)

---

## 🛠️ Features

- Add, edit, delete recipes and ingredients
- Store user accounts with roles (admin/user)
- Fully normalized SQL database
- Clean MVC structure
- Built using Gradle and JavaFX

---

## 🔧 How to Set Up the Project

1. Clone this repo.
2. Install [MySQL](https://www.mysql.com/), [Java 17+](https://adoptopenjdk.net/), and [Gradle](https://gradle.org/).
3. Create a database using the schema described below (see [User Manual](./USERMANUAL.md)).
4. In the `DatabaseConnection.java` file, update **your** MySQL username and password.

```java
String url = "jdbc:mysql://localhost:3306/your_database_name";
String user = "your_username";
String password = "your_password";
```

5. Run with:
```bash
./gradlew run
```

---

## 📚 Tech Stack

`java` &nbsp; `javafx` &nbsp; `mysql` &nbsp; `gradle` &nbsp; `mvc` &nbsp; `oop` &nbsp; `gui` &nbsp; `recipe-app`

---

## 📝 License

This project is licensed under the **MIT License** — because good code, like good food, is meant to be shared.  
See the [LICENSE](./LICENSE) file for full details.

