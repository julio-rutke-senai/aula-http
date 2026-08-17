package org.example.videothread;

public class ExecucaoMultithread {

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println("=== EXECUÇÃO COM MÚLTIPLAS THREADS ===");
        System.out.println();

        Thread thread1 = new Thread(
                () -> executarTarefa("Tarefa 1"),
                "thread-tarefa-1"
        );

        Thread thread2 = new Thread(
                () -> executarTarefa("Tarefa 2"),
                "thread-tarefa-2"
        );

        Thread thread3 = new Thread(
                () -> executarTarefa("Tarefa 3"),
                "thread-tarefa-3"
        );

        long inicio = System.nanoTime();

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        long fim = System.nanoTime();
        double tempoTotal = (fim - inicio) / 1_000_000_000.0;

        System.out.println();
        System.out.printf(
                "Todas as tarefas foram finalizadas em %.2f segundos.%n",
                tempoTotal
        );
    }

    private static void executarTarefa(String nome) {
        String nomeThread = Thread.currentThread().getName();

        System.out.println(
                nome + " iniciada por " + nomeThread + "."
        );

        try {
            for (int etapa = 1; etapa <= 4; etapa++) {
                Thread.sleep(500);

                System.out.println(
                        "[" + nomeThread + "] "
                                + nome
                                + " executando etapa "
                                + etapa
                                + " de 4."
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            System.out.println(nome + " foi interrompida.");
            return;
        }

        System.out.println(
                nome + " finalizada por " + nomeThread + "."
        );
    }
}