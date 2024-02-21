import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class SistemaEntregas {
    private static final String ARQUIVO_DADOS = "events.data";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Evento> eventos = new ArrayList<>();

    public static void main(String[] args) {
        carregarDados();

        while (true) {
            exibirMenu();
            int opcao = scanner.nextInt();
            scanner.nextLine();  // Limpar o buffer do scanner

            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    cadastrarEvento();
                    break;
                case 3:
                    consultarEventos();
                    break;
                case 4:
                    participarEvento();
                    break;
                case 5:
                    exibirEventosProximos();
                    break;
                case 6:
                    exibirEventosPassados();
                    break;
                case 7:
                    exibirEventosConfirmados();
                    break;
                case 8:
                    cancelarParticipacao();
                    break;
                case 9:
                    salvarDados();
                    System.out.println("Saindo do sistema...");
                    System.exit(0);
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("Escolha uma opção:");
        System.out.println("1. Cadastrar usuário");
        System.out.println("2. Cadastrar evento");
        System.out.println("3. Consultar eventos");
        System.out.println("4. Participar de um evento");
        System.out.println("5. Exibir eventos próximos");
        System.out.println("6. Exibir eventos passados");
        System.out.println("7. Exibir eventos confirmados");
        System.out.println("8. Cancelar participação em um evento");
        System.out.println("9. Sair");
    }

    private static void cadastrarUsuario() {
        System.out.println("Cadastro de Usuário");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario usuario = new Usuario(nome, email, senha);
        usuarios.add(usuario);
        System.out.println("Usuário cadastrado com sucesso!");
    }

    private static void cadastrarEvento() {
        System.out.println("Cadastro de Evento");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        System.out.print("Categoria: ");
        String categoria = scanner.nextLine();
        System.out.print("Horário (formato dd/MM/yyyy HH:mm): ");
        String horarioString = scanner.nextLine();
        LocalDateTime horario = LocalDateTime.parse(horarioString, Evento.getFormatadorDataHora());
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        Evento evento = new Evento(nome, endereco, categoria, horario, descricao);
        eventos.add(evento);
        System.out.println("Evento cadastrado com sucesso!");
    }

    private static void consultarEventos() {
        System.out.println("Lista de Eventos");
        int i = 0;
        for (Evento evento : eventos) {
            System.out.println(i++ + ". " + evento.getNome() + " - " + Evento.getFormatadorDataHora().format(evento.getHorario()));
        }
    }

    private static void participarEvento() {
        consultarEventos();  // Exibe a lista de eventos para escolha
        System.out.print("Escolha o evento pelo número: ");
        int numeroEvento = scanner.nextInt();
        scanner.nextLine();  // Limpar o buffer do scanner

        // Verifica se o número do evento é válido
        if (numeroEvento >= 0 && numeroEvento < eventos.size()) {
            Usuario usuario = obterUsuario();
            Evento evento = eventos.get(numeroEvento);
            evento.adicionarParticipante(usuario);
            System.out.println("Você agora está participando do evento: " + evento.getNome());
        } else {
            System.out.println("Número de evento inválido.");
        }
    }

    private static Usuario obterUsuario() {
        System.out.print("Informe seu email: ");
        String email = scanner.nextLine();
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }
        System.out.println("Usuário não encontrado. Cadastre-se primeiro.");
        cadastrarUsuario();
        return usuarios.get(usuarios.size() - 1);
    }

    private static void exibirEventosProximos() {
        eventos.stream()
                .filter(evento -> evento.getHorario().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Evento::getHorario))
                .forEach(evento -> System.out.println(evento.getNome() + " - " + evento.getHorario()));
    }

    private static void exibirEventosPassados() {
        eventos.stream()
                .filter(evento -> evento.getHorario().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Evento::getHorario).reversed())
                .forEach(evento -> System.out.println(evento.getNome() + " - " + evento.getHorario()));
    }

    private static void exibirEventosConfirmados() {
        Usuario usuario = obterUsuario();
        eventos.stream()
                .filter(evento -> evento.getParticipantes().contains(usuario))
                .filter(evento -> !evento.getParticipantes().isEmpty()) // Excluir eventos cancelados
                .forEach(evento -> System.out.println(evento.getNome() + " - " + evento.getHorario()));
    }


    private static void cancelarParticipacao() {
        Usuario usuario = obterUsuario();
        System.out.print("Digite o nome do evento que deseja cancelar a participação: ");
        String nomeEvento = scanner.nextLine();

        Optional<Evento> eventoOpt = eventos.stream()
                .filter(evento -> evento.getNome().equalsIgnoreCase(nomeEvento))
                .findFirst();

        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            evento.removerParticipante(usuario);
            System.out.println("Participação no evento cancelada: " + evento.getNome());
        } else {
            System.out.println("Evento não encontrado.");
        }
    }



    private static void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_DADOS))) {
            for (Usuario usuario : usuarios) {
                writer.write(usuario.getNome() + "," + usuario.getEmail() + "," + usuario.getSenha());
                writer.newLine();
            }
            for (Evento evento : eventos) {
                writer.write(evento.getNome() + "," + evento.getEndereco() + "," + evento.getCategoria() + ","
                        + Evento.getFormatadorDataHora().format(evento.getHorario()) + "," + evento.getDescricao());
                writer.newLine();
            }
            System.out.println("Dados salvos com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados no arquivo.");
        }
    }

    private static void carregarDados() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_DADOS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Usuario usuario = new Usuario(parts[0], parts[1], parts[2]);
                    usuarios.add(usuario);
                } else if (parts.length == 5) {
                    LocalDateTime horario = LocalDateTime.parse(parts[3], Evento.getFormatadorDataHora());
                    Evento evento = new Evento(parts[0], parts[1], parts[2], horario, parts[4]);
                    eventos.add(evento);
                }
            }
            System.out.println("Dados carregados com sucesso.");
        } catch (IOException e) {
            System.out.println("Arquivo de dados não encontrado. Criando novo arquivo.");
        }
    }


}
