package com.ltk.wordle.screens.main;

import com.ltk.wordle.Application;
import com.ltk.wordle.exceptions.WordleException;
import com.ltk.wordle.screens.AFacade;
import com.ltk.wordle.util.CharacterUtil;
import com.ltk.wordle.util.Possibility;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MainFacade extends AFacade {

    public static final Comparator<String> ALPHABETIC_COMPARATOR = Comparator.naturalOrder();

    public static final Comparator<String> VOLWELS_QUANTITY_COMPARATOR = Comparator.comparing((Function<String, Integer>) string -> CharacterUtil.splitChars(string.replaceAll("[^aeiou]", "")).size()).reversed().thenComparing(ALPHABETIC_COMPARATOR);

    private final Set<String> allWords = new HashSet<>();

    private final Set<Character> nonexistentChars = new HashSet<>();

    private final List<Set<Character>> wrongCharsByIndex = Arrays.asList(
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>()
    );

    private final List<Character> fixedChars = Arrays.asList(null,null,null,null,null);

    private Comparator<String> comparator = ALPHABETIC_COMPARATOR;

    public MainFacade(IMainFacadeListener listener) {
        super(listener);
    }

    public void readWords() throws WordleException {
        allWords.clear();
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
            throw new WordleException("Erro ao carregar as palavras!", e);
        }
    }

    public void onNonexistentCharsChanged(String str) {
        Set<Character> chars = CharacterUtil.splitChars(str);
        nonexistentChars.clear();
        nonexistentChars.addAll(chars);
        search();
    }

    public void onFixedCharChanged(int index, Character ch) {
        if (index < 0 || index > 4) {
            return;
        }
        fixedChars.set(index, CharacterUtil.normalizeChar(ch));
        search();
    }

    public void onWrongCharsChanged(int index, String letters) {
        if (index < 0 || index > 4) {
            return;
        }
        wrongCharsByIndex.get(index).clear();
        wrongCharsByIndex.get(index).addAll(CharacterUtil.splitChars(letters));
        search();
    }

    public void onSortChanged(Comparator<String> comparator) {
        this.comparator = Optional.ofNullable(comparator).orElse(ALPHABETIC_COMPARATOR);
        search();
    }

    public void search() {

        long start = System.currentTimeMillis();

        Collection<String> results = findResults();
        Collection<String> possibilities = findPossibilities();

        long end = System.currentTimeMillis();

        System.out.println("Tempo: " + (end - start) + "ms");

        listener.onFacadeListenerUpdateResults(results);
        listener.onFacadeListenerUpdatePossibilities(possibilities);

    }

    private Collection<String> findPossibilities() {

        List<Possibility> possibilitiesResult = new ArrayList<>();

        List<Character> charsToPermute = wrongCharsByIndex.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .distinct()
                .filter(Predicate.not(fixedChars::contains))
                .collect(Collectors.toList());

        int totalCharsToPermutate = charsToPermute.size();

        if(!charsToPermute.isEmpty()) {

            Character firstChar = charsToPermute.stream()
                    .findFirst()
                    .get();

            List<Possibility> firstIteration = generateFirstIteration(firstChar);

            charsToPermute.remove(firstChar);

            if(!charsToPermute.isEmpty()) {
                // ainda tem mais letras

                // imutavel
                List<Possibility> referenceToCopy = firstIteration.stream()
                        .map(Possibility::clone)
                        .collect(Collectors.toList());

                for (int currentCharIndex = 0; currentCharIndex < charsToPermute.size(); currentCharIndex++) {
                    Character currentChar = charsToPermute.get(currentCharIndex);

                    List<Possibility> resultsWithThisCurrentChar = new ArrayList<>();

                    for (int attemptIndex = 0; attemptIndex < 5; attemptIndex++) {

                        Iterator<Possibility> iterator = referenceToCopy.stream()
                                .map(Possibility::clone)
                                .iterator();

                        while (iterator.hasNext()) {
                            Possibility possibility = iterator.next();

                            if(!possibility.isEmpty(attemptIndex)) {
                                continue;
                            }

                            possibility.set(attemptIndex, currentChar);

                            resultsWithThisCurrentChar.add(possibility);

                        }

                    }

                    if(currentCharIndex < charsToPermute.size() - 1) {
                        // nao é o ultimo

                        referenceToCopy = resultsWithThisCurrentChar.stream()
                                .map(Possibility::clone)
                                .collect(Collectors.toList());

                        // duplica
                        referenceToCopy.addAll(
                                referenceToCopy.stream()
                                        .map(Possibility::clone)
                                        .collect(Collectors.toList())
                        );

                    } else {
                        // é o ultimo, acabou aqui

                        possibilitiesResult.addAll(
                                resultsWithThisCurrentChar.stream()
                                        .map(Possibility::clone)
                                        .collect(Collectors.toList())
                        );

                    }

                }

            } else {
                // só tinha uma letra pra iterar

                possibilitiesResult.addAll(
                        firstIteration.stream()
                                .map(Possibility::clone)
                                .collect(Collectors.toList())
                );

            }

            // retirar as letras nas posições onde elas não sao

            for (int wrongCharIndex = 0; wrongCharIndex < wrongCharsByIndex.size(); wrongCharIndex++) {
                Set<Character> wrongCharsInThisIndex = wrongCharsByIndex.get(wrongCharIndex);
                for (Character wrongCharInThisIndex : wrongCharsInThisIndex) {

                    Iterator<Possibility> iterator = possibilitiesResult.iterator();
                    while (iterator.hasNext()) {
                        Possibility possibility = iterator.next();

                        if(possibility.get(wrongCharIndex) == wrongCharInThisIndex) {
                            iterator.remove();
                        }

                    }

                }
            }

        } else {

            // sem wrong chars

            long fixedCharsCount = this.fixedChars.stream()
                    .filter(Objects::nonNull)
                    .count();

            if(fixedCharsCount > 0) {
                // tem letras fixas

                possibilitiesResult.add(new Possibility(this.fixedChars));

            }

        }

        if(possibilitiesResult.isEmpty()) {
            possibilitiesResult.add(new Possibility());
        }

        return possibilitiesResult.stream()
                .map(Possibility::toString)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Possibility> generateFirstIteration(Character ch) {

        List<Possibility> possibilities = IntStream.range(0, 5)
                .boxed()
                .map(i -> new Possibility(this.fixedChars))
                .collect(Collectors.toList());

        for (int i = 0; i < 5; i++) {
            if(this.fixedChars.get(i) == null) {
                Possibility possibility = possibilities.get(i);
                possibility.set(i, ch);
            }
        }

        return possibilities;
    }

    private Collection<String> findResults() {

        List<String> filteredWords = new ArrayList<>(allWords);

        if (!nonexistentChars.isEmpty()) {
            for (Character ch : nonexistentChars) {
                String l = String.valueOf(ch);
                filteredWords.removeIf(w -> w.contains(l));
            }
        }
        for (int i = 0; i < 5; i++) {
            final int index = i;
            Character fixedChar = fixedChars.get(index);
            if (fixedChar != null) {
                filteredWords.removeIf(w -> w.charAt(index) != fixedChar);
            } else {
                for (Character wrongChar : wrongCharsByIndex.get(index)) {
                    filteredWords.removeIf(w -> w.charAt(index) == wrongChar);
                }
            }
        }
        wrongCharsByIndex.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(existingChar -> {
                    filteredWords.removeIf(w -> !w.contains(String.valueOf(existingChar)));
                });


        filteredWords.sort(comparator);

        return filteredWords;
    }

}
