package com.suai;

public class Main {
    public static void main(String[] args) {
        String[] testStrings = { "", "The quick brown fox jumps over the lazy dog", "md4" };
        MD4 test = new MD4();

        for (String s : testStrings)
            System.out.println(test.toHexString(test.hashMD4(s.getBytes())) + " --- \"" + s + "\"");
    }
}
