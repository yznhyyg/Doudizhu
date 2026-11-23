import java.util.*;


public class Game {

    static HashMap<String, Integer> hm = new HashMap<>();
    static ArrayList<String> list = new ArrayList<>();

    static {
        String[] color = {"♠", "♥", "♣", "♦"};
        String[] number = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"};

        for (String n : number) {
            for (String c : color) {
                list.add(c + n);
            }
        }
        list.add(" 小王");
        list.add(" 大王");

        hm.put("J", 11);
        hm.put("Q", 12);
        hm.put("K", 13);
        hm.put("A", 14);
        hm.put("2", 15);
        hm.put("小王", 16);
        hm.put("大王", 17);
    }

    ArrayList<String> player1 = new ArrayList<>();
    ArrayList<String> player2 = new ArrayList<>();
    ArrayList<String> player3 = new ArrayList<>();
    ArrayList<String> dipai = new ArrayList<>();

    public Game() {

        Collections.shuffle(list);

        for (int i = 0; i < list.size(); i++) {

            String serial = list.get(i);

            if (i <= 2) {
                dipai.add(serial);
                continue;
            }

            if (i % 3 == 0) {
                player1.add(serial);
            } else if (i % 3 == 1) {
                player2.add(serial);
            } else {
                player3.add(serial);
            }
        }

        order(player1);
        order(player2);
        order(player3);
        order(dipai);

        lookPoker("Player 1", player1);
        lookPoker("Player 2", player2);
        lookPoker("Player 3", player3);
        lookPoker("Lord", dipai);
    }

    public void lookPoker(String player, ArrayList<String> list) {
        System.out.println(player + ":");

        for (String serial : list) {
            System.out.print(serial + " ");
        }
        System.out.println();
    }

    public void order(ArrayList<String> list){
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                String color1 = o1.substring(0, 1);
                int value1 = getValue(o1);
                int value2 = getValue(o2);
                String color2 = o2.substring(0, 1);

                int i = value1 - value2;

                return i == 0 ? color1.compareTo(color2) : i;
            }
        });
    }

    public int getValue(String poker){
        String number = poker.substring(1);
        if(hm.containsKey(number)){
            return hm.get(number);
        }else{
            return Integer.parseInt(number);
        }
    }
}
// My first GitHub update