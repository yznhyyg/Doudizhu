import javax.swing.*;
import java.util.*;


public class Player {
    private String name;
    private final List<String> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public void addCard(String card) {
        Objects.requireNonNull(card, "Card cannot be null");
        if (card.isBlank()) {
            throw new IllegalArgumentException("Card cannot be blank");
        }
        hand.add(card);
    }

    public void addCards(List<String> cards) {
        Objects.requireNonNull(cards, "Cards cannot be null");
        for (String card : cards) {
            Objects.requireNonNull(card, "Cards cannot contain null");

            if (card.isBlank()) {
                throw new IllegalArgumentException("Cards cannot contain blank values");
            }
        }

        hand.addAll(cards);
    }

    public void clearHand(){
        hand.clear();
    }

    public List<String> removeCards(int...indexes){

        if (indexes == null || indexes.length == 0) {
            throw new IllegalArgumentException("Indexes cannot be empty");
        }

        Set<Integer> checkedIndexes = new HashSet<>();
        List<String> removedCards = new ArrayList<>();

        for(int index : indexes){
            if (index < 0 || index >= hand.size()) {
                throw new IllegalArgumentException("Invalid index: " + index);
            }
            if (checkedIndexes.add(index)) {
                removedCards.add(hand.get(index));
            }
        }

        List<Integer> sortedIndexes = new ArrayList<>(checkedIndexes);
        sortedIndexes.sort(Collections.reverseOrder());

        for (int index : sortedIndexes) {
            hand.remove(index);
        }

        return removedCards;
    }

    public void sortHand(Comparator<String> comparator) {
        hand.sort(comparator);
    }

}
