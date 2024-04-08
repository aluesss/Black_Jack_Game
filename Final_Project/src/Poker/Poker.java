package Poker;

import java.util.*;

public class Poker{
	//扑克牌表达形式为点数+花色，点数为123456789,10为T，剩下的JQKA。
	//花色为C（club 草花） D（diamond 方片) S（spade 黑桃） H（heart 红桃)
	public static Map<String, String> map = new HashMap<>();
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
        int count = 1;
        for (int i = 1; i < pokers.size(); i++) {
        	if (pokers.get(i) - pokers.get(i - 1) != 1) {
        		break;
        	}
        	else {
        		count ++;
        	}
        	
        }
        if (count >= 5){
        	return 5;
        }
        else {
        	return 0;
        }
	}
	//同花
	private static int flush(String cards) {
		ArrayList<String> pokers = splitColor(cards);
		HashMap<String, Integer> frequencyMap = new HashMap<>();
		for (String color : pokers) {
	        frequencyMap.put(color, frequencyMap.getOrDefault(color, 0) + 1);
	    }
		for (Integer value : frequencyMap.values()) {
			if (value >= 5){
				return 6;
			}
		}
		return 0;
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
        //System.out.println(list);
        Collections.sort(list);
        return list.get(list.size() - 1);
	}
	//计算牌总点数
	private int sumPoint(ArrayList<Integer> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }
	//输入两个玩家的牌型，比较出胜者
	public String compare_winner(String P1, String P2) {
		int p1 = check(P1);
		int p2 = check(P2);
		if (p1 > p2) {
			return "Player1 wins";
		}
		else if (p2 > p1) {
			return "Player2 wins";
		}
		else {
			ArrayList<Integer> p1_num = splitNum(P1);
            ArrayList<Integer> p2_num = splitNum(P2);
            Collections.sort(p1_num);
            Collections.sort(p2_num);
            //顺子，同花顺，比较最大牌即可
            if(p1 == 9 || p1 == 5) {
            	int r1 = p1_num.get(p1_num.size() - 1);
                int r2 = p2_num.get(p2_num.size() - 1);
                if (r1 > r2) {return "Player1 wins";}
                else if (r2 > r1){return "Player2 wins";}
                else {return "Tie";}
            }
            //同花和高牌，挨张比较
            if(p1 == 1 || p1 == 6) {
            	Collections.reverse(p1_num);
            	Collections.reverse(p2_num);
            	for(int i = 0; i < p1_num.size(); i++) {
            		int v1 = p1_num.get(i), v2 = p2_num.get(i);
            		if(v1 > v2) {return "Player1 wins";}
	            	else if(v1 < v2) {return "Player2 wins";};
            	}
            	return "Tie";
            }
            HashMap<Integer, Integer> fre_P1 = new HashMap<>();
            HashMap<Integer, Integer> fre_P2 = new HashMap<>();
    	    for (int value : p1_num) {
    	    	fre_P1.put(value, fre_P1.getOrDefault(value, 0) + 1);
    	    }
    	    for (int value : p2_num) {
    	    	fre_P2.put(value, fre_P2.getOrDefault(value, 0) + 1);
    	    }
    	    //葫芦，先比三张再比二张
    	    if(p1 == 7) {
    	    	int p1_three = 0, p1_two = 0;
    	    	int p2_three = 0, p2_two = 0;
    	    	for (int key : fre_P1.keySet()) {
    	            int count = fre_P1.get(key);
    	            if (count == 3) {
    	                p1_three = key;
    	            }
    	            if (count == 2) {
    	                p1_two = key;
    	            }
    	        }
    	    	for (int key : fre_P2.keySet()) {
    	            int count = fre_P2.get(key);
    	            if (count == 3) {
    	                p2_three = key;
    	            }
    	            if (count == 2) {
    	                p2_two = key;
    	            }
    	        }
    	    	if (p1_three > p2_three) {
    	    		return "Player1 wins";
    	        } else if (p1_three < p2_three) {
    	        	return "Player2 wins";
    	        } else {
    	            // 三张一样，比较两张
    	            if (p1_two > p2_two) {
    	            	return "Player1 wins";
    	            } else if (p1_two < p2_two) {
    	            	return "Player2 wins";
    	            } else {
    	            	return "Tie";
    	            }
    	        }
    	    }
    	    //四条，先比四张再比一张
    	    if(p1 == 8) {
    	    	int p1_four = 0, p1_one = 0;
    	    	int p2_four = 0, p2_one = 0;
    	    	for (int key : fre_P1.keySet()) {
    	            int count = fre_P1.get(key);
    	            if (count == 4) {
    	            	p1_four = key;
    	            }
    	            if (count == 1) {
    	            	p1_one = key;
    	            }
    	        }
    	    	for (int key : fre_P2.keySet()) {
    	            int count = fre_P2.get(key);
    	            if (count == 4) {
    	            	p2_four = key;
    	            }
    	            if (count == 1) {
    	            	p2_one = key;
    	            }
    	        }
    	    	if (p1_four > p2_four) {
    	    		return "Player1 wins";
    	        } else if (p1_four < p2_four) {
    	        	return "Player2 wins";
    	        } else {
    	            // 四张一样，比较一张
    	            if (p1_one > p2_one) {
    	            	return "Player1 wins";
    	            } else if (p1_one < p2_one) {
    	            	return "Player2 wins";
    	            } else {
    	            	return "Tie";
    	            }
    	        }
    	    }
    	    //三条，先比三张再比剩下的两张
    	    if(p1 == 4) {
    	    	int p1_three = 0, p2_three = 0;
    	    	ArrayList<Integer> p1_ones = new ArrayList<>();
    	    	ArrayList<Integer> p2_ones = new ArrayList<>();
    	    	for (int key : fre_P1.keySet()) {
    	            int count = fre_P1.get(key);
    	            if (count == 3) {
    	            	p1_three = key;
    	            }
    	            if (count == 1) {
    	            	p1_ones.add(key);
    	            }
    	        }
    	    	for (int key : fre_P2.keySet()) {
    	            int count = fre_P2.get(key);
    	            if (count == 3) {
    	            	p2_three = key;
    	            }
    	            if (count == 1) {
    	            	p2_ones.add(key);
    	            }
    	        }
    	    	Collections.sort(p1_ones);
    	    	Collections.sort(p2_ones);
    	    	Collections.reverse(p1_ones);
    	    	Collections.reverse(p2_ones);
    	    	if (p1_three > p2_three) {
    	    		return "Player1 wins";
    	        } else if (p1_three < p2_three) {
    	        	return "Player2 wins";
    	        } else {
    	            for(int i = 0; i < p1_ones.size(); i++) {
    	            	int v1 = p1_ones.get(i), v2 = p2_ones.get(i);
    	            	if(v1 > v2) {return "Player1 wins";}
    	            	else if(v1 < v2) {return "Player2 wins";}
    	            }
    	            return "Tie";
    	        }
    	    }
    	    //两对
    	    if(p1 == 3) {
    	    	ArrayList<Integer> p1_pairs = new ArrayList<>();
    	    	ArrayList<Integer> p2_pairs = new ArrayList<>();
    	    	int p1_one = 0, p2_one = 0;
    	    	for (int key : fre_P1.keySet()) {
    	            int count = fre_P1.get(key);
    	            if (count == 2) {
    	            	p1_pairs.add(key);
    	            }
    	            if (count == 1) {
    	            	p1_one = key;
    	            }
    	        }
    	    	for (int key : fre_P2.keySet()) {
    	            int count = fre_P2.get(key);
    	            if (count == 2) {
    	            	p2_pairs.add(key);
    	            }
    	            if (count == 1) {
    	            	p2_one = key;
    	            }
    	        }
    	    	Collections.sort(p1_pairs);
    	    	Collections.reverse(p1_pairs);
    	    	Collections.sort(p2_pairs);
    	    	Collections.reverse(p2_pairs);
    	    	//比较对子
    	    	for(int i = 0; i < p1_pairs.size(); i++) {
	            	int v1 = p1_pairs.get(i), v2 = p2_pairs.get(i);
	            	if(v1 > v2) {return "Player1 wins";}
	            	else if(v1 < v2) {return "Player2 wins";}
	            }
    	    	//比较散牌
    	    	if(p1_one > p2_one) {return "Player1 wins";}
    	    	else if(p1_one < p1_one) {return "Player2 wins";}
    	    	else {return "Tie";}
    	    }
    	    //一对
    	    if(p1 == 2) {
    	    	ArrayList<Integer> p1_ones = new ArrayList<>();
    	    	ArrayList<Integer> p2_ones = new ArrayList<>();
    	    	int p1_pair = 0, p2_pair = 0;
    	    	for (int key : fre_P1.keySet()) {
    	            int count = fre_P1.get(key);
    	            if (count == 2) {
    	            	p1_pair = key;
    	            }
    	            if (count == 1) {
    	            	p1_ones.add(key);
    	            }
    	        }
    	    	for (int key : fre_P2.keySet()) {
    	            int count = fre_P2.get(key);
    	            if (count == 2) {
    	            	p2_pair = key;
    	            }
    	            if (count == 1) {
    	            	p2_ones.add(key);
    	            }
    	        }
    	    	Collections.sort(p1_ones);
    	    	Collections.reverse(p1_ones);
    	    	Collections.sort(p2_ones);
    	    	Collections.reverse(p2_ones);
    	    	//比较对子
    	    	if(p1_pair > p2_pair) {return "Player1 wins";}
    	    	else if(p1_pair < p2_pair) {return "Player2 wins";}
    	    	else {
    	    		//比较散排
    	    		for(int i = 0; i < p2_ones.size(); i++) {
    	            	int v1 = p2_ones.get(i), v2 = p2_ones.get(i);
    	            	if(v1 > v2) {return "Player1 wins";}
    	            	else if(v1 < v2) {return "Player2 wins";}
    	            }
    	    		return "Tie";
    	    	}
    	    }
    	    else {
    	    	return "Something is wrong, shouldn't reach here";
    	    }
		}
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
		i = check("2C 3H 4S TC AH 6D 7H");
		System.out.println(i);
		//一对，2
		i = check("2C 2H 4S TC AH 6D 7H");
		System.out.println(i);
		//两对，3
		i = check("2C 2H 4S 4C AH 6D 7H");
		System.out.println(i);
		//三条，4
		i = check("2C 2H 2S 4C AH 6D 7H");
		System.out.println(i);
		//顺子,5
		i = check("2C 3H 4S 5C 6H 6D 7H");
		System.out.println(i);
		//同花，6
		i = check("2C 3C JC 5C KC 6D 7H");
		System.out.println(i);
		//葫芦，7
		i = check("2C 2H 2S 5C 5H 6D 7H");
		System.out.println(i);
		//四条，8
		i = check("2C 2H 2S 2D 4H 6D 7H");
		System.out.println(i);
		//同花顺，9
		i = check("2C 3C 4C 5C 6C 6D 7H");
		System.out.println(i);
		
	}
}