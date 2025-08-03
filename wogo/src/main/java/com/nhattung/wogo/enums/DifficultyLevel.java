package com.nhattung.wogo.enums;

public enum DifficultyLevel {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String level;

    DifficultyLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}
