import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

public class MailBoxInterface {
    private JButton lastBtn;
    private JButton nextBtn;
    public JButton log;
    public JEditorPane editorPane1;
    public JPanel panel;
    public JComboBox comboBox1;
    public JLabel date;
    public JLabel from;
    public JLabel subject;
    public JLabel attach;
    private MailBoxInterface window;
    public JPasswordUI pwUI;
    public writeUI write;
    JFrame frame;

    public MailBoxInterface(){
        frame = new JFrame("信箱");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocation(600, 300);
        frame.setSize(600, 400);

        JMenu menu;
        JMenuBar mb=new JMenuBar();
        menu=new JMenu("操作");
        JMenuItem i1 = new JMenuItem("寫信");
        JMenuItem i2 = new JMenuItem("轉寄");
        JMenuItem i3 = new JMenuItem("回覆");
        JMenuItem i4 =new JMenuItem("下載檔案");
        JMenuItem i5 =new JMenuItem("刪除信件");
        menu.add(i1);
        menu.add(i2);
        menu.add(i3);
        menu.add(i4);
        menu.add(i5);
        mb.add(menu);
        frame.setJMenuBar(mb);
        editorPane1.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        editorPane1.setEditable(false);
        this.window = this;
        try {
            pwUI = new JPasswordUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    write = new writeUI(getWindow());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();




        boolean login = false;
        String[] id = new String[1];
        String[] pw = new String[1];

        i1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pwUI.getLogin()) {
                        write.setVisible(true);
                        frame.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });

        i2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pwUI.getLogin()) {
                        new Pop(getWindow(), id[0], pw[0], 5).start();
                    } else {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });

        i3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pwUI.getLogin()) {
                        JLabel label = new JLabel("請輸入欲回覆訊息");
                        JTextArea textArea = new JTextArea(20, 5);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        label.setSize(100, 100);
                        panel.add(label);
                        panel.add(scrollPane);
                        int inputValue = JOptionPane.showConfirmDialog(null, panel);
                        if (inputValue == JOptionPane.OK_OPTION) {
                            String From = "";
                            for (int i = from.getText().split(" ").length - 1; i >= 0; i--) {
                                if (from.getText().split(" ")[i].contains("@")) {
                                    From = from.getText().split(" ")[i];
                                }
                            }
                            new Mail(id[0], From, textArea.getText() + "<br/>原文======================================================================================<br/>" + window.editorPane1.getText().split("<body.*?>|</body>")[1], "回覆: " + subject.getText(), 3).start();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });

        i4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pwUI.getLogin()) {
                        new Pop(getWindow(), id[0], pw[0], 4).start();
                    } else {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });

        i5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int num = comboBox1.getSelectedIndex();
                    if (pwUI.getLogin()) {
                        Pop dele = new Pop(getWindow(), id[0], pw[0], 3);
                        dele.doDele(num + 1);
                        log.doClick();
                        pwUI.setVisible(false);
                        pwUI.setId(id[0]);
                        pwUI.setPw(pw[0]);
                        pwUI.setLogin(true);
                        log.doClick();
                    } else {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if (log.getText().equals("登出")){
                        pwUI.setLogout();
                        comboBox1.removeAllItems();
                        date.setText("");
                        from.setText("");
                        subject.setText("");
                        editorPane1.setText("");
                        attach.setText("");
                        new Pop().logout();
                        log.setText("登入");
                    }
                    else{
                        if (!pwUI.getLogin()){
                            pwUI.setVisible(true);
                        }
                        else{
                            pwUI.setVisible(false);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!pwUI.getLogin()){System.out.println();}
                                id[0] = pwUI.getId();
                                pw[0] = pwUI.getPw();
                                new Pop(getWindow(), id[0], pw[0],1).start();
                            }
                        }).start();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!pwUI.getLogin()) {
                        if (log.getText().equals("登入")) {
                            JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        if (e.getSource() == comboBox1){
                            new Pop(getWindow(), id[0], pw[0], 2).start();
                        }
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });
        lastBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!pwUI.getLogin()) {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (comboBox1.getSelectedIndex() - 1 > 0) {
                            comboBox1.setSelectedIndex(comboBox1.getSelectedIndex() - 1);
                            new Pop(getWindow(), id[0], pw[0], 2).start();
                        } else {
                            JOptionPane.showMessageDialog(null, "該信為最後一封信", "錯誤", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });
        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!pwUI.getLogin()) {
                        JOptionPane.showMessageDialog(null, "請先登入", "錯誤", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (comboBox1.getSelectedIndex() + 1 <= comboBox1.getItemCount() - 1) {
                            comboBox1.setSelectedIndex(comboBox1.getSelectedIndex() + 1);
                            new Pop(getWindow(), id[0], pw[0], 2).start();
                        } else {
                            JOptionPane.showMessageDialog(null, "該信為第一封信", "錯誤", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (HeadlessException headlessException) {
                    headlessException.printStackTrace();
                }
            }
        });
    }

    private void log(String username, String password){

    }

    private Object makeObj(final String item)  {
        return new Object() { public String toString() { return item; } };
    }
    private MailBoxInterface getWindow(){
        return window;
    }
    public void setVisible(boolean b){
        frame.setVisible(b);
    }

    public static void main(String[] args){
        new MailBoxInterface();
    }

}
