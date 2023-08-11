package com.ltk.wordle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    private Label lblResultados;

    @FXML
    private Label lblQtdResultados;

    @FXML
    private TextField txfLetrasExistentes;

    @FXML
    private TextField txfLetrasInexistentes;

    @FXML
    private TextField txfLetter1;

    @FXML
    private TextField txfLetter2;

    @FXML
    private TextField txfLetter3;

    @FXML
    private TextField txfLetter4;

    @FXML
    private TextField txfLetter5;

    @FXML
    private TextField txfWrongLetter1;

    @FXML
    private TextField txfWrongLetter2;

    @FXML
    private TextField txfWrongLetter3;

    @FXML
    private TextField txfWrongLetter4;

    @FXML
    private TextField txfWrongLetter5;

    private final Set<Character> letrasExistentes = new HashSet<>();

    private final Set<Character> letrasInexistentes = new HashSet<>();

    private final Set<String> allWords = new HashSet<>();

    private final Set<Character> allLetters = Set.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readWords();
        txfLetrasExistentes.textProperty().addListener((obs, oldV, newV) -> onLetrasExistentesChanged(newV));
        txfLetrasInexistentes.textProperty().addListener((obs, oldV, newV) -> onLetrasInexistentesChanged(newV));
        txfLetter1.textProperty().addListener((obs, oldV, newV) -> search());
        txfLetter2.textProperty().addListener((obs, oldV, newV) -> search());
        txfLetter3.textProperty().addListener((obs, oldV, newV) -> search());
        txfLetter4.textProperty().addListener((obs, oldV, newV) -> search());
        txfLetter5.textProperty().addListener((obs, oldV, newV) -> search());
        txfWrongLetter1.textProperty().addListener((obs, oldV, newV) -> search());
        txfWrongLetter2.textProperty().addListener((obs, oldV, newV) -> search());
        txfWrongLetter3.textProperty().addListener((obs, oldV, newV) -> search());
        txfWrongLetter4.textProperty().addListener((obs, oldV, newV) -> search());
        txfWrongLetter5.textProperty().addListener((obs, oldV, newV) -> search());
        search();
    }

    @FXML
    public void onClickZerar() {
        txfLetrasExistentes.setText("");
        txfLetrasInexistentes.setText("");
        txfLetter1.setText("");
        txfLetter2.setText("");
        txfLetter3.setText("");
        txfLetter4.setText("");
        txfLetter5.setText("");
        txfWrongLetter1.setText("");
        txfWrongLetter2.setText("");
        txfWrongLetter3.setText("");
        txfWrongLetter4.setText("");
        txfWrongLetter5.setText("");
    }

    private void readWords() {
        try {
            File file = new File(Application.class.getResource("words.txt").toURI());
            Scanner scanner = new Scanner(new FileInputStream(file));
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                if (word == null) {
                    continue;
                }
                if (word.trim().length() != 5) {
                    continue;
                }
                allWords.add(word.trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onLetrasExistentesChanged(String newV) {
        Set<Character> charsExistentes = Optional.ofNullable(newV)
                .map(s -> Arrays.asList(s.split("[^a-zA-Z]")))
                .orElse(new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.trim().isEmpty())
                .map(String::toLowerCase)
                .map(s -> s.charAt(0))
                .collect(Collectors.toSet());
        letrasExistentes.clear();
        letrasExistentes.addAll(charsExistentes);
        search();
    }

    private void onLetrasInexistentesChanged(String newV) {
        Set<Character> charsInexistentes = Optional.ofNullable(newV)
                .map(s -> Arrays.asList(s.split("[^a-zA-Z]")))
                .orElse(new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.trim().isEmpty())
                .map(String::toLowerCase)
                .map(s -> s.charAt(0))
                .collect(Collectors.toSet());
        letrasInexistentes.clear();
        letrasInexistentes.addAll(charsInexistentes);
        search();
    }

    private Character getFixedChar(int index) {
        String text = null;
        switch (index) {
            case 0:
                text = txfLetter1.getText();
                break;
            case 1:
                text = txfLetter2.getText();
                break;
            case 2:
                text = txfLetter3.getText();
                break;
            case 3:
                text = txfLetter4.getText();
                break;
            case 4:
                text = txfLetter5.getText();
                break;
            default:
                return null;
        }
        return Optional.ofNullable(text)
                .filter(s -> !s.trim().isEmpty())
                .map(String::toLowerCase)
                .map(s -> s.charAt(0))
                .orElse(null);
    }

    private Set<Character> getWrongChars(int index) {
        String text;
        if (index == 0) {
            text = txfWrongLetter1.getText();
        } else if (index == 1) {
            text = txfWrongLetter2.getText();
        } else if (index == 2) {
            text = txfWrongLetter3.getText();
        } else if (index == 3) {
            text = txfWrongLetter4.getText();
        } else if (index == 4) {
            text = txfWrongLetter5.getText();
        } else {
            return new HashSet<>();
        }
        if (text == null) {
            return new HashSet<>();
        }
        return text.toLowerCase().chars()
                .mapToObj(ch -> (char) ch)
                .filter(c -> String.valueOf(c).matches("[A-Za-z]"))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void search() {

        Comparator<String> alfabetica = Comparator.naturalOrder();
        Comparator<String> quantidadeVogais = Comparator.comparing((Function<String, Integer>) string -> string.replaceAll("[^aeiou]", "").length()).reversed();

        LinkedHashSet<String> palavrasFiltradas = allWords.stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!letrasExistentes.isEmpty()) {
            for (Character letra : letrasExistentes) {
                String l = String.valueOf(letra);
                palavrasFiltradas.removeIf(w -> !w.contains(l));
            }
        }
        if (!letrasInexistentes.isEmpty()) {
            for (Character letra : letrasInexistentes) {
                String l = String.valueOf(letra);
                palavrasFiltradas.removeIf(w -> w.contains(l));
            }
        }
        for (int i = 0; i < 5; i++) {
            final int index = i;
            Character fixedChar = getFixedChar(index);
            if (fixedChar != null) {
                palavrasFiltradas.removeIf(w -> w.charAt(index) != fixedChar);
            } else {
                Set<Character> wrongChars = getWrongChars(index);
                for (Character wrongChar : wrongChars) {
                    palavrasFiltradas.removeIf(w -> w.charAt(index) == wrongChar);
                }
            }
        }

        lblResultados.setText(String.join("\n", palavrasFiltradas));
        lblQtdResultados.setText("(" + palavrasFiltradas.size() + ")");

    }

}
