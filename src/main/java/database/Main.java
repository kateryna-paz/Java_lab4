package database;

import java.sql.*;
public class Main {
    public static void main(String[] args) throws SQLException {
        // створюємо об'єкт меню та викликаємо його
        Menu menu = new Menu();
        menu.DisplayMenu();

    }
}
