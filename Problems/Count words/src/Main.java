import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int currChar = reader.read();
        while (currChar != -1) {
            if (currChar == ' ' && sb.length() > 0) {
                list.add(sb.toString());
                sb.setLength(0);
            } else if (currChar == ' ' && sb.length() == 0) {
                currChar = reader.read();
                continue;
            } else {
                sb.append(currChar);
            }
            currChar = reader.read();
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        reader.close();
        System.out.println(list.size());
    }
}