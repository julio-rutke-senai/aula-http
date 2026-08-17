package org.example.videosocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {

    private static final int PORTA = 5000;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR ===");

        try (ServerSocket servidor = new ServerSocket(PORTA)) {

            System.out.println(
                    "Servidor aguardando conexão na porta " + PORTA + "..."
            );

            try (Socket cliente = servidor.accept();
                 BufferedReader entrada = new BufferedReader(
                         new InputStreamReader(cliente.getInputStream())
                 );
                 PrintWriter saida = new PrintWriter(
                         cliente.getOutputStream(),
                         true
                 )) {

                System.out.println();
                System.out.println(
                        "Cliente conectado: "
                                + cliente.getInetAddress().getHostAddress()
                                + ":"
                                + cliente.getPort()
                );

                saida.println("Conexão estabelecida com o servidor.");

                String mensagem;

                while ((mensagem = entrada.readLine()) != null) {
                    if (mensagem.equalsIgnoreCase("sair")) {
                        break;
                    }

                    System.out.println("Mensagem do cliente: " + mensagem);

                    saida.println(
                            "Servidor confirmou o recebimento: " + mensagem
                    );
                }

                System.out.println();
                System.out.println("Cliente desconectado.");
            }

        } catch (IOException exception) {
            System.err.println(
                    "Erro no servidor: " + exception.getMessage()
            );
        }
    }
}