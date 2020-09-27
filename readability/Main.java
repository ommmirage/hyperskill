package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        try (Scanner in = new Scanner(file)) {
            String text = in.nextLine();
            String[] wordsArr = text.split("\\s");
            int words = wordsArr.length;
            String[] sentencesArr = text.split("[.!?]");
            int sentences = 1;
            if (sentencesArr.length > 0) {
                sentences = sentencesArr.length;
            }
            int characters = 0;
            int syllables = 0;
            int polysyllables = 0;
            for (String word : wordsArr) {
                characters += word.length();
                int syllablesInWord = getSyllables(word);
                syllables += syllablesInWord;
                if (syllablesInWord > 2) {
                    polysyllables++;
                }
            }
            System.out.printf("Words: %d\n", words);
            System.out.printf("Sentences: %d\n", sentences);
            System.out.printf("Characters: %d\n", characters);
            System.out.printf("Syllables: %d\n", syllables);
            System.out.printf("Polysyllables: %d\n", polysyllables);

            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.nextLine()) {
                case "ARI":
                    printARI(characters, words, sentences);
                    break;
                case "FK":
                    printFK(words, sentences, syllables);
                    break;
                case "SMOG":
                    printSMOG(polysyllables, sentences);
                    break;
                case "CL":
                    printCL(characters, words, sentences);
                    break;
                case "all":
                    printAll(characters, words, sentences, syllables, polysyllables);
                    break;
            }
        }
    }

    static int getSyllables(String word) {
        char[] arr = word.toCharArray();
        int syllables = 0;
        boolean prevVowel = false;
        for (int i = 0; i < arr.length - 1; i++) {
            char c = arr[i];
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y') {
                if (!prevVowel) {
                    syllables++;
                }
                prevVowel = true;
            } else {
                prevVowel = false;
            }
        }
        char c = arr[arr.length - 1];
        if (c == 'a' || c == 'i' || c == 'o' || c == 'u' || c == 'y') {
            if (!prevVowel) {
                syllables++;
            }
        }
        if (syllables == 0) {
            syllables = 1;
        }
        return syllables;
    }

    static void printARI(int characters, int words, int sentences) {
        double score = 4.71 * characters / words + 0.5 * words /sentences - 21.43;
        System.out.printf("Automated Readability Index: %f ", score);
        printAge(score);
    }

    static void printFK(int words, int sentences, int syllables) {
        double score = 0.39 * words /sentences + 11.8 * syllables / words - 15.59;
        System.out.printf("Flesch–Kincaid readability tests: %f ", score);
        printAge(score);
    }

    static void printSMOG(int polysyllables, int sentences) {
        double score = 1.043 * Math.sqrt(polysyllables * 30 / sentences) + 3.1291;
        System.out.printf("Simple Measure of Gobbledygook: %f ", score);
        printAge(score);
    }

    static void printCL(int characters, int words, int sentences) {
        double score = 5.88 * characters / words - 29.6 * sentences / words - 15.8;
        System.out.printf("Coleman–Liau index: %f ", score);
        printAge(score);
    }

    static void printAll(int characters, int words, int sentences, int syllables, int polysyllables) {
        printARI(characters, words, sentences);
        printFK(words, sentences, syllables);
        printSMOG(polysyllables, sentences);
        printCL(characters, words, sentences);
        System.out.println("");
        int age = getAge(4.71 * characters / words + 0.5 * words / sentences - 21.43);
        age += getAge(0.39 * words / sentences + 11.8 * syllables / words - 15.59);
        age += getAge(1.043 * Math.sqrt(polysyllables * 30 / sentences) + 3.1291);
        age += getAge(5.88 * characters / words - 29.6 * sentences / words - 15.8);
        System.out.printf("This text should be understood in average by %f year olds.\n", (double) age / 4);
    }

    static void printAge(double score) {
        Map<Integer, Integer> scoreAge = new HashMap<>();
        scoreAge.put(2,7);
        scoreAge.put(3, 9);
        scoreAge.put(4, 10);
        scoreAge.put(5, 11);
        scoreAge.put(6, 12);
        scoreAge.put(7, 13);
        scoreAge.put(8, 14);
        scoreAge.put(9, 15);
        scoreAge.put(10, 16);
        scoreAge.put(11, 17);
        scoreAge.put(12, 18);
        scoreAge.put(13, 24);

        if (score >= 13) {
            System.out.printf("(24+ year olds).\n");
        } else if (score < 1) {
            System.out.printf("(about 6 year olds).\n");
        } else {
            int scoreFloor = (int) Math.floor(score);
            int age = scoreAge.get(scoreFloor);
            System.out.printf("(about %d year olds).\n", age);
        }
    }

    static int getAge(double score) {
        Map<Integer, Integer> scoreAge = new HashMap<>();
        scoreAge.put(2,7);
        scoreAge.put(3, 9);
        scoreAge.put(4, 10);
        scoreAge.put(5, 11);
        scoreAge.put(6, 12);
        scoreAge.put(7, 13);
        scoreAge.put(8, 14);
        scoreAge.put(9, 15);
        scoreAge.put(10, 16);
        scoreAge.put(11, 17);
        scoreAge.put(12, 18);
        scoreAge.put(13, 24);

        if (score >= 13) {
            return 24;
        } else if (score < 1) {
            return 6;
        } else {
            int scoreFloor = (int) Math.floor(score);
            return scoreAge.get(scoreFloor);
        }
    }
}
