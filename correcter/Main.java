package correcter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write a mode: ");
        String action = scanner.nextLine();
        switch (action) {
            case "encode":
                encode();
                break;
            case "send":
                send();
                break;
            case "decode":
                decode();
                break;
            default:
                break;
        }
    }

    public static void encode() throws Exception{
        byte[] bytes = Files.readAllBytes(Path.of("send.txt"));
        try(FileOutputStream fileOutputStream = new FileOutputStream("encoded.txt")) {
            for (byte b : bytes) {
                String s = "00000000" + Integer.toBinaryString(b);
                char[] initialByte = s.substring(s.length() - 8).toCharArray();
                StringBuilder encodedString = new StringBuilder();
                encodedString.append((initialByte[0] + initialByte[1] + initialByte[3]) % 2);
                encodedString.append((initialByte[0] + initialByte[2] + initialByte[3]) % 2);
                encodedString.append(initialByte[0]);
                encodedString.append((initialByte[1] + initialByte[2] + initialByte[3]) % 2);
                encodedString.append(initialByte[1]);
                encodedString.append(initialByte[2]);
                encodedString.append(initialByte[3]);
                encodedString.append('0');
                byte encodedByte = (byte)Integer.parseInt(encodedString.toString(), 2);
                fileOutputStream.write(encodedByte);

                encodedString = new StringBuilder();
                encodedString.append((initialByte[4] + initialByte[5] + initialByte[7]) % 2);
                encodedString.append((initialByte[4] + initialByte[6] + initialByte[7]) % 2);
                encodedString.append(initialByte[4]);
                encodedString.append((initialByte[5] + initialByte[6] + initialByte[7]) % 2);
                encodedString.append(initialByte[5]);
                encodedString.append(initialByte[6]);
                encodedString.append(initialByte[7]);
                encodedString.append('0');
                encodedByte = (byte)Integer.parseInt(encodedString.toString(), 2);
                fileOutputStream.write(encodedByte);
            }
        }
    }

    public static void send() throws Exception{
        byte[] bytes = Files.readAllBytes(Path.of("encoded.txt"));
        try (FileOutputStream fileOutputStream = new FileOutputStream("received.txt")) {
            for (int b : bytes) {
                if ((b & 1) == 1) {
                    b--;
                } else {
                    b++;
                }
                fileOutputStream.write((byte)b);
            }
        }
    }

    public static void decode() throws Exception{
        byte[] bytes = Files.readAllBytes(Path.of("received.txt"));
        try (FileOutputStream fileOutputStream = new FileOutputStream("decoded.txt")) {
            StringBuilder decodedByte = new StringBuilder();
            boolean halfByteWritten = false;
            for (byte b: bytes) {
                int[] receivedByte = new int[8];
                for (int i = 0; i < 8; i++) {
                    receivedByte[i] = (b >> 7 - i) & 1;
                }
                int errorIndex = 0;
                if (receivedByte[0] != (receivedByte[2] + receivedByte[4] + receivedByte[6]) % 2) {
                    errorIndex++;
                }
                if (receivedByte[1] != (receivedByte[2] + receivedByte[5] + receivedByte[6]) % 2) {
                    errorIndex += 2;
                }
                if (receivedByte[3] != (receivedByte[4] + receivedByte[5] + receivedByte[6]) % 2) {
                    errorIndex += 4;
                }
                errorIndex--;
                if (errorIndex != -1) {
                    if (receivedByte[errorIndex] == 1) {
                        receivedByte[errorIndex] = 0;
                    } else {
                        receivedByte[errorIndex] = 1;
                    }
                }
                decodedByte.append(receivedByte[2]);
                decodedByte.append(receivedByte[4]);
                decodedByte.append(receivedByte[5]);
                decodedByte.append(receivedByte[6]);
                if (halfByteWritten) {
                    fileOutputStream.write((byte)Integer.parseInt(decodedByte.toString(), 2));
                    halfByteWritten = false;
                    decodedByte = new StringBuilder();
                } else {
                    halfByteWritten = true;
                }
            }
        }
    }
}