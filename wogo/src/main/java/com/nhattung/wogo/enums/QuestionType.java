package com.nhattung.wogo.enums;

import lombok.Getter;

@Getter
public enum QuestionType {
    SINGLE_CHOICE("Single Choice"),
    MULTIPLE_CHOICE("Multiple Choice"),
    FILL_IN_THE_BLANK("Fill in the Blank"),
    SHORT_ANSWER("Short Answer"),
    LONG_ANSWER("Long Answer");

    private final String type;

    QuestionType(String type) {
        this.type = type;
    }

}
