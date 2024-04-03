package modele;

public class Cible extends Case {
    public Cible(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreParcouru() {
        return e == null;
    }
}
