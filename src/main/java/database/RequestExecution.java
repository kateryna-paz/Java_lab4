package database;

import java.sql.*;

public class RequestExecution extends DatabaseRequest {

    // Виконання запиту SELECT
    @Override
    protected void DoSelectRequest(Statement stmt, String query, String[] columns) throws SQLException {
        // Виконуємо запит
        rs = stmt.executeQuery(query);

        // Перевірка, чи є результати запиту
        if (!rs.isBeforeFirst()) {
            System.out.println("Немає даних для виведення.");
            return;
        }

        // Визначення ширини стовпчиків та відступу між ними
        int columnWidth = 25;
        int spacing = 2;

        // Друкуємо заголовки стовпців
        for (String column : columns) {
            System.out.printf("%-" + columnWidth + "s%" + spacing + "s", column, ""); // Використовуємо printf() для відформатованого виведення значення стовпця та додаткового простору
        }
        System.out.println();

        // Виводимо результати запиту поки є рядки
        while (rs.next()) {
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];

                // Отримуємо значення рядка у цьому стовпці
                String value = rs.getString(column);
                if (value == null || value.equals("[null]")) value = "-";

                // Якщо останній стовпець, робимо перенос рядка
                if (i == columns.length - 1) {
                    System.out.printf("%-" + columnWidth + "s%" + spacing + "s", value, "");
                    System.out.println();
                } else {
                    System.out.printf("%-" + columnWidth + "s%" + spacing + "s", value, "");
                }
            }
        }
    }

    // Виконання запиту UPDATE або DELETE
    @Override
    protected void DoUpdateRequest(Statement stmt, String query) throws SQLException {
        // Виконуємо запит
        int affectedRows = stmt.executeUpdate(query);

        // Перевіряємо, чи виконання запиту успішне, відображуючи кількість змінених рядків
        if (affectedRows > 0) {
            System.out.println("Запит виконано успішно. Змінено рядків: " + affectedRows);
        } else {
            System.out.println("Запит не змінив жодного рядка.");
        }
    }

    // Виконання запиту SELECT для вибору у меню
    @Override
    protected String DoRequestForMenu(Statement stmt, String query) throws SQLException {
        // Виконуємо запит
        rs = stmt.executeQuery(query);

        // Перевірка, чи є результати запиту
        if (!rs.isBeforeFirst()) {
            return "..Немає даних..";
        }

        StringBuilder result = new StringBuilder();  // Використовується для ефективного збирання рядків результату запиту
        while (rs.next()) {
            result.append(rs.getString(1)); // Додаємо значення з поточного рядка до рядка результату
            if (!rs.isLast()) {
                result.append(", "); // Додаємо кому, якщо це не останній рядок
            }
        }

        return result.toString();
    }

    // Прості SELECT запити для відображення основних даних під час роботи з меню
    public String SelectParameterGroups() throws SQLException {
        String query = "SELECT group_name FROM parameter_groups";
        String[] columns = {"group_name"};
        return DoRequest(query, columns);
    }
    public String SelectParameters() throws SQLException {
        String query = "SELECT parameter_name FROM parameters";
        String[] columns = {"parameter_name"};
        return DoRequest(query, columns);
    }
    public String SelectProductGroups() throws SQLException {
        String query = "SELECT group_name FROM product_groups";
        String[] columns = {"group_name"};
        return DoRequest(query, columns);
    }

    // "Вивести перелік параметрів для заданої групи параметрів"
    public void Request1(String groupName) throws SQLException {
        String query =
                "SELECT DISTINCT p.parameter_name, m.unit_name " +
                        "FROM product_parameter_groups ppg " +
                        "JOIN parameters p ON ppg.parameter_group_id = p.parameter_group_id " +
                        "LEFT JOIN measurement_units m ON p.measurement_unit_id = m.unit_id " +
                        "WHERE ppg.parameter_group_id = (SELECT group_id FROM parameter_groups WHERE group_name = '" + groupName + "')";
        String[] columns = {"parameter_name", "unit_name"};
        DoRequest(query, columns);
    }

    // "Вивести перелік продукції, що не містить заданого параметра"
    public void Request2(String parameterName) throws SQLException {
        String query =
                "SELECT product_id, product_name\n" +
                        "FROM products\n" +
                        "WHERE product_id NOT IN (\n" +
                        "    SELECT DISTINCT pp.product_id\n" +
                        "    FROM product_parameters pp\n" +
                        "    JOIN parameters prm ON pp.parameter_id = prm.parameter_id\n" +
                        "    WHERE prm.parameter_name = '" + parameterName + "')";
        String[] columns = {"product_id", "product_name"};
        DoRequest(query, columns);
    }

    // "Вивести інформацію про продукцію для заданої групи"
    public void Request3(String groupName) throws SQLException {
        String query =
                "SELECT product_name, release_date, description\n" +
                        "FROM products p\n" +
                        "JOIN products.product_groups pg ON pg.group_id = p.product_group_id\n" +
                        "WHERE pg.group_name = '" + groupName + "'";
        String[] columns = {"product_name", "release_date", "description"};
        DoRequest(query, columns);
    }

    // "Вивести інформацію про продукцію та всі її параметри зі значеннями"
    public void Request4() throws SQLException {
        String query =
                "SELECT\n" +
                        "    pr.product_id,\n" +
                        "    pr.product_name,\n" +
                        "    pr.release_date,\n" +
                        "    pr.description,\n" +
                        "    pg.group_name AS product_group,\n" +
                        "    JSON_ARRAYAGG(\n" +
                        "        CONCAT('', pa.parameter_name, ': ', COALESCE(pp.parameter_value, ''), ' ', COALESCE(mu.unit_name, ''), '')\n" +
                        "    ) AS parameters_info\n" +
                        "FROM\n" +
                        "    products pr\n" +
                        "LEFT JOIN\n" +
                        "    product_parameters pp ON pr.product_id = pp.product_id\n" +
                        "LEFT JOIN\n" +
                        "    parameters pa ON pp.parameter_id = pa.parameter_id\n" +
                        "LEFT JOIN\n" +
                        "    measurement_units mu ON pa.measurement_unit_id = mu.unit_id\n" +
                        "JOIN\n" +
                        "    product_groups pg ON pr.product_group_id = pg.group_id\n" +
                        "GROUP BY\n" +
                        "    pr.product_id;";
        String[] columns = {"product_id", "product_name", "release_date", "description", "product_group", "parameters_info"};
        DoRequest(query, columns);
    }

    // "Видалити з бази продукцію, що містить задані параметри"
    public void DeleteRequest(String[] parameterNames) throws SQLException {
        // Перевірка наявності параметрів
        if (parameterNames.length == 0) {
            System.out.println("Не вказано жодного параметра для видалення продукції.");
            return;
        }
        // Створення пусто змінюваного рядка та додавання до нього параметрів з parameterNames
        StringBuilder parameterValues = new StringBuilder();
        int amount = 0;
        for (String parameterName : parameterNames) {
            parameterValues.append("'").append(parameterName).append("', ");
            amount++;
        }
        // Видалення останньої коми та пробілу
        parameterValues.delete(parameterValues.length() - 2, parameterValues.length());

        String query =
                    "DELETE FROM products\n" +
                    "WHERE product_id IN (\n" +
                    "    SELECT DISTINCT pp.product_id\n" +
                    "    FROM product_parameters pp\n" +
                    "    JOIN parameters p ON pp.parameter_id = p.parameter_id\n" +
                    "    WHERE p.parameter_name IN (" + parameterValues.toString() + ")\n" +
                    "    GROUP BY pp.product_id\n" +
                    "    HAVING COUNT(*) = " + amount + "\n" +
                    ");";
        DoRequest(query, null);
    }

    // "Перемістити групу параметрів з однієї групи товарів в іншу"
    public void UpdateRequest(String oldProductGroup, String newProductGroup, String parameterGroup) throws SQLException {
        String query =
                "UPDATE product_parameter_groups ppg\n" +
                        "SET ppg.product_group_id = (SELECT group_id FROM product_groups\n" +
                        "                                                WHERE group_name = '" + newProductGroup + "')\n" +
                        "WHERE ppg.product_group_id = (SELECT group_id FROM product_groups\n" +
                        "                                                WHERE group_name = '" + oldProductGroup + "')\n" +
                        "  AND ppg.parameter_group_id = (SELECT group_id FROM parameter_groups\n" +
                        "                                                WHERE group_name = '" + parameterGroup + "');";
        DoRequest(query, null);
    }


}
