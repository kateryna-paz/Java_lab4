package database;

import java.sql.*;
public class InsertRequest {
    // Метод для виконання запиту на додавання товару та його параметрів
    public void AddProduct(String productName, String productGroupName, String description, String releaseDate, ParameterValue... parameters) {
        // Отримуємо з'єднання з базою даних
        Connection conn = Connect.getConnection();

        // Якщо з'єднання успішно встановлено
        if (conn != null) {
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                // Додавання нового продукту
                String addProductQuery = "INSERT INTO products (product_name, product_group_id, description, release_date)\n" +
                        "VALUES (?, (SELECT group_id FROM product_groups WHERE group_name = ?), ?, ?)";
                stmt = conn.prepareStatement(addProductQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, productName);
                stmt.setString(2, productGroupName);
                stmt.setString(3, description);
                stmt.setString(4, releaseDate);
                stmt.executeUpdate();  // Вставляємо дані у БД

                // Отримання product_id нового продукту
                rs = stmt.getGeneratedKeys();
                int newProductId = 0;
                if (rs.next()) {  // Перевірка існування нового згенерованого ключа
                    newProductId = rs.getInt(1);
                }

                // Додавання параметрів нового продукту
                String addParameterQuery = "INSERT INTO product_parameters (product_id, parameter_id, parameter_value)\n" +
                        " VALUES (?, (SELECT parameter_id FROM parameters WHERE parameter_name = ?), ?)";
                stmt = conn.prepareStatement(addParameterQuery);
                for (ParameterValue parameter : parameters) {
                    stmt.setInt(1, newProductId);
                    stmt.setString(2, parameter.getName());
                    stmt.setString(3, parameter.getValue());
                    stmt.addBatch();  // Додавання операції до пакету запиту
                }
                stmt.executeBatch();  // Виконання пакету з операціями на додавання параметрів

                System.out.println("Продукт додано успішно!");

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Закриваємо ресурси
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    conn.close();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Помилка підключення до бази даних.");
        }
    }
}

