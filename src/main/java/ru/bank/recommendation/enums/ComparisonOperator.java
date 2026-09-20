package ru.bank.recommendation.enums;

public enum ComparisonOperator {
    GT(">"), LT("<"), EQ("="), GTE(">="), LTE("<=");

    private final String symbol;

    ComparisonOperator(String symbol) { this.symbol = symbol; }

    public static ComparisonOperator fromSymbol(String s) {
        return switch (s) {
            case ">" -> GT;
            case "<" -> LT;
            case "=" -> EQ;
            case ">=" -> GTE;
            case "<=" -> LTE;
            default -> throw new IllegalArgumentException("Unknown operator: " + s); }; }

    public boolean compareQuantities(int left, int right) {
        return switch (this) {
                case GT -> left > right;
                case LT -> left < right;
                case EQ -> Double.compare(left, right) == 0;
                case GTE -> left >= right;
                case LTE -> left <= right; };
    }
}
