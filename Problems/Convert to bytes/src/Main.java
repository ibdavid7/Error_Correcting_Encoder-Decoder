import java.io.InputStream;

class Main {
    public static void main(String[] args) throws Exception {

        try (InputStream inputStream = System.in) {
            int charAsNumber = inputStream.read();
            while (charAsNumber != -1) {
                System.out.print(charAsNumber);
                charAsNumber = inputStream.read();
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}