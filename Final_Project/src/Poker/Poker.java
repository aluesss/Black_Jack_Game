package Poker;

import java.util.*;

public class Poker{
	//扑克牌表达形式为点数+花色，点数为123456789,10为T，剩下的JQKA。
	//花色为C（club 草花） D（diamond 方片) S（spade 黑桃） H（heart 红桃)
	public static Map<String, String> map = new HashMap<>();
	public static int[] pair = {0, 0, 0};
    public static int[] pairOne = {0, 0, 0, 0};
	//获取点数
	private static ArrayList<Integer> splitNum(String cards) {
        cards =  cards.replaceAll("T", "10");
        cards =  cards.replaceAll("J", "11");
        cards =  cards.replaceAll("Q", "12");
        cards =  cards.replaceAll("K", "13");
        cards =  cards.replaceAll("A", "14");
        ArrayList<Integer> cardList = new ArrayList<>();
        String[] cardSplits = cards.split(" ");
        for (String card : cardSplits
        ) {
            cardList.add(Integer.valueOf(card.substring(0,card.length()-1)));
        }
        return cardList;
    }
	//获取花色
	private static ArrayList<String> splitColor(String cards){
		ArrayList<String> cardList = new ArrayList<>();
        String[] cardSplits = cards.split(" ");
        for(String card: cardSplits) {
        	cardList.add(card.substring(card.length()-1, card.length()));
        }
        return cardList;
	}
	//判断牌型：
	//一对
	private static int pair(String cards) {
		ArrayList<Integer> pokers = splitNum(cards);
		Collections.sort(pokers);
		for(int i = 1; i < pokers.size(); i++) {
			if(pokers.get(i).equals(pokers.get(i - 1))) {
				pairOne[0] = pokers.get(i);
				return 2;
			}
		}
		return 0;
	}
	//两对
	private static int twopair(String cards) {
		ArrayList<Integer> pokers = splitNum(cards);
		Collections.sort(pokers);
		HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        for (int value : pokers) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }
        int count = 0;
        for (int j : frequencyMap.values()) {
        	if(j == 2) {
        		count ++;
        	}
        	if (count == 2) {
        		return 3;
        	}
        }
        return 0;
	}
	//三条
	private static int threeofKind(String cards) {
		ArrayList<Integer> pokers = splitNum(cards);
        Collections.sort(pokers);
        int count = 1;
        for (int i = 1; i < pokers.size(); i++) {
            if (pokers.get(i).equals(pokers.get(i - 1))) {
                count++;
            }else {
                count = 1;
            }
            if (count == 3) {
                return 4;
            }
        }

        return 0;
	}
	//顺子
	private static int straight(String cards) {
		ArrayList<Integer> pokers = splitNum(cards);
        Collections.sort(pokers);
        for (int i = 1; i < pokers.size(); i++) {
        	if (pokers.get(i) - pokers.get(i - 1) != 1) {
        		return 0;
        	}
        }
        return 5;
	}
	//同花
	private static int flush(String cards) {
		ArrayList<String> pokers = splitColor(cards);
		for (int i = 1; i < pokers.size(); i++) {
			if (!pokers.get(i).equals(pokers.get(i - 1))) {
                return 0;
            }
        }
		return 6;
	}
	//葫芦
	private static int fullhouse(String cards) {
		ArrayList<Integer> pokers = splitNum(cards);
        Collections.sort(pokers);
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        for (int value : pokers) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }
        boolean hasThree = false;
        boolean hasPair = false;
        for (int count : frequencyMap.values()) {
            if (count == 3) {
            	hasThree = true;
            }
            if (count == 2) {
                hasPair = true;
            }
        }
        if (hasThree && hasPair) {
        	return 7;
        }
        else {
        	return 0;
        }
        
	}
	//四条
	private static int fourofKind(String cards) {
		ArrayList<Integer> pokers = splitNum(cards);
        Collections.sort(pokers);
        int count = 1;
        for (int i = 1; i < pokers.size(); i++) {
            if (pokers.get(i).equals(pokers.get(i - 1))) {
                count++;
            }else {
                count = 1;
            }
            if (count == 4) {
                return 8;
            }
        }
        return 0;
	}
	//同花顺
	private static int straightflush(String cards) {
		if (straight(cards) == 5 && flush(cards) == 6) {
            return 9;
        }
        return 0;
	}
	//比较牌型
	private static int check(String cards) {
        List<Integer> list = new ArrayList<>();
        list.add(straightflush(cards));
        list.add(fourofKind(cards));
        list.add(fullhouse(cards));
        list.add(flush(cards));
        list.add(straight(cards));
        list.add(threeofKind(cards));
        list.add(twopair(cards));
        list.add(pair(cards));
        list.add(1);
        System.out.println(list);
        Collections.sort(list);
        return list.get(list.size() - 1);
    }
	
	public static void main(String[] args) {
		ArrayList<Integer> res1 = new ArrayList<>();
		res1 = splitNum("2C 3H 4S TC AH");
		ArrayList<String> res2 = new ArrayList<>();
		res2 = splitColor("2C 3H 4S TC AH");
		System.out.println(res1);
		System.out.println(res2);
		int i;
		//高牌，1
		i = check("2C 3H 4S TC AH");
		System.out.println(i);
		//一对，2
		i = check("2C 2H 4S TC AH");
		System.out.println(i);
		//两对，3
		i = check("2C 2H 4S 4C AH");
		System.out.println(i);
		//三条，4
		i = check("2C 2H 2S 4C AH");
		System.out.println(i);
		//顺子,5
		i = check("2C 3H 4S 5C 6H");
		System.out.println(i);
		//同花，6
		i = check("2C 3C JC 5C KC");
		System.out.println(i);
		//葫芦，7
		i = check("2C 2H 2S 5C 5H");
		System.out.println(i);
		//四条，8
		i = check("2C 2H 2S 2D 4H");
		System.out.println(i);
		//同花顺，9
		i = check("2C 3C 4C 5C 6C");
		System.out.println(i);
		
	}
}