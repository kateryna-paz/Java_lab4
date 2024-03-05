package database;

import java.util.*;
import java.sql.*;
import java.util.regex.*;
public class Menu {
    private static final int OPTION_EXIT = 0;
    private static final int OPTION_MIN = 0;
    private static final int OPTION_MAX = 7;
    private final Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$"); // Паттерн для перевірки формату РІК-МІСЯЦЬ-ДЕНЬ
    private String parameters;
    private String parameterGroups;
    private String productGroups;
    private final database.RequestExecution requestExecution;
    private final database.InsertRequest insertRequest;
    private final Scanner scanner;

    // Конструктор меню зі створенням потрібних об'єктів
    public Menu() {
        this.requestExecution = new database.RequestExecution();
        this.insertRequest = new database.InsertRequest();
        this.scanner = new Scanner(System.in);
    }

    // Метод відображення меню
    public void DisplayMenu() throws SQLException {
        while(true) {
            System.out.println("Меню:");
            System.out.println("1. Вивести перелік параметрів для заданої групи параметрів");
            System.out.println("2. Вивести перелік продукції, що не містить заданого параметра");
            System.out.println("3. Вивести інформацію про продукцію для заданої групи");
            System.out.println("4. Вивести інформацію про продукцію та всі її параметри зі значеннями");
            System.out.println("5. Видалити з бази продукцію, що містить задані параметри");
            System.out.println("6. Перемістити групу параметрів з однієї групи товарів в іншу");
            System.out.println("7. Додати новий товар з інформацією про його параметри");
            System.out.println("0. Вийти");

            System.out.println("\nОберіть цифру для здійснення відповідної дії");

            // Робимо запити до БД для отримання переліків параметрів, груп параметрів та груп продукції
            parameters = requestExecution.SelectParameters();
            parameterGroups = requestExecution.SelectParameterGroups();
            productGroups = requestExecution.SelectProductGroups();

            int option = GetOptionFromUser();

            switch (option) {
                case 1:
                    DoCase1();
                    break;
                case 2:
                    DoCase2();
                    break;
                case 3:
                    DoCase3();
                    break;
                case 4:
                    DoCase4();
                    break;
                case 5:
                    DoCase5();
                    break;
                case 6:
                    DoCase6();
                    break;
                case 7:
                    DoCase7();
                    break;
                case OPTION_EXIT:
                    System.out.println("Вихід.");
                    return;
                default:
                    System.out.println("Упс. Такої опції немає.");
                    break;
            }
            scanner.nextLine(); // Очікування натискання клавіші
        }
    }

    // Метод для отримання вибраної опції та перевірки введеного значення

    private int GetOptionFromUser() {
        int option;
        do {
            System.out.print("Введіть цифру від " + OPTION_MIN + " до " + OPTION_MAX + ": ");
            while (!scanner.hasNextInt()) {
                System.out.print("Будь ласка, введіть число: ");
                scanner.next();
            }
            option = scanner.nextInt();
        } while (option < OPTION_MIN || option > OPTION_MAX);
        scanner.nextLine(); // Очистити буфер після nextInt()
        return option;
    }

    // Метод для перевірки введеного рядка на наявність тільки літер (укр та англ), ком та пробілів
    public static boolean isValidInputString(String input) {
        // Порівнюємо рядок з регулярним виразом
        return input.matches("[\\s,а-яА-Яa-zA-ZґҐєЄіІїЇ]+");
    }

    protected void DoCase1() throws SQLException {
        // Виводимо повідомлення і дані
        System.out.println("Опція 1. Вивести перелік параметрів для заданої групи параметрів");
        System.out.println("Введіть одну з нижче запропонованих назву групи параметрів:");
        System.out.println(parameterGroups);
        // Читаємо введену назву
        String groupName = scanner.nextLine();

        // Перевірка, чи назва не порожня і відповідає одній із наявних груп
        if (groupName.trim().isEmpty()) {
            System.out.println("Назва групи параметрів не може бути порожньою.");
        } else if (!parameterGroups.contains(groupName)) {
            System.out.println("Назва групи параметрів невірна.");
        } else {
            // Якщо назва вірна, робимо запит до БД та виводимо результат
            System.out.println("\nПерелік параметрів для групи '" + groupName + "':");
            requestExecution.Request1(groupName);
        }
    }

