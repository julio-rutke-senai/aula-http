package org.example.videosocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteSocket {

    private static final String ENDERECO_SERVIDOR = "localhost";
    private static final int PORTA = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE ===");

        try (Socket socket = new Socket(ENDERECO_SERVIDOR, PORTA);
             BufferedReader entradaServidor = new BufferedReader(
                     new InputStreamReader(socket.getInputStream())
             );
             PrintWriter saidaServidor = new PrintWriter(
                     socket.getOutputStream(),
                     true
             );
             BufferedReader teclado = new BufferedReader(
                     new InputStreamReader(System.in)
             )) {

            System.out.println(
                    "Conectado ao servidor "
                            + ENDERECO_SERVIDOR
                            + ":"
                            + PORTA
            );

            System.out.println(
                    "Servidor: " + entradaServidor.readLine()
            );

            System.out.println();
            System.out.println(
                    "Digite uma mensagem ou utilize \"sair\" para encerrar."
            );

            String mensagem;

            while (true) {
                System.out.print("Cliente: ");
                mensagem = teclado.readLine();

                if (mensagem == null) {
                    break;
                }

                saidaServidor.println(mensagem);

                if (mensagem.equalsIgnoreCase("sair")) {
                    break;
                }

                String resposta = entradaServidor.readLine();
                System.out.println(resposta);
            }

            System.out.println();
            System.out.println("Conexão encerrada pelo cliente.");

        } catch (IOException exception) {
            System.err.println(
                    "Não foi possível conectar ao servidor: "
                            + exception.getMessage()
            );
        }
    }
}