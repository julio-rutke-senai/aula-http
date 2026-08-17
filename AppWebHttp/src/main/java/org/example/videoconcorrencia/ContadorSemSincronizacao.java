package org.example.videoconcorrencia;

import java.util.concurrent.CountDownLatch;

public class ContadorSemSincronizacao {

    private static volatile int contador = 0;

    private static final int QUANTIDADE_THREADS = 10;
    private static final int INCREMENTOS_POR_THREAD = 1_000;

    public static void main(String[] args)
            throws InterruptedException {

        Thread[] threads = new Thread[QUANTIDADE_THREADS];

        CountDownLatch inicioSimultaneo = new CountDownLatch(1);

        for (int i = 0; i < QUANTIDADE_THREADS; i++) {
            threads[i] = new Thread(() -> {
                aguardarInicio(inicioSimultaneo);

                for (int incremento = 0;
                     incremento < INCREMENTOS_POR_THREAD;
                     incremento++) {

                    contador++;
                }
            });

            threads[i].start();
        }

        inicioSimultaneo.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        int resultadoEsperado =
                QUANTIDADE_THREADS * INCREMENTOS_POR_THREAD;

        System.out.println("=== CONTADOR COMPARTILHADO ===");
        System.out.println("Quantidade de threads: "
                + QUANTIDADE_THREADS);
        System.out.println("Incrementos por thread: "
                + INCREMENTOS_POR_THREAD);
        System.out.println("Resultado esperado: "
                + resultadoEsperado);
        System.out.println("Resultado obtido:   "
                + contador);

        if (contador != resultadoEsperado) {
            System.out.println(
                    "Condição de corrida detectada!"
            );
        } else {
            System.out.println(
                    "Nesta execução, o resultado coincidiu com o esperado."
            );
        }
    }

    private static void aguardarInicio(
            CountDownLatch inicioSimultaneo
    ) {
        try {
            inicioSimultaneo.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
