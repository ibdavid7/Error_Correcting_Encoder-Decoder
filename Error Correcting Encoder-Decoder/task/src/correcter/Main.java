package correcter;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Main {
    //1. input stream from file.txt as byte[]
    private static byte[] readFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return bytes;
    }
    //2. Expand and parity to encode
    private static byte[] encodeHamming(byte[] bytes) {

        BitSet orig = BitSet.valueOf(bytes);
        //create expanded BitSet based on twice the length
        BitSet bitset = new BitSet(bytes.length * 8 * 2);    //lenght is twice since 4 data digits go in 1 Hamming code byte
        int bitsetIndex = 0;
        //Hamming code encoding
        for (int i = 0; i < bytes.length; i++) {
            //process 4 bits of original BitSet index 4 - 7 and then at index 0 - 3
            for (int j = 0; j < 2; j++) {
                //encode 4 bits
                if (orig.get(i * 8 + (1 - j) * 4 + 0)) bitset.set(i * 8 * 2 + j * 8 + 1); //1st bit to 2nd slot
                if (orig.get(i * 8 + (1 - j) * 4 + 1)) bitset.set(i * 8 * 2 + j * 8 + 2); //2nd bit to 3rd slot
                if (orig.get(i * 8 + (1 - j) * 4 + 2)) bitset.set(i * 8 * 2 + j * 8 + 3); //3rd bit to 4th slot
                if (orig.get(i * 8 + (1 - j) * 4 + 3)) bitset.set(i * 8 * 2 + j * 8 + 5); //4th bit to 6th slot
                // add parity
                if (bitset.get(i * 8 * 2 + j * 8 + 1) ^  bitset.get(i * 8 * 2 + j * 8 + 3) ^ bitset.get(i * 8 * 2 + j * 8 + 5)) bitset.set(i * 8 * 2 + j * 8 + 7); //parity at index 7
                if (bitset.get(i * 8 * 2 + j * 8 + 1) ^  bitset.get(i * 8 * 2 + j * 8 + 2) ^ bitset.get(i * 8 * 2 + j * 8 + 5)) bitset.set(i * 8 * 2 + j * 8 + 6); //parity at index 6
                if (bitset.get(i * 8 * 2 + j * 8 + 1) ^  bitset.get(i * 8 * 2 + j * 8 + 2) ^ bitset.get(i * 8 * 2 + j * 8 + 3)) bitset.set(i * 8 * 2 + j * 8 + 4); //parity at index 4
            }
        }

        //push bitset to byte array and reverse all bytes within the array
        byte[] result = bitset.toByteArray();
        return result;
    }

    //3. introduce random bit errors
    private static byte[] randomError(@NotNull byte[] bytes) {
        Random random = new Random(100);
        for (int i = 0; i < bytes.length; i++) {
            int j = random.nextInt(8);
            bytes[i] = (byte) (bytes[i] ^ 1 << j);
        }
        return bytes;
    }

    //4. decode received byte[] with errors
    private static byte[] decodeHamming(byte[] bytes) {

        //load reversed byte[] into BitSet
        BitSet bitSet = BitSet.valueOf(bytes);

        //find an error in the byte
        for (int i = 0; i < bytes.length; i++) {
            //check of errors
            BitSet bitWithError = new BitSet(3);
            if (bitSet.get(i * 8 + 7) ^ bitSet.get(i * 8 + 5) ^ bitSet.get(i * 8 + 3) ^ bitSet.get(i * 8 + 1)) bitWithError.set(0);
            if (bitSet.get(i * 8 + 6) ^ bitSet.get(i * 8 + 5) ^ bitSet.get(i * 8 + 2) ^ bitSet.get(i * 8 + 1)) bitWithError.set(1);
            if (bitSet.get(i * 8 + 4) ^ bitSet.get(i * 8 + 3) ^ bitSet.get(i * 8 + 2) ^ bitSet.get(i * 8 + 1)) bitWithError.set(2);
            //fix error if found
            int bitWithErrorIndex = (int) (8 - convert(bitWithError));
            if (bitWithErrorIndex >= 0) {
                if (bitSet.get(i * 8 + bitWithErrorIndex)) {
                    bitSet.clear(i * 8 + bitWithErrorIndex);
                } else {
                    bitSet.set(i * 8 + bitWithErrorIndex);
                }

            }
        }


        //reduce original BitSet by half
        BitSet resultBitSet = new BitSet(bytes.length * 8 / 2); //half the size of the encoded BitSet
        for (int i = 0; i < bytes.length / 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (bitSet.get(i * 8 * 2 + j * 8 + 1)) resultBitSet.set(i * 8 + (1 - j) * 4 + 0);
                if (bitSet.get(i * 8 * 2 + j * 8 + 2)) resultBitSet.set(i * 8 + (1 - j) * 4 + 1);
                if (bitSet.get(i * 8 * 2 + j * 8 + 3)) resultBitSet.set(i * 8 + (1 - j) * 4 + 2);
                if (bitSet.get(i * 8 * 2 + j * 8 + 5)) resultBitSet.set(i * 8 + (1 - j) * 4 + 3);
            }
        }

        //push bitset to byte array and reverse all bytes within the array
        byte[] resultByteArray = resultBitSet.toByteArray();

        return resultByteArray;
    }

    public static long convert(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    //5. stream out to external file
    private static void fileOutput(String path, byte[] bytes) {
        try (FileOutputStream outputStream = new FileOutputStream(path, false)) {
            for (byte b : bytes) {
                outputStream.write(b);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void main(String[] args) throws IOException {

        String inputFilePath = ".\\send.txt";
        String encodedFilePath = ".\\encoded.txt";
        String receivedFilePath = ".\\received.txt";
        String decodedFilePath = ".\\decoded.txt";

        //1. Encode: Input stream from file.txt; Encode and save results to encoded.txt
        byte[] bytes = readFile(inputFilePath);
        bytes = encodeHamming(bytes);
        fileOutput(encodedFilePath, bytes);
        //2. Send: Input stream from encoded.txt; Introduce random error and save results to received.txt
        bytes = readFile(encodedFilePath);
        bytes = randomError(bytes);
        fileOutput(receivedFilePath, bytes);
        //3. Decode: Input stream from received.txt; Decode and save results to decoded
        bytes = readFile(receivedFilePath);
        bytes = decodeHamming(bytes);
        fileOutput(decodedFilePath, bytes);
    }
}
