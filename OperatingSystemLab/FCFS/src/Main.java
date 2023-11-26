import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JTextField[] arrivalTimeField;
    private JTextField[] burstTimeField;
    private JTextField[] priorityField;
    private JButton executeFCFSButton;
    private JButton executePriorityButton;
    private JTable fcfsTable;
    private JTable priorityTable;

    public Main() {
        setTitle("CPU Scheduling Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        int n = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of processes:"));

        arrivalTimeField = new JTextField[n];
        burstTimeField = new JTextField[n];
        priorityField = new JTextField[n];

        for (int i = 0; i < n; i++) {
            inputPanel.add(createLabel("Arrival Time for Process " + (i + 1) + ":"));
            arrivalTimeField[i] = new JTextField();
            inputPanel.add(arrivalTimeField[i]);

            inputPanel.add(createLabel("Burst Time for Process " + (i + 1) + ":"));
            burstTimeField[i] = new JTextField();
            inputPanel.add(burstTimeField[i]);

            inputPanel.add(createLabel("Priority for Process " + (i + 1) + ":"));
            priorityField[i] = new JTextField();
            inputPanel.add(priorityField[i]);
        }

        executeFCFSButton = createButton("Execute FCFS", e -> executeFCFS(n));
        executePriorityButton = createButton("Execute Priority Scheduling", e -> executePriority(n));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(executeFCFSButton);
        buttonPanel.add(executePriorityButton);

        // Create tables and their models
        DefaultTableModel fcfsTableModel = new DefaultTableModel();
        fcfsTableModel.addColumn("PID");
        fcfsTableModel.addColumn("Arrival");
        fcfsTableModel.addColumn("Burst");
        fcfsTableModel.addColumn("Complete");
        fcfsTableModel.addColumn("Turnaround");
        fcfsTableModel.addColumn("Waiting");

        DefaultTableModel priorityTableModel = new DefaultTableModel();
        priorityTableModel.addColumn("PID");
        priorityTableModel.addColumn("Arrival");
        priorityTableModel.addColumn("Burst");
        priorityTableModel.addColumn("Priority");
        priorityTableModel.addColumn("Complete");
        priorityTableModel.addColumn("Turnaround");
        priorityTableModel.addColumn("Waiting");

        fcfsTable = new JTable(fcfsTableModel);
        priorityTable = new JTable(priorityTableModel);

        JScrollPane fcfsScrollPane = new JScrollPane(fcfsTable);
        JScrollPane priorityScrollPane = new JScrollPane(priorityTable);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        tablesPanel.add(fcfsScrollPane);
        tablesPanel.add(priorityScrollPane);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();  // Adjust frame size to fit components
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private void executeFCFS(int n)
    {
        int[] pid = new int[n];
        int[] ar = new int[n];
        int[] bt = new int[n];
        int[] ct = new int[n];
        int[] ta = new int[n];
        int[] wt = new int[n];
        float avgwt = 0, avgta = 0;
        int temp;

        for (int i = 0; i < n; i++) {
            ar[i] = Integer.parseInt(arrivalTimeField[i].getText());
            bt[i] = Integer.parseInt(burstTimeField[i].getText());
            pid[i] = i + 1;
        }

        // Sort processes according to arrival times
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - (i + 1); j++) {
                if (ar[j] > ar[j + 1]) {
                    temp = ar[j];
                    ar[j] = ar[j + 1];
                    ar[j + 1] = temp;
                    temp = bt[j];
                    bt[j] = bt[j + 1];
                    bt[j + 1] = temp;
                    temp = pid[j];
                    pid[j] = pid[j + 1];
                    pid[j + 1] = temp;
                }
            }
        }

        // Finding completion times
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                ct[i] = ar[i] + bt[i];
            } else {
                if (ar[i] > ct[i - 1]) {
                    ct[i] = ar[i] + bt[i];
                } else {
                    ct[i] = ct[i - 1] + bt[i];
                }
            }
            ta[i] = ct[i] - ar[i];
            wt[i] = ta[i] - bt[i];
            avgwt += wt[i];
            avgta += ta[i];
        }
        DefaultTableModel fcfsTableModel = (DefaultTableModel) fcfsTable.getModel();
        fcfsTableModel.setRowCount(0); // Clear existing rows

        for (int i = 0; i < n; i++) {
            fcfsTableModel.addRow(new Object[]{
                    pid[i], ar[i], bt[i], ct[i], ta[i], wt[i]
            });
        }
        // Display the result
        StringBuilder result = new StringBuilder("\n");
        result.append("+-----+---------+-------+----------+------------+---------+---------\n");
        result.append(String.format("| PID | Arrival | Burst | Complete | Turnaround | Waiting |\n"));

        for (int i = 0; i < n; i++) {
            result.append(String.format("| %3d | %9d | %7d | %9d | %15d | %20d |\n",
                    pid[i], ar[i], bt[i], ct[i], ta[i], wt[i]));
        }

        result.append("+-----+---------+-------+----------+------------+---------+---------\n");

        result.append("\nAverage Waiting Time: ").append(avgwt / n);
        result.append("\nAverage Turnaround Time: ").append(avgta / n);

        JOptionPane.showMessageDialog(this, result.toString());



    }

    private void executePriority(int n) {
        int[] pid = new int[n];
        int[] ar = new int[n];
        int[] bt = new int[n];
        int[] priority = new int[n];
        int[] ct = new int[n];
        int[] ta = new int[n];
        int[] wt = new int[n];
        float avgwt = 0, avgta = 0;
        int temp;

        for (int i = 0; i < n; i++) {
            ar[i] = Integer.parseInt(arrivalTimeField[i].getText());
            bt[i] = Integer.parseInt(burstTimeField[i].getText());
            priority[i] = Integer.parseInt(priorityField[i].getText());
            pid[i] = i + 1;
        }

        // Sort processes according to priority
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - (i + 1); j++) {
                if (priority[j] > priority[j + 1]) {
                    temp = ar[j];
                    ar[j] = ar[j + 1];
                    ar[j + 1] = temp;
                    temp = bt[j];
                    bt[j] = bt[j + 1];
                    bt[j + 1] = temp;
                    temp = priority[j];
                    priority[j] = priority[j + 1];
                    priority[j + 1] = temp;
                    temp = pid[j];
                    pid[j] = pid[j + 1];
                    pid[j + 1] = temp;
                }
            }
        }

        // Finding completion times
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                ct[i] = ar[i] + bt[i];
            } else {
                if (ar[i] > ct[i - 1]) {
                    ct[i] = ar[i] + bt[i];
                } else {
                    ct[i] = ct[i - 1] + bt[i];
                }
            }
            ta[i] = ct[i] - ar[i];
            wt[i] = ta[i] - bt[i];
            avgwt += wt[i];
            avgta += ta[i];
        }

        DefaultTableModel priorityTableModel = (DefaultTableModel) priorityTable.getModel();
        priorityTableModel.setRowCount(0); // Clear existing rows

        for (int i = 0; i < n; i++) {
            priorityTableModel.addRow(new Object[]{
                    pid[i], ar[i], bt[i], priority[i], ct[i], ta[i], wt[i]
            });
        }

        // Display the result
        StringBuilder result = new StringBuilder("\n");
        result.append("+-----+---------+-------+----------+------------+---------+---------\n");
        result.append(String.format("| PID | Arrival | Burst | Priority | Complete | Turnaround | Waiting |\n"));

        for (int i = 0; i < n; i++) {
            result.append(String.format("|  %3d | %9d | %7d | %9d | %9d | %15d | %20d |\n",
                    pid[i], ar[i], bt[i], priority[i], ct[i], ta[i], wt[i]));
        }

        result.append("+-----+---------+-------+----------+------------+---------+---------\n");

        result.append("\nAverage Waiting Time: ").append(avgwt / n);
        result.append("\nAverage Turnaround Time: ").append(avgta / n);

        JOptionPane.showMessageDialog(this, result.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
