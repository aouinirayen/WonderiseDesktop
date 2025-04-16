package com.esprit.wonderwise.Model;

public enum Status {
    TRAITEE("traitée"),
    REJETEE("rejetée"),
    EN_COURS("en cours"),
    ENVOYEE("envoyée");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
