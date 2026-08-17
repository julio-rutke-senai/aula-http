package org.example.videothread;

public class ExecucaoSequencial {

    public static void main(String[] args) {
        System.out.println("=== EXECUÇÃO SEQUENCIAL ===");
        System.out.println();

        long inicio = System.nanoTime();

        executarTarefa("Tarefa 1");
        executarTarefa("Tarefa 2");
        executarTarefa("Tarefa 3");

        long fim = System.nanoTime();
        double tempoTotal = (fim - inicio) / 1_000_000_000.0;

        System.out.println();
        System.out.printf(
                "Todas as tarefas foram finalizadas em %.2f segundos.%n",
                tempoTotal
        );
    }

    private static void executarTarefa(String nome) {
        System.out.println(nome + " iniciada.");

        try {
            for (int etapa = 1; etapa <= 4; etapa++) {
                Thread.sleep(500);

                System.out.println(
                        nome + " executando etapa " + etapa + " de 4."
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            System.out.println(nome + " foi interrompida.");
            return;
        }

        System.out.println(nome + " finalizada.");
        System.out.println();
    }
}
