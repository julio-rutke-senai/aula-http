package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class WebServerSocket {
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Servidor iniciado em http://localhost:8080");

            while (true) {
                try (Socket socket = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(
                             socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintStream out = new PrintStream(
                             socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

                    String requestLine = in.readLine();
                    if (requestLine == null || requestLine.isBlank()) {
                        continue;
                    }

                    String[] requestParts = requestLine.split("\\s+", 3);
                    if (requestParts.length != 3) {
                        enviarResposta(out, 400, "Bad Request",
                                "{\"erro\":\"Request line inválida\"}");
                        continue;
                    }

                    String method = requestParts[0];
                    String path = requestParts[1].split("\\?", 2)[0];

                    Map<String, String> headers =
                            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

                    String headerLine;
                    while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                        int separator = headerLine.indexOf(':');
                        if (separator > 0) {
                            String name = headerLine.substring(0, separator).trim();
                            String value = headerLine.substring(separator + 1).trim();
                            headers.put(name, value);
                        }
                    }

                    System.out.println("\n" + method + " " + path);
                    System.out.println("X-Aula-HTTP: "
                            + headers.getOrDefault("X-Aula-HTTP", "não enviado"));

                    if (method.equals("GET") && path.equals("/status")) {
                        enviarResposta(out, 200, "OK",
                                "{\"status\":\"online\"}");
                    } else if (path.equals("/status")) {
                        enviarResposta(out, 405, "Method Not Allowed",
                                "{\"erro\":\"Método não permitido\"}");
                    } else {
                        enviarResposta(out, 404, "Not Found",
                                "{\"erro\":\"Caminho não encontrado\"}");
                    }
                } catch (IOException exception) {
                    System.err.println("Erro ao processar cliente: " + exception.getMessage());
                }
            }
        }
    }

    private static void enviarResposta(
            PrintStream out,
            int status,
            String reasonPhrase,
            String body
    ) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        out.print("HTTP/1.1 " + status + " " + reasonPhrase + "\r\n");
        out.print("Content-Type: application/json; charset=utf-8\r\n");
        out.print("Content-Length: " + bodyBytes.length + "\r\n");
        out.print("Connection: close\r\n");
        out.print("\r\n");
        out.write(bodyBytes, 0, bodyBytes.length);
        out.flush();
    }
}
