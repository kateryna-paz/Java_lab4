package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// клас для підключення до бази даних
public class Connect {

    // Вказуємо дані для підключення до бази даних
    static String DB_URL = "jdbc:mysql://localhost:3306/products";
    static String DB_USER = "root";
    static String DB_PASSWORD = "sa";
    // Метод, який повертає з'єднання з базою даних
    public static Connection getConnection() {
        // Оголошення змінної, яка буде використовуватися для представлення з'єднання з базою даних
        Connection dbConnection = null;

        // Спроба підключитися до бази даних
        try {
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }

        return dbConnection;
    }
}