    protected void DoCase2() throws SQLException {
        System.out.println("Опція 2. Вивести перелік продукції, що не містить заданого параметра");
        System.out.println("Введіть одну з нижче запропонованих назв параметру:");
        System.out.println(parameters);
        String parameterName = scanner.nextLine();

        // Перевірка, чи назва не порожня і відповідає одній із наявних назв параметру
        if (parameterName.trim().isEmpty()) {
            System.out.println("Назва параметру не може бути порожньою.");
        } else if (!parameters.contains(parameterName)) {
            System.out.println("Назва параметру невірна.");
        } else {
            System.out.println("\nПерелік продукції, що не містить параметера '" + parameterName + "':");
            requestExecution.Request2(parameterName);
        }
    }

    protected void DoCase3() throws SQLException {
        System.out.println("Опція 3. Вивести перелік продукції, що не містить заданого параметра");
        System.out.println("Введіть одну з нижче запропонованих назву групи продукції:");
        System.out.println(productGroups);
        String groupName = scanner.nextLine();

        // Перевірка, чи назва не порожня і відповідає одній із наявних груп
        if (groupName.trim().isEmpty()) {
            System.out.println("Назва групи продукції не може бути порожньою.");
        } else if (!productGroups.contains(groupName)) {
            System.out.println("Назва групи продукції невірна.");
        } else {
            System.out.println("\nІнформація про продукцію для групи '" + groupName + "':");
            requestExecution.Request3(groupName);
        }
    }

    protected void DoCase4() throws SQLException {
        // Робимо запит до БД та виводимо результат
        System.out.println("\nІнформація про продукцію та всі її параметри зі значеннями:");
        requestExecution.Request4();
    }

    protected void DoCase5() throws SQLException {
        System.out.println("Опція 5. Видалити з бази продукцію, що містить задані параметри");
        System.out.println("Введіть одну або декілька назв параметрів з нижче запропонованих, розділяючи їх комою та пробілом. (Приклад: Колір, Ширина, Висота):");
        System.out.println(parameters);
        String parameterNames = scanner.nextLine();

        // Перевірка, чи введено непорожню та правильні назви параметрів
        if (parameterNames.trim().isEmpty()) {
            System.out.println("Назва параметру не може бути порожньою.");
        } else if (!isValidInputString(parameterNames)) {
            System.out.println("Рядок з назвами параметрів може містити тільки літери, коми та пробіли.");
        } else {
            // Розділяємо регулярним виразом (будь-яка кількість пробілів - кома - будь-яка кількість пробів)
            String[] parameterNamesArray = parameterNames.split("\\s*,\\s*");
            requestExecution.DeleteRequest(parameterNamesArray);
        }
    }

    protected void DoCase6() throws SQLException {
        // Виводимо повідомлення, дані для вибору та реалізуємо введення
        System.out.println("Опція 6. Перемістити групу параметрів з однієї групи товарів в іншу");
        System.out.println("Введіть одну з нижче запропонованих назву групи параметрів:");
        System.out.println(parameterGroups);
        String parameterGroup = scanner.nextLine();
        System.out.println("Введіть одну з нижче запропонованих назву старої групи товарів, що потрібно змінити:");
        System.out.println(productGroups);
        String oldProductGroup = scanner.nextLine();
        System.out.println("Введіть одну з вище запропонованих назву групи товарів, на яку потрібно змінити:");
        String newProductGroup = scanner.nextLine();

        // Перевірка, чи введено непорожні назви груп та чи відповідаєть вони існуючим
        if (parameterGroup.trim().isEmpty() || oldProductGroup.trim().isEmpty() || newProductGroup.trim().isEmpty()) {
            System.out.println("Назви груп не можуть бути порожніми.");
        } else if (!parameterGroups.contains(parameterGroup) || !productGroups.contains(oldProductGroup) || !productGroups.contains(newProductGroup)) {
            System.out.println("Назви груп не відповідають існуючим.");
        } else {
            requestExecution.UpdateRequest(oldProductGroup, newProductGroup, parameterGroup);
        }
    }

    protected void DoCase7() {
        // Визначаємо поля для зберігання введених даних
        String name = null, productGroup = null, date = null, description = null;
        boolean flag = false;  // флаг для можливості повторення введення

        System.out.println("Опція 7. Додати новий товар з інформацією про його параметри");

        while (!flag) {
            System.out.println("Введіть назву товару:");
            name = scanner.nextLine();
            if (!name.trim().isEmpty()) {  // перевірка
                flag = true;
            } else {
                System.out.println("Назва не може бути порожньою, введіть коректну назву.");
            }
        }
        flag = false;

        while (!flag) {
            System.out.println("Введіть одну із запропонованих назву групи продукції, до якої належить товар:");
            System.out.println(productGroups);
            productGroup = scanner.nextLine();
            if (productGroups.contains(productGroup)) {  // перевірка
                flag = true;
            } else {
                System.out.println("Введдена назва групи не існує, оберіть з переліку.");
            }
        }
        flag = false;

        while (!flag) {
            System.out.println("Введіть дату випуску у форматі РІК-МІСЯЦЬ-ДЕНЬ. (Приклад, 2022-09-25):");
            date = scanner.nextLine();
            // Створюємо об'єкт типу Matcher, який буде порівнювати введений рядок із заданим регулярним виразом pattern
            Matcher matcher = pattern.matcher(date);
            if (matcher.matches()) {  // якщо рядок відповідає регулярному виразу
                flag = true;
            } else {
                System.out.println("Неправильний формат дати. Будь ласка, введіть у форматі РІК-МІСЯЦЬ-ДЕНЬ.");
            }
        }
        flag = false;

        while (!flag) {
            System.out.println("Введіть короткий опис товару:");
            description = scanner.nextLine();
            if (!description.trim().isEmpty()) {  // перевірка
                flag = true;
            } else {
                System.out.println("Опис не може бути порожнім, введіть коректні дані.");
            }
        }

        System.out.println("Введіть кількість параметрів, які ви хочите додати з переліку, що запропонований нижче:");
        System.out.println(parameters);
        int parameterCount = scanner.nextInt();
        scanner.nextLine(); // Очистити буфер після nextInt()

        ArrayList<database.ParameterValue> parameterList = new ArrayList<>();

        if (parameterCount > 0) {
            WriteParameters(parameterCount, parameterList);
        }

        if (name != null && productGroup != null && date != null && description != null) {
            insertRequest.AddProduct(name, productGroup, description, date, parameterList.toArray(new database.ParameterValue[0]));
            /* Останній аргумент створює новий масив типу ParameterValue з нульовою довжиною.
            Потім метод toArray() заповнює цей масив об'єктами зі списку parameterList і повертає його.
            */
        } else {
            System.out.println("Введенні дані не коректні, не вдалося додати новий продукт.");
        }
    }

    private void WriteParameters(int parameterCount, ArrayList<database.ParameterValue> parameterList) {
        for (int i = 0; i < parameterCount; i++) {
            String parameterName;
            do {
                System.out.println("Введіть назву параметру з запропонованих вище:");
                parameterName = scanner.nextLine();
                if (!parameters.contains(parameterName)) {
                    System.out.println("Введена назва параметру не існує, оберіть з переліку.");
                }
            } while (!parameters.contains(parameterName));

            System.out.println("Введіть значення параметру (без урахування одиниць виміру):");
            String parameterValue = scanner.nextLine();
            parameterList.add(new database.ParameterValue(parameterName, parameterValue));
        }
    }
}
