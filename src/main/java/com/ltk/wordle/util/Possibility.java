package com.ltk.wordle.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class Possibility implements Cloneable {

    public static final Character EMPTY_CHAR = '_';

    public static final List<Character> EMPTY = Arrays.asList(EMPTY_CHAR, EMPTY_CHAR, EMPTY_CHAR, EMPTY_CHAR, EMPTY_CHAR);

    private final List<Character> chars = new ArrayList<>(EMPTY);

    public Possibility() {
    }

    public Possibility(List<Character> fixedChars) {
        for (int i = 0; i < fixedChars.size(); i++) {
            if(fixedChars.get(i) != null) {
                chars.set(i, fixedChars.get(i));
            }
        }
    }

    public boolean isEmpty(int index) {
        return chars.get(index) == EMPTY_CHAR;
    }

    public void set(int index, Character ch) {
        this.chars.set(index, ch);
    }

    public boolean has(Character ch) {
        return chars.contains(ch);
    }

    public Character get(int index) {
        return chars.get(index);
    }

    public int getTotalChars() {
        return (int) chars.stream()
                .filter(ch -> ch != EMPTY_CHAR)
                .count();
    }

    @Override
    public String toString() {
        return chars.stream()
                .map(Objects::toString)
                .map(String::toUpperCase)
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Possibility other = (Possibility) o;
        return comparaListas(chars, other.getChars());
    }

    private boolean comparaListas(List<Character> lista1, List<Character> lista2) {
        if (lista1.size() != lista2.size()) {
            return false;
        }
        for (int i = 0; i < lista1.size(); i++) {
            if (!Objects.equals(lista1.get(i), lista2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chars);
    }

    @Override
    public Possibility clone() {
        return new Possibility(this.chars);
    }

}
