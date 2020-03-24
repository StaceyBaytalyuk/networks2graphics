package nuos.networks.lab2;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Controller {
    @FXML private TextField textField;
    @FXML private Text decimalText;
    @FXML private Text encodingText;
    @FXML private Canvas canvas;
    private GraphicsContext gc;
    private DifferentialManchesterEncoding dme = new DifferentialManchesterEncoding();
    private double smallStrokeWidth = 2.0;
    private double bigStrokeWidth = 4.0;
    private int maxHeight = 200;
    private int maxWidth = 800;
    private int steps;
    private int stepLength;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
    }

    public void onOk() {
        // заливка чтобы очистить предыдущий график
        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        String inputString = textField.getText();
        // если в строке меньше 8 символов - заполняем нулями слева
        if ( inputString.length() < 8 ) {
            String zero = "0";
            inputString = zero.repeat(8-inputString.length()) + inputString;
            textField.setText(inputString); // обновляем текстовое поле
        }

        if ( checkInput(inputString) ) {
            int decimal=Integer.parseInt(inputString,2);
            decimalText.setText("Десяткове число: " + decimal);
            String outputString = dme.encode(inputString);
            encodingText.setText("Кодування: "+outputString);
            drawGraph(outputString);
            drawTactBounds();
        } else {
            decimalText.setText("");
            encodingText.setText("");
            showAlert("Неправильно введені дані", "Рядок має містити 8 символів \"0\" або \"1\"");
        }
    }

    // границы между тактами
    private void drawTactBounds() {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(smallStrokeWidth);
        for (int i = 0; i < steps; i++) {
            int X = (i*stepLength);
            gc.strokeLine(X, 1, X, stepLength);
        }
    }

    private void drawGraph(String outputString) {
        int halfSteps = outputString.length(); // кол-во +-0
        steps = halfSteps/2; // кол-во 01
        stepLength = stepLength(steps); // длина полоски одного такта
        int halfStepLength = stepLength(halfSteps); // длина полоски одного полутакта

        // настроить размер холста
        canvas.setHeight(stepLength);
        canvas.setWidth( (stepLength)*steps );

        // настройки кисти
        gc.setStroke(Color.RED);
        gc.setLineWidth(bigStrokeWidth);

        int x, y;
        x=1; y=stepLength; // поставили перо в левый нижний угол холста
        gc.beginPath();
        gc.lineTo(x, y);

        for (int i = 0; i < halfSteps; i++) {
            // вертикальная линия
            if ( outputString.charAt(i) == '+' ) {
                y -= stepLength;
                gc.lineTo(x, y);
            } else if ( outputString.charAt(i) == '-' ) {
                y += stepLength;
                gc.lineTo(x, y);
            } // else if 0 - Y остаётся прежним

            // горизонтальная линия
            x += halfStepLength;
            gc.lineTo(x, y);
        }
        gc.stroke();
    }

    private int stepLength(int steps) {
        int max = maxWidth / steps;
        return Math.min(max, maxHeight);
    }

    private boolean checkInput(String inputString) {
        int length = inputString.length();
        if ( length == 0 ) return false;
        if ( length > 8 ) return false;

        for (int i = 0; i < length; i++) {
            if ( (inputString.charAt(i) != '0') && (inputString.charAt(i) != '1') ) { // найден посторонний символ (не 0 и не 1)
                return false;
            }
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}