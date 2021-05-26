import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class Mail {
    final int SMTP_PORT = 25;
    String smtp_server = "msa.hinet.net"/*"stumail.nutn.edu.tw"*/;
    String my_email_addr = "S10759020@gm2.nutn.edu.tw";
    String loginID = "S10759020@gm2.nutn.edu.tw";

    /*public void sendAuth() {
        if (smtp_server == null) return;
        int response = 0;
        response= sendCmd("AUTH LOGIN", "");
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "AUTH LOGIN", 250);
        if (response == 334) {
            Base64 b = new Base64();
            String id = b.encode((loginID).getBytes());
            String p = b.encode((pwd).getBytes());
            auth(id);
            auth(p);
        }
        else {
            sc.showCmd("執行驗證發生錯誤");
            close();
        }
    }*/


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
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "MAIL FROM:" + my_email_addr, 250);
        for (int i = 0; i < to.length; i++){
            sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "RCPT TO:" + to[i], 250);
        }
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "DATA", 354);
        smtp_out.print("Subject:" + subject + "\r\n");
        System.out.println("send> Subject:" + subject);
        smtp_out.print("\r\n");
        for (int i = 0; i < msgs.length - 1; ++i){
            smtp_out.print(msgs[i] + "\r\n");
            System.out.println("end> " + msgs[i]);
        }
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "\r\n.", 250);
        sendCommandAndResultCheck(smtp, smtp_in, smtp_out, "QUIT", 221);
        smtp.close();
    }

    public void setAddress(){
        String buf = "";
        BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
        boolean cont = true;

        try {
            while(cont){
                System.out.println("Enter Mail Server: ");
                smtp_server = lineread.readLine();
                System.out.println("Enter your mail address");
                my_email_addr = lineread.readLine();
                System.out.println(" Mail Server: " + smtp_server);
                System.out.println(" Your mail address: " + my_email_addr);
                System.out.println("Above is right?(y/n)");
                buf = lineread.readLine();
                if("y".equals(buf)){
                    cont = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String[] setMsgs(){
        String buf = "";
        BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
        boolean cont = true;
        Vector msgs_list = new Vector();
        String[] msgs = null;

        try{
            System.out.println("Enter what you want to send: ");
            System.out.println("End with period");
            while (cont){
                buf = lineread.readLine();
                msgs_list.addElement(buf);
                if(".".equals(buf)){
                    cont = false;
                }
                msgs = new String[msgs_list.size()];
                msgs_list.copyInto(msgs);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return msgs;
    }

    public void mainproc(String[] args){
        /*for (int i = 0; i < args.length; i++){
            System.out.println(args[i]);
        }*/
        String usage = "java Mail [-s subject] to-addr ...";
        String subject = "";
        Vector to_list = new Vector();
        for (int i = 0; i < args.length; i++){
            if("-s".equals(args[i])){
                i++;
                subject = args[i];
            }
            else{
                to_list.addElement(args[i]);
            }
        }
        if(to_list.size() > 0){
            try{
                String[] to = new String[to_list.size()];
                to_list.copyInto(to);
                //setAddress();
                String[] msgs = setMsgs();
                send(subject, to, msgs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("usage: " + usage);
        }
    }

    public static void main(String[] args) {
        Mail m = new Mail();
        m.mainproc(args);
    }
}
