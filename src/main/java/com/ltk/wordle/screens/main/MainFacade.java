package com.ltk.wordle.screens.main;

import com.ltk.wordle.Application;
import com.ltk.wordle.exceptions.WordleException;
import com.ltk.wordle.screens.AFacade;
import com.ltk.wordle.util.CharacterUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainFacade extends AFacade {

    public static final Comparator<String> ALPHABETIC_COMPARATOR = Comparator.naturalOrder();

    public static final Comparator<String> VOLWELS_QUANTITY_COMPARATOR = Comparator.comparing((Function<String, Integer>) string -> CharacterUtil.splitChars(string.replaceAll("[^aeiou]", "")).size()).reversed().thenComparing(ALPHABETIC_COMPARATOR);

    private final Set<String> allWords = new HashSet<>();

    private final Set<Character> existingChars = new HashSet<>();

    private final Set<Character> nonexistentChars = new HashSet<>();

    private final List<Set<Character>> wrongCharsByIndex = Arrays.asList(
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>()
    );

    private final Character[] fixedChars = new Character[5];

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

    public void onExistingCharsChanged(String str) {
        existingChars.clear();
        existingChars.addAll(CharacterUtil.splitChars(str));
        search();
    }

    public void onNonexistentCharsChanged(String str) {
        nonexistentChars.clear();
        nonexistentChars.addAll(CharacterUtil.splitChars(str));
        search();
    }

    public void onFixedCharChanged(int index, Character ch) {
        if (index < 0 || index > 4) {
            return;
        }
        fixedChars[index] = CharacterUtil.normalizeChar(ch);
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

        LinkedHashSet<String> filteredWords = allWords.stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!existingChars.isEmpty()) {
            for (Character letra : existingChars) {
                String l = String.valueOf(letra);
                filteredWords.removeIf(w -> !w.contains(l));
            }
        }
        if (!nonexistentChars.isEmpty()) {
            for (Character letra : nonexistentChars) {
                String l = String.valueOf(letra);
                filteredWords.removeIf(w -> w.contains(l));
            }
        }
        for (int i = 0; i < 5; i++) {
            final int index = i;
            Character fixedChar = fixedChars[index];
            if (fixedChar != null) {
                filteredWords.removeIf(w -> w.charAt(index) != fixedChar);
            } else {
                for (Character wrongChar : wrongCharsByIndex.get(index)) {
                    filteredWords.removeIf(w -> w.charAt(index) == wrongChar);
                }
            }
        }

        listener.onFacadeListenerUpdateResults(filteredWords);

    }

}
