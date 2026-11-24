import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    public void showCards(String player, List<String> list) {
        System.out.println(player + ":");

        for (String serial : list) {
            System.out.print(serial + " ");
        }
        System.out.println();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);

            String line = scanner.nextLine().trim();

            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("请输入数字");
            }
        }
    }

    public int[] selectCards(String playerName, List<String> hand){
        showCards(playerName, hand);
        while (true) {
            System.out.println(playerName + "，请选择要打出的牌序号：");

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

                if (indexes[i] < 0 || indexes[i] >= hand.size()) {
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
    public void showMessage(String message) {
        System.out.println(message);
    }
}
