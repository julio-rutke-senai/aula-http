package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

public class ApiSincrona {

    public static void main(String[] args) throws IOException {
        HttpServer servidor = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        servidor.createContext("/api/saldo", ApiSincrona::consultarSaldo);

        servidor.setExecutor(
                Executors.newVirtualThreadPerTaskExecutor()
        );

        servidor.start();

        System.out.println("API iniciada em http://localhost:8080");
        System.out.println("Endpoint disponível: GET /api/saldo");
    }

    private static void consultarSaldo(HttpExchange requisicao)
            throws IOException {

        System.out.println();
        System.out.println("Requisição recebida em: " + LocalDateTime.now());
        System.out.println("Consultando o saldo...");

        try {
            Thread.sleep(6_000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        String resposta = """
                {
                  "cliente": "João da Silva",
                  "saldo": 3250.75,
                  "status": "consulta realizada"
                }
                """;

        byte[] conteudo = resposta.getBytes(StandardCharsets.UTF_8);

        requisicao.getResponseHeaders().add(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        requisicao.sendResponseHeaders(200, conteudo.length);

        try (var corpoResposta = requisicao.getResponseBody()) {
            corpoResposta.write(conteudo);
        }

        System.out.println("Resposta enviada em: " + LocalDateTime.now());
    }
}