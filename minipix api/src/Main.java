import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // guarda quem mandou e quanto (em centavos)
    record Transacao(String de, long valor) {}

    static class Conta {
        String dono;
        String tipoDeconta;
        String senha; // null = ainda não criou senha
        long saldoCentavos; // dinheiro em centavos (long), pra não ter erro de arredondamento
        List<Transacao> recebidos = new ArrayList<>(); // cada conta tem a SUA lista

        Conta(String dono, String tipoDeconta, long saldoCentavos) {
            this.dono = dono;
            this.tipoDeconta = tipoDeconta;
            this.saldoCentavos = saldoCentavos;
        }
    }

    // nome -> conta (pra "entrar" na conta)
    static Map<String, Conta> contas = new HashMap<>();

    // chave Pix -> conta
    static Map<String, Conta> chaves = new HashMap<>();

    // ---------- VISUAL ----------

    static void pausa(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // não precisa fazer nada
        }
    }

    static void processando() {
        System.out.print("\nProcessando");
        for (int i = 0; i < 3; i++) {
            pausa(400);
            System.out.print(".");
        }
        System.out.println("\n");
    }

    static void cabecalho(Conta c) {
        System.out.println("==============================");
        System.out.println("  Usuário: " + c.dono);
        System.out.println("  Conta:   " + c.tipoDeconta);
        System.out.println("  Saldo:   " + reais(c.saldoCentavos));
        System.out.println("==============================");
    }

    static boolean continuar(Scanner verLinha) {
        System.out.println("\nDeseja continuar? (s/n)");
        return verLinha.next().equalsIgnoreCase("s");
    }

    // transforma centavos em texto, ex: 2550 vira "R$ 25,50"
    static String reais(long centavos) {
        return "R$ " + (centavos / 100) + "," + String.format("%02d", centavos % 100);
    }

    // ---------- LOGIN ----------

    // um lugar só pra conferir a senha (depois fica fácil trocar a forma de guardar)
    static boolean senhaCorreta(Conta c, String tentativa) {
        return c.senha != null && c.senha.equals(tentativa);
    }

    // devolve a conta se entrou, ou null se falhou
    static Conta entrar(Scanner verLinha) {
        System.out.println("Quem é você? (P ou Clara)");
        Conta conta = contas.get(verLinha.next());
        if (conta == null) {
            System.out.println("Conta não encontrada!!");
            return null;
        }

        // primeiro acesso: cria a senha
        if (conta.senha == null) {
            System.out.println("Primeiro acesso! Crie uma senha: ");
            String nova = verLinha.next();
            System.out.println("Repita a senha: ");
            if (!nova.equals(verLinha.next())) {
                System.out.println("As senhas não batem!!");
                return null;
            }
            conta.senha = nova;
            System.out.println("Senha criada!!");
            return conta;
        }

        // já tem senha: 3 tentativas
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            System.out.println("Senha: ");
            if (senhaCorreta(conta, verLinha.next())) {
                return conta;
            }
            System.out.println("Senha errada!! (" + tentativa + "/3)");
        }
        System.out.println("Muitas tentativas erradas.");
        return null;
    }

    // ---------- REGRAS DO BANCO ----------

    static boolean transferir(String chaveDestino, Conta origem, long valor) {
        Conta destino = chaves.get(chaveDestino);
        if (destino == null) return false; // chave não existe
        if (valor <= 0) return false; // valor inválido
        if (origem.saldoCentavos < valor) return false; // sem saldo
        // TODO: não deixar mandar pra própria conta

        origem.saldoCentavos -= valor;
        destino.saldoCentavos += valor;
        destino.recebidos.add(new Transacao(origem.dono, valor));
        return true;
    }

    static boolean cadastrarChave(String chave, Conta dona) {
        if (chave.isBlank()) return false; // chave vazia
        if (chaves.containsKey(chave)) return false; // já tem dono
        chaves.put(chave, dona);
        return true;
    }

    public static void main(String[] args) {

        Conta p = new Conta("P", "Credito", 22_0000000);  // R$ 2200000,00
        Conta clara = new Conta("Clara", "Debito", 0); // R$ 0 rsrsrs
        contas.put("P", p);
        contas.put("Clara", clara);
        chaves.put("pkey", p);
        chaves.put("medadinheiro", clara);

        int opcao = 0;
        Scanner verLinha = new Scanner(System.in);

        // Entrar na conta
        Conta atual = entrar(verLinha);
        if (atual == null) {
            return; // não conseguiu entrar, encerra
        }

        //Menu
        String menu = """
                *** Digite a opção desejada ***
                
                1 - Ver saldo da conta
                2 - Enviar dinheiro
                3 - Ver o que recebi
                4 - Ver tudo
                5 - Trocar de conta
                6 - Cadastrar chave Pix
                7 - Sair
                """;

        //Opções
        while (opcao != 7) {
            cabecalho(atual);
            System.out.println(menu);
            opcao = verLinha.nextInt();

            if (opcao == 7) {
                System.out.println("\nAté mais, " + atual.dono + "!");
                break;
            }

            if (opcao == 1) {
                processando();
                System.out.println("Saldo atual: " + reais(atual.saldoCentavos));
            } else if (opcao == 2) {
                System.out.println("Chave de quem vai receber: ");
                String chave = verLinha.next();
                System.out.println("Valor a enviar: ");
                double valor = verLinha.nextDouble();
                long centavos = Math.round(valor * 100); // reais -> centavos

                processando();
                if (transferir(chave, atual, centavos)) {
                    System.out.println("Enviado!! Saldo atual: " + reais(atual.saldoCentavos));
                } else {
                    System.out.println("Não deu pra enviar (chave errada, valor inválido ou sem saldo)");
                }
            } else if (opcao == 3) {
                processando();
                if (atual.recebidos.isEmpty()) {
                    System.out.println("Você ainda não recebeu nada");
                }
                for (Transacao t : atual.recebidos) {
                    System.out.println("Recebeu " + reais(t.valor()) + " de " + t.de());
                }
            } else if (opcao == 4) {
                processando();
                System.out.println("*************************");
                System.out.println("\nUsuário: " + atual.dono);
                System.out.println("Tipo da conta: " + atual.tipoDeconta);
                System.out.println("Saldo atual: " + reais(atual.saldoCentavos));
                System.out.println("\n**************************");
            } else if (opcao == 5) {
                Conta nova = entrar(verLinha); // pede senha da outra conta também
                if (nova != null) {
                    processando();
                    atual = nova;
                    System.out.println("Agora você está na conta de " + atual.dono);
                }
                // se der errado, continua na conta de antes
            } else if (opcao == 6) {
                System.out.println("Qual chave você quer cadastrar? (ex: apelido, email)");
                String novaChave = verLinha.next();
                processando();
                if (cadastrarChave(novaChave, atual)) {
                    System.out.println("Chave cadastrada! Quem mandar pra \"" + novaChave + "\" cai na conta de " + atual.dono);
                } else {
                    System.out.println("Não deu pra cadastrar (chave vazia ou já em uso)");
                }
            } else {
                System.out.println("Opção invalida!! ");
            }

            // depois de qualquer opção, pergunta se quer continuar
            if (!continuar(verLinha)) {
                System.out.println("\nAté mais, " + atual.dono + "!");
                break;
            }
        }

    }
}