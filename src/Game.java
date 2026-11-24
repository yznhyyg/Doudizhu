import java.util.*;


public class Game {

    Scanner scanner = new Scanner(System.in);
    static HashMap<String, Integer> cardValues = new HashMap<>();
    private ArrayList<String> deck = new ArrayList<>();
    private int landlordIndex;

    static {

        cardValues.put("J", 11);
        cardValues.put("Q", 12);
        cardValues.put("K", 13);
        cardValues.put("A", 14);
        cardValues.put("2", 15);
        cardValues.put("小王", 16);
        cardValues.put("大王", 17);
    }

    private void initPlayers() {
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        players.add(new Player("Player 3"));
    }
    private void initDeck() {
        String[] color = {"♠", "♥", "♣", "♦"};
        String[] number = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"};

        for (String n : number) {
            for (String c : color) {
                deck.add(c + n);
            }
        }
        deck.add(" 小王");
        deck.add(" 大王");
    }

    private void dealCards(){
        for (int i = 0; i < deck.size(); i++) {

            String serial = deck.get(i);

            if (i <= 2) {
                dipai.add(serial);
                continue;
            }
            players.get(i % 3).addCard(serial);
        }
    }

    private void sortHands(){
        order(players.get(0).getHand());
        order(players.get(1).getHand());
        order(players.get(2).getHand());
        order(dipai);
    }

    private void showHands(){
        lookPoker("Player 1", players.get(0).getHand());
        lookPoker("Player 2", players.get(1).getHand());
        lookPoker("Player 3", players.get(2).getHand());
    }

    private void giveBottomCards() {
        players.get(landlordIndex).addCards(dipai);
    }

    private ArrayList<Player> players = new ArrayList<>();

    ArrayList<String> dipai = new ArrayList<>();

    public Game() {

        initPlayers();

        initDeck();
        do {
            Collections.shuffle(deck);

            for (Player player : players) {
                player.clearHand();
            }
            dipai.clear();

            dealCards();
            sortHands();
            showHands();

            landlordIndex = callLandlord();

            if (landlordIndex == -1) {
                System.out.println("本轮无人叫地主，重新发牌");
            }
        } while (landlordIndex == -1);

        giveBottomCards();

        order(players.get(landlordIndex).getHand());

        showHands();
        lookPoker("Lord", dipai);
    }

    public void lookPoker(String player, ArrayList<String> list) {
        System.out.println(player + ":");

        for (String serial : list) {
            System.out.print(serial + " ");
        }
        System.out.println();
    }

    public void order(ArrayList<String> list) {
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

    public int getValue(String poker) {
        String number = poker.substring(1);
        if (cardValues.containsKey(number)) {
            return cardValues.get(number);
        } else {
            return Integer.parseInt(number);
        }
    }

    public int callLandlord() {
        int maxBid = 0;
        int winnerIndex = -1;

        for (int i = 0; i < 3; i++) {
            System.out.println("玩家" + (i + 1) + "叫地主，请输入叫分：");
            int bid;

            while (true) {
                if (!scanner.hasNextInt()) {
                    System.out.println("请输入数字");
                    scanner.next();
                    continue;
                }
                bid = scanner.nextInt();
                if ((bid <= maxBid && bid != 0) || bid > 3) {
                    System.out.println("请输入正确的叫分");
                    continue;
                }
                break;
            }

            if (bid == 3) {
                System.out.println("玩家" + (i + 1) + "是地主");
                return i;
            }

            if (bid > maxBid) {
                maxBid = bid;
                winnerIndex = i;
            }
        }

        if (maxBid == 0) {
            return -1;
        }

        System.out.println("玩家" + (winnerIndex + 1) + "是地主");

        return winnerIndex;
    }
}