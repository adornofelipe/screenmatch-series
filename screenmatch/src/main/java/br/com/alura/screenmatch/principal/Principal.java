package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverterDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    Scanner leitor = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverterDados conversor = new ConverterDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e7cf6d57";


//    https://www.omdbapi.com/?t=gilmore+girls&season="+i+"&apikey=e7cf6d57"

    public void exibeMenu() {
        System.out.println("Digite o nome da sério para busca");
        var nomeSerie = leitor.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

//    for (int i =0; i< dados.totalTemporadas(); i++){
//        List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//        for (int j = 0; j<episodiosTemporada.size(); j++){
//            System.out.println(episodiosTemporada.get(j).titulo());
//        }
//    }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<String> nomes = Arrays.asList("F3","Dani","Bete","Alicio");
//        nomes.stream()
//                .sorted()
//                .limit(2)
//                .filter(n ->n.startsWith("B"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toUnmodifiableList());

        System.out.println("\n Top 5 Episodios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);


        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(),d))
                )
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("A Partir de que ano voce deseja ver os episodios");
        var ano = leitor.nextInt();
        leitor.nextLine();

        LocalDate databusca = LocalDate.of(ano,1,1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento()!=null && e.getDataLancamento().isAfter(databusca))
                .forEach(e -> {
                    System.out.println(" Temporada:  " + e.getTemporada()+
                            " Episodio:  "+ e.getTitulo()+
                            " Data Lançamento:  " + e.getDataLancamento().format(formatador)
                            );
                });


    }

    }
