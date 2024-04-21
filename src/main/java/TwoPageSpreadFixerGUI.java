import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public class TwoPageSpreadFixerGUI {
    private JFrame frame;
    private JFileChooser fileChooser;
    private JTextArea consoleArea;
    private ConsoleOutputStream consoleOutputStream;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TwoPageSpreadFixerGUI app = new TwoPageSpreadFixerGUI();
            app.displayGUI();
        });
    }

    public void displayGUI() {
        //set up window
        frame = new JFrame("Two Page Spread Fixer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set up panel with border layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //set up console area
        consoleArea = new JTextArea(20, 60);
        //make text read only
        consoleArea.setEditable(false);
        //add scroll pane and border
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createLineBorder(Color.BLACK)));
        panel.add(scrollPane, BorderLayout.CENTER);

        //directory button
        JButton selectDirectoryButton = new JButton("Select Directory");
        selectDirectoryButton.addActionListener(e -> selectDirectory());

        //run button
        JButton runFixerButton = new JButton("Fix");
        runFixerButton.addActionListener(e -> runFixer());

        //add buttons to panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectDirectoryButton);
        buttonPanel.add(runFixerButton);
        //add button panel to panel
        panel.add(buttonPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //add the panel to the frame
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        //prompt user to select directory
        selectDirectory();

        //redirect System.out to the console
        consoleOutputStream = new ConsoleOutputStream(consoleArea);
        PrintStream printStream = new PrintStream(consoleOutputStream);
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void selectDirectory() {
        //redirect System.out to the console
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

            //run program on seperate thread to allow gui to still function while it runs
            Thread fixerThread = new Thread(() -> {
                TwoPageSpreadFixer.fixDirectorySpreads(directory);
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
