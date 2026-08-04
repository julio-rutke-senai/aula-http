package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebClientSocket {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 8080);
             PrintStream out = new PrintStream(socket.getOutputStream(), true, UTF_8);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.print("GET /batata HTTP/1.1\r\n");
            out.print("Host: localhost\r\n");
            out.print("Accept: text/plain\r\n");
            out.print("Connection: close\r\n\r\n");
            String line;
            while ((line = in.readLine()) != null)
                System.out.println(line);
        }
    }

}
