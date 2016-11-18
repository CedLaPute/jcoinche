/**
 * Created by héhéhéhéhéhéhéhé on 18/11/2016.
 */
public class Card {
    private int number;
    private int value;
    private int color;

    public Card(int n, int v, int c) {
        this.number = n;
        this.value = v;
        this.color = c;
    }

    public int getNumber() {
        return this.number;
    }

    public int getValue() {
        return this.value;
    }

    public int getColor() {
        return this.color;
    }
}
