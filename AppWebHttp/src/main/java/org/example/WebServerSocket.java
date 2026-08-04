package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebServerSocket {
    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(8080);
             Socket socket = server.accept();
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintStream out = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            String requestLine = in.readLine();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty())
                System.out.println(line);

            String[] requestLineElements = requestLine.split(" ");

            String body = "";
            if(requestLineElements[1].equals("/alunos")){
                body = """
                    <html>
                        <h2>Olá Aluno de HTTP!</h2>
                    </html>
                    """;
            } else if(requestLineElements[1].equals("/professores")){
                body = """
                    <html>
                        <h2>Olá Professor de HTTP!</h2>
                    </html>
                    """;
            } else {
                body = """
                    <html>
                        <h2>Não Encontrado!</h2>
                    </html>
                    """;
            }


            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: text/html; charset=utf-8\r\n");
            out.print("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n");
            out.print("Connection: close\r\n\r\n");
            out.print(body);
        }

    }
}
