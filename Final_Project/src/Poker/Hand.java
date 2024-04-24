package Poker;
import java.util.*;

public class Hand {
	private Card[] cards = new Card[5];
	private int num_cards = 0;
	//初始化空手
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

	    // 遍历手中的每张牌，累加点数
	    for (int i = 0; i < num_cards; i++) {
	        int value = cards[i].getRankValue(); // Card类有一个方法getRankValue()来返回牌的21点游戏中的点数
	        if (value == 1) { // 如果是Ace
	            aceCount++;
	            totalValue += 1; // 先把Ace当作1分计算
	        } else {
	            totalValue += value;
	        }
	    }

	    // 尽可能地将Ace当作11分计算，但要保证总分不超过21
	    while (aceCount > 0 && totalValue + 10 <= 21) {
	        totalValue += 10; // 将一个Ace从1分调整为11分，增加10分
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