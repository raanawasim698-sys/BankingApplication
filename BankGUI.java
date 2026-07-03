package bank.gui;

import bank.model.*;
import bank.util.DataStore;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BankGUI extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────────────
    private static final Color PURPLE       = new Color(0x6C, 0x5C, 0xE7);
    private static final Color PURPLE_LIGHT = new Color(0xA2, 0x9B, 0xFE);
    private static final Color PURPLE_PALE  = new Color(0xF4, 0xF3, 0xFF);
    private static final Color PURPLE_CHIP  = new Color(0xEE, 0xED, 0xFE);
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(0x18, 0x16, 0x2A);
    private static final Color TEXT_GREY    = new Color(0x99, 0x99, 0x99);
    private static final Color TEXT_MED     = new Color(0x55, 0x55, 0x66);
    private static final Color SUCCESS      = new Color(0x2E, 0xCC, 0x9A);
    private static final Color DANGER       = new Color(0xE7, 0x4C, 0x6A);
    private static final Color AMBER        = new Color(0xF5, 0xA6, 0x23);
    private static final Color BG_PAGE      = new Color(0xF4, 0xF3, 0xFF);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    private static final Font F_TITLE   = new Font("SansSerif", Font.BOLD,   22);
    private static final Font F_SECTION = new Font("SansSerif", Font.BOLD,   13);
    private static final Font F_BODY    = new Font("SansSerif", Font.PLAIN,  13);
    private static final Font F_SMALL   = new Font("SansSerif", Font.PLAIN,  11);
    private static final Font F_BOLD    = new Font("SansSerif", Font.BOLD,   13);
    private static final Font F_AMOUNT  = new Font("SansSerif", Font.BOLD,   26);
    private static final Font F_BTN     = new Font("SansSerif", Font.BOLD,   13);

    private Bank   bank;
    private JPanel contentArea;
    private JPanel navBar;
    private int    activeNav = 0;

    // Nav panels (lazy-built)
    private JPanel homePanel;
    private JPanel accountsPanel;
    private JPanel transactionsPanel;
    private JPanel billsPanel;

    // Summary labels updated on each refresh
    private JLabel lblBalance;
    private JLabel lblGreet;

    public BankGUI() {
        Bank loaded = DataStore.load();
        bank = (loaded != null) ? loaded : new Bank("Vault National Bank");

        setTitle("Vault — " + bank.getName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { exitApp(); }
        });
        setSize(420, 760);
        setResizable(false);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG_PAGE);
        setLayout(new BorderLayout());

        // Top status bar
        add(buildStatusBar(), BorderLayout.NORTH);

        // Scrollable content
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_PAGE);
        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        // Bottom nav
        navBar = buildBottomNav();
        add(navBar, BorderLayout.SOUTH);

        showHome();
        setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  STATUS BAR
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 8, 20));

        lblGreet = new JLabel(greetName());
        lblGreet.setFont(F_BOLD);
        lblGreet.setForeground(TEXT_DARK);

        JLabel time = new JLabel("Vault Bank");
        time.setFont(F_SMALL);
        time.setForeground(TEXT_GREY);
        time.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(lblGreet, BorderLayout.WEST);
        p.add(time,     BorderLayout.EAST);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(0xEE, 0xED, 0xFE)),
            BorderFactory.createEmptyBorder(10, 20, 8, 20)));
        return p;
    }

    private String greetName() {
        String h = bank.getClList().isEmpty() ? "Guest" :
                   bank.getClList().get(0).getPersonDetails().getName().split(" ")[0];
        return "Hello, " + h + " \uD83D\uDC4B";
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  BOTTOM NAV
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildBottomNav() {
        JPanel nav = new JPanel(new GridLayout(1, 5));
        nav.setBackground(WHITE);
        nav.setBorder(new MatteBorder(1, 0, 0, 0, new Color(0xEE, 0xED, 0xFE)));
        nav.setPreferredSize(new Dimension(420, 62));

        String[] icons = {"\uD83C\uDFE0", "\uD83D\uDCB3", "+", "\uD83D\uDCB8", "\uD83E\uDDFE"};
        String[] labels = {"Home", "Accounts", "", "Transfer", "Bills"};

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            JPanel item = new JPanel(new BorderLayout());
            item.setBackground(WHITE);
            item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (i == 2) {
                // FAB centre button
                JPanel fab = new JPanel() {
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(PURPLE);
                        g2.fillOval(4, 2, getWidth() - 8, getHeight() - 8);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                fab.setOpaque(false);
                fab.setLayout(new GridBagLayout());
                JLabel plus = new JLabel("+");
                plus.setFont(new Font("SansSerif", Font.BOLD, 22));
                plus.setForeground(WHITE);
                fab.add(plus);
                fab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                fab.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) { showAddClientDialog(); }
                });
                item.add(fab, BorderLayout.CENTER);
            } else {
                JLabel ico = new JLabel(icons[i], SwingConstants.CENTER);
                ico.setFont(new Font("SansSerif", Font.PLAIN, 18));
                JLabel lbl = new JLabel(labels[i], SwingConstants.CENTER);
                lbl.setFont(F_SMALL);
                lbl.setForeground(i == activeNav ? PURPLE : TEXT_GREY);
                ico.setForeground(i == activeNav ? PURPLE : TEXT_GREY);

                item.setLayout(new GridLayout(2, 1));
                item.add(ico);
                item.add(lbl);
                item.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        activeNav = idx;
                        refreshNav();
                        switch (idx) {
                            case 0: showHome();         break;
                            case 1: showAccounts();     break;
                            case 3: showTransactions(); break;
                            case 4: showBills();        break;
                        }
                    }
                    public void mouseEntered(MouseEvent e) { item.setBackground(PURPLE_CHIP); }
                    public void mouseExited(MouseEvent e)  { item.setBackground(WHITE); }
                });
            }
            nav.add(item);
        }
        return nav;
    }

    private void refreshNav() {
        // rebuild nav to reflect new active
        getContentPane().remove(navBar);
        navBar = buildBottomNav();
        getContentPane().add(navBar, BorderLayout.SOUTH);
        revalidate(); repaint();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  HOME PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    private void showHome() {
        contentArea.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PAGE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));

        // Balance card
        p.add(buildBalanceCard());
        p.add(vgap(14));

        // Quick actions row
        p.add(buildQuickActionsRow());
        p.add(vgap(18));

        // Clients section
        p.add(sectionHeader("Clients", "Add New", e -> showAddClientDialog()));
        p.add(vgap(8));
        p.add(buildClientsCards());
        p.add(vgap(18));

        // Recent transactions
        p.add(sectionHeader("Recent Transactions", "See all", e -> showTransactions()));
        p.add(vgap(8));
        p.add(buildRecentTransactions());

        contentArea.add(p, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel buildBalanceCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // decorative circle
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(getWidth() - 80, -40, 140, 140);
                g2.fillOval(getWidth() - 40, getHeight() - 30, 90, 90);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel lbl = new JLabel("CURRENT BALANCE");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(new Color(255, 255, 255, 180));

        lblBalance = new JLabel("PKR " + fmt(bank.totalAmount()));
        lblBalance.setFont(F_AMOUNT);
        lblBalance.setForeground(WHITE);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chips.setOpaque(false);
        chips.add(chip("↑ Income",  new Color(255, 255, 255, 40), WHITE));
        chips.add(chip("↓ Expense", new Color(255, 255, 255, 40), WHITE));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.add(lbl);
        inner.add(vgap(6));
        inner.add(lblBalance);
        inner.add(vgap(12));
        inner.add(chips);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickActionsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        row.add(quickAction("⬆", "Deposit",   SUCCESS, e -> doDeposit()));
        row.add(quickAction("⬇", "Withdraw",  DANGER,  e -> doWithdraw()));
        row.add(quickAction("⇄", "Transfer",  PURPLE,  e -> doTransfer()));
        row.add(quickAction("🧾", "Pay Bill",  AMBER,   e -> showBills()));
        return row;
    }

    private JPanel quickAction(String icon, String label, Color accent, ActionListener al) {
        JPanel card = roundCard(WHITE);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createEmptyBorder(12, 6, 10, 6));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30);
                g2.setColor(c);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(38, 38));
        JLabel ico = new JLabel(icon, SwingConstants.CENTER);
        ico.setFont(new Font("SansSerif", Font.PLAIN, 16));
        ico.setForeground(accent);
        iconCircle.add(ico);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(F_SMALL);
        lbl.setForeground(TEXT_MED);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        top.setOpaque(false);
        top.add(iconCircle);

        card.add(top,  BorderLayout.NORTH);
        card.add(lbl,  BorderLayout.SOUTH);
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { al.actionPerformed(null); }
            public void mouseEntered(MouseEvent e) { card.setBackground(PURPLE_PALE); }
            public void mouseExited(MouseEvent e)  { card.setBackground(WHITE); }
        });
        return card;
    }

    private JPanel buildClientsCards() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        if (bank.getClList().isEmpty()) {
            p.add(emptyState("No clients yet. Add one to get started."));
        } else {
            for (Client c : bank.getClList()) {
                p.add(buildClientRow(c));
                p.add(vgap(8));
            }
        }
        return p;
    }

    private JPanel buildClientRow(Client c) {
        JPanel card = roundCard(WHITE);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        // Avatar circle
        JPanel avatar = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE_CHIP);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(42, 42));
        String initials = initials(c.getPersonDetails().getName());
        JLabel av = new JLabel(initials);
        av.setFont(new Font("SansSerif", Font.BOLD, 14));
        av.setForeground(PURPLE);
        avatar.add(av);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel name = new JLabel(c.getPersonDetails().getName());
        name.setFont(F_BOLD);
        name.setForeground(TEXT_DARK);
        JLabel sub = new JLabel(c.getId() + "  ·  " + c.getAcList().size() + " account(s)");
        sub.setFont(F_SMALL);
        sub.setForeground(TEXT_GREY);
        info.add(name);
        info.add(sub);

        JLabel amt = new JLabel("PKR " + fmt(c.totalAmount()));
        amt.setFont(F_BOLD);
        amt.setForeground(PURPLE);

        card.add(avatar, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);
        card.add(amt,    BorderLayout.EAST);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showClientDetail(c); }
            public void mouseEntered(MouseEvent e) { card.setBackground(PURPLE_PALE); }
            public void mouseExited(MouseEvent e)  { card.setBackground(WHITE); }
        });
        return card;
    }

    private JPanel buildRecentTransactions() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel card = roundCard(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));

        if (bank.getBillHistory().isEmpty() && bank.getAcList().isEmpty()) {
            card.add(emptyState("No transactions yet."));
        } else {
            // Show accounts as rows
            int shown = 0;
            for (Account a : bank.getAcList()) {
                if (shown++ >= 4) break;
                card.add(txRow(a.getAcHolder().getPersonDetails().getName(),
                        a.getAccountType() + " — " + a.getNumber(),
                        "PKR " + fmt(a.getAmount()), SUCCESS));
                card.add(divider());
            }
            for (int i = bank.getBillHistory().size() - 1; i >= 0 && shown < 6; i--, shown++) {
                BillPayment bp = bank.getBillHistory().get(i);
                card.add(txRow(bp.getBillType().getLabel(), bp.getTimestamp(),
                        "−PKR " + fmt(bp.getAmount()), DANGER));
                card.add(divider());
            }
        }
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel txRow(String title, String sub, String amount, Color amtColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE_CHIP);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(36, 36));
        JLabel ico = new JLabel("$"); ico.setFont(F_BOLD); ico.setForeground(PURPLE);
        iconBox.add(ico);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel t = new JLabel(title); t.setFont(F_BOLD);    t.setForeground(TEXT_DARK);
        JLabel s = new JLabel(sub);   s.setFont(F_SMALL);   s.setForeground(TEXT_GREY);
        info.add(t); info.add(s);

        JLabel amt = new JLabel(amount);
        amt.setFont(F_BOLD);
        amt.setForeground(amtColor);

        row.add(iconBox, BorderLayout.WEST);
        row.add(info,    BorderLayout.CENTER);
        row.add(amt,     BorderLayout.EAST);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  ACCOUNTS PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    private void showAccounts() {
        contentArea.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PAGE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));

        p.add(pageTitle("Accounts"));
        p.add(vgap(14));
        p.add(sectionHeader("All Accounts", "Open New", e -> showOpenAccountDialog()));
        p.add(vgap(8));

        if (bank.getAcList().isEmpty()) {
            p.add(emptyState("No accounts yet."));
        } else {
            for (Account a : bank.getAcList()) {
                p.add(buildAccountCard(a));
                p.add(vgap(10));
            }
        }

        contentArea.add(p, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel buildAccountCard(Account a) {
        Color accent = a instanceof SavingsAccount ? SUCCESS :
                       a instanceof LoanAccount    ? DANGER  : PURPLE;

        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // left accent stripe
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 16));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setOpaque(false);
        JLabel name = new JLabel(a.getAcHolder().getPersonDetails().getName() + "  ·  " + a.getNumber());
        name.setFont(F_BOLD); name.setForeground(TEXT_DARK);
        JLabel type = new JLabel(a.getAccountType() + " Account");
        type.setFont(F_SMALL); type.setForeground(TEXT_GREY);
        info.add(name); info.add(type);

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 2));
        right.setOpaque(false);
        JLabel amt = new JLabel("PKR " + fmt(a.getAmount()));
        amt.setFont(F_BOLD); amt.setForeground(accent); amt.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel badge = new JLabel(a.getAccountType());
        badge.setFont(F_SMALL); badge.setForeground(TEXT_GREY); badge.setHorizontalAlignment(SwingConstants.RIGHT);
        right.add(amt); right.add(badge);

        card.add(info,  BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.repaint(); }
        });
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TRANSACTIONS PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    private void showTransactions() {
        contentArea.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PAGE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));

        p.add(pageTitle("Transactions"));
        p.add(vgap(14));

        // 3 action cards
        JPanel grid = new JPanel(new GridLayout(1, 3, 10, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        grid.add(txActionCard("Deposit",  SUCCESS, "Add funds",      e -> doDeposit()));
        grid.add(txActionCard("Withdraw", DANGER,  "Take out funds", e -> doWithdraw()));
        grid.add(txActionCard("Transfer", PURPLE,  "Move between",   e -> doTransfer()));
        p.add(grid);
        p.add(vgap(18));

        p.add(sectionHeader("All Accounts", null, null));
        p.add(vgap(8));

        JPanel card = roundCard(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));

        if (bank.getAcList().isEmpty()) {
            card.add(emptyState("Open an account first."));
        } else {
            for (Account a : bank.getAcList()) {
                card.add(txRow(a.getNumber() + "  ·  " + a.getAccountType(),
                        a.getAcHolder().getPersonDetails().getName(),
                        "PKR " + fmt(a.getAmount()), PURPLE));
                card.add(divider());
            }
        }
        p.add(card);

        contentArea.add(p, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel txActionCard(String title, Color accent, String sub, ActionListener al) {
        JPanel card = roundCard(WHITE);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top colour strip
        JPanel strip = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        strip.setOpaque(false);
        strip.setPreferredSize(new Dimension(0, 36));
        strip.setLayout(new GridBagLayout());
        JLabel ico = new JLabel(title.substring(0, 1));
        ico.setFont(new Font("SansSerif", Font.BOLD, 18));
        ico.setForeground(accent);
        strip.add(ico);

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(F_BOLD); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel(sub, SwingConstants.CENTER);
        s.setFont(F_SMALL); s.setForeground(TEXT_GREY);

        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 2));
        labels.setOpaque(false);
        labels.add(t); labels.add(s);

        card.add(strip,  BorderLayout.NORTH);
        card.add(labels, BorderLayout.SOUTH);
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { al.actionPerformed(null); }
            public void mouseEntered(MouseEvent e) { card.setBackground(PURPLE_PALE); }
            public void mouseExited(MouseEvent e)  { card.setBackground(WHITE); }
        });
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  BILLS PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    private void showBills() {
        contentArea.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PAGE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));

        p.add(pageTitle("Pay Bills"));
        p.add(vgap(14));

        // Bill type grid 2x3
        JPanel grid = new JPanel(new GridLayout(2, 3, 10, 10));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        BillPayment.BillType[] types = BillPayment.BillType.values();
        String[] billIcons = {"⚡", "\uD83D\uDD25", "\uD83D\uDCA7", "\uD83C\uDF10", "\uD83D\uDCF1", "\uD83C\uDF93"};
        Color[]  billColors = {AMBER, DANGER, new Color(0x5C, 0x9B, 0xD4), SUCCESS, PURPLE, new Color(0xFF, 0x7F, 0x50)};

        for (int i = 0; i < types.length; i++) {
            final BillPayment.BillType bt = types[i];
            final String ic = billIcons[i];
            final Color  ac = billColors[i];
            grid.add(billTypeCard(bt.getLabel().split(" / ")[0], ic, ac, e -> showBillDialog(bt)));
        }
        p.add(grid);
        p.add(vgap(20));

        // History
        p.add(sectionHeader("Payment History", null, null));
        p.add(vgap(8));
        JPanel hist = roundCard(WHITE);
        hist.setLayout(new BoxLayout(hist, BoxLayout.Y_AXIS));
        hist.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));

        if (bank.getBillHistory().isEmpty()) {
            hist.add(emptyState("No bill payments yet."));
        } else {
            List<BillPayment> bh = bank.getBillHistory();
            for (int i = bh.size() - 1; i >= Math.max(0, bh.size() - 8); i--) {
                BillPayment bp = bh.get(i);
                hist.add(txRow(bp.getBillType().getLabel(), bp.getTimestamp(),
                        "−PKR " + fmt(bp.getAmount()), DANGER));
                hist.add(divider());
            }
        }
        p.add(hist);

        contentArea.add(p, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel billTypeCard(String label, String icon, Color accent, ActionListener al) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 8, 12, 8));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel circle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(40, 40));
        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("SansSerif", Font.PLAIN, 18));
        circle.add(ico);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(F_SMALL);
        lbl.setForeground(TEXT_MED);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        top.setOpaque(false);
        top.add(circle);

        card.add(top, BorderLayout.NORTH);
        card.add(lbl, BorderLayout.SOUTH);
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { al.actionPerformed(null); }
            public void mouseEntered(MouseEvent e) { card.repaint(); }
        });
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  DIALOGS
    // ═══════════════════════════════════════════════════════════════════════════
    private void showAddClientDialog() {
        JTextField fName  = styledField("Full Name");
        JTextField fCnic  = styledField("CNIC  (e.g. 35202-1234567-1)");
        JTextField fPhone = styledField("Phone  (e.g. 03001234567)");

        Object[] msg = { label("Full Name"), fName, label("CNIC"), fCnic, label("Phone"), fPhone };
        if (confirm("Register New Client", msg)) {
            try {
                Person p = new Person(fName.getText(), fCnic.getText(), fPhone.getText());
                Client c = bank.addClient(p);
                DataStore.save(bank);
                refresh();
                ok("Client registered!\nID: " + c.getId());
            } catch (Exception ex) { err(ex.getMessage()); }
        }
    }

    private void showClientDetail(Client c) {
        JTextArea ta = new JTextArea(c.toString(), 10, 36);
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setBackground(PURPLE_PALE);
        ta.setForeground(TEXT_DARK);
        ta.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        Object[] btns = {"Close", "Remove Client"};
        int res = JOptionPane.showOptionDialog(this, new JScrollPane(ta),
                c.getPersonDetails().getName(), JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, btns, btns[0]);
        if (res == 1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove " + c.getPersonDetails().getName() + " and all their accounts?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bank.removeClient(c.getId());
                DataStore.save(bank);
                refresh();
            }
        }
    }

    private void showOpenAccountDialog() {
        if (bank.getClList().isEmpty()) { err("Add a client first."); return; }
        JComboBox<String> cbC = styledCombo();
        for (Client c : bank.getClList())
            cbC.addItem(c.getId() + " — " + c.getPersonDetails().getName());
        String[] types = {"Savings (5% interest, min PKR 500)", "Current (PKR 50/mo charge)", "Loan (10% monthly interest)"};
        JComboBox<String> cbT = styledCombo(types);
        JTextField fAmt = styledField("Amount in PKR");
        Object[] msg = { label("Client"), cbC, label("Type"), cbT, label("Initial Amount"), fAmt };
        if (confirm("Open Account", msg)) {
            try {
                Client c = bank.getClList().get(cbC.getSelectedIndex());
                String type = cbT.getSelectedIndex() == 0 ? "savings" : cbT.getSelectedIndex() == 1 ? "current" : "loan";
                Account a = bank.addAccount(c.getId(), parsePos(fAmt.getText(), "Amount"), type);
                DataStore.save(bank);
                refresh();
                ok("Account opened!\n" + a.getNumber());
            } catch (Exception ex) { err(ex.getMessage()); }
        }
    }

    private void doDeposit() {
        String accNo = ask("Account Number", "Deposit");
        if (accNo == null) return;
        String amtS = ask("Amount (PKR)", "Deposit");
        if (amtS == null) return;
        try {
            double amt = parsePos(amtS, "Amount");
            Account acc = bank.searchAccount(accNo.trim());
            if (acc == null) { err("Account not found."); return; }
            double bal = acc.deposit(amt);
            DataStore.save(bank); refresh();
            ok("Deposited PKR " + fmt(amt) + "\nNew Balance: PKR " + fmt(bal));
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    private void doWithdraw() {
        String accNo = ask("Account Number", "Withdraw");
        if (accNo == null) return;
        String amtS = ask("Amount (PKR)", "Withdraw");
        if (amtS == null) return;
        try {
            double amt = parsePos(amtS, "Amount");
            Account acc = bank.searchAccount(accNo.trim());
            if (acc == null) { err("Account not found."); return; }
            double bal = acc.withdraw(amt);
            DataStore.save(bank); refresh();
            ok("Withdrawn PKR " + fmt(amt) + "\nRemaining: PKR " + fmt(bal));
        } catch (Exception ex) { err(ex.getMessage()); }
    }

    private void doTransfer() {
        JTextField fFrom = styledField("From Account No");
        JTextField fTo   = styledField("To Account No");
        JTextField fAmt  = styledField("Amount (PKR)");
        Object[] msg = { label("From Account"), fFrom, label("To Account"), fTo, label("Amount"), fAmt };
        if (confirm("Transfer Funds", msg)) {
            try {
                bank.transfer(fFrom.getText().trim(), fTo.getText().trim(), parsePos(fAmt.getText(), "Amount"));
                DataStore.save(bank); refresh();
                ok("Transfer successful!");
            } catch (Exception ex) { err(ex.getMessage()); }
        }
    }

    private void showBillDialog(BillPayment.BillType bt) {
        if (bank.getAcList().isEmpty()) { err("No accounts exist yet."); return; }
        JComboBox<String> cbA = styledCombo();
        for (Account a : bank.getAcList())
            if (!(a instanceof LoanAccount))
                cbA.addItem(a.getNumber() + " — " + a.getAcHolder().getPersonDetails().getName() + " (PKR " + fmt(a.getAmount()) + ")");
        if (cbA.getItemCount() == 0) { err("No eligible accounts."); return; }
        JTextField fRef = styledField("Consumer / Reference No");
        JTextField fAmt = styledField("Amount (PKR)");
        Object[] msg = { label("Account"), cbA, label("Reference No"), fRef, label("Amount"), fAmt };
        if (confirm("Pay — " + bt.getLabel(), msg)) {
            try {
                java.util.List<Account> elig = new java.util.ArrayList<>();
                for (Account a : bank.getAcList()) if (!(a instanceof LoanAccount)) elig.add(a);
                BillPayment bp = bank.payBill(elig.get(cbA.getSelectedIndex()).getNumber(),
                        bt, fRef.getText(), parsePos(fAmt.getText(), "Amount"));
                DataStore.save(bank); refresh();
                ok(bt.getLabel() + " paid!\nPKR " + fmt(bp.getAmount()));
            } catch (Exception ex) { err(ex.getMessage()); }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    private void refresh() {
        lblGreet.setText(greetName());
        if (lblBalance != null) lblBalance.setText("PKR " + fmt(bank.totalAmount()));
        // re-render active screen
        switch (activeNav) {
            case 0: showHome();         break;
            case 1: showAccounts();     break;
            case 3: showTransactions(); break;
            case 4: showBills();        break;
        }
    }

    private JPanel roundCard(Color bg) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setBackground(bg);
        card.setOpaque(false);
        return card;
    }

    private JPanel sectionHeader(String title, String actionLabel, ActionListener al) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lbl = new JLabel(title);
        lbl.setFont(F_SECTION);
        lbl.setForeground(TEXT_DARK);
        row.add(lbl, BorderLayout.WEST);
        if (actionLabel != null && al != null) {
            JLabel act = new JLabel(actionLabel);
            act.setFont(F_SMALL);
            act.setForeground(PURPLE);
            act.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            act.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { al.actionPerformed(null); }
            });
            row.add(act, BorderLayout.EAST);
        }
        return row;
    }

    private JLabel pageTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_TITLE);
        l.setForeground(TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel chip(String text, Color bg, Color fg) {
        JPanel c = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setBackground(bg);
        c.setOpaque(false);
        c.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        JLabel l = new JLabel(text);
        l.setFont(F_SMALL);
        l.setForeground(fg);
        c.add(l);
        return c;
    }

    private JPanel divider() {
        JPanel d = new JPanel();
        d.setBackground(new Color(0xEE, 0xED, 0xFE));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(100, 1));
        return d;
    }

    private Component vgap(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    private JPanel emptyState(String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel l = new JLabel(msg);
        l.setFont(F_SMALL);
        l.setForeground(TEXT_GREY);
        p.add(l);
        return p;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField(22);
        f.setFont(F_BODY);
        f.setForeground(TEXT_DARK);
        f.setBackground(PURPLE_PALE);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xDD, 0xDA, 0xFF), 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    private JComboBox<String> styledCombo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(PURPLE_PALE);
        cb.setFont(F_BODY);
        return cb;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_SMALL);
        l.setForeground(TEXT_MED);
        return l;
    }

    private boolean confirm(String title, Object[] msg) {
        int r = JOptionPane.showConfirmDialog(this, msg, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return r == JOptionPane.OK_OPTION;
    }

    private String ask(String prompt, String title) {
        return JOptionPane.showInputDialog(this, prompt, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void ok(String msg)  { JOptionPane.showMessageDialog(this, msg, "Done", JOptionPane.PLAIN_MESSAGE); }
    private void err(String msg) { JOptionPane.showMessageDialog(this, "⚠  " + msg, "Error", JOptionPane.ERROR_MESSAGE); }

    private String fmt(double v) { return String.format("%,.2f", v); }
    private String initials(String name) {
        String[] p = name.trim().split("\\s+");
        return p.length >= 2 ? ("" + p[0].charAt(0) + p[1].charAt(0)).toUpperCase()
                             : name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private double parsePos(String s, String field) {
        double v;
        try { v = Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(field + " must be a valid number."); }
        if (v <= 0) throw new IllegalArgumentException(field + " must be greater than zero.");
        return v;
    }

    private void exitApp() {
        int opt = JOptionPane.showConfirmDialog(this, "Save before exiting?", "Exit",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (opt == JOptionPane.YES_OPTION) { DataStore.save(bank); System.exit(0); }
        else if (opt == JOptionPane.NO_OPTION) System.exit(0);
    }
}
