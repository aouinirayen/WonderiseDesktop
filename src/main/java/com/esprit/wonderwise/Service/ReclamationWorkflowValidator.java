package com.esprit.wonderwise.Service;



import com.esprit.wonderwise.Model.Status;

public class ReclamationWorkflowValidator {

    public static void validateTransition(Status current, Status newStatus) {
        if (current == newStatus) return; // Pas de changement

        switch (current) {
            case ENVOYEE:
                if (newStatus != Status.EN_COURS && newStatus != Status.REJETEE) {
                    throw new IllegalStateException("Transition invalide: " + current + " → " + newStatus);
                }
                break;

            case EN_COURS:
                if (newStatus != Status.TRAITEE && newStatus != Status.REJETEE) {
                    throw new IllegalStateException("Transition invalide: " + current + " → " + newStatus);
                }
                break;

            case TRAITEE:
            case REJETEE:
                throw new IllegalStateException("Statut final " + current + " ne peut pas être modifié");

            default:
                throw new IllegalArgumentException("Statut inconnu: " + current);
        }
    }
}