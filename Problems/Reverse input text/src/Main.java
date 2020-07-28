import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        List<Integer> buffer = new ArrayList<>();
        int currChar = reader.read();
        while (currChar != -1) {
            buffer.add(currChar);
            currChar = reader.read();
        }
        reader.close();
        Collections.reverse(buffer);
        buffer.stream()
                .forEach(ch -> System.out.printf("%c", ch));
    }
}