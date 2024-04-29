package Poker;
import java.util.*;

public class Hand {
	private Card[] cards = new Card[5];
	private int num_cards = 0;
	//Initial empty hand
	public Hand() {
        // Empty implementation.
    }
	
	public Hand(Card[] cards) {
        addCards(cards);
    }
	
	public Hand(Collection<Card> cards) {
        for (Card card : cards) {
            addCard(card);
        }
    }
	//Example: "KH 7D 4C AS JS"
	public Hand(String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Null or empty string");
        }
        
        String[] parts = s.split("\\s");
        if (parts.length > 5) {
            throw new IllegalArgumentException("Too many cards in hand");
        }
        for (String part : parts) {
            addCard(new Card(part));
        }
    }
	
	public int size() {
        return num_cards;
    }
	
	public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Null card");
        }
        
        int insertIndex = -1;
        for (int i = 0; i < num_cards; i++) {
            if (card.compareTo(cards[i]) > 0) {
                insertIndex = i;
                break;
            }
        }
        if (insertIndex == -1) {
            // Could not insert anywhere, so append at the end.
            cards[num_cards++] = card;
        } else {
            for (int i = num_cards; i > insertIndex; i--) {
                cards[i] = cards[i - 1];
            }
            cards[insertIndex] = card;
            num_cards++;
        }
    }
	
	public void addCards(Card[] cards) {
        if (cards == null) {
            throw new IllegalArgumentException("Null array");
        }
        if (cards.length > 5) {
            throw new IllegalArgumentException("Too many cards");
        }
        for (Card card : cards) {
            addCard(card);
        }
    }
	
	public void addCards(Collection<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("Null collection");
        }
        if (cards.size() > 5) {
            throw new IllegalArgumentException("Too many cards");
        }
        for (Card card : cards) {
            addCard(card);
        }
    }
	
	public Card[] getCards() {
        Card[] dest = new Card[num_cards];
        System.arraycopy(cards, 0, dest, 0, num_cards);
        return dest;
    }
	
	public void removeAllCards() {
		num_cards = 0;
    }
	
	public int getBlackjackValue() {
	    int totalValue = 0;
	    int aceCount = 0;

	    // Traverse every card in the hand
	    for (int i = 0; i < num_cards; i++) {
	        int value = cards[i].getRankValue(); // getRankValue() in card class to get the rank value of card in BlackJack.
	        if (value == 1) { // If ACE
	            aceCount++;
	            totalValue += 1; // Consider ACE as 1
	        } else {
	            totalValue += value;
	        }
	    }

	    // Try best to consider ACE as 11, as long as not busted
	    while (aceCount > 0 && totalValue + 10 <= 21) {
	        totalValue += 10; // Fix an ACE from 1 to 11, so add 10
	        aceCount--;
	    }

	    return totalValue;
	}

	
	
	public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num_cards; i++) {
            sb.append(cards[i]);
            if (i < (num_cards - 1)) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
	
	public static void main(String[] args) {
		Hand h1 = new Hand("5H 7D 7S AC");
		System.out.println(h1.getBlackjackValue());
		Hand h2 = new Hand("5H AC");
		System.out.println(h2.getBlackjackValue());
		Hand h3 = new Hand("5H 7D 7S AC 8H");
		System.out.println(h3.getBlackjackValue());
		Card card2 = new Card("7H");
		h2.addCard(card2);
		System.out.println(h2.toString());
		System.out.println("1:" + h3.toString());
		h3.removeAllCards();
		h3.addCard(card2);
		System.out.println("2:" + h3.toString());
	}
}