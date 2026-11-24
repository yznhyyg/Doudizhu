import java.util.*;
import java.util.stream.Collectors;


public class Game {

    private enum TurnResult {
        PLAYED,
        PASSED,
        WON
    }

    private enum CardType {
        SINGLE,
        PAIR,
        TRIPLE,
        TRIPLE_WITH_SINGLE,
        TRIPLE_WITH_PAIR,
        STRAIGHT,
        CONSECUTIVE_PAIRS,
        AIRPLANE,
        AIRPLANE_WITH_SINGLES,
        AIRPLANE_WITH_PAIRS,
        FOUR_WITH_TWO_SINGLES,
        FOUR_WITH_TWO_PAIRS,
        BOMB,
        ROCKET
    }

    private static class CardPattern {
        private final CardType type;
        private final int mainValue;
        private final int cardCount;

        public CardPattern(CardType type, int mainValue, int cardCount) {
            this.type = type;
            this.mainValue = mainValue;
            this.cardCount = cardCount;
        }
    }

    private final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Integer> cardValues = new HashMap<>();
    private final List<String> deck = new ArrayList<>();
    private int landlordIndex;
    private int currentPlayerIndex;
    private List<String> lastPlayedCards = new ArrayList<>();
    private int lastPlayerIndex = -1;
    private int passCount = 0;
    private final List<Player> players = new ArrayList<>();
    private final ArrayList<String> dipai = new ArrayList<>();

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

    private void dealCards() {
        for (int i = 0; i < deck.size(); i++) {

            String serial = deck.get(i);

            if (i <= 2) {
                dipai.add(serial);
                continue;
            }
            players.get(i % 3).addCard(serial);
        }
    }

    private void sortHands() {
        Comparator<String> comparator = createCardComparator();
        for (Player player : players) {
            player.sortHand(comparator);
        }

        dipai.sort(comparator);
    }

    private void showHands() {
        lookPoker("Player 1", players.get(0).getHand());
        lookPoker("Player 2", players.get(1).getHand());
        lookPoker("Player 3", players.get(2).getHand());
    }

    private void giveBottomCards() {
        players.get(landlordIndex).addCards(dipai);
    }

    public void lookPoker(String player, List<String> list) {
        System.out.println(player + ":");

        for (String serial : list) {
            System.out.print(serial + " ");
        }
        System.out.println();
    }

