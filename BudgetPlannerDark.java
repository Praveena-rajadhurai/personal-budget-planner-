
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.text.DecimalFormat;

class Expense {

    String category;
    double amount;

    Expense(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }
}

public class BudgetPlannerDark extends JFrame {

    private JTextField txtSalary, txtAmount;
    private JComboBox<String> cmbCategory;
    private DefaultTableModel tableModel;
    private JLabel lblTotalExpenses, lblRemaining, lblGoal, lblStatus;
    private JProgressBar goalProgress;
    private ArrayList<Expense> expenses = new ArrayList<>();

    private final DecimalFormat df = new DecimalFormat("#.##");

    private final Color BG = new Color(28, 30, 34);
    private final Color PANEL = new Color(36, 39, 43);
    private final Color ACCENT = new Color(38, 198, 218);
    private final Color BTN = new Color(58, 66, 73);
    private final Color TEXT = Color.WHITE;
    private final Color WARNING = new Color(255, 99, 71);

    public BudgetPlannerDark() {
        setTitle("Budget Planner — Dark Mode");
        setSize(760, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        JPanel top = new JPanel();
        top.setBackground(BG);
        top.setLayout(new BorderLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(top, BorderLayout.NORTH);

        JPanel inputCard = createCardPanel();
        inputCard.setLayout(null);
        inputCard.setPreferredSize(new Dimension(720, 130));
        top.add(inputCard, BorderLayout.CENTER);

        JLabel lblSalaryTitle = createLabel("Monthly Salary (₹):", 14);
        lblSalaryTitle.setBounds(18, 16, 160, 24);
        inputCard.add(lblSalaryTitle);

        txtSalary = createField();
        txtSalary.setBounds(18, 42, 180, 30);
        inputCard.add(txtSalary);

        JLabel lblRecGoal = createLabel("Recommended Goal (20%):", 14);
        lblRecGoal.setBounds(220, 16, 200, 24);
        inputCard.add(lblRecGoal);

        lblGoal = createLabel("₹0.00", 16);
        lblGoal.setForeground(ACCENT);
        lblGoal.setBounds(220, 42, 200, 30);
        inputCard.add(lblGoal);

        JLabel lblCategory = createLabel("Category:", 14);
        lblCategory.setBounds(420, 16, 120, 24);
        inputCard.add(lblCategory);

        String[] categories = {"Food", "Vegetables", "Personal", "Entertainment", "Others"};
        cmbCategory = new JComboBox<>(categories);
        styleComboBox(cmbCategory);
        cmbCategory.setBounds(420, 42, 140, 30);
        inputCard.add(cmbCategory);

        JLabel lblAmountTitle = createLabel("Amount (₹):", 14);
        lblAmountTitle.setBounds(580, 16, 120, 24);
        inputCard.add(lblAmountTitle);

        txtAmount = createField();
        txtAmount.setBounds(580, 42, 120, 30);
        inputCard.add(txtAmount);

        JButton btnAdd = createButton("Add Expense");
        btnAdd.setBounds(18, 82, 160, 36);
        inputCard.add(btnAdd);

        JButton btnClear = createButton("Clear Inputs");
        btnClear.setBounds(200, 82, 160, 36);
        inputCard.add(btnClear);

        JButton btnReport = createButton("View Report");
        btnReport.setBounds(382, 82, 160, 36);
        inputCard.add(btnReport);

        JButton btnReset = createButton("Reset All");
        btnReset.setBounds(564, 82, 140, 36);
        inputCard.add(btnReset);

        JPanel center = new JPanel();
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(0, 12, 12, 12));
        center.setLayout(new BorderLayout(12, 12));
        add(center, BorderLayout.CENTER);

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setPreferredSize(new Dimension(720, 280));
        center.add(tableCard, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{"Category", "Amount (₹)"}, 0);
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        tableCard.add(sp, BorderLayout.CENTER);

        JPanel statsCard = createCardPanel();
        statsCard.setPreferredSize(new Dimension(720, 120));
        statsCard.setLayout(null);
        center.add(statsCard, BorderLayout.SOUTH);

        lblTotalExpenses = createLabel("Total Expenses: ₹0.00", 14);
        lblTotalExpenses.setBounds(18, 8, 300, 24);
        statsCard.add(lblTotalExpenses);

        lblRemaining = createLabel("Remaining: ₹0.00", 14);
        lblRemaining.setBounds(18, 38, 300, 24);
        statsCard.add(lblRemaining);

        lblStatus = createLabel("Status: —", 14);
        lblStatus.setBounds(18, 68, 500, 24);
        statsCard.add(lblStatus);

        goalProgress = new JProgressBar(0, 100);
        goalProgress.setBounds(420, 20, 280, 30);
        goalProgress.setStringPainted(true);
        styleProgressBar(goalProgress);
        statsCard.add(goalProgress);

        JLabel goalLabel = createLabel("Goal Progress", 12);
        goalLabel.setBounds(420, 52, 120, 20);
        statsCard.add(goalLabel);

        btnAdd.addActionListener(e -> addExpenseAction());
        btnClear.addActionListener(e -> clearInputs());
        btnReport.addActionListener(e -> showReportDialog());
        btnReset.addActionListener(e -> resetAll());

        txtSalary.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateRecommendedGoalLabel();
                updateAnalysis();
            }
        });

        setVisible(true);
    }

    private JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.setOpaque(true);
        return p;
    }

    private JLabel createLabel(String text, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, size));
        return l;
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(45, 48, 52));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(new Color(45, 48, 52));
        cb.setForeground(Color.WHITE);
        cb.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BTN);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(70, 75, 80)));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return b;
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(40, 44, 48));
        t.setForeground(Color.WHITE);
        t.setShowGrid(false);
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(new Color(34, 37, 41));
        t.getTableHeader().setForeground(Color.LIGHT_GRAY);
    }

    private void styleProgressBar(JProgressBar p) {
        p.setBackground(new Color(50, 53, 57));
        p.setForeground(ACCENT);
        p.setBorder(BorderFactory.createLineBorder(new Color(55, 60, 66)));
    }

    private void addExpenseAction() {
        String amtText = txtAmount.getText().trim();
        String salaryText = txtSalary.getText().trim();

        if (salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Monthly Salary first.", "Missing Salary", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (amtText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter expense amount.", "Missing Amount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amt = Double.parseDouble(amtText);
            if (amt < 0) {
                throw new NumberFormatException();
            }

            String cat = cmbCategory.getSelectedItem().toString();
            expenses.add(new Expense(cat, amt));
            tableModel.addRow(new Object[]{cat, df.format(amt)});
            txtAmount.setText("");
            updateAnalysis();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive number for amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputs() {
        txtAmount.setText("");
    }

    private void resetAll() {
        int confirm = JOptionPane.showConfirmDialog(this, "Reset all data? This will clear expenses and salary.", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        txtSalary.setText("");
        txtAmount.setText("");
        tableModel.setRowCount(0);
        expenses.clear();
        lblTotalExpenses.setText("Total Expenses: ₹0.00");
        lblRemaining.setText("Remaining: ₹0.00");
        lblGoal.setText("₹0.00");
        lblStatus.setText("Status: —");
        goalProgress.setValue(0);
    }

    private void updateRecommendedGoalLabel() {
        String s = txtSalary.getText().trim();
        try {
            if (s.isEmpty()) {
                lblGoal.setText("₹0.00");
                return;
            }
            double salary = Double.parseDouble(s);
            double rec = salary * 0.20;
            lblGoal.setText("₹" + df.format(rec));
        } catch (Exception e) {
            lblGoal.setText("₹0.00");
        }
    }

    private void updateAnalysis() {
        double total = 0.0;
        for (Expense ex : expenses) {
            total += ex.amount;
        }

        lblTotalExpenses.setText("Total Expenses: ₹" + df.format(total));

        double salary = 0.0;
        try {
            String s = txtSalary.getText().trim();
            if (!s.isEmpty()) {
                salary = Double.parseDouble(s);
            }
        } catch (Exception ignored) {
        }

        double remaining = salary - total;
        lblRemaining.setText("Remaining: ₹" + df.format(remaining));

        double recommendedGoal = salary * 0.20;

        int progressPct = 0;
        if (recommendedGoal > 0) {
            double achieved = Math.max(0, remaining); // only positive remaining contributes to savings
            progressPct = (int) Math.round((achieved / recommendedGoal) * 100.0);
            if (progressPct < 0) {
                progressPct = 0;
            }
            if (progressPct > 200) {
                progressPct = 200;

            }
        }

        goalProgress.setValue(Math.min(progressPct, 100));
        goalProgress.setString(progressPct + "%");

        if (salary == 0) {
            lblStatus.setText("Status: Enter salary to get analysis.");
            lblStatus.setForeground(TEXT);
        } else if (remaining < 0) {
            lblStatus.setText("Status: ⚠ You are OVER budget by ₹" + df.format(Math.abs(remaining)));
            lblStatus.setForeground(WARNING);
        } else {

            if (progressPct >= 100) {
                lblStatus.setText("Status: 🎉 Goal achieved! You saved ₹" + df.format(remaining));
                lblStatus.setForeground(new Color(124, 209, 76)); // green
            } else if (progressPct >= 50) {
                lblStatus.setText("Status: 👍 On track — save a little more to hit the goal.");
                lblStatus.setForeground(ACCENT);
            } else {
                lblStatus.setText("Status: ⚠ Low savings — try to cut optional expenses.");
                lblStatus.setForeground(WARNING);
            }
        }
    }

    private void showReportDialog() {
        double total = 0;
        double food = 0, veg = 0, personal = 0, entertainment = 0, others = 0;
        for (Expense ex : expenses) {
            total += ex.amount;
            switch (ex.category) {
                case "Food" ->
                    food += ex.amount;
                case "Vegetables" ->
                    veg += ex.amount;
                case "Personal" ->
                    personal += ex.amount;
                case "Entertainment" ->
                    entertainment += ex.amount;
                default ->
                    others += ex.amount;
            }
        }

        double salary = 0;
        try {
            String s = txtSalary.getText().trim();
            if (!s.isEmpty()) {
                salary = Double.parseDouble(s);
            }
        } catch (Exception ignored) {
        }

        double remaining = salary - total;
        double recommendedGoal = salary * 0.20;
        int progressPct = recommendedGoal > 0 ? (int) Math.round((Math.max(0, remaining) / recommendedGoal) * 100.0) : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("----- Monthly Budget Report -----\n");
        sb.append("Salary: ₹").append(df.format(salary)).append("\n");
        sb.append("Total Expenses: ₹").append(df.format(total)).append("\n");
        sb.append("Remaining: ₹").append(df.format(remaining)).append("\n\n");

        sb.append("Category wise:\n");
        sb.append(" - Food: ₹").append(df.format(food)).append("\n");
        sb.append(" - Vegetables: ₹").append(df.format(veg)).append("\n");
        sb.append(" - Personal: ₹").append(df.format(personal)).append("\n");
        sb.append(" - Entertainment: ₹").append(df.format(entertainment)).append("\n");
        sb.append(" - Others: ₹").append(df.format(others)).append("\n\n");

        sb.append("Recommended saving goal (20% of salary): ₹").append(df.format(recommendedGoal)).append("\n");
        sb.append("Goal Progress: ").append(progressPct).append("%\n\n");

        sb.append("Smart Tips:\n");
        if (salary == 0) {
            sb.append(" - Enter salary to get tailored tips.\n");
        } else if (remaining < 0) {
            sb.append(" - You are overspending. Reduce Personal & Entertainment expenses first.\n");
            sb.append(" - Cook at home to reduce Food expense.\n");
            sb.append(" - Use shopping lists & avoid impulse buys.\n");
        } else {
            if (progressPct >= 100) {
                sb.append(" - Great! You have achieved your savings goal. Keep it up.\n");
                sb.append(" - Consider investing the extra savings (FD/Mutual Funds).\n");
            } else {
                sb.append(" - Try to reduce Entertainment & Personal spending by 10-20%.\n");
                sb.append(" - Shift to cheaper grocery options or buy in bulk.\n");
                sb.append(" - Automate saving: move recommended goal amount to a savings account each month.\n");
            }
        }

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setBackground(new Color(24, 26, 28));
        ta.setForeground(Color.WHITE);
        ta.setFont(new Font("Consolas", Font.PLAIN, 13));
        ta.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(ta);
        scroll.setPreferredSize(new Dimension(580, 360));
        JOptionPane.showMessageDialog(this, scroll, "Monthly Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {

        try {
            UIManager.put("OptionPane.background", new Color(36, 39, 43));
            UIManager.put("Panel.background", new Color(36, 39, 43));
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(BudgetPlannerDark::new);
    }
}
