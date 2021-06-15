import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class writeUI {
    public JTextField to;
    public JTextField subject;
    public JTextArea content;
    private JPanel pane;
    private JButton finishBtn;
    private JButton cnacelBtn;
    JFrame frame;

    public writeUI(MailBoxInterface win) {
        frame = new JFrame("寄信");
        frame.setContentPane(this.pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(650, 300);
        frame.setSize(550, 350);

        finishBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                win.setVisible(true);
                to.setText("");
                subject.setText("");
                content.setText("");
                new Mail(win.pwUI.getId(), to.getText(), content.getText(), subject.getText(), 1).start();
            }
        });
        cnacelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                win.setVisible(true);
                to.setText("");
                subject.setText("");
                content.setText("");
            }
        });
    }

    public void setVisible(boolean b){
        frame.setVisible(b);
    }



    public static void main(String[] args) {
    }
}
