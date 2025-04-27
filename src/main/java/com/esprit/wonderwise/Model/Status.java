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
                return TRAITEE; // par défaut passer à TRAITEE
            default:
                return null; // TRAITEE et REJETEE sont terminaux
        }
    }

    public Status getAlternativeStatus() {
        if (this == EN_COURS) {
            return REJETEE; // Si en cours, possibilité de rejet
        }
        return null;
    }

}
