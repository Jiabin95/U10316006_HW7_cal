import java.util.function.BinaryOperator;

public enum Operator {
	//calculate +,-,x,/
    DIVIDE("\u00F7", (x, y) -> x / y),
    MULTIPLY("x", (x, y) -> x * y),
    SUBTRACT("-", (x, y) -> x - y),
    ADD("+", (x, y) -> x + y);

	//final symbol & equation
    private final String symbol;
    private final BinaryOperator<Double> equation;

    
    Operator(String symbol, BinaryOperator<Double> equation) {
        this.symbol = symbol;
        this.equation = equation;
    }
    //Constructor java api
    public double compute(double alpha, double beta) {
        return equation.apply(alpha, beta);
    }

    @Override
	//return symbol
    public String toString() {
        return symbol;
    }
}
