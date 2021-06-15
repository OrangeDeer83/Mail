import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class JPasswordUI extends JFrame
{
    private JLabel Jlb_ID = new JLabel("帳號");
    private JLabel Jlb_PW = new JLabel("密碼");
    private JPasswordField jpw = new JPasswordField(8);
    private JTextField jid = new JTextField();
    private JButton Jbtn_YES = new JButton("確定");
    private JButton Jbtn_NO = new JButton("清除");
    private ButtonHandler hbtHandler = new ButtonHandler();
    private JPasswordUI window;
    private boolean login;


    public JPasswordUI()
    {
        super("");
        login = false;
        this.window = this;
        Container c = getContentPane();
        c.setLayout(null);

        Jlb_ID.setLocation(20,10);
        Jlb_ID.setSize(50,20);
        Jlb_ID.setFont(new Font("Serif",Font.BOLD,16));
        c.add(Jlb_ID);

        jid.setLocation(70,10);
        jid.setSize(100,20);
        c.add(jid);

        Jlb_PW.setLocation(20,50);
        Jlb_PW.setSize(50,20);
        Jlb_PW.setFont(new Font("Serif",Font.BOLD,16));
        c.add(Jlb_PW);

        jpw.setLocation(70,50);
        jpw.setSize(100,20);
        jpw.setEchoChar('●');
        jpw.setToolTipText("密碼長度8個字元");
        c.add(jpw);

        jpw.addActionListener(hbtHandler);

        Jbtn_YES.setLocation(10,90);
        Jbtn_YES.setSize(80,20);
        Jbtn_YES.addActionListener(hbtHandler);
        c.add(Jbtn_YES);

        Jbtn_NO.setLocation(100,90);
        Jbtn_NO.setSize(80,20);
        Jbtn_NO.addActionListener(hbtHandler);
        c.add(Jbtn_NO);

        setSize(200,150);
        setLocation(700,400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evtE)
        {
            if(evtE.getSource() == Jbtn_YES)
            {
                //char [] pw = jpw.getPassword();
                //System.out.println(""+pw.toString());
                login = true;
                setVisible(false);
            }else if(evtE.getSource() == Jbtn_NO)
            {
                //char [] pw = jpw.getPassword();
                //System.out.println(""+pw.toString());
                jid.setText("S10759020@stumail.nutn.edu.tw");
                jpw.setText("STEVEN200083");
            }
        }
    }

    public boolean getLogin(){
        return login;
    }

    public void setLogout(){
        login = false;
        jid.setText("");
        jpw.setText("");
    }

    public String getId(){
        return jid.getText();
    }

    public String getPw(){
        return jpw.getText();
    }

    public void setId(String id){
        jid.setText(id);
    }

    public void setPw(String pw){
        jpw.setText(pw);
    }

    public void setLogin(boolean b){
        login = b;
    }
}