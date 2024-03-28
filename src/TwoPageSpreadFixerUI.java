import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public class TwoPageSpreadFixerUI {
    private JFrame frame;
    private JFileChooser fileChooser;
    private JTextArea consoleArea;
    private ConsoleOutputStream consoleOutputStream;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TwoPageSpreadFixerUI app = new TwoPageSpreadFixerUI();
            app.createAndShowUI();
        });
    }

    public void createAndShowUI() {
        frame = new JFrame("Two Page Spread Fixer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        consoleArea = new JTextArea(20, 60);
        consoleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createLineBorder(Color.BLACK)));
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton selectDirectoryButton = new JButton("Select Directory");
        selectDirectoryButton.addActionListener(e -> selectDirectory());

        JButton runFixerButton = new JButton("Fix");
        runFixerButton.addActionListener(e -> runFixer());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectDirectoryButton);
        buttonPanel.add(runFixerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        // Prompt the user to select a directory
        selectDirectory();

        // Redirect System.out to the console area
        consoleOutputStream = new ConsoleOutputStream(consoleArea);
        PrintStream printStream = new PrintStream(consoleOutputStream);
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void selectDirectory() {
        // Redirect System.out to the console area
        consoleOutputStream = new ConsoleOutputStream(consoleArea);
        PrintStream printStream = new PrintStream(consoleOutputStream);
        System.setOut(printStream);
        System.setErr(printStream);

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("Selected directory: " + directoryPath);
        } else {
            System.out.println("Please select a directory.");
        }
    }


    private void runFixer() {
        if (fileChooser.getSelectedFile() != null) {
            String directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            File directory = new File(directoryPath);
            System.out.println("Fixing two-page spreads in directory: " + directoryPath);

            // Run the fixing process in a separate thread
            Thread fixerThread = new Thread(() -> {
                TwoPageSpreadFixer.scanVolumes(directory);
            });
            fixerThread.start();
        } else {
            System.out.println("Please select a directory.");
        }
    }

    private static class ConsoleOutputStream extends OutputStream {
        private JTextArea consoleArea;

        public ConsoleOutputStream(JTextArea consoleArea) {
            this.consoleArea = consoleArea;
        }

        @Override
        public void write(int b) {
            consoleArea.append(String.valueOf((char) b));
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        }
    }
}
