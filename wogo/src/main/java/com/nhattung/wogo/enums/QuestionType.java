package com.nhattung.wogo.enums;

public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("True/False"),
    FILL_IN_THE_BLANK("Fill in the Blank"),
    SHORT_ANSWER("Short Answer"),
    LONG_ANSWER("Long Answer");

    private final String type;

    QuestionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
