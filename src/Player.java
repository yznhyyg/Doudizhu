import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<String> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getHand() {
        return hand;
    }

    public void addCard(String card) {
        hand.add(card);
    }

    public void addCards(ArrayList<String> cards) {
        hand.addAll(cards);
    }

    public void clearHand(){
        hand.clear();
    }

}
