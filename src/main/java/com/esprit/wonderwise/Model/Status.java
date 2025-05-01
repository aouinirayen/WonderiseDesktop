package com.esprit.wonderwise.Model;

public enum Status {
    TRAITEE("traitee"),
    REJETEE("rejetee"),
    EN_COURS("en cours"),
    ENVOYEE("envoyee");

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
    }public Status getNextStatus() {
        switch (this) {
            case ENVOYEE:
                return EN_COURS;
            case EN_COURS:
                return TRAITEE;
            default:
                return null;
        }
    }

    public Status getAlternativeStatus() {
        if (this == EN_COURS) {
            return REJETEE;
        }
        return null;
    }

}
