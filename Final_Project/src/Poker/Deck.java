package Poker;

import java.security.SecureRandom;
import java.util.*;

public class Deck{
	private Card[] cards;
	private int nextCardIndex = 0;
	private Random random = new SecureRandom(); //随机数生成
	//生成牌堆
	public Deck() {
        cards = new Card[52];
        int index = 0;
        for (int suit = 4 - 1; suit >= 0; suit--) {
            for (int rank = 13 - 1; rank >= 0 ; rank--) {
                cards[index++] = new Card(rank, suit);
            }
        }
    }
	//洗牌
	public void Shuffle() {
        for (int oldIndex = 0; oldIndex < 52; oldIndex++) {
            int newIndex = random.nextInt(52);
            Card tempCard = cards[oldIndex];
            cards[oldIndex] = cards[newIndex];
            cards[newIndex] = tempCard;
        }
        nextCardIndex = 0;
    }
	//重置发牌进度
	public void Reset() {
        nextCardIndex = 0;
    }
	//发一张牌
	public Card Deal() {
        return cards[nextCardIndex++];
    }
	//发好几张牌
	public List<Card> Deal(int NumOfCards) {
        List<Card> dealtCards = new ArrayList<Card>();
        for (int i = 0; i < NumOfCards; i++) {
            dealtCards.add(cards[nextCardIndex++]);
        }
        return dealtCards;
    }
	//发一张特定的牌
	public Card Deal(String cc) {
		String rank = cc.substring(0, 1);
        char suit = cc.charAt(1);
        Card card = null;
        int rankIndex = -1;
        int suitIndex = -1;
        for (int i = 0; i < 13; i++) {
            if (Card.RANK_SYMBOLS[i].equals(rank)) {
                rankIndex = i;
                break;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (Card.SUIT_SYMBOLS[i] == suit) {
                suitIndex = i;
                break;
            }
        }
        int index = -1;
        for (int i = nextCardIndex; i < 52; i++) {
        	if ((cards[i].getRank() == rankIndex) && (cards[i].getSuit() == suitIndex)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            if (index != nextCardIndex) {
                Card nextCard = cards[nextCardIndex];
                cards[nextCardIndex] = cards[index];
                cards[index] = nextCard;
            }
            card = Deal();
        }
        return card;
    }
	
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card);
            sb.append(' ');
        }
        return sb.toString().trim();
    }
	
	public static void main(String[] args) {
		Deck dd = new Deck();
		System.out.println(dd.toString());
		dd.Shuffle();
		System.out.println(dd.toString());
		System.out.println(dd.Deal());
		System.out.println(dd.Deal());
		System.out.println(dd.Deal());
		System.out.println(dd.Deal(5));
		System.out.println(dd.Deal("4C"));
		System.out.println(dd.Deal("4C"));//应该发不出来，因为4C没了
	}
}