import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Chris on 6/9/2015.
 */
class MultiLineOptionPane extends JOptionPane {
    public static String showInputDialog(final String message) {
        String data = null;
        class GetData extends JDialog implements ActionListener {
            JTextArea ta = new JTextArea(5, 10);
            JScrollPane sp = new JScrollPane(ta);
            JButton btnOK = new JButton("   OK   ");
            String str = null;

            public GetData() {
                setModal(true);
                getContentPane().setLayout(new BorderLayout());
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                setLocation(400, 300);
                getContentPane().add(new JLabel(message), BorderLayout.NORTH);
                getContentPane().add(sp, BorderLayout.CENTER);
                JPanel jp = new JPanel();
                btnOK.addActionListener(this);
                jp.add(btnOK);
                getContentPane().add(jp, BorderLayout.SOUTH);
                pack();
                setVisible(true);
            }

            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == btnOK) str = ta.getText();
                dispose();
            }

            public String getData() {
                return str;
            }
        }
        data = new GetData().getData();
        return data;
    }
}
