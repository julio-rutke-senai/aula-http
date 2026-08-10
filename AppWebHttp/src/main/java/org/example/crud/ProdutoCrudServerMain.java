package org.example.crud;

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
        record Produto(
                long id,
                String nome,
                double preco,
                int estoque
        ) {
            String toJson() {
                return String.format(
                        Locale.US,
                        """
                        {"id":%d,"nome":"%s","preco":%.2f,"estoque":%d}
                        """,
                        id,
                        nome,
                        preco,
                        estoque
                ).trim();
            }
        }

        List<Produto> produtos = new ArrayList<>();

        produtos.add(
                new Produto(1, "Teclado", 150.00, 10)
        );

        produtos.add(
                new Produto(2, "Mouse", 80.00, 20)
        );

        long proximoId = 3;

        Pattern nomePattern = Pattern.compile(
                "\"nome\"\\s*:\\s*\"([^\"]+)\""
        );

        Pattern precoPattern = Pattern.compile(
                "\"preco\"\\s*:\\s*(\\d+(?:\\.\\d+)?)"
        );

        Pattern estoquePattern = Pattern.compile(
                "\"estoque\"\\s*:\\s*(\\d+)"
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
                    String responseBody;

                    if (method.equals("GET") && path.equals("/produtos")) {

                        responseBody = produtos.stream()
                                .map(Produto::toJson)
                                .reduce(
                                        (left, right) ->
                                                left + "," + right
                                )
                                .map(json -> "[" + json + "]")
                                .orElse("[]");

                    } else if (method.equals("GET") && path.matches("/produtos/\\d+")) {
                        long id = Long.parseLong(
                                path.substring(
                                        "/produtos/".length()
                                )
                        );

                        Produto encontrado = produtos.stream()
                                .filter(
                                        produto ->
                                                produto.id() == id
                                )
                                .findFirst()
                                .orElse(null);

                        if (encontrado == null) {
                            status = 404;
                            reasonPhrase = "Not Found";

                            responseBody =
                                    """
                                    {"erro":"Produto não encontrado"}
                                    """.trim();

                        } else {
                            responseBody =
                                    encontrado.toJson();
                        }

                    } else if (method.equals("POST") && path.equals("/produtos")) {

                        Matcher nomeMatcher =
                                nomePattern.matcher(requestBody);

                        Matcher precoMatcher =
                                precoPattern.matcher(requestBody);

                        Matcher estoqueMatcher =
                                estoquePattern.matcher(requestBody);

                        boolean possuiNome =
                                nomeMatcher.find();

                        boolean possuiPreco =
                                precoMatcher.find();

                        boolean possuiEstoque =
                                estoqueMatcher.find();

                        if (!possuiNome || !possuiPreco || !possuiEstoque) {

                            status = 400;
                            reasonPhrase = "Bad Request";

                            responseBody =
                                    """
                                    {"erro":"Informe nome, preco e estoque"}
                                    """.trim();
                        } else {
                            Produto novoProduto =
                                    new Produto(
                                            proximoId++,
                                            nomeMatcher.group(1),
                                            Double.parseDouble(
                                                    precoMatcher.group(1)
                                            ),
                                            Integer.parseInt(
                                                    estoqueMatcher.group(1)
                                            )
                                    );

                            produtos.add(novoProduto);

                            status = 201;
                            reasonPhrase = "Created";
                            responseBody =
                                    novoProduto.toJson();
                        }

                    } else if (
                            method.equals("PUT")
                                    && path.matches("/produtos/\\d+")
                    ) {
                        long id = Long.parseLong(
                                path.substring(
                                        "/produtos/".length()
                                )
                        );

                        int index = -1;

                        for (
                                int i = 0;
                                i < produtos.size();
                                i++
                        ) {

                            if (produtos.get(i).id() == id) {
                                index = i;
                                break;
                            }
                        }

                        Matcher nomeMatcher =
                                nomePattern.matcher(requestBody);

                        Matcher precoMatcher =
                                precoPattern.matcher(requestBody);

                        Matcher estoqueMatcher =
                                estoquePattern.matcher(requestBody);

                        boolean possuiNome =
                                nomeMatcher.find();

                        boolean possuiPreco =
                                precoMatcher.find();

                        boolean possuiEstoque =
                                estoqueMatcher.find();

                        if (index < 0) {

                            status = 404;
                            reasonPhrase = "Not Found";

                            responseBody =
                                    """
                                    {"erro":"Produto não encontrado"}
                                    """.trim();

                        } else if (!possuiNome || !possuiPreco || !possuiEstoque) {

                            status = 400;
                            reasonPhrase = "Bad Request";

                            responseBody =
                                    """
                                    {"erro":"Informe nome, preco e estoque"}
                                    """.trim();

                        } else {

                            Produto atualizado =
                                    new Produto(
                                            id,
                                            nomeMatcher.group(1),
                                            Double.parseDouble(
                                                    precoMatcher.group(1)
                                            ),
                                            Integer.parseInt(
                                                    estoqueMatcher.group(1)
                                            )
                                    );

                            produtos.set(index, atualizado);

                            responseBody =
                                    atualizado.toJson();
                        }

                    } else if (method.equals("DELETE") && path.matches("/produtos/\\d+")) {

                        long id = Long.parseLong(
                                path.substring(
                                        "/produtos/".length()
                                )
                        );

                        boolean removido = produtos.removeIf(
                                produto -> produto.id() == id
                        );

                        if (removido) {

                            status = 204;
                            reasonPhrase = "No Content";
                            responseBody = "";

                        } else {

                            status = 404;
                            reasonPhrase = "Not Found";

                            responseBody =
                                    """
                                    {"erro":"Produto não encontrado"}
                                    """.trim();
                        }

                    } else if (
                            path.equals("/produtos") || path.startsWith("/produtos/")
                    ) {

                        status = 405;
                        reasonPhrase =
                                "Method Not Allowed";

                        responseBody =
                                """
                                {"erro":"Método não permitido"}
                                """.trim();

                    } else {

                        status = 404;
                        reasonPhrase = "Not Found";

                        responseBody =
                                """
                                {"erro":"Caminho não encontrado"}
                                """.trim();
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