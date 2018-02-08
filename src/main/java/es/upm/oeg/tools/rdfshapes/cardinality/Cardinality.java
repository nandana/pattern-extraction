package es.upm.oeg.tools.rdfshapes.cardinality;

/**
 * @author Nandana Mihindukulasooriya
 * @since 26/04/17.
 */
public class Cardinality {

    int card;

    long count;

    double precentage;

    public Cardinality() {
    }

    public Cardinality(int card, long count, double precentage) {
        this.card = card;
        this.count = count;
        this.precentage = precentage;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getPrecentage() {
        return precentage;
    }

    public void setPrecentage(double precentage) {
        this.precentage = precentage;
    }
}
