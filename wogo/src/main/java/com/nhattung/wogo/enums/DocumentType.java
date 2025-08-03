package com.nhattung.wogo.enums;

public enum DocumentType {

    IDENTITY_CARD("Identity Card"),
    WORKER_LICENSE("Worker License"),
    EXPERIENCE_PROOF("Experience Proof"),
    RESIDENCE_PERMIT("Residence Permit"),
    OTHER("Other");

    private final String type;

    DocumentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
