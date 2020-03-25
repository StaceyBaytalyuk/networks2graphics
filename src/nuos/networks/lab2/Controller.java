package nuos.networks.lab2;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Controller {
    @FXML private TextField binaryTextField;
    @FXML private Text decimalText;
    @FXML private Text encodingText;
    @FXML private Canvas canvas;
    private GraphicsContext gc;
    private DifferentialManchesterEncoding dme = new DifferentialManchesterEncoding();

    // константы
    private int tactLength;
    private static final int tacts = 8;
    private static final int maxHeight = 200;
    private static final int maxWidth = 800;
    private static final double boundStrokeWidth = 2.0;
    private static final double graphStrokeWidth = 4.0;
    private static final Color boundColor = Color.BLACK;
    private static final Color graphColor = Color.RED;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
    }

    public void onOk() {
        // заливка чтобы очистить предыдущий график
        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        String inputString = binaryTextField.getText();
        // если в строке меньше 8 символов - заполняем нулями слева
        if ( inputString.length() < 8 ) {
            String zero = "0";
            inputString = zero.repeat(8-inputString.length()) + inputString;
            binaryTextField.setText(inputString); // обновляем текстовое поле
        }

        if ( checkInput(inputString) ) {
            int decimal = Integer.parseInt(inputString,2);
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
        gc.setStroke(boundColor);
        gc.setLineWidth(boundStrokeWidth);
        for (int i = 0; i < tacts; i++) {
            int X = (i* tactLength);
            gc.strokeLine(X, 1, X, tactLength);
        }
    }

    private void drawGraph(String outputString) {
        int halfTacts = tacts*2;
        int halfTactLength = tactLength(halfTacts); // экранная длина одного полутакта
        tactLength = tactLength(tacts); // экранная длина одного такта

        // настроить размер холста
        canvas.setHeight(tactLength);
        canvas.setWidth(tacts*tactLength);

        // настройки кисти
        gc.setStroke(graphColor);
        gc.setLineWidth(graphStrokeWidth);

        // поставили перо в левый нижний угол холста
        int x=1, y=tactLength;
        gc.beginPath();
        gc.lineTo(x, y);

        for (int i = 0; i < halfTacts; i++) {
            // вертикальная линия
            if ( outputString.charAt(i) == '+' ) {
                y -= tactLength;
                gc.lineTo(x, y);
            } else if ( outputString.charAt(i) == '-' ) {
                y += tactLength;
                gc.lineTo(x, y);
            } // else if 0 - Y остаётся прежним

            // горизонтальная линия
            x += halfTactLength;
            gc.lineTo(x, y);
        }
        gc.stroke();
    }

    private int tactLength(int steps) {
        int max = maxWidth / steps;
        return Math.min(max, maxHeight);
    }

    // проверка правильности введённых данных
    // не допускается строка свыше 8 символов, а также содержащая символы кроме 0 и 1
    private boolean checkInput(String inputString) {
        int length = inputString.length();
        if ( length > tacts ) return false;
        for (int i = 0; i < tacts; i++) {
            if ( (inputString.charAt(i) != '0') && (inputString.charAt(i) != '1') ) { // найден посторонний символ
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