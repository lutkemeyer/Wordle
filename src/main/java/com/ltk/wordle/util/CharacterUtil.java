package com.ltk.wordle.util;

import java.util.LinkedHashSet;
import java.util.Set;

public class CharacterUtil {

    public static Character normalizeChar(Character ch) {
        return ch != null && ch.toString().matches("[a-zA-Z]") ? Character.toLowerCase(ch) : null;
    }

    public static Set<Character> splitChars(String str) {
        LinkedHashSet<Character> set = new LinkedHashSet<>();
        if (str == null || str.trim().isEmpty()) {
            return set;
        }
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                set.add(Character.toLowerCase(c));
            }
        }
        return set;
    }

}
