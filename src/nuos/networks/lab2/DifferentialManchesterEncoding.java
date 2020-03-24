package nuos.networks.lab2;

public class DifferentialManchesterEncoding {
    private boolean potential = false; // напряжение 0 или 1

    public String encode(String inputString) {
        potential = false; // сбрасываем перед использованием
        int length = inputString.length();
        char[] outputString = new char[length*2]; //+-0

        for (int i = 0; i < length; i++) {

            if ( inputString.charAt(i) == '0' ) {
                drop(outputString, i*2); // перепад при передаче нуля
            } else {
                outputString[i*2] = '0'; // при передаче единицы потенциал не меняется
            }

            drop(outputString, (i*2)+1); // перепад на середине такта

        }

        return new String(outputString);
    }

    // перепад потенциала
    private void drop(char[] outputString, int i) {
        if (potential) {
            outputString[i] = '-';
        } else {
            outputString[i] = '+';
        }
        potential = !potential; // смена потенциала на противоположный
    }

}