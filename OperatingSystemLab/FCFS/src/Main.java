import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private JTextField[] arrivalTimeField;
    private JTextField[] burstTimeField;
    private JButton executeButton;

    public Main() {
        setTitle("FCFS Scheduler");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        int n = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of processes:"));

        arrivalTimeField = new JTextField[n];
        burstTimeField = new JTextField[n];

        for (int i = 0; i < n; i++) {
            inputPanel.add(new JLabel("Arrival Time for Process " + (i + 1) + ":"));
            arrivalTimeField[i] = new JTextField();
            inputPanel.add(arrivalTimeField[i]);

            inputPanel.add(new JLabel("Burst Time for Process " + (i + 1) + ":"));
            burstTimeField[i] = new JTextField();
            inputPanel.add(burstTimeField[i]);
        }

        executeButton = new JButton("Execute FCFS");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeFCFS(n);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(executeButton);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
