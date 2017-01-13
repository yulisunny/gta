package ca.cvst.gta;

public class Filter {

    private String fieldName;
    private Operation operation;
    private String fieldValue;
    public Filter(String fieldName, Operation operation, String fieldValue) {
        this.fieldName = fieldName;
        this.operation = operation;
        this.fieldValue = fieldValue;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Operation getOperation() {
        return operation;
    }

    public enum Operation {
        EQ("="),
        GT(">"),
        LT("<"),
        GTE(">="),
        LTE("<=");

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return this.symbol;
        }
    }

}
