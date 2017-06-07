package net.amarantha.gpiomofo.scenario;

public class ApiParam {

    private final String fieldName;
    private final String description;
    private final String value;

    public ApiParam(String fieldName, String description, String value) {
        this.fieldName = fieldName;
        this.description = description;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }
}
