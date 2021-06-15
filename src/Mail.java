import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Mail extends Thread{
    final int SMTP_PORT = 25;
    String smtp_server = "msa.hinet.net";
    String my_email_addr = "steven200083@gmail.com";
    String subject;
    String my_email;
    String[] to, msgs;
    int type;

    public Mail(String my_email, String to, String msgs, String subject, int type){
        this.my_email = my_email;
        this.to = to.split(", ");
        if (type == 1){
            msgs = msgs.replace("\n", "<br/>");
            this.msgs = new String[1];
            this.msgs[0] = msgs;
        }
        else{
            this.msgs = msgs.split("\n");
        }
        System.out.println(msgs);
        this.subject = subject;
        this.type = type;
    }

    public void sendCommandAndResultCheck(Socket smtp,
                                          BufferedReader smtp_in,
                                          PrintWriter smtp_out,
                                          String command,
                                          int success_code)
            throws IOException {
        smtp_out.print(command + "\r\n");
        smtp_out.flush();
        System.out.println("send> " + command);
        resultCheck(smtp, smtp_in, smtp_out, success_code);
    }

    public void resultCheck(Socket smtp, BufferedReader smtp_in,
                            PrintWriter smtp_out,
                            int success_code)
        throws IOException {
        String res = smtp_in.readLine();
        System.out.println("reev> " + res);
        if(Integer.parseInt(res.substring(0, 3)) != success_code){
            smtp.close();
            throw new  RuntimeException(res);
        }
    }

    public void send(String subject, String[] to, String[] msgs) throws IOException{
        Socket smtp = new Socket(smtp_server, SMTP_PORT);
        BufferedReader smtp_in = new BufferedReader(new InputStreamReader(smtp.getInputStream()));
        PrintWriter smtp_out = new PrintWriter(smtp.getOutputStream());
        resultCheck(smtp, smtp_in, smtp_out, 220);

        String myname = InetAddress.getLocalHost().getHostName();
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "HELO " + myname, 250);
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "MAIL FROM:" + my_email, 250);
        for (int i = 0; i < to.length; i++){
            sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "RCPT TO:" + to[i], 250);
        }
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "DATA", 354);
        smtp_out.print("Subject: " + subject + "\r\n");
        smtp_out.flush();
        smtp_out.print("MIME-Version: 1.0\r\n");
        smtp_out.flush();
        if (type == 1 || type == 3){
            smtp_out.print("Content-Type: text/html; charset=\"UTF-8\"\r\n");
            smtp_out.flush();
        }
        smtp_out.print("\r\n");
        smtp_out.flush();
        for (int i = 0; i < msgs.length; ++i){
            smtp_out.print(msgs[i] + "\r\n");
            System.out.println("end> " + msgs[i]);
        }
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "\r\n.", 250);
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "QUIT", 221);
        smtp.close();
        JOptionPane.showMessageDialog(null, "寄送完成", "寄送信件", JOptionPane.INFORMATION_MESSAGE);
    }

    public void run(){
        try {
            send(subject, to, msgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
