package br.com.desafio.literalura.principal;

import br.com.desafio.literalura.dto.LivroDTO;
import br.com.desafio.literalura.dto.ResultsDTO;
import br.com.desafio.literalura.model.Autor;
import br.com.desafio.literalura.model.Livro;
import br.com.desafio.literalura.repository.AutorRepository;
import br.com.desafio.literalura.repository.LivroRepository;
import br.com.desafio.literalura.service.ConsumoAPI;
import br.com.desafio.literalura.service.ConverterDados;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI api = new ConsumoAPI();
    private final ConverterDados conversor = new ConverterDados();
    private final String apiEndpoint = "http://gutendex.com/books/?search=";

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void menu() {
        int option = -1;
        while (option != 0) {
            System.out.println("Choose an option below: ");
            String menu = """
                    1 - Search book by title
                    2 - List registered books
                    3 - List registered authors
                    4 - List living authors in a certain year
                    5 - List books in a certain language
                    0 - Exit
                    """;

            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> searchBookByTitle();
                case 2 -> listRegisteredBooks();
                case 3 -> listRegisteredAuthors();
                case 4 -> listLivingAuthorsInYear();
                case 5 -> listBooksByLanguage();
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void searchBookByTitle() {
        System.out.println("Enter the book title: ");
        String bookTitle = scanner.nextLine();
        String jsonResponse = api.consumirApi(apiEndpoint + bookTitle.replace(" ", "%20"));

        LivroDTO bookData = conversor.obterDados(jsonResponse, LivroDTO.class);

        if (bookData != null && bookData.resultados() != null && !bookData.resultados().isEmpty()) {
            ResultsDTO searchedBook = bookData.resultados().get(0);
            Livro book = new Livro(searchedBook);
            System.out.println(book);

            livroRepository.save(book);
        } else {
            System.out.println("No book found.");
        }
    }

    private void listRegisteredBooks() {
        List<Livro> books = livroRepository.findAll();

        if (books.isEmpty()) {
            System.out.println("No registered books.");
        } else {
            books.forEach(System.out::println);
        }
    }

    private void listRegisteredAuthors() {
        List<Autor> authors = autorRepository.findAll();

        if (authors.isEmpty()) {
            System.out.println("No registered authors.");
        } else {
            authors.forEach(System.out::println);
        }
    }

    private void listLivingAuthorsInYear() {
        System.out.println("Enter the year you want to search:");
        int year = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer
        List<Autor> authors = autorRepository.findAllByAno(year);

        if (authors.isEmpty()) {
            System.out.println("No authors found.");
        } else {
            authors.forEach(System.out::println);
        }
    }

    private void listBooksByLanguage() {
        System.out.println("""
                Enter the language you want to choose:
                Pt - Portuguese
                En - English
                Es - Spanish
                Fr - French
                """);
        String chosenLanguage = scanner.nextLine();
        List<Livro> books = livroRepository.findAllByIdioma(chosenLanguage);

        if (books.isEmpty()) {
            System.out.println("Language not found.");
        } else {
            books.forEach(System.out::println);
        }
    }
}
