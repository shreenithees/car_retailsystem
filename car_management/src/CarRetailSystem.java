import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.io.*;
import javax.swing.Timer;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CarRetailSystem extends JFrame {
    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(241, 196, 15);
    private final Color BACKGROUND_COLOR = new Color(45, 45, 45); // Dark background
    private final Color TEXT_COLOR = Color.BLACK;
    private final Color CARD_BACKGROUND = new Color(60, 63, 65); // Dark card background
    private final Color HOVER_COLOR = new Color(80, 80, 80);

    // Components
    private JTabbedPane tabbedPane;
    private JPanel dashboardPanel, inventoryPanel, salesPanel, customersPanel, employeesPanel, reportsPanel, settingsPanel;
    private JPanel loginPanel;
    private JFrame loginFrame;

    // Data
    private DefaultTableModel inventoryModel, salesModel, customersModel, employeesModel;
    private ArrayList<Car> cars = new ArrayList<>();
    private ArrayList<Sale> sales = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();

    // Formatters
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public CarRetailSystem() {
        setTitle("🚗 Car Retail Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Set application icon
        try {
            ImageIcon icon = new ImageIcon("car_icon.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        // Create and show login screen first
        createLoginScreen();
    }

    private void createLoginScreen() {
        loginFrame = new JFrame("🔒 Login - Car Retail System");
        loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setUndecorated(true);
        loginFrame.setShape(new RoundRectangle2D.Double(0, 0, loginFrame.getWidth(), loginFrame.getHeight(), 30, 30));

        loginPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(41, 128, 185), 0, getHeight(), new Color(52, 152, 219));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("🚗 CAR RETAIL SYSTEM", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("👤 Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        usernameLabel.setForeground(Color.WHITE);

        JTextField usernameField = new JTextField(25);
        styleTextField(usernameField);

        JLabel passwordLabel = new JLabel("🔑 Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passwordLabel.setForeground(Color.WHITE);

        JPasswordField passwordField = new JPasswordField(25);
        styleTextField(passwordField);

        JButton loginButton = new JButton("🚪 LOGIN");
        styleButton(loginButton, PRIMARY_COLOR, Color.WHITE);

        // Add hover effect with gradient
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(52, 152, 219));
                loginButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(PRIMARY_COLOR);
                loginButton.setForeground(Color.WHITE);
            }
        });

        JButton exitButton = new JButton("❌ EXIT");
        styleButton(exitButton, new Color(231, 76, 60), Color.WHITE);

        // Add hover effect with gradient
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(new Color(241, 148, 138));
                exitButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(new Color(231, 76, 60));
                exitButton.setForeground(Color.WHITE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(logoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        gbc.gridy++;
        formPanel.add(exitButton, gbc);

        // Button actions

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());


            if (authenticate(username, password)) {
                loginFrame.dispose();
                initializeMainApplication();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                // Shake animation for wrong credentials
                new Thread(() -> {
                    Point originalLocation = loginFrame.getLocation();
                    for (int i = 0; i < 5; i++) {
                        try {
                            loginFrame.setLocation(originalLocation.x + 5, originalLocation.y);
                            Thread.sleep(50);
                            loginFrame.setLocation(originalLocation.x - 5, originalLocation.y);
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    loginFrame.setLocation(originalLocation);
                }).start();
            }
        });


        exitButton.addActionListener(e -> System.exit(0));

        loginPanel.add(formPanel, BorderLayout.CENTER);

        // Add footer with version info
        JLabel footerLabel = new JLabel("Car Retail System v1.0 © 2023", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        loginPanel.add(footerLabel, BorderLayout.SOUTH);

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);

        // Add fade-in animation
        loginFrame.setOpacity(0f);
        Timer fadeInTimer = new Timer(20, new ActionListener() {
            float opacity = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1.0f) {
                    opacity += 0.05f;
                    loginFrame.setOpacity(opacity);
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        fadeInTimer.start();
    }

    private boolean authenticate(String username, String password) {
        // Simple authentication for demo purposes
        return username.equals("admin") && password.equals("admin123");
    }

    private void initializeMainApplication() {
        // Initialize UI
        initUI();

        // Load sample data
        loadSampleData();

        // Show main application
        setVisible(true);

        // Add fade-in animation for main window
        setOpacity(0f);
        Timer fadeInTimer = new Timer(20, new ActionListener() {
            float opacity = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1.0f) {
                    opacity += 0.05f;
                    setOpacity(opacity);
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        fadeInTimer.start();
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_COLOR);
        field.setBackground(new Color(255, 255, 255, 200));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 150)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void initUI() {
        // Create tabbed pane with improved styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PRIMARY_COLOR);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Add hover effect to tabs
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tabbedPane.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tabbedPane.setForeground(Color.WHITE);
            }
        });

        // Create panels
        createDashboardPanel();
        createInventoryPanel();
        createSalesPanel();
        createCustomersPanel();
        createEmployeesPanel();
        createReportsPanel();
        createSettingsPanel();

        // Add tabs with icons
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);
        tabbedPane.addTab("🚗 Inventory", inventoryPanel);
        tabbedPane.addTab("💰 Sales", salesPanel);
        tabbedPane.addTab("👥 Customers", customersPanel);
        tabbedPane.addTab("👔 Employees", employeesPanel);
        tabbedPane.addTab("📈 Reports", reportsPanel);
        tabbedPane.addTab("⚙ Settings", settingsPanel);

        add(tabbedPane);
    }

    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBackground(BACKGROUND_COLOR);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("📊 Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel("📅 Today: " + dateFormat.format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        dashboardPanel.add(headerPanel, BorderLayout.NORTH);

        // Stats Panel with improved layout
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Stat Cards with improved styling and hover effects
        statsPanel.add(createStatCard("🚗 Total Cars", String.valueOf(cars.size()), PRIMARY_COLOR, "car_count.png"));
        statsPanel.add(createStatCard("✅ Available Cars",
                String.valueOf(cars.stream().filter(c -> c.getStatus().equals("Available")).count()),
                new Color(46, 204, 113), "available_cars.png"));
        statsPanel.add(createStatCard("💰 Sold Today",
                String.valueOf(sales.stream().filter(s -> s.getDate().equals(dateFormat.format(new Date()))).count()),
                new Color(231, 76, 60), "sold_today.png"));
        statsPanel.add(createStatCard("💵 Total Sales",
                currencyFormat.format(sales.stream().mapToDouble(Sale::getPrice).sum()),
                new Color(155, 89, 182), "total_sales.png"));
        statsPanel.add(createStatCard("👥 New Customers",
                String.valueOf(customers.size()),
                new Color(26, 188, 156), "new_customers.png"));
        statsPanel.add(createStatCard("👔 Employees",
                String.valueOf(employees.size()),
                new Color(230, 126, 34), "employees.png"));

        dashboardPanel.add(statsPanel, BorderLayout.CENTER);

        // Bottom panel with recent activity and quick actions
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Recent Activity with improved styling
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(CARD_BACKGROUND);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel activityTitle = new JLabel("📋 Recent Sales Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitle.setForeground(Color.WHITE);
        activityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        activityPanel.add(activityTitle, BorderLayout.NORTH);

        String[] activityColumns = {"Date", "Customer", "Car", "Amount", "Salesperson"};
        Object[][] activityData = getRecentSalesData();

        JTable activityTable = new JTable(activityData, activityColumns);
        styleTable(activityTable);
        activityTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(activityTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        // Quick Actions Panel
        JPanel quickActionsPanel = new JPanel(new BorderLayout());
        quickActionsPanel.setBackground(CARD_BACKGROUND);
        quickActionsPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel quickActionsTitle = new JLabel("⚡ Quick Actions");
        quickActionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quickActionsTitle.setForeground(Color.WHITE);
        quickActionsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        quickActionsPanel.add(quickActionsTitle, BorderLayout.NORTH);

        JPanel actionsGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        actionsGrid.setBackground(CARD_BACKGROUND);

        actionsGrid.add(createQuickActionButton("💰 New Sale", "new_sale.png", SECONDARY_COLOR, e -> showNewSaleDialog()));
        actionsGrid.add(createQuickActionButton("➕ Add Car", "add_car.png", new Color(46, 204, 113), e -> showAddCarDialog()));
        actionsGrid.add(createQuickActionButton("👥 Add Customer", "add_customer.png", new Color(155, 89, 182), e -> showAddCustomerDialog()));
        actionsGrid.add(createQuickActionButton("👔 Add Employee", "add_employee.png", new Color(230, 126, 34), e -> showAddEmployeeDialog()));
        actionsGrid.add(createQuickActionButton("📊 Generate Report", "generate_report.png", new Color(26, 188, 156), e -> showReportDialog()));
        actionsGrid.add(createQuickActionButton("⚠ View Alerts", "view_alerts.png", new Color(231, 76, 60), e -> showAlertsDialog()));

        quickActionsPanel.add(actionsGrid, BorderLayout.CENTER);

        bottomPanel.add(activityPanel);
        bottomPanel.add(quickActionsPanel);

        dashboardPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private Object[][] getRecentSalesData() {
        // Get the 5 most recent sales
        List<Sale> recentSales = sales.stream()
                .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                .limit(5)
                .toList();

        Object[][] data = new Object[recentSales.size()][5];

        for (int i = 0; i < recentSales.size(); i++) {
            Sale sale = recentSales.get(i);
            String customerName = customers.stream()
                    .filter(c -> c.getId() == sale.getCustomerId())
                    .findFirst()
                    .map(Customer::getName)
                    .orElse("Unknown");

            String carDetails = cars.stream()
                    .filter(c -> c.getId() == sale.getCarId())
                    .findFirst()
                    .map(c -> c.getMake() + " " + c.getModel())
                    .orElse("Unknown");

            String employeeName = employees.stream()
                    .filter(e -> e.getId() == sale.getEmployeeId())
                    .findFirst()
                    .map(Employee::getName)
                    .orElse("Unknown");

            data[i] = new Object[]{
                    sale.getDate(),
                    customerName,
                    carDetails,
                    currencyFormat.format(sale.getPrice()),
                    employeeName
            };
        }

        return data;
    }

    private JPanel createStatCard(String title, String value, Color color, String iconPath) {
        class StatCardPanel extends JPanel {
            private boolean isHovered = false;

            public StatCardPanel() {
                super(new BorderLayout(10, 10));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isHovered) {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(0, 0, color.brighter(), 0, getHeight(), color);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        }

        StatCardPanel card = new StatCardPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Header with icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);

        try {
            ImageIcon icon = new ImageIcon(iconPath);
            JLabel iconLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
            headerPanel.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // If icon not found, just show text
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);

        // Value with improved styling
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        // Add hover effect with animation
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.isHovered = true;
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(color, 2),
                        new EmptyBorder(15, 15, 15, 15)
                ));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.isHovered = false;
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(15, 15, 15, 15)
                ));
                card.repaint();
            }
        });

        return card;
    }

    private JButton createQuickActionButton(String text, String iconPath, Color bgColor, ActionListener action) {
        class QuickActionButton extends JButton {
            private boolean isHovered = false;

            public QuickActionButton(String text) {
                super(text);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (isHovered) {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(0, 0, bgColor.brighter(), 0, getHeight(), bgColor);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        }

        QuickActionButton button = new QuickActionButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon icon = new ImageIcon(iconPath);
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            button.setIconTextGap(10);
        } catch (Exception e) {
            // If icon not found, just show text
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.isHovered = true;
                button.setBackground(bgColor.brighter());
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.isHovered = false;
                button.setBackground(bgColor);
                button.repaint();
            }
        });

        button.addActionListener(action);
        return button;
    }

    private void createInventoryPanel() {
        inventoryPanel = new JPanel(new BorderLayout(10, 10));
        inventoryPanel.setBackground(BACKGROUND_COLOR);
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("🚗 Car Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);

        JButton addButton = createStyledButton("➕ Add Car", SECONDARY_COLOR, "add.png");
        JButton editButton = createStyledButton("✏ Edit Car", new Color(26, 188, 156), "edit.png");
        JButton deleteButton = createStyledButton("🗑 Delete Car", new Color(231, 76, 60), "delete.png");
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(52, 73, 94), "refresh.png");
        JButton exportButton = createStyledButton("📤 Export", new Color(155, 89, 182), "export.png");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        inventoryPanel.add(headerPanel, BorderLayout.NORTH);

        // Table with improved styling
        String[] columns = {"ID", "Make", "Model", "Year", "Color", "Price", "Status", "Mileage", "VIN"};
        inventoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable inventoryTable = new JTable(inventoryModel);
        styleTable(inventoryTable);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add row sorter
        inventoryTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);

        // Search Panel with improved styling
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "🔍 Search Inventory", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));

        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchFieldsPanel.setBackground(BACKGROUND_COLOR);

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_COLOR);

        JComboBox<String> searchType = new JComboBox<>(new String[]{"All", "Make", "Model", "Year", "Color", "Price Range", "Status"});
        searchType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchType.setForeground(TEXT_COLOR);

        JButton searchButton = createStyledButton("🔍 Search", PRIMARY_COLOR, "search.png");
        JButton clearButton = createStyledButton("🧹 Clear", new Color(120, 120, 120), "clear.png");

        searchFieldsPanel.add(new JLabel("Search:"));
        searchFieldsPanel.add(searchField);
        searchFieldsPanel.add(new JLabel("By:"));
        searchFieldsPanel.add(searchType);
        searchFieldsPanel.add(searchButton);
        searchFieldsPanel.add(clearButton);

        searchPanel.add(searchFieldsPanel, BorderLayout.CENTER);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(BACKGROUND_COLOR);

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterLabel.setForeground(Color.WHITE);

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Available", "Sold", "Reserved", "In Service"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.setForeground(TEXT_COLOR);

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);

        searchPanel.add(filterPanel, BorderLayout.SOUTH);

        inventoryPanel.add(searchPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> showAddCarDialog());
        editButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                int carId = (int) inventoryModel.getValueAt(selectedRow, 0);
                showEditCarDialog(carId);
            } else {
                JOptionPane.showMessageDialog(inventoryPanel, "Please select a car to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                int carId = (int) inventoryModel.getValueAt(selectedRow, 0);
                deleteCar(carId);
            } else {
                JOptionPane.showMessageDialog(inventoryPanel, "Please select a car to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> refreshInventoryTable());
        searchButton.addActionListener(e -> searchInventory(searchField.getText(), (String) searchType.getSelectedItem()));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshInventoryTable();
        });
        exportButton.addActionListener(e -> exportToCSV(inventoryTable, "inventory_export.csv"));
        statusFilter.addActionListener(e -> filterByStatus((String) statusFilter.getSelectedItem()));
    }

    private void showEditCarDialog(int carId) {
        Car carToEdit = cars.stream().filter(c -> c.getId() == carId).findFirst().orElse(null);
        if (carToEdit == null) {
            JOptionPane.showMessageDialog(this, "Car not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit Car", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Form panel with improved styling
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields with labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Make:"), gbc);

        gbc.gridx = 1;
        JTextField makeField = createFormTextField();
        makeField.setText(carToEdit.getMake());
        formPanel.add(makeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Model:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = createFormTextField();
        modelField.setText(carToEdit.getModel());
        formPanel.add(modelField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Year:"), gbc);

        gbc.gridx = 1;
        JTextField yearField = createFormTextField();
        yearField.setText(String.valueOf(carToEdit.getYear()));
        formPanel.add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Color:"), gbc);

        gbc.gridx = 1;
        JTextField colorField = createFormTextField();
        colorField.setText(carToEdit.getColor());
        formPanel.add(colorField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Price:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = createFormTextField();
        priceField.setText(String.valueOf(carToEdit.getPrice()));
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Mileage:"), gbc);

        gbc.gridx = 1;
        JTextField mileageField = createFormTextField();
        mileageField.setText(String.valueOf(carToEdit.getMileage()));
        formPanel.add(mileageField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("VIN:"), gbc);

        gbc.gridx = 1;
        JTextField vinField = createFormTextField();
        vinField.setText(carToEdit.getVin());
        formPanel.add(vinField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Status:"), gbc);

        gbc.gridx = 1;
        String[] statusOptions = {"Available", "Sold", "Reserved", "In Service"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusCombo.setSelectedItem(carToEdit.getStatus());
        formPanel.add(statusCombo, gbc);

        // Buttons with improved styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            // Validate and save car
            try {
                carToEdit.setMake(makeField.getText());
                carToEdit.setModel(modelField.getText());
                carToEdit.setYear(Integer.parseInt(yearField.getText()));
                carToEdit.setColor(colorField.getText());
                carToEdit.setPrice(Double.parseDouble(priceField.getText()));
                carToEdit.setStatus((String) statusCombo.getSelectedItem());
                carToEdit.setMileage(Integer.parseInt(mileageField.getText()));
                carToEdit.setVin(vinField.getText());

                refreshInventoryTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Car updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for year, price and mileage", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteCar(int carId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this car?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            cars.removeIf(c -> c.getId() == carId);
            refreshInventoryTable();
            JOptionPane.showMessageDialog(this, "Car deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createSalesPanel() {
        salesPanel = new JPanel(new BorderLayout(10, 10));
        salesPanel.setBackground(BACKGROUND_COLOR);
        salesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("💰 Sales Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);

        JButton newSaleButton = createStyledButton("💰 New Sale", SECONDARY_COLOR, "new_sale.png");
        JButton returnButton = createStyledButton("🔄 Process Return", new Color(231, 76, 60), "return.png");
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(52, 73, 94), "refresh.png");
        JButton exportButton = createStyledButton("📤 Export", new Color(155, 89, 182), "export.png");

        buttonPanel.add(newSaleButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        salesPanel.add(headerPanel, BorderLayout.NORTH);

        // Table with improved styling
        String[] columns = {"Sale ID", "Date", "Customer", "Car", "Price", "Salesperson", "Payment Method"};
        salesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable salesTable = new JTable(salesModel);
        styleTable(salesTable);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add row sorter
        salesTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        salesPanel.add(scrollPane, BorderLayout.CENTER);

        // Search Panel with improved styling
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "🔍 Search Sales",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        ));

        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchFieldsPanel.setBackground(BACKGROUND_COLOR);

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_COLOR);

        JComboBox<String> searchType = new JComboBox<>(new String[]{"All", "Customer", "Car", "Salesperson", "Payment Method"});
        searchType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchType.setForeground(TEXT_COLOR);

        JButton searchButton = createStyledButton("🔍 Search", PRIMARY_COLOR, "search.png");
        JButton clearButton = createStyledButton("🧹 Clear", new Color(120, 120, 120), "clear.png");

        searchFieldsPanel.add(new JLabel("Search:"));
        searchFieldsPanel.add(searchField);
        searchFieldsPanel.add(new JLabel("By:"));
        searchFieldsPanel.add(searchType);
        searchFieldsPanel.add(searchButton);
        searchFieldsPanel.add(clearButton);

        searchPanel.add(searchFieldsPanel, BorderLayout.CENTER);

        // Date range panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        datePanel.setBackground(BACKGROUND_COLOR);

        JLabel dateLabel = new JLabel("Date Range:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.WHITE);

        JTextField fromDateField = new JTextField(10);
        fromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fromDateField.setForeground(TEXT_COLOR);

        JLabel toLabel = new JLabel("to");
        toLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toLabel.setForeground(Color.WHITE);

        JTextField toDateField = new JTextField(10);
        toDateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toDateField.setForeground(TEXT_COLOR);

        JButton dateFilterButton = createStyledButton("🔍 Filter", PRIMARY_COLOR, "filter.png");

        datePanel.add(dateLabel);
        datePanel.add(fromDateField);
        datePanel.add(toLabel);
        datePanel.add(toDateField);
        datePanel.add(dateFilterButton);

        searchPanel.add(datePanel, BorderLayout.SOUTH);

        salesPanel.add(searchPanel, BorderLayout.SOUTH);

        // Button actions
        newSaleButton.addActionListener(e -> showNewSaleDialog());
        refreshButton.addActionListener(e -> refreshSalesTable());
        searchButton.addActionListener(e -> searchSales(searchField.getText(), (String) searchType.getSelectedItem()));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            fromDateField.setText("");
            toDateField.setText("");
            refreshSalesTable();
        });
        exportButton.addActionListener(e -> exportToCSV(salesTable, "sales_export.csv"));
        dateFilterButton.addActionListener(e -> filterSalesByDate(fromDateField.getText(), toDateField.getText()));
    }

    private void createCustomersPanel() {
        customersPanel = new JPanel(new BorderLayout(10, 10));
        customersPanel.setBackground(BACKGROUND_COLOR);
        customersPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("👥 Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);

        JButton addButton = createStyledButton("➕ Add Customer", SECONDARY_COLOR, "add.png");
        JButton editButton = createStyledButton("✏ Edit Customer", new Color(26, 188, 156), "edit.png");
        JButton deleteButton = createStyledButton("🗑 Delete Customer", new Color(231, 76, 60), "delete.png");
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(52, 73, 94), "refresh.png");
        JButton exportButton = createStyledButton("📤 Export", new Color(155, 89, 182), "export.png");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        customersPanel.add(headerPanel, BorderLayout.NORTH);

        // Table with improved styling
        String[] columns = {"ID", "Name", "Phone", "Email", "Address", "Purchases", "Last Purchase"};
        customersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable customersTable = new JTable(customersModel);
        styleTable(customersTable);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add row sorter
        customersTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(customersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        customersPanel.add(scrollPane, BorderLayout.CENTER);

        // Search Panel with improved styling
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "🔍 Search Customers",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        ));

        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchFieldsPanel.setBackground(BACKGROUND_COLOR);

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_COLOR);

        JComboBox<String> searchType = new JComboBox<>(new String[]{"All", "Name", "Phone", "Email", "Address"});
        searchType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchType.setForeground(TEXT_COLOR);

        JButton searchButton = createStyledButton("🔍 Search", PRIMARY_COLOR, "search.png");
        JButton clearButton = createStyledButton("🧹 Clear", new Color(120, 120, 120), "clear.png");

        searchFieldsPanel.add(new JLabel("Search:"));
        searchFieldsPanel.add(searchField);
        searchFieldsPanel.add(new JLabel("By:"));
        searchFieldsPanel.add(searchType);
        searchFieldsPanel.add(searchButton);
        searchFieldsPanel.add(clearButton);

        searchPanel.add(searchFieldsPanel, BorderLayout.CENTER);

        customersPanel.add(searchPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> showAddCustomerDialog());
        editButton.addActionListener(e -> {
            int selectedRow = customersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customersModel.getValueAt(selectedRow, 0);
                showEditCustomerDialog(customerId);
            } else {
                JOptionPane.showMessageDialog(customersPanel, "Please select a customer to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = customersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customersModel.getValueAt(selectedRow, 0);
                deleteCustomer(customerId);
            } else {
                JOptionPane.showMessageDialog(customersPanel, "Please select a customer to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> refreshCustomersTable());
        searchButton.addActionListener(e -> searchCustomers(searchField.getText(), (String) searchType.getSelectedItem()));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshCustomersTable();
        });
        exportButton.addActionListener(e -> exportToCSV(customersTable, "customers_export.csv"));
    }

    private void showEditCustomerDialog(int customerId) {
        Customer customerToEdit = customers.stream().filter(c -> c.getId() == customerId).findFirst().orElse(null);
        if (customerToEdit == null) {
            JOptionPane.showMessageDialog(this, "Customer not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit Customer", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Name:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = createFormTextField();
        nameField.setText(customerToEdit.getName());
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Phone:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = createFormTextField();
        phoneField.setText(customerToEdit.getPhone());
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = createFormTextField();
        emailField.setText(customerToEdit.getEmail());
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Address:"), gbc);

        gbc.gridx = 1;
        JTextField addressField = createFormTextField();
        addressField.setText(customerToEdit.getAddress());
        formPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Driver License:"), gbc);

        gbc.gridx = 1;
        JTextField licenseField = createFormTextField();
        licenseField.setText(customerToEdit.getDriverLicense());
        formPanel.add(licenseField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and phone are required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            customerToEdit.setName(nameField.getText());
            customerToEdit.setPhone(phoneField.getText());
            customerToEdit.setEmail(emailField.getText());
            customerToEdit.setAddress(addressField.getText());
            customerToEdit.setDriverLicense(licenseField.getText());

            refreshCustomersTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteCustomer(int customerId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this customer?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            customers.removeIf(c -> c.getId() == customerId);
            refreshCustomersTable();
            JOptionPane.showMessageDialog(this, "Customer deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createEmployeesPanel() {
        employeesPanel = new JPanel(new BorderLayout(10, 10));
        employeesPanel.setBackground(BACKGROUND_COLOR);
        employeesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("👔 Employee Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);

        JButton addButton = createStyledButton("➕ Add Employee", SECONDARY_COLOR, "add.png");
        JButton editButton = createStyledButton("✏ Edit Employee", new Color(26, 188, 156), "edit.png");
        JButton deleteButton = createStyledButton("🗑 Delete Employee", new Color(231, 76, 60), "delete.png");
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(52, 73, 94), "refresh.png");
        JButton exportButton = createStyledButton("📤 Export", new Color(155, 89, 182), "export.png");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        employeesPanel.add(headerPanel, BorderLayout.NORTH);

        // Table with improved styling
        String[] columns = {"ID", "Name", "Position", "Phone", "Email", "Hire Date", "Salary", "Sales Count"};
        employeesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable employeesTable = new JTable(employeesModel);
        styleTable(employeesTable);
        employeesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add row sorter
        employeesTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(employeesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        employeesPanel.add(scrollPane, BorderLayout.CENTER);

        // Search Panel with improved styling
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "🔍 Search Employees",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        ));

        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchFieldsPanel.setBackground(BACKGROUND_COLOR);

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_COLOR);

        JComboBox<String> searchType = new JComboBox<>(new String[]{"All", "Name", "Position", "Phone", "Email"});
        searchType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchType.setForeground(TEXT_COLOR);

        JButton searchButton = createStyledButton("🔍 Search", PRIMARY_COLOR, "search.png");
        JButton clearButton = createStyledButton("🧹 Clear", new Color(120, 120, 120), "clear.png");

        searchFieldsPanel.add(new JLabel("Search:"));
        searchFieldsPanel.add(searchField);
        searchFieldsPanel.add(new JLabel("By:"));
        searchFieldsPanel.add(searchType);
        searchFieldsPanel.add(searchButton);
        searchFieldsPanel.add(clearButton);

        searchPanel.add(searchFieldsPanel, BorderLayout.CENTER);

        employeesPanel.add(searchPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> showAddEmployeeDialog());
        editButton.addActionListener(e -> {
            int selectedRow = employeesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int employeeId = (int) employeesModel.getValueAt(selectedRow, 0);
                showEditEmployeeDialog(employeeId);
            } else {
                JOptionPane.showMessageDialog(employeesPanel, "Please select an employee to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = employeesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int employeeId = (int) employeesModel.getValueAt(selectedRow, 0);
                deleteEmployee(employeeId);
            } else {
                JOptionPane.showMessageDialog(employeesPanel, "Please select an employee to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> refreshEmployeesTable());
        searchButton.addActionListener(e -> searchEmployees(searchField.getText(), (String) searchType.getSelectedItem()));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshEmployeesTable();
        });
        exportButton.addActionListener(e -> exportToCSV(employeesTable, "employees_export.csv"));
    }

    private void showEditEmployeeDialog(int employeeId) {
        Employee employeeToEdit = employees.stream().filter(e -> e.getId() == employeeId).findFirst().orElse(null);
        if (employeeToEdit == null) {
            JOptionPane.showMessageDialog(this, "Employee not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit Employee", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Name:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = createFormTextField();
        nameField.setText(employeeToEdit.getName());
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Position:"), gbc);

        gbc.gridx = 1;
        String[] positionOptions = {"Salesperson", "Manager", "Mechanic", "Accountant", "Administrator"};
        JComboBox<String> positionCombo = new JComboBox<>(positionOptions);
        positionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        positionCombo.setSelectedItem(employeeToEdit.getPosition());
        formPanel.add(positionCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Phone:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = createFormTextField();
        phoneField.setText(employeeToEdit.getPhone());
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = createFormTextField();
        emailField.setText(employeeToEdit.getEmail());
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Hire Date:"), gbc);

        gbc.gridx = 1;
        JTextField hireDateField = createFormTextField();
        hireDateField.setText(employeeToEdit.getHireDate());
        formPanel.add(hireDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Salary:"), gbc);

        gbc.gridx = 1;
        JTextField salaryField = createFormTextField();
        salaryField.setText(String.valueOf(employeeToEdit.getSalary()));
        formPanel.add(salaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = createFormTextField();
        usernameField.setText(employeeToEdit.getUsername());
        formPanel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordField.setText(employeeToEdit.getPassword());
        formPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            try {
                employeeToEdit.setName(nameField.getText());
                employeeToEdit.setPosition((String) positionCombo.getSelectedItem());
                employeeToEdit.setPhone(phoneField.getText());
                employeeToEdit.setEmail(emailField.getText());
                employeeToEdit.setHireDate(hireDateField.getText());
                employeeToEdit.setSalary(Double.parseDouble(salaryField.getText()));
                employeeToEdit.setUsername(usernameField.getText());
                employeeToEdit.setPassword(new String(passwordField.getPassword()));

                refreshEmployeesTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid salary", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteEmployee(int employeeId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this employee?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            employees.removeIf(e -> e.getId() == employeeId);
            refreshEmployeesTable();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createReportsPanel() {
        reportsPanel = new JPanel(new BorderLayout(10, 10));
        reportsPanel.setBackground(BACKGROUND_COLOR);
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("📈 Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        reportsPanel.add(headerPanel, BorderLayout.NORTH);

        // Report options with improved styling
        JPanel reportOptionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        reportOptionsPanel.setBackground(BACKGROUND_COLOR);
        reportOptionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        reportOptionsPanel.add(createReportOptionCard("💰 Sales Report", "sales_report.png", PRIMARY_COLOR));
        reportOptionsPanel.add(createReportOptionCard("🚗 Inventory Report", "inventory_report.png", new Color(46, 204, 113)));
        reportOptionsPanel.add(createReportOptionCard("👥 Customer Report", "customer_report.png", new Color(155, 89, 182)));
        reportOptionsPanel.add(createReportOptionCard("👔 Employee Performance", "employee_report.png", new Color(230, 126, 34)));

        reportsPanel.add(reportOptionsPanel, BorderLayout.CENTER);

        // Date range panel with improved styling
        JPanel dateRangePanel = new JPanel(new BorderLayout(10, 10));
        dateRangePanel.setBackground(BACKGROUND_COLOR);
        dateRangePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "📅 Report Parameters",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        ));

        JPanel dateFieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dateFieldsPanel.setBackground(BACKGROUND_COLOR);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fromLabel.setForeground(Color.WHITE);

        JTextField fromDateField = new JTextField(10);
        fromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fromDateField.setForeground(TEXT_COLOR);

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toLabel.setForeground(Color.WHITE);

        JTextField toDateField = new JTextField(10);
        toDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toDateField.setForeground(TEXT_COLOR);

        JButton generateButton = createStyledButton("📊 Generate Report", PRIMARY_COLOR, "generate_report.png");
        JButton printButton = createStyledButton("🖨 Print", new Color(52, 73, 94), "print.png");

        dateFieldsPanel.add(fromLabel);
        dateFieldsPanel.add(fromDateField);
        dateFieldsPanel.add(toLabel);
        dateFieldsPanel.add(toDateField);
        dateFieldsPanel.add(generateButton);
        dateFieldsPanel.add(printButton);

        dateRangePanel.add(dateFieldsPanel, BorderLayout.CENTER);

        reportsPanel.add(dateRangePanel, BorderLayout.SOUTH);

        // Button actions
        generateButton.addActionListener(e -> generateReport(fromDateField.getText(), toDateField.getText()));
        printButton.addActionListener(e -> printReport());
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout(10, 10));
        settingsPanel.setBackground(BACKGROUND_COLOR);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with improved styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel("⚙ System Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        settingsPanel.add(headerPanel, BorderLayout.NORTH);

        // Settings options
        JPanel settingsOptionsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        settingsOptionsPanel.setBackground(BACKGROUND_COLOR);
        settingsOptionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Backup/Restore panel
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupPanel.setBackground(CARD_BACKGROUND);
        backupPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel backupLabel = new JLabel("💾 Data Backup & Restore");
        backupLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backupLabel.setForeground(Color.WHITE);
        backupPanel.add(backupLabel, BorderLayout.NORTH);

        JPanel backupButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        backupButtonsPanel.setBackground(CARD_BACKGROUND);

        JButton backupButton = createStyledButton("💾 Backup Data", PRIMARY_COLOR, "backup.png");
        JButton restoreButton = createStyledButton("🔄 Restore Data", new Color(46, 204, 113), "restore.png");

        backupButtonsPanel.add(backupButton);
        backupButtonsPanel.add(restoreButton);

        backupPanel.add(backupButtonsPanel, BorderLayout.CENTER);

        // User Management panel
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(CARD_BACKGROUND);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel userLabel = new JLabel("👤 User Management");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel, BorderLayout.NORTH);

        JPanel userButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userButtonsPanel.setBackground(CARD_BACKGROUND);

        JButton addUserButton = createStyledButton("➕ Add User", PRIMARY_COLOR, "add_user.png");
        JButton editUserButton = createStyledButton("✏ Edit User", new Color(155, 89, 182), "edit_user.png");
        JButton changePasswordButton = createStyledButton("🔑 Change Password", new Color(230, 126, 34), "password.png");

        userButtonsPanel.add(addUserButton);
        userButtonsPanel.add(editUserButton);
        userButtonsPanel.add(changePasswordButton);

        userPanel.add(userButtonsPanel, BorderLayout.CENTER);

        // System Preferences panel
        JPanel prefPanel = new JPanel(new BorderLayout());
        prefPanel.setBackground(CARD_BACKGROUND);
        prefPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel prefLabel = new JLabel("🎨 System Preferences");
        prefLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        prefLabel.setForeground(Color.WHITE);
        prefPanel.add(prefLabel, BorderLayout.NORTH);

        JPanel prefFieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        prefFieldsPanel.setBackground(CARD_BACKGROUND);

        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        themeLabel.setForeground(Color.WHITE);

        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Light", "Dark", "Blue", "Green"});
        themeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        themeCombo.setSelectedItem("Dark");

        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currencyLabel.setForeground(Color.WHITE);

        JComboBox<String> currencyCombo = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY"});
        currencyCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        prefFieldsPanel.add(themeLabel);
        prefFieldsPanel.add(themeCombo);
        prefFieldsPanel.add(currencyLabel);
        prefFieldsPanel.add(currencyCombo);

        JButton savePrefButton = createStyledButton("💾 Save Preferences", PRIMARY_COLOR, "save.png");

        prefPanel.add(prefFieldsPanel, BorderLayout.CENTER);
        prefPanel.add(savePrefButton, BorderLayout.SOUTH);

        settingsOptionsPanel.add(backupPanel);
        settingsOptionsPanel.add(userPanel);
        settingsOptionsPanel.add(prefPanel);

        settingsPanel.add(settingsOptionsPanel, BorderLayout.CENTER);

        // Button actions
        backupButton.addActionListener(e -> backupData());
        restoreButton.addActionListener(e -> restoreData());
        addUserButton.addActionListener(e -> showAddUserDialog());
        editUserButton.addActionListener(e -> showEditUserDialog());
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        savePrefButton.addActionListener(e -> savePreferences(
                (String) themeCombo.getSelectedItem(),
                (String) currencyCombo.getSelectedItem()
        ));
    }

    private JPanel createReportOptionCard(String title, String iconPath, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Icon and title
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(CARD_BACKGROUND);

        try {
            ImageIcon icon = new ImageIcon(iconPath);
            JLabel iconLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
            headerPanel.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // If icon not found, just show text
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);

        // Description
        JTextArea description = new JTextArea(getReportDescription(title));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(Color.WHITE);
        description.setBackground(CARD_BACKGROUND);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setEditable(false);
        description.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(description, BorderLayout.CENTER);

        // Add click effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Generating " + title + "...", "Report", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(color.brighter(), 2),
                        new EmptyBorder(15, 15, 15, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(color, 2),
                        new EmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return card;
    }

    private String getReportDescription(String title) {
        switch (title) {
            case "💰 Sales Report":
                return "Generate detailed sales reports by date range, salesperson, or vehicle type. Includes revenue analysis and trends.";
            case "🚗 Inventory Report":
                return "View current inventory status, including available vehicles, age of inventory, and valuation reports.";
            case "👥 Customer Report":
                return "Analyze customer demographics, purchase history, and loyalty metrics.";
            case "👔 Employee Performance":
                return "Track sales performance, commission calculations, and productivity metrics for employees.";
            default:
                return "Generate comprehensive reports for analysis and decision making.";
        }
    }

    private JButton createStyledButton(String text, Color bgColor, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        try {
            ImageIcon icon = new ImageIcon(iconPath);
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
            button.setIconTextGap(8);
        } catch (Exception e) {
            // If icon not found, just show text
        }

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.setForeground(TEXT_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setForeground(TEXT_COLOR);
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(Color.WHITE);
        table.setBackground(CARD_BACKGROUND);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(100, 100, 100));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
    }

    // Dialog methods
    private void showAddCarDialog() {
        JDialog dialog = new JDialog(this, "Add New Car", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Form panel with improved styling
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields with labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Make:"), gbc);

        gbc.gridx = 1;
        JTextField makeField = createFormTextField();
        formPanel.add(makeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Model:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = createFormTextField();
        formPanel.add(modelField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Year:"), gbc);

        gbc.gridx = 1;
        JTextField yearField = createFormTextField();
        formPanel.add(yearField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Color:"), gbc);

        gbc.gridx = 1;
        JTextField colorField = createFormTextField();
        formPanel.add(colorField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Price:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = createFormTextField();
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Mileage:"), gbc);

        gbc.gridx = 1;
        JTextField mileageField = createFormTextField();
        formPanel.add(mileageField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("VIN:"), gbc);

        gbc.gridx = 1;
        JTextField vinField = createFormTextField();
        formPanel.add(vinField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Status:"), gbc);

        gbc.gridx = 1;
        String[] statusOptions = {"Available", "Sold", "Reserved", "In Service"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(statusCombo, gbc);

        // Buttons with improved styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            // Validate and save car
            try {
                Car car = new Car(
                        cars.size() + 1,
                        makeField.getText(),
                        modelField.getText(),
                        Integer.parseInt(yearField.getText()),
                        colorField.getText(),
                        Double.parseDouble(priceField.getText()),
                        (String) statusCombo.getSelectedItem(),
                        Integer.parseInt(mileageField.getText()),
                        vinField.getText()
                );

                cars.add(car);
                refreshInventoryTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Car added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for year, price and mileage", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createFormTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void showNewSaleDialog() {
        JDialog dialog = new JDialog(this, "Record New Sale", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Sale Date:"), gbc);

        gbc.gridx = 1;
        JTextField dateField = createFormTextField();
        dateField.setText(dateFormat.format(new Date()));
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Customer:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customers.forEach(c -> customerCombo.addItem(c.getName() + " (" + c.getId() + ")"));
        formPanel.add(customerCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Car:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> carCombo = new JComboBox<>();
        carCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cars.stream()
                .filter(c -> c.getStatus().equals("Available"))
                .forEach(c -> carCombo.addItem(c.getMake() + " " + c.getModel() + " (" + c.getId() + ")"));
        formPanel.add(carCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Sale Price:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = createFormTextField();
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Salesperson:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> employeeCombo = new JComboBox<>();
        employeeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employees.forEach(e -> employeeCombo.addItem(e.getName() + " (" + e.getId() + ")"));
        formPanel.add(employeeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Payment Method:"), gbc);

        gbc.gridx = 1;
        String[] paymentOptions = {"Cash", "Credit Card", "Bank Loan", "Lease"};
        JComboBox<String> paymentCombo = new JComboBox<>(paymentOptions);
        paymentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(paymentCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Record Sale", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            try {
                int customerId = Integer.parseInt(customerCombo.getSelectedItem().toString().split("\\(")[1].replace(")", ""));
                int carId = Integer.parseInt(carCombo.getSelectedItem().toString().split("\\(")[1].replace(")", ""));
                int employeeId = Integer.parseInt(employeeCombo.getSelectedItem().toString().split("\\(")[1].replace(")", ""));

                Sale sale = new Sale(
                        sales.size() + 1,
                        dateField.getText(),
                        customerId,
                        carId,
                        Double.parseDouble(priceField.getText()),
                        employeeId,
                        (String) paymentCombo.getSelectedItem()
                );

                sales.add(sale);

                // Update car status
                cars.stream()
                        .filter(c -> c.getId() == carId)
                        .findFirst()
                        .ifPresent(c -> c.setStatus("Sold"));

                refreshSalesTable();
                refreshInventoryTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Sale recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please fill all fields correctly", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add New Customer", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Name:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = createFormTextField();
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Phone:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = createFormTextField();
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = createFormTextField();
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Address:"), gbc);

        gbc.gridx = 1;
        JTextField addressField = createFormTextField();
        formPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Driver License:"), gbc);

        gbc.gridx = 1;
        JTextField licenseField = createFormTextField();
        formPanel.add(licenseField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and phone are required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer customer = new Customer(
                    customers.size() + 1,
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    licenseField.getText()
            );

            customers.add(customer);
            refreshCustomersTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Name:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = createFormTextField();
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Position:"), gbc);

        gbc.gridx = 1;
        String[] positionOptions = {"Salesperson", "Manager", "Mechanic", "Accountant", "Administrator"};
        JComboBox<String> positionCombo = new JComboBox<>(positionOptions);
        positionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(positionCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Phone:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = createFormTextField();
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = createFormTextField();
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Hire Date:"), gbc);

        gbc.gridx = 1;
        JTextField hireDateField = createFormTextField();
        hireDateField.setText(dateFormat.format(new Date()));
        formPanel.add(hireDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Salary:"), gbc);

        gbc.gridx = 1;
        JTextField salaryField = createFormTextField();
        formPanel.add(salaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = createFormTextField();
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            try {
                Employee employee = new Employee(
                        employees.size() + 1,
                        nameField.getText(),
                        (String) positionCombo.getSelectedItem(),
                        phoneField.getText(),
                        emailField.getText(),
                        hireDateField.getText(),
                        Double.parseDouble(salaryField.getText()),
                        usernameField.getText(),
                        new String(passwordField.getPassword())
                );

                employees.add(employee);
                refreshEmployeesTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid salary", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = createFormTextField();
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Role:"), gbc);

        gbc.gridx = 1;
        String[] roleOptions = {"Admin", "Manager", "Salesperson"};
        JComboBox<String> roleCombo = new JComboBox<>(roleOptions);
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(roleCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username and password are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // In a real application, you would save the user to a database
            JOptionPane.showMessageDialog(dialog, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        JOptionPane.showMessageDialog(this, "Select a user to edit from the employees list", "Edit User", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Current Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(currentPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("New Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(createFormLabel("Confirm New Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = createStyledButton("💾 Save", PRIMARY_COLOR, "save.png");
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60), "cancel.png");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(e -> {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "New passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // In a real application, you would verify the current password and update it
            JOptionPane.showMessageDialog(dialog, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showReportDialog() {
        JOptionPane.showMessageDialog(this, "Select a report type and date range to generate a report", "Generate Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAlertsDialog() {
        JOptionPane.showMessageDialog(this, "No alerts at this time", "View Alerts", JOptionPane.INFORMATION_MESSAGE);
    }

    // Data methods
    private void loadSampleData() {
        // Sample cars
        cars.add(new Car(1, "Toyota", "Camry", 2022, "Silver", 25000, "Available", 15000, "JT2BF22K1W0123456"));
        cars.add(new Car(2, "Honda", "Accord", 2021, "Black", 28500, "Available", 22000, "1HGCM82633A123456"));
        cars.add(new Car(3, "Ford", "Mustang", 2023, "Red", 42000, "Available", 5000, "1FA6P8TH3J5123456"));
        cars.add(new Car(4, "Tesla", "Model 3", 2023, "White", 48000, "Available", 8000, "5YJ3E1EA1PF123456"));
        cars.add(new Car(5, "BMW", "X5", 2022, "Blue", 62000, "Available", 18000, "5UXCR6C05N9123456"));

        // Sample customers
        customers.add(new Customer(1, "John Doe", "555-0101", "john@example.com", "123 Main St, Anytown", "DL12345678"));
        customers.add(new Customer(2, "Jane Smith", "555-0102", "jane@example.com", "456 Oak Ave, Somewhere", "DL23456789"));
        customers.add(new Customer(3, "Robert Brown", "555-0103", "robert@example.com", "789 Pine Rd, Nowhere", "DL34567890"));
        customers.add(new Customer(4, "Emily Davis", "555-0104", "emily@example.com", "321 Elm St, Anywhere", "DL45678901"));
        customers.add(new Customer(5, "Michael Lee", "555-0105", "michael@example.com", "654 Maple Dr, Everywhere", "DL56789012"));

        // Sample employees
        employees.add(new Employee(1, "Sarah Smith", "Salesperson", "555-0201", "sarah@example.com", "2022-01-15", 45000, "sarah", "password123"));
        employees.add(new Employee(2, "Mike Johnson", "Salesperson", "555-0202", "mike@example.com", "2022-03-10", 48000, "mike", "password123"));
        employees.add(new Employee(3, "David Wilson", "Manager", "555-0203", "david@example.com", "2021-11-05", 65000, "david", "password123"));
        employees.add(new Employee(4, "Lisa Taylor", "Salesperson", "555-0204", "lisa@example.com", "2023-02-20", 42000, "lisa", "password123"));
        employees.add(new Employee(5, "James Anderson", "Mechanic", "555-0205", "james@example.com", "2022-05-15", 38000, "james", "password123"));

        // Sample sales
        sales.add(new Sale(1, "2023-05-15", 1, 1, 25000, 1, "Credit Card"));
        sales.add(new Sale(2, "2023-05-14", 2, 2, 28500, 2, "Bank Loan"));
        sales.add(new Sale(3, "2023-05-14", 3, 3, 42000, 1, "Cash"));
        sales.add(new Sale(4, "2023-05-13", 4, 4, 48000, 3, "Credit Card"));
        sales.add(new Sale(5, "2023-05-12", 5, 5, 62000, 4, "Bank Loan"));

        // Update car statuses for sold cars
        cars.get(0).setStatus("Sold");
        cars.get(1).setStatus("Sold");
        cars.get(2).setStatus("Sold");
        cars.get(3).setStatus("Sold");
        cars.get(4).setStatus("Sold");

        // Refresh all tables
        refreshInventoryTable();
        refreshSalesTable();
        refreshCustomersTable();
        refreshEmployeesTable();
    }

    private void refreshInventoryTable() {
        inventoryModel.setRowCount(0);
        for (Car car : cars) {
            inventoryModel.addRow(new Object[]{
                    car.getId(),
                    car.getMake(),
                    car.getModel(),
                    car.getYear(),
                    car.getColor(),
                    currencyFormat.format(car.getPrice()),
                    car.getStatus(),
                    car.getMileage(),
                    car.getVin()
            });
        }
    }

    private void refreshSalesTable() {
        salesModel.setRowCount(0);
        for (Sale sale : sales) {
            // Find customer name
            String customerName = customers.stream()
                    .filter(c -> c.getId() == sale.getCustomerId())
                    .findFirst()
                    .map(Customer::getName)
                    .orElse("Unknown");

            // Find car details
            String carDetails = cars.stream()
                    .filter(c -> c.getId() == sale.getCarId())
                    .findFirst()
                    .map(c -> c.getMake() + " " + c.getModel())
                    .orElse("Unknown");

            // Find employee name
            String employeeName = employees.stream()
                    .filter(e -> e.getId() == sale.getEmployeeId())
                    .findFirst()
                    .map(Employee::getName)
                    .orElse("Unknown");

            salesModel.addRow(new Object[]{
                    sale.getId(),
                    sale.getDate(),
                    customerName,
                    carDetails,
                    currencyFormat.format(sale.getPrice()),
                    employeeName,
                    sale.getPaymentMethod()
            });
        }
    }

    private void refreshCustomersTable() {
        customersModel.setRowCount(0);
        for (Customer customer : customers) {
            // Count purchases
            long purchaseCount = sales.stream()
                    .filter(s -> s.getCustomerId() == customer.getId())
                    .count();

            // Find last purchase date
            String lastPurchase = sales.stream()
                    .filter(s -> s.getCustomerId() == customer.getId())
                    .map(Sale::getDate)
                    .max(String::compareTo)
                    .orElse("Never");

            customersModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getEmail(),
                    customer.getAddress(),
                    purchaseCount,
                    lastPurchase
            });
        }
    }

    private void refreshEmployeesTable() {
        employeesModel.setRowCount(0);
        for (Employee employee : employees) {
            // Count sales
            long salesCount = sales.stream()
                    .filter(s -> s.getEmployeeId() == employee.getId())
                    .count();

            employeesModel.addRow(new Object[]{
                    employee.getId(),
                    employee.getName(),
                    employee.getPosition(),
                    employee.getPhone(),
                    employee.getEmail(),
                    employee.getHireDate(),
                    currencyFormat.format(employee.getSalary()),
                    salesCount
            });
        }
    }

    private void searchInventory(String query, String searchType) {
        if (query.isEmpty()) {
            refreshInventoryTable();
            return;
        }

        inventoryModel.setRowCount(0);
        for (Car car : cars) {
            boolean match = false;
            String queryLower = query.toLowerCase();

            switch (searchType) {
                case "All":
                    match = car.getMake().toLowerCase().contains(queryLower) ||
                            car.getModel().toLowerCase().contains(queryLower) ||
                            String.valueOf(car.getYear()).contains(query) ||
                            car.getColor().toLowerCase().contains(queryLower) ||
                            String.valueOf(car.getPrice()).contains(query) ||
                            car.getStatus().toLowerCase().contains(queryLower) ||
                            String.valueOf(car.getMileage()).contains(query) ||
                            car.getVin().toLowerCase().contains(queryLower);
                    break;
                case "Make":
                    match = car.getMake().toLowerCase().contains(queryLower);
                    break;
                case "Model":
                    match = car.getModel().toLowerCase().contains(queryLower);
                    break;
                case "Year":
                    match = String.valueOf(car.getYear()).contains(query);
                    break;
                case "Color":
                    match = car.getColor().toLowerCase().contains(queryLower);
                    break;
                case "Price Range":
                    try {
                        double price = car.getPrice();
                        if (query.contains("-")) {
                            String[] range = query.split("-");
                            double min = Double.parseDouble(range[0].trim());
                            double max = Double.parseDouble(range[1].trim());
                            match = price >= min && price <= max;
                        } else {
                            double target = Double.parseDouble(query);
                            match = price >= target * 0.9 && price <= target * 1.1;
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    break;
                case "Status":
                    match = car.getStatus().equalsIgnoreCase(query);
                    break;
            }

            if (match) {
                inventoryModel.addRow(new Object[]{
                        car.getId(),
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getColor(),
                        currencyFormat.format(car.getPrice()),
                        car.getStatus(),
                        car.getMileage(),
                        car.getVin()
                });
            }
        }
    }

    private void searchSales(String query, String searchType) {
        if (query.isEmpty()) {
            refreshSalesTable();
            return;
        }

        salesModel.setRowCount(0);
        for (Sale sale : sales) {
            boolean match = false;
            String queryLower = query.toLowerCase();

            // Find customer name
            String customerName = customers.stream()
                    .filter(c -> c.getId() == sale.getCustomerId())
                    .findFirst()
                    .map(Customer::getName)
                    .orElse("Unknown");

            // Find car details
            String carDetails = cars.stream()
                    .filter(c -> c.getId() == sale.getCarId())
                    .findFirst()
                    .map(c -> c.getMake() + " " + c.getModel())
                    .orElse("Unknown");

            // Find employee name
            String employeeName = employees.stream()
                    .filter(e -> e.getId() == sale.getEmployeeId())
                    .findFirst()
                    .map(Employee::getName)
                    .orElse("Unknown");

            switch (searchType) {
                case "All":
                    match = sale.getDate().contains(query) ||
                            customerName.toLowerCase().contains(queryLower) ||
                            carDetails.toLowerCase().contains(queryLower) ||
                            String.valueOf(sale.getPrice()).contains(query) ||
                            employeeName.toLowerCase().contains(queryLower) ||
                            sale.getPaymentMethod().toLowerCase().contains(queryLower);
                    break;
                case "Customer":
                    match = customerName.toLowerCase().contains(queryLower);
                    break;
                case "Car":
                    match = carDetails.toLowerCase().contains(queryLower);
                    break;
                case "Salesperson":
                    match = employeeName.toLowerCase().contains(queryLower);
                    break;
                case "Payment Method":
                    match = sale.getPaymentMethod().toLowerCase().contains(queryLower);
                    break;
            }

            if (match) {
                salesModel.addRow(new Object[]{
                        sale.getId(),
                        sale.getDate(),
                        customerName,
                        carDetails,
                        currencyFormat.format(sale.getPrice()),
                        employeeName,
                        sale.getPaymentMethod()
                });
            }
        }
    }

    private void searchCustomers(String query, String searchType) {
        if (query.isEmpty()) {
            refreshCustomersTable();
            return;
        }

        customersModel.setRowCount(0);
        for (Customer customer : customers) {
            boolean match = false;
            String queryLower = query.toLowerCase();

            switch (searchType) {
                case "All":
                    match = customer.getName().toLowerCase().contains(queryLower) ||
                            customer.getPhone().contains(query) ||
                            customer.getEmail().toLowerCase().contains(queryLower) ||
                            customer.getAddress().toLowerCase().contains(queryLower);
                    break;
                case "Name":
                    match = customer.getName().toLowerCase().contains(queryLower);
                    break;
                case "Phone":
                    match = customer.getPhone().contains(query);
                    break;
                case "Email":
                    match = customer.getEmail().toLowerCase().contains(queryLower);
                    break;
                case "Address":
                    match = customer.getAddress().toLowerCase().contains(queryLower);
                    break;
            }

            if (match) {
                // Count purchases
                long purchaseCount = sales.stream()
                        .filter(s -> s.getCustomerId() == customer.getId())
                        .count();

                // Find last purchase date
                String lastPurchase = sales.stream()
                        .filter(s -> s.getCustomerId() == customer.getId())
                        .map(Sale::getDate)
                        .max(String::compareTo)
                        .orElse("Never");

                customersModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getEmail(),
                        customer.getAddress(),
                        purchaseCount,
                        lastPurchase
                });
            }
        }
    }

    private void searchEmployees(String query, String searchType) {
        if (query.isEmpty()) {
            refreshEmployeesTable();
            return;
        }

        employeesModel.setRowCount(0);
        for (Employee employee : employees) {
            boolean match = false;
            String queryLower = query.toLowerCase();

            switch (searchType) {
                case "All":
                    match = employee.getName().toLowerCase().contains(queryLower) ||
                            employee.getPosition().toLowerCase().contains(queryLower) ||
                            employee.getPhone().contains(query) ||
                            employee.getEmail().toLowerCase().contains(queryLower);
                    break;
                case "Name":
                    match = employee.getName().toLowerCase().contains(queryLower);
                    break;
                case "Position":
                    match = employee.getPosition().toLowerCase().contains(queryLower);
                    break;
                case "Phone":
                    match = employee.getPhone().contains(query);
                    break;
                case "Email":
                    match = employee.getEmail().toLowerCase().contains(queryLower);
                    break;
            }

            if (match) {
                // Count sales
                long salesCount = sales.stream()
                        .filter(s -> s.getEmployeeId() == employee.getId())
                        .count();

                employeesModel.addRow(new Object[]{
                        employee.getId(),
                        employee.getName(),
                        employee.getPosition(),
                        employee.getPhone(),
                        employee.getEmail(),
                        employee.getHireDate(),
                        currencyFormat.format(employee.getSalary()),
                        salesCount
                });
            }
        }
    }

    private void filterByStatus(String status) {
        if (status.equals("All")) {
            refreshInventoryTable();
            return;
        }

        inventoryModel.setRowCount(0);
        for (Car car : cars) {
            if (car.getStatus().equals(status)) {
                inventoryModel.addRow(new Object[]{
                        car.getId(),
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getColor(),
                        currencyFormat.format(car.getPrice()),
                        car.getStatus(),
                        car.getMileage(),
                        car.getVin()
                });
            }
        }
    }

    private void filterSalesByDate(String fromDate, String toDate) {
        if (fromDate.isEmpty() && toDate.isEmpty()) {
            refreshSalesTable();
            return;
        }

        salesModel.setRowCount(0);
        for (Sale sale : sales) {
            boolean match = true;

            if (!fromDate.isEmpty()) {
                match = match && sale.getDate().compareTo(fromDate) >= 0;
            }

            if (!toDate.isEmpty()) {
                match = match && sale.getDate().compareTo(toDate) <= 0;
            }

            if (match) {
                // Find customer name
                String customerName = customers.stream()
                        .filter(c -> c.getId() == sale.getCustomerId())
                        .findFirst()
                        .map(Customer::getName)
                        .orElse("Unknown");

                // Find car details
                String carDetails = cars.stream()
                        .filter(c -> c.getId() == sale.getCarId())
                        .findFirst()
                        .map(c -> c.getMake() + " " + c.getModel())
                        .orElse("Unknown");

                // Find employee name
                String employeeName = employees.stream()
                        .filter(e -> e.getId() == sale.getEmployeeId())
                        .findFirst()
                        .map(Employee::getName)
                        .orElse("Unknown");

                salesModel.addRow(new Object[]{
                        sale.getId(),
                        sale.getDate(),
                        customerName,
                        carDetails,
                        currencyFormat.format(sale.getPrice()),
                        employeeName,
                        sale.getPaymentMethod()
                });
            }
        }
    }

    private void exportToCSV(JTable table, String fileName) {
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            // Write headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                writer.print(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) {
                    writer.print(",");
                }
            }
            writer.println();

            // Write data
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    writer.print(value != null ? value.toString() : "");
                    if (j < table.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            JOptionPane.showMessageDialog(this, "Data exported successfully to " + fileName, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backupData() {
        try {
            // Backup cars
            exportToCSV(new JTable(inventoryModel), "cars_backup.csv");

            // Backup customers
            exportToCSV(new JTable(customersModel), "customers_backup.csv");

            // Backup employees
            exportToCSV(new JTable(employeesModel), "employees_backup.csv");

            // Backup sales
            exportToCSV(new JTable(salesModel), "sales_backup.csv");

            JOptionPane.showMessageDialog(this, "All data backed up successfully!", "Backup Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during backup: " + e.getMessage(), "Backup Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreData() {
        JOptionPane.showMessageDialog(this, "Restore functionality would load data from backup files here", "Restore Data", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateReport(String fromDate, String toDate) {
        JOptionPane.showMessageDialog(this, "Generating report for date range: " + fromDate + " to " + toDate, "Generate Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void printReport() {
        JOptionPane.showMessageDialog(this, "Print functionality would print the current report here", "Print Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void savePreferences(String theme, String currency) {
        JOptionPane.showMessageDialog(this, "Preferences saved:\nTheme: " + theme + "\nCurrency: " + currency, "Preferences Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    // Data classes
    class Car {
        private int id;
        private String make;
        private String model;
        private int year;
        private String color;
        private double price;
        private String status;
        private int mileage;
        private String vin;

        public Car(int id, String make, String model, int year, String color, double price, String status, int mileage, String vin) {
            this.id = id;
            this.make = make;
            this.model = model;
            this.year = year;
            this.color = color;
            this.price = price;
            this.status = status;
            this.mileage = mileage;
            this.vin = vin;
        }

        // Getters and setters
        public int getId() { return id; }
        public String getMake() { return make; }
        public String getModel() { return model; }
        public int getYear() { return year; }
        public String getColor() { return color; }
        public double getPrice() { return price; }
        public String getStatus() { return status; }
        public int getMileage() { return mileage; }
        public String getVin() { return vin; }
        public void setStatus(String status) { this.status = status; }
        public void setMake(String make) { this.make = make; }
        public void setModel(String model) { this.model = model; }
        public void setYear(int year) { this.year = year; }
        public void setColor(String color) { this.color = color; }
        public void setPrice(double price) { this.price = price; }
        public void setMileage(int mileage) { this.mileage = mileage; }
        public void setVin(String vin) { this.vin = vin; }
    }

    class Sale {
        private int id;
        private String date;
        private int customerId;
        private int carId;
        private double price;
        private int employeeId;
        private String paymentMethod;

        public Sale(int id, String date, int customerId, int carId, double price, int employeeId, String paymentMethod) {
            this.id = id;
            this.date = date;
            this.customerId = customerId;
            this.carId = carId;
            this.price = price;
            this.employeeId = employeeId;
            this.paymentMethod = paymentMethod;
        }

        // Getters
        public int getId() { return id; }
        public String getDate() { return date; }
        public int getCustomerId() { return customerId; }
        public int getCarId() { return carId; }
        public double getPrice() { return price; }
        public int getEmployeeId() { return employeeId; }
        public String getPaymentMethod() { return paymentMethod; }
    }

    class Customer {
        private int id;
        private String name;
        private String phone;
        private String email;
        private String address;
        private String driverLicense;

        public Customer(int id, String name, String phone, String email, String address, String driverLicense) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.driverLicense = driverLicense;
        }

        // Getters and setters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public String getDriverLicense() { return driverLicense; }
        public void setName(String name) { this.name = name; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setEmail(String email) { this.email = email; }
        public void setAddress(String address) { this.address = address; }
        public void setDriverLicense(String driverLicense) { this.driverLicense = driverLicense; }
    }

    class Employee {
        private int id;
        private String name;
        private String position;
        private String phone;
        private String email;
        private String hireDate;
        private double salary;
        private String username;
        private String password;

        public Employee(int id, String name, String position, String phone, String email, String hireDate, double salary, String username, String password) {
            this.id = id;
            this.name = name;
            this.position = position;
            this.phone = phone;
            this.email = email;
            this.hireDate = hireDate;
            this.salary = salary;
            this.username = username;
            this.password = password;
        }

        // Getters and setters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getPosition() { return position; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getHireDate() { return hireDate; }
        public double getSalary() { return salary; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public void setName(String name) { this.name = name; }
        public void setPosition(String position) { this.position = position; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setEmail(String email) { this.email = email; }
        public void setHireDate(String hireDate) { this.hireDate = hireDate; }
        public void setSalary(double salary) { this.salary = salary; }
        public void setUsername(String username) { this.username = username; }
        public void setPassword(String password) { this.password = password; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // ====== ADD EMOJI FONT SUPPORT ======
                Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 12);
                UIManager.put("Button.font", emojiFont);
                UIManager.put("Label.font", emojiFont);
                UIManager.put("TabbedPane.font", emojiFont);
                UIManager.put("TextField.font", emojiFont);
                UIManager.put("ComboBox.font", emojiFont);
                UIManager.put("Table.font", emojiFont);
                UIManager.put("TableHeader.font", emojiFont);
                // ====== END OF EMOJI FIX ======

                // Original UI color settings (keep these)
                UIManager.put("Button.foreground", Color.BLACK);
                UIManager.put("Label.foreground", Color.WHITE);
                UIManager.put("TextField.foreground", Color.BLACK);
                UIManager.put("TextArea.foreground", Color.WHITE);
                UIManager.put("TextArea.background", new Color(60, 63, 65));
                UIManager.put("ComboBox.foreground", Color.BLACK);
                UIManager.put("Table.foreground", Color.WHITE);
                UIManager.put("Table.background", new Color(60, 63, 65));
                UIManager.put("TableHeader.foreground", Color.WHITE);
                UIManager.put("TableHeader.background", new Color(41, 128, 185));
                UIManager.put("TabbedPane.foreground", Color.WHITE);
                UIManager.put("TitledBorder.titleColor", Color.WHITE);

                // Create and show the GUI
                new CarRetailSystem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }