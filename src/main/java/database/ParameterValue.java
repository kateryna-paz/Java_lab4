package database;

// клас об'єкту параметрів (продукції)
public class ParameterValue {
    private String name;
    private String value;

    public ParameterValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }

    public String getValue() {
        return value;
    }
}
