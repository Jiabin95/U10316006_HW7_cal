import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Calcula extends Application {
    static Operator currentOperator;
    static boolean operatorSelected;
    static boolean resultDisplayed;

	//main method
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
		//construct a new layout
        BorderPane layout = new BorderPane();

		//set the text-field
        TextField auxiliary = new TextField();
        auxiliary.setStyle("-fx-font-size: 15; -fx-text-fill: gray");
        auxiliary.setMaxWidth(415); // 415 = total width, including margins of buttons
        auxiliary.setEditable(false);

		//set the result-text-field
        TextField result = new TextField();
        result.setStyle("-fx-font-size: 40");
        result.setMaxWidth(415); // 415 = total width, including margins of buttons
        result.setEditable(false);

        VBox resultLayout = new VBox();
		//add resultLayout to gridpane
        resultLayout.getChildren().addAll(auxiliary, result);
        layout.setTop(resultLayout);

		//construct a buttonLayout
        GridPane buttonLayout = new GridPane();
        buttonLayout.setPadding(new Insets(10, 0, 0, 0));
        buttonLayout.setHgap(5);//The width of the horizontal gaps between columns
        buttonLayout.setVgap(5);//The height of the vertical gaps between rows.
        layout.setCenter(buttonLayout);

		//delete the number 
        Button backButton = new Button("\u2190");
        backButton.setMinSize(100, 100);
        backButton.setOnAction(e -> {
            String currentText = result.getText();
            if (!currentText.isEmpty() && !resultDisplayed){
                result.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        buttonLayout.add(backButton, 2, 0);

		//clear all the number in the text-field
        Button clearButton = new Button("C");
        clearButton.setMinSize(200, 100);
        clearButton.setOnAction(e -> {
            result.clear();
            auxiliary.clear();
            operatorSelected = false;
        });
        GridPane.setColumnSpan(clearButton, 2); 
        buttonLayout.add(clearButton, 0, 0);

		//use array do the number[0-9]
        Button[] numberButtons = new Button[10];
        for (int i = 3, target = 1; i >= 1; i--) {
            for (int j = 0; j <= 2; j++) {
                String number = Integer.toString(target);

                numberButtons[target] = new Button(number);
                numberButtons[target].setMinSize(100, 100);
                numberButtons[target].setOnAction(e -> {
                    if (resultDisplayed) {
                        result.setText(number);
                        resultDisplayed = false;
                    } else {
                        result.appendText(number);
                    }

                    operatorSelected = false;
                });
                buttonLayout.add(numberButtons[target++], j, i);
            }   
        }
		
		//construct 0 number
        numberButtons[0] = new Button("0");
        numberButtons[0].setMinSize(200, 100);
        numberButtons[0].setOnAction(e -> {
            if (!result.getText().isEmpty() && !resultDisplayed) {
                result.appendText("0");
                operatorSelected = false;
            }
        });
		//set 0 number in the column
        GridPane.setColumnSpan(numberButtons[0], 2);
        buttonLayout.add(numberButtons[0], 0, 4);

		//construct "." button
        Button decimalButton = new Button(".");
        decimalButton.setMinSize(100, 100);
        decimalButton.setOnAction(e -> {
            if (result.getText().indexOf('.') == -1) {
                result.appendText(".");
            }
        });
        buttonLayout.add(decimalButton, 2, 4);

        for (Operator op : Operator.values()) {
            String symbol = op.toString();
			
            Button button = new Button(symbol);
            button.setMinSize(100, 100);
            button.setStyle("-fx-color: orange");
            button.setOnAction(e -> {
                if (auxiliary.getText().isEmpty()) {
                    auxiliary.setText(result.getText().isEmpty() ? "0" : acquireValue(result.getText()));
                    auxiliary.appendText(" " + symbol);
                    currentOperator = op;
                    resultDisplayed = true;
                    operatorSelected = true;
                } else if (operatorSelected) {
                    currentOperator = op;
                    int end = auxiliary.getText().length();
                    auxiliary.replaceText(end - 1, end,  symbol);
                } else {
                    auxiliary.setText(calculate(currentOperator, result, auxiliary) + " " + symbol);
                    result.clear();
                    currentOperator = op;
                    resultDisplayed = true;
                    operatorSelected = true;
                }
            });
            buttonLayout.addColumn(3, button);
        }
		//do "=" action
        Button equalsButton = new Button("=");
        equalsButton.setStyle("-fx-color: blue");
        equalsButton.setMinSize(100, 100);
        equalsButton.setOnAction(e -> {
			//cannot be empty
            if (!auxiliary.getText().isEmpty()) {
                result.setText(
                    calculate(currentOperator, result, auxiliary)
                );
                resultDisplayed = true;
                operatorSelected = false;
                auxiliary.clear();
            }
        });
        buttonLayout.addColumn(3, equalsButton);
        equalsButton.setDefaultButton(true);
		
		//Built-in Layout Panes
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("Calcula.css");//add css sheet
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Calculator");
        stage.show();
    }

	//calculate two number	
	//string change to double
    private static String calculate(Operator op, TextField main, TextField auxiliary) {
        double val1 = Double.parseDouble(auxiliary.getText().replaceAll("[^\\.0-9]", ""));
        double val2 = Double.parseDouble(main.getText());

		//if number is 0 cannot divide	
        if (val2 == 0 && op == Operator.DIVIDE) {
            return "Cannot divide by 0";
        }

		//print val1 and val2 on text-field
        double result = op.compute(val1, val2);
        return toCalculatorString(result);
    }

    private static String acquireValue(String val) {
        double result = Double.parseDouble(val);
        return toCalculatorString(result);
    }

	//a replace function target to replacement
    private static String removeDecimalTrailingZeroes(String s) {
        return s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
    }
	//Count the point "to.6f"
    private static String toCalculatorString(double input) {
        return input == (int)input ? 
            Integer.toString((int)input) : removeDecimalTrailingZeroes(String.format("%.6f", input));
    }
}
