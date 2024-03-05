package database;
import java.sql.*;

// абстракний клас для обробки запитів
public abstract class DatabaseRequest {
    protected static PreparedStatement stmt = null;
    protected static ResultSet rs = null;
    // Метод для виконання запиту з вибором типу запиту
    public String DoRequest(String query, String[] columns) throws SQLException {
        // Отримуємо з'єднання з базою даних
        Connection conn = Connect.getConnection();

        // Якщо з'єднання успішно встановлено
        if (conn != null) {
            try {
                // Створюємо об'єкт Statement для виконання запиту
                stmt = conn.prepareStatement(query);

                // Виконуємо відповідний запит залежно від типу
                if (columns == null) {
                    DoUpdateRequest(stmt, query);
                } else if (columns.length == 1) {
                    return DoRequestForMenu(stmt, query);
                } else {
                    DoSelectRequest(stmt, query, columns);
                }
            // незалежно від результату, закриваємо ресурси (підключення до БД)
            } finally {
                closeResources();
            }
        } else {
            System.out.println("Помилка підключення до бази даних.");
        }
        return "";
    }

    // Виконання запиту SELECT
    protected abstract void DoSelectRequest(java.sql.Statement stmt, String query, String[] columns) throws SQLException;

    // Виконання запиту UPDATE або DELETE
    protected abstract void DoUpdateRequest(Statement stmt, String query) throws SQLException;

    // Виконання запиту SELECT для вибору у меню
    protected abstract String DoRequestForMenu(Statement stmt, String query) throws SQLException;

    // Метод для закриття ресурсів
    private static void closeResources() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            Connect.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
