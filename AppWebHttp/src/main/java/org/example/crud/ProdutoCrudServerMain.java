package org.example.crud;

import org.example.tier.controller.ProdutoController;
import org.example.tier.controller.dto.ProdutoResponseDTO;
import org.example.tier.model.Produto;
import org.example.tier.service.PedidoService;
import org.example.tier.service.ProdutoService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProdutoCrudServerMain {

    public static void main(String[] args) throws IOException {

        ProdutoService produtoService = new ProdutoService();
        PedidoService pedidoService =  new PedidoService(produtoService);
        ProdutoController produtoController = new ProdutoController(produtoService);

        produtoService.cadastrar(new Produto("Teclado", 150.00, 10));

        produtoService.cadastrar(
                new Produto("Mouse", 80.00, 20)
        );

        try (ServerSocket server = new ServerSocket(8080)) {

            System.out.println(
                    "API de produtos disponível em http://localhost:8080"
            );

            while (true) {
                try (
                        Socket socket = server.accept();
                        BufferedReader input = new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        );
                        PrintStream output = new PrintStream(
                                socket.getOutputStream(),
                                true,
                                StandardCharsets.UTF_8
                        )
                ) {

                    String requestLine = input.readLine();

                    if (requestLine == null || requestLine.isBlank()) {
                        continue;
                    }

                    System.out.println();
                    System.out.println("Request line: " + requestLine);

                    String[] requestParts =
                            requestLine.split("\\s+", 3);

                    if (requestParts.length != 3) {
                        enviarResposta(
                                output,
                                400,
                                "Bad Request",
                                """
                                {"erro":"Request line inválida"}
                                """
                        );
                        continue;
                    }

                    String method = requestParts[0];
                    String requestTarget = requestParts[1];

                    String path = requestTarget.split("\\?", 2)[0];

                    String httpVersion = requestParts[2];

                    Map<String, String> headers = new TreeMap<>(
                                    String.CASE_INSENSITIVE_ORDER
                            );

                    String headerLine;
                    while ((headerLine = input.readLine()) != null && !headerLine.isEmpty()) {

                        int separator = headerLine.indexOf(':');
                        if (separator > 0) {
                            String headerName = headerLine.substring(0, separator).trim();

                            String headerValue = headerLine.substring(separator + 1).trim();

                            headers.put(headerName, headerValue);
                        }
                    }

                    int contentLength = Integer.parseInt(
                            headers.getOrDefault(
                                    "Content-Length",
                                    "0"
                            )
                    );

                    String requestBody = lerBody(input, contentLength);

                    System.out.println("Método: " + method);
                    System.out.println("Caminho: " + path);
                    System.out.println("Versão HTTP: " + httpVersion);
                    System.out.println("Headers: " + headers);

                    if (requestBody.isEmpty()) {
                        System.out.println("Body: não enviado");
                    } else {
                        System.out.println("Body: " + requestBody);
                    }

                    int status = 200;
                    String reasonPhrase = "OK";
                    String responseBody = "";

                    if(path.equals("/produtos")) {
                        ProdutoResponseDTO handler = produtoController.handler(method, path, requestBody);
                        status = handler.getStatus();
                        reasonPhrase = handler.getReasonPhrase();
                        responseBody = handler.getResponseBody();
                    }

                    enviarResposta(
                            output,
                            status,
                            reasonPhrase,
                            responseBody
                    );

                } catch (Exception exception) {
                    System.err.println(
                            "Erro ao processar requisição: "
                                    + exception.getMessage()
                    );
                }
            }
        }
    }

    private static String lerBody(
            BufferedReader input,
            int contentLength
    ) throws IOException {

        if (contentLength <= 0) {
            return "";
        }

        char[] bodyChars =
                new char[contentLength];

        int totalLido = 0;

        while (totalLido < contentLength) {

            int quantidadeLida = input.read(
                    bodyChars,
                    totalLido,
                    contentLength - totalLido
            );

            if (quantidadeLida == -1) {
                break;
            }

            totalLido += quantidadeLida;
        }

        return new String(
                bodyChars,
                0,
                totalLido
        );
    }

    private static void enviarResposta(
            PrintStream output,
            int status,
            String reasonPhrase,
            String responseBody
    ) {

        byte[] responseBytes =
                responseBody.getBytes(
                        StandardCharsets.UTF_8
                );

        output.print(
                "HTTP/1.1 "
                        + status
                        + " "
                        + reasonPhrase
                        + "\r\n"
        );

        output.print(
                "Content-Type: application/json; charset=utf-8\r\n"
        );

        output.print(
                "Content-Length: "
                        + responseBytes.length
                        + "\r\n"
        );

        output.print("Connection: close\r\n");
        output.print("\r\n");

        output.write(
                responseBytes,
                0,
                responseBytes.length
        );

        output.flush();
    }
}