    private Comparator<String> createCardComparator() {

        return
                (o1, o2) -> {
                    String color1 = o1.substring(0, 1);
                    int value1 = getValue(o1);
                    int value2 = getValue(o2);
                    String color2 = o2.substring(0, 1);

                    int i = value1 - value2;

                    return i == 0 ? color1.compareTo(color2) : i;
                };
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

    private int[] selectCards(Player player) {
        lookPoker(player.getName(), player.getHand());
        while (true) {
            System.out.println(player.getName() + "，请选择要打出的牌序号：");

            String line = scanner.nextLine().trim();

            if (line.equals("0")) {
                return new int[0];
            }

            if (line.isEmpty()) {
                System.out.println("请输入序号");
                continue;
            }
            boolean valid = true;

            String[] parts = line.split("\\s+");
            int[] indexes = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {

                try {
                    indexes[i] = Integer.parseInt(parts[i]) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("请输入正确的数字");
                    valid = false;
                    break;
                }

                if (indexes[i] < 0 || indexes[i] >= player.getHand().size()) {
                    System.out.println("序号超出范围，请重新输入");
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                continue;
            }
            return indexes;
        }
    }


    private TurnResult handlePlay(Player player) {
        while (true) {
            int[] indexes = selectCards(player);
            if (indexes.length == 0) {
                if (lastPlayedCards.isEmpty()) {
                    System.out.println("当前不能不要");
                    continue;
                }
                return TurnResult.PASSED;
            }
            List<String> selectedCards = new ArrayList<>();

            for (int index : indexes) {
                selectedCards.add(player.getHand().get(index));
            }

            if (!canPlay(selectedCards)) {
                System.out.println("出牌不合法，或者无法压过上家！");
                continue;
            }
            lastPlayedCards = player.removeCards(indexes);
            break;
        }
        if (player.getHand().isEmpty()) {
            return TurnResult.WON;
        }
        return TurnResult.PLAYED;
    }

    private boolean playTurn() {

        Player player = players.get(currentPlayerIndex);

        TurnResult result = handlePlay(player);

        if (result == TurnResult.WON) {
            return true;
        } else if (result == TurnResult.PLAYED) {
            lastPlayerIndex = currentPlayerIndex;
            passCount = 0;
            nextPlayer();
            return false;
        } else if (result == TurnResult.PASSED) {
            passCount++;
            if (passCount == 2) {
                currentPlayerIndex = lastPlayerIndex;
                lastPlayedCards.clear();
                passCount = 0;
            } else {
                nextPlayer();
            }
            return false;
        }
        return false;
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void startGame() {
        System.out.println("游戏开始！");
        while (true) {
            if (playTurn()) {
                System.out.println(players.get(currentPlayerIndex).getName() + " 获胜！");
                break;
            }
        }
    }

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
        currentPlayerIndex = landlordIndex;

        players.get(landlordIndex).sortHand(createCardComparator());

        showHands();
        lookPoker("Lord", dipai);

        startGame();
    }

    private int countRanksWithCount(
            Map<Integer, Integer> counts, int target) {

        int result = 0;

        for (int count : counts.values()) {
            if (count == target) {
                result++;
            }
        }

        return result;
    }

    private int findRankWithCount(
            Map<Integer, Integer> counts, int target) {

        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {

            if (entry.getValue() == target) {
                return entry.getKey();
            }
        }

        return -1;
    }

    private boolean isConsecutiveRanks(
            TreeMap<Integer, Integer> counts,
            int expectedRanks,
            int expectedCount) {

        if (counts.size() != expectedRanks) {
            return false;
        }

        if (counts.firstKey() < 3 || counts.lastKey() > 14) {
            return false;
        }

        if (counts.lastKey() - counts.firstKey() + 1 != expectedRanks) {
            return false;
        }

        for (int count : counts.values()) {
            if (count != expectedCount) {
                return false;
            }
        }

        return true;
    }

    private int findAirplaneMainValue(
            TreeMap<Integer, Integer> counts,
            int tripleCount,
            boolean pairWings) {

        // 从较大的连续三张组合开始寻找
        for (int start = 15 - tripleCount; start >= 3; start--) {

            TreeMap<Integer, Integer> remaining =
                    new TreeMap<>(counts);

            boolean valid = true;

            // 先取出连续的三张核心牌
            for (int rank = start;
                 rank < start + tripleCount;
                 rank++) {

                int count = remaining.getOrDefault(rank, 0);

                if (count < 3) {
                    valid = false;
                    break;
                }

                if (count == 3) {
                    remaining.remove(rank);
                } else {
                    remaining.put(rank, count - 3);
                }
            }

            if (!valid) {
                continue;
            }

            // 检查剩余的翅膀
            int remainingCount = 0;

            for (int count : remaining.values()) {
                remainingCount += count;
            }

            int expectedCount = pairWings
                    ? tripleCount * 2
                    : tripleCount;

            if (remainingCount != expectedCount) {
                continue;
            }

            boolean validWings = true;

            for (int count : remaining.values()) {

                if (pairWings && count != 2) {
                    validWings = false;
                    break;
                }

                if (!pairWings && count != 1) {
                    validWings = false;
                    break;
                }
            }

            if (validWings) {
                return start + tripleCount - 1;
            }
        }

        return -1;
    }

    private boolean hasFourWithTwoPairs(
            TreeMap<Integer, Integer> counts) {

        int fourRank = findRankWithCount(counts, 4);

        if (fourRank == -1) {
            return false;
        }

        int remainingCount = 0;

        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {

            if (entry.getKey() == fourRank) {
                continue;
            }

            if (entry.getValue() != 2) {
                return false;
            }

            remainingCount += entry.getValue();
        }

        return remainingCount == 4;
    }

    private CardPattern analyzeCards(List<String> cards) {

        if (cards == null || cards.isEmpty()) {
            return null;
        }

        // 统计每种点数出现的次数
        TreeMap<Integer, Integer> counts = new TreeMap<>();

        Set<String> uniqueCards = new HashSet<>();

        for (String card : cards) {

            if (card == null || !uniqueCards.add(card)) {
                return null;
            }

            int value = getValue(card);

            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }

        int size = cards.size();

        // 王炸
        if (size == 2
                && counts.size() == 2
                && counts.getOrDefault(16, 0) == 1
                && counts.getOrDefault(17, 0) == 1) {

            return new CardPattern(CardType.ROCKET, 17, size);
        }

        // 单张、对子、三张、炸弹
        if (counts.size() == 1) {

            int value = counts.firstKey();

            if (size == 1) {
                return new CardPattern(CardType.SINGLE, value, size);
            }

            if (size == 2) {
                return new CardPattern(CardType.PAIR, value, size);
            }

            if (size == 3) {
                return new CardPattern(CardType.TRIPLE, value, size);
            }

            if (size == 4) {
                return new CardPattern(CardType.BOMB, value, size);
            }

            return null;
        }

        // 三带一
        if (size == 4
                && countRanksWithCount(counts, 3) == 1
                && countRanksWithCount(counts, 1) == 1) {

            return new CardPattern(
                    CardType.TRIPLE_WITH_SINGLE,
                    findRankWithCount(counts, 3),
                    size
            );
        }

        // 三带二
        if (size == 5
                && countRanksWithCount(counts, 3) == 1
                && countRanksWithCount(counts, 2) == 1) {

            return new CardPattern(
                    CardType.TRIPLE_WITH_PAIR,
                    findRankWithCount(counts, 3),
                    size
            );
        }

        // 顺子
        if (size >= 5 && isConsecutiveRanks(counts, size, 1)) {
            return new CardPattern(CardType.STRAIGHT, counts.lastKey(), size);
        }

        // 连对
        if (size >= 6
                && size % 2 == 0
                && isConsecutiveRanks(counts, size / 2, 2)) {

            return new CardPattern(
                    CardType.CONSECUTIVE_PAIRS,
                    counts.lastKey(),
                    size
            );
        }

        // 纯飞机
        if (size >= 6
                && size % 3 == 0
                && isConsecutiveRanks(counts, size / 3, 3)) {

            return new CardPattern(
                    CardType.AIRPLANE,
                    counts.lastKey(),
                    size
            );
        }

        // 飞机带单
        if (size >= 8 && size % 4 == 0) {

            int mainValue = findAirplaneMainValue(
                    counts, size / 4, false
            );

            if (mainValue != -1) {
                return new CardPattern(
                        CardType.AIRPLANE_WITH_SINGLES,
                        mainValue,
                        size
                );
            }
        }

        // 飞机带对子
        if (size >= 10 && size % 5 == 0) {

            int mainValue = findAirplaneMainValue(
                    counts, size / 5, true
            );

            if (mainValue != -1) {
                return new CardPattern(
                        CardType.AIRPLANE_WITH_PAIRS,
                        mainValue,
                        size
                );
            }
        }

        // 四带二
        if (size == 6 && countRanksWithCount(counts, 4) == 1) {

            return new CardPattern(
                    CardType.FOUR_WITH_TWO_SINGLES,
                    findRankWithCount(counts, 4),
                    size
            );
        }

        // 四带两对
        if (size == 8 && countRanksWithCount(counts, 4) == 1
                && hasFourWithTwoPairs(counts)) {

            return new CardPattern(
                    CardType.FOUR_WITH_TWO_PAIRS,
                    findRankWithCount(counts, 4),
                    size
            );
        }

        // 不符合任何已支持的牌型
        return null;
    }

    private boolean canPlay(List<String> selectedCards) {

        CardPattern current = analyzeCards(selectedCards);

        // 当前选择不是合法牌型
        if (current == null) {
            return false;
        }

        // 当前拥有自由出牌权
        if (lastPlayedCards == null || lastPlayedCards.isEmpty()) {
            return true;
        }

        // 获取上家的牌型
        CardPattern previous = analyzeCards(lastPlayedCards);

        if (previous == null) {
            return false;
        }

        // 王炸最大
        if (current.type == CardType.ROCKET) {
            return previous.type != CardType.ROCKET;
        }

        // 上家打出王炸，其他牌型都不能压
        if (previous.type == CardType.ROCKET) {
            return false;
        }

        // 炸弹可以压制非炸弹
        if (current.type == CardType.BOMB
                && previous.type != CardType.BOMB) {
            return true;
        }

        // 非炸弹不能压制炸弹
        if (current.type != CardType.BOMB
                && previous.type == CardType.BOMB) {
            return false;
        }

        // 普通牌型必须相同，且牌数相同
        if (current.type != previous.type
                || current.cardCount != previous.cardCount) {
            return false;
        }

        // 比较主牌大小
        return current.mainValue > previous.mainValue;
    }
}