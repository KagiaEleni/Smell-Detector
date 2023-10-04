import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String projectDirectory = "C:\\Users\\nenik\\eclipse-workspace\\Hotels\\src";

        try (Scanner scanner = new Scanner(System.in)) {
            // Prompt the user to enter the smell detector choice
            System.out.println("Select the smell detector:");
            for (SmellType smellType : SmellType.values()) {
                System.out.println(smellType.ordinal() + 1 + ". " + smellType.getName());
            }

            int smellChoice;
            while (true) {
                try {
                    smellChoice = scanner.nextInt();
                    break; // Input is valid, exit the loop
                } catch (java.util.InputMismatchException e) {
                    scanner.nextLine(); // Consume invalid input
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            if (smellChoice < 1 || smellChoice > SmellType.values().length) {
                System.out.println("Invalid smell detector choice.");
                return;
            }

            SmellType selectedSmellType = SmellType.values()[smellChoice - 1];

            SmellDetectionManager smellDetectionManager = new SmellDetectionManager(selectedSmellType, projectDirectory);
        }
    }
}
