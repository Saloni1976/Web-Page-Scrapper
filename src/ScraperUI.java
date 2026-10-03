import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Set;

public class ScraperUI extends JFrame {

    private JTextField urlField;
    private JTextArea outputArea;
    private JLabel statusLabel;
    private final RegexScraper scraper;

    public ScraperUI() {
        scraper = new RegexScraper();

        setTitle("Web Page Scraper (Regex-Based)");
        setSize(1100, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color bgColor = new Color(242, 245, 250);
        Color primaryBlue = new Color(45, 95, 190);
        Color panelWhite = Color.WHITE;

        getContentPane().setBackground(bgColor);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(bgColor);
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Web Page Scraper Using Regex");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(20, 60, 120));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Extract Emails, Links, and H1 Headings from Web Pages");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(80, 80, 80));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(subtitleLabel);
        topPanel.add(Box.createVerticalStrut(20));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(panelWhite);
        inputPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 205, 210), 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel urlLabel = new JLabel("Enter URL:");
        urlLabel.setFont(new Font("Arial", Font.BOLD, 18));

        urlField = new JTextField("https://example.com");
        urlField.setFont(new Font("Arial", Font.PLAIN, 16));

        inputPanel.add(urlLabel, BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);

        topPanel.add(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(bgColor);

        JButton fetchAllBtn = createButton("Fetch All", primaryBlue);
        JButton emailBtn = createButton("Emails", primaryBlue);
        JButton linkBtn = createButton("Links", primaryBlue);
        JButton h1Btn = createButton("H1 Headings", primaryBlue);
        JButton clearBtn = createButton("Clear", primaryBlue);
        JButton exitBtn = createButton("Exit", primaryBlue);

        buttonPanel.add(fetchAllBtn);
        buttonPanel.add(emailBtn);
        buttonPanel.add(linkBtn);
        buttonPanel.add(h1Btn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(exitBtn);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(bgColor);
        northWrapper.add(topPanel, BorderLayout.NORTH);
        northWrapper.add(buttonPanel, BorderLayout.SOUTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 17));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setMargin(new Insets(15, 15, 15, 15));
        outputArea.setBackground(panelWhite);
        outputArea.setForeground(new Color(35, 35, 35));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new TitledBorder(
                new LineBorder(new Color(180, 185, 190), 1, true),
                "Extracted Results",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(20, 60, 120)
        ));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(bgColor);
        centerPanel.setBorder(new EmptyBorder(0, 20, 12, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(228, 232, 238));
        statusLabel.setBorder(new EmptyBorder(8, 10, 8, 10));

        add(northWrapper, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        fetchAllBtn.addActionListener(e -> fetchAll());
        emailBtn.addActionListener(e -> extractEmails());
        linkBtn.addActionListener(e -> extractLinks());
        h1Btn.addActionListener(e -> extractH1());
        clearBtn.addActionListener(e -> clearOutput());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(155, 44));
        return button;
    }

    private boolean validateUrl() {
        String url = urlField.getText().trim();

        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a URL.");
            return false;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            JOptionPane.showMessageDialog(this, "URL must start with http:// or https://");
            return false;
        }

        return true;
    }

    private String formatSection(String title, Set<String> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== ").append(title).append(" ==========\n\n");

        if (data.isEmpty()) {
            sb.append("No ").append(title.toLowerCase()).append(" found.\n");
        } else {
            int count = 1;
            for (String item : data) {
                sb.append(count++).append(". ").append(item).append("\n");
            }
            sb.append("\nTotal ").append(title).append(": ").append(data.size()).append("\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    private void fetchAll() {
        if (!validateUrl()) return;

        try {
            statusLabel.setText(" Fetching data...");
            String html = scraper.getHtml(urlField.getText().trim());

            Set<String> emails = scraper.extractEmails(html);
            Set<String> links = scraper.extractLinks(html);
            Set<String> h1s = scraper.extractH1(html);

            StringBuilder sb = new StringBuilder();
            sb.append(formatSection("EMAILS", emails));
            sb.append(formatSection("LINKS", links));
            sb.append(formatSection("H1 HEADINGS", h1s));

            outputArea.setText(sb.toString());
            outputArea.setCaretPosition(0);
            statusLabel.setText(" Completed successfully");

        } catch (Exception ex) {
            statusLabel.setText(" Error occurred");
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void extractEmails() {
        if (!validateUrl()) return;

        try {
            statusLabel.setText(" Extracting emails...");
            String html = scraper.getHtml(urlField.getText().trim());
            Set<String> emails = scraper.extractEmails(html);

            outputArea.setText(formatSection("EMAILS", emails));
            outputArea.setCaretPosition(0);
            statusLabel.setText(" Email extraction completed");

        } catch (Exception ex) {
            statusLabel.setText(" Error occurred");
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void extractLinks() {
        if (!validateUrl()) return;

        try {
            statusLabel.setText(" Extracting links...");
            String html = scraper.getHtml(urlField.getText().trim());
            Set<String> links = scraper.extractLinks(html);

            outputArea.setText(formatSection("LINKS", links));
            outputArea.setCaretPosition(0);
            statusLabel.setText(" Link extraction completed");

        } catch (Exception ex) {
            statusLabel.setText(" Error occurred");
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void extractH1() {
        if (!validateUrl()) return;

        try {
            statusLabel.setText(" Extracting H1 headings...");
            String html = scraper.getHtml(urlField.getText().trim());
            Set<String> h1s = scraper.extractH1(html);

            outputArea.setText(formatSection("H1 HEADINGS", h1s));
            outputArea.setCaretPosition(0);
            statusLabel.setText(" H1 extraction completed");

        } catch (Exception ex) {
            statusLabel.setText(" Error occurred");
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearOutput() {
        outputArea.setText("");
        statusLabel.setText(" Output cleared");
    }
}