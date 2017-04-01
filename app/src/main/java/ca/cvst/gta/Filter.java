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

    public static Filter fromString(String filterString) {
        if (filterString.equals("")) {
            throw new IllegalArgumentException("filter String is null");
        }
        int operatorIndex = filterString.indexOf(Operation.EQ.getSymbol());
        if (operatorIndex != -1) {
            return new Filter(filterString.substring(0, operatorIndex), Operation.EQ, filterString.substring(operatorIndex + 1));
        }

        operatorIndex = filterString.indexOf(Operation.GT.getSymbol());
        if (operatorIndex != -1) {
            return new Filter(filterString.substring(0, operatorIndex), Operation.GT, filterString.substring(operatorIndex + 1));
        }

        operatorIndex = filterString.indexOf(Operation.LT.getSymbol());
        if (operatorIndex != -1) {
            return new Filter(filterString.substring(0, operatorIndex), Operation.LT, filterString.substring(operatorIndex + 1));
        }

        operatorIndex = filterString.indexOf(Operation.GTE.getSymbol());
        if (operatorIndex != -1) {
            return new Filter(filterString.substring(0, operatorIndex), Operation.GTE, filterString.substring(operatorIndex + 2));
        }

        operatorIndex = filterString.indexOf(Operation.LTE.getSymbol());
        if (operatorIndex != -1) {
            return new Filter(filterString.substring(0, operatorIndex), Operation.LTE, filterString.substring(operatorIndex + 2));
        }
        throw new IllegalStateException();
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

    @Override
    public String toString() {
        return fieldName + operation.getSymbol() + fieldValue;
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
