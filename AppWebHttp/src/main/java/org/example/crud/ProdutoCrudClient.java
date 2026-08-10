package org.example.crud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProdutoCrudClient {

    private static final String HOST = "localhost";
    private static final int PORTA = 8080;

    public static void main(String[] args) throws IOException {

        executarTeste(
                "1. Listar todos os produtos",
                "GET",
                "/produtos",
                ""
        );

        executarTeste(
                "2. Consultar o produto 1",
                "GET",
                "/produtos/1",
                ""
        );

        executarTeste(
                "3. Cadastrar um produto",
                "POST",
                "/produtos",
                """
                {
                    "nome": "Monitor",
                    "preco": 1200.00,
                    "estoque": 5
                }
                """
        );

        executarTeste(
                "4. Consultar o produto cadastrado",
                "GET",
                "/produtos/3",
                ""
        );

        executarTeste(
                "5. Atualizar o produto 3",
                "PUT",
                "/produtos/3",
                """
                {
                    "nome": "Monitor Gamer",
                    "preco": 1500.00,
                    "estoque": 8
                }
                """
        );

        executarTeste(
                "6. Consultar o produto atualizado",
                "GET",
                "/produtos/3",
                ""
        );

        executarTeste(
                "7. Remover o produto 3",
                "DELETE",
                "/produtos/3",
                ""
        );

        executarTeste(
                "8. Consultar o produto removido",
                "GET",
                "/produtos/3",
                ""
        );

        executarTeste(
                "9. Testar um caminho desconhecido",
                "GET",
                "/categorias",
                ""
        );
    }

    private static void executarTeste(
            String descricao,
            String method,
            String path,
            String requestBody
    ) throws IOException {

        System.out.println();
        System.out.println("========================================");
        System.out.println(descricao);
        System.out.println("========================================");

        enviarRequisicao(
                method,
                path,
                requestBody
        );
    }

    private static void enviarRequisicao(
            String method,
            String path,
            String requestBody
    ) throws IOException {

        try (
                Socket socket = new Socket(HOST, PORTA);

                PrintStream output = new PrintStream(
                        socket.getOutputStream(),
                        true,
                        StandardCharsets.UTF_8
                );

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                )
        ) {

            byte[] requestBodyBytes =
                    requestBody.getBytes(StandardCharsets.UTF_8);

            output.print(
                    method
                            + " "
                            + path
                            + " HTTP/1.1\r\n"
            );

            output.print("Host: " + HOST + ":" + PORTA + "\r\n");

            output.print(
                    "Content-Type: application/json; charset=utf-8\r\n"
            );

            output.print(
                    "Content-Length: "
                            + requestBodyBytes.length
                            + "\r\n"
            );

            output.print(
                    "X-Aula-HTTP: cliente-java\r\n"
            );

            output.print("Connection: close\r\n");

            output.print("\r\n");

            if (!requestBody.isEmpty()) {
                output.print(requestBody);
            }

            output.flush();

            System.out.println("Requisição enviada:");
            System.out.println(
                    method
                            + " "
                            + path
                            + " HTTP/1.1"
            );

            if (!requestBody.isEmpty()) {
                System.out.println();
                System.out.println(requestBody);
            }

            System.out.println("Resposta recebida:");
            System.out.println();

            String responseLine;

            while ((responseLine = input.readLine()) != null) {
                System.out.println(responseLine);
            }
        }
    }
}
