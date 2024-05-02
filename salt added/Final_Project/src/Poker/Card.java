package Poker;

import java.util.*;

public class Card{
	public static String[] RANK_SYMBOLS = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
	public static char[] SUIT_SYMBOLS = { 'C', 'D', 'S', 'H' };
	
	private int rank;
    private int suit;
	
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    public Card(String s) {
    	String rankSymbol = s.substring(0, 1);
        char suitSymbol = s.charAt(1);
        int rank = -1;
        for (int i = 0; i < 13; i++) {
            if (rankSymbol.equals(RANK_SYMBOLS[i])) {
                rank = i;
                break;
            }
        }
        int suit = -1;
        for (int i = 0; i < 4; i++) {
            if (suitSymbol == SUIT_SYMBOLS[i]) {
                suit = i;
                break;
            }
        }
        this.rank = rank;
        this.suit = suit;
    }
    
    public int getSuit() {
        return suit;
    }
    
    public int getRank() {
        return rank;
    }
    
    public int getRankValue() {
        if (rank >= 0 && rank < 9) { // '2' to '9' （rank is 0 to 7）
            return rank + 2; // rank value 0 means '2', so actual rank should be rank+2
        } else if (rank == 8) { // 'T' （rank为8）
            return 10;
        } else if (rank >= 9 && rank <= 11) { // 'J', 'Q', 'K' （rank from 9 to 11）
            return 10;
        } else if (rank == 12) { // 'A' （rank 12）
            return 1;
        } else {
            throw new IllegalArgumentException("Invalid card rank");
        }
    }
    
    
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            return ((Card) obj).getRank() == this.getRank() && ((Card) obj).getSuit() == this.getSuit();
        } else {
            return false;
        }
    }
    
    public int compareTo(Card card) {
        int thisValue = this.getRank();
        int otherValue = card.getRank();
        if (thisValue < otherValue) {
            return -1;
        } else if (thisValue > otherValue) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public String toString() {
        return RANK_SYMBOLS[this.rank] + SUIT_SYMBOLS[this.suit];
    }
    
    public static void main(String[] args) {
    	Card card1 = new Card(3,0);
    	System.out.println(card1.getRank());
    	System.out.println(card1.getSuit());
    	System.out.println(card1.toString());
    	Card card2 = new Card("7H");
    	System.out.println(card2.toString());
    	System.out.println(card1.equals(card2));
    	System.out.println(card1.compareTo(card2));
    	System.out.println(card2.getRankValue());
    	Card card3 = new Card("JD");
    	System.out.println(card3.getRankValue());
    }
}