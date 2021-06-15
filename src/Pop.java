import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Pop extends Thread{
    final int POP_PORT = 110;
    BufferedReader pop_in = null;
    PrintWriter pop_out = null;
    Socket pop = null;
    static int max = 0;
    int action;
    private MailBoxInterface window;
    String username, password;

    public Pop(){ }

    public Pop(MailBoxInterface win, String id, String pw, int act){
        window = win;
        username = id;
        password = pw;
        action = act;
        setCombobox(max);
    }

    public void transaction() throws IOException {

        switch (action){
            case 1:
                doList();
                break;
            case 2:
            case 4:
            case 5:
                if (window.comboBox1.getSelectedIndex() + 1 > 0){
                    doRetr(window.comboBox1.getSelectedIndex() + 1);
                }
                break;
            case 3:
                System.out.println(window.comboBox1.getSelectedIndex());
                doDele(window.comboBox1.getSelectedIndex() + 1);
                break;
        }
    }

    private void doList() throws IOException {
        boolean cont = true;
        String buf = "", lastLine;

        pop_out.print("LIST\r\n");
        pop_out.flush();
        String res = pop_in.readLine();
        System.out.println(res);
        if (!res.substring(0, 3).equals("+OK")) {
            pop.close();
            throw new RuntimeException(res);
        }

        while (cont) {
            lastLine = buf;
            buf = pop_in.readLine();
            System.out.println(buf);
            if (buf.equals(".")) {
                setCombobox(Integer.parseInt(lastLine.split(" ")[0]));
                cont = false;
            }
        }
    }

    private void setCombobox(int max){
        System.out.println("this max: " + this.max);
        System.out.println("max: " + max);
        if (max < this.max){
            for (int i = max; i < this.max; i++){
                window.comboBox1.remove(i);
            }
        }
        else{
            for (int i = this.max + 1; i <= max; i++) {
                window.comboBox1.addItem(makeObj(String.valueOf(i)));
            }
        }
        this.max = max;
    }

    private Object makeObj(final String item)  {
        return new Object() { public String toString() { return item; } };
    }

    private void doRetr(int number) throws IOException {
        getLines("RETR " + number);
    }

    public void logout(){
        max = 0;
    }

    public void doDele(int number) throws IOException {
        authorization();
        pop_out.print("DELE " + number + "\r\n");
        pop_out.flush();
        String res = pop_in.readLine();
        System.out.println(res);
        if (!res.substring(0, 3).equals("+OK")){
            pop.close();
            throw new RuntimeException(res);
        }
        update();
    }

    public void getLines(String cmd) throws IOException {
        String save = "";
        String ContentType = "";
        String From = "";
        String ContentTransferEncodingPlain = "";
        String ContentTransferEncodingHTML = "";
        String Subject = "";
        String Date = "";
        boolean cont = true;
        String buf = null;
        String contentPlain = "";
        String CharsetPlain = "";
        String contentHTML = "";
        String CharsetHTML = "";

        pop_out.print(cmd + "\r\n");
        pop_out.flush();
        String res = pop_in.readLine();
        System.out.println(res);
        if (!res.substring(0, 3).equals("+OK")){
            pop.close();
            throw new RuntimeException(res);
        }

        boolean subjectStart = false, fromStart = false, multipart = false, saveStart = false;
        boolean multi_alt = false, multi_mix = false, multi_re = false, text = false, none = false;
        String[] boundary = new String[3];
        ArrayList<String> related = new ArrayList<>();
        ArrayList<String> related_id = new ArrayList<>();
        ArrayList<String> mixed = new ArrayList<>();
        ArrayList<String> mixed_name = new ArrayList<>();
        String tempForFile = "";

        if (action == 5){
            buf = pop_in.readLine();
            System.out.println(buf);
            while(!buf.contains("Content-Type")){
                if (buf.contains("Subject:") || subjectStart){
                    if (buf.contains(":") && subjectStart){
                        subjectStart = false;
                        Subject = Subject.replace("Subject: ", "");
                    }
                    else{
                        subjectStart = true;
                        Subject += buf + "\r\n";
                    }
                }
                buf = pop_in.readLine();
                System.out.println(buf);
            }
            while(cont){
                contentPlain += buf + "\n";
                buf = pop_in.readLine();
                System.out.println(buf);
                if (buf.equals(".")){
                    cont = false;
                }
            }
        }
        else {
            while (cont) {
                buf = pop_in.readLine();
                System.out.println(buf);
                if (buf.contains("Subject:") || subjectStart) {
                    if (buf.contains(":") && subjectStart) {
                        subjectStart = false;
                    } else {
                        subjectStart = true;
                        Subject += buf;
                    }
                }
                if (buf.contains("From:") || fromStart) {
                    if (buf.contains(":") && fromStart) {
                        fromStart = false;
                    } else {
                        fromStart = true;
                        From += buf;
                    }
                }
                if (buf.contains("Date:")) {
                    Date = buf.substring(6);
                }
                if (buf.contains("Content-Type:")) {
                    if (buf.contains("multipart/mixed")) {
                        multi_mix = true;
                        if (!buf.contains("boundary")) {
                            buf = pop_in.readLine();
                            System.out.println(buf);
                        }
                        boundary[0] = buf.split("boundary=\"|\"|boundary=")[1];
                    } else if (buf.contains("multipart/related")) {
                        multi_re = true;
                        if (!buf.contains("boundary")) {
                            buf = pop_in.readLine();
                            System.out.println(buf);
                        }
                        boundary[1] = buf.split("boundary=\"|\"|boundary=")[1];
                    } else if (buf.contains("multipart/alternative")) {
                        multi_alt = true;
                        if (!buf.contains("boundary")) {
                            buf = pop_in.readLine();
                            System.out.println(buf);
                        }
                        boundary[2] = buf.split("boundary=\"|\"|boundary=")[1];
                    } else if (buf.contains("text/")) {
                        text = true;
                        if (buf.contains("text/plain")) {
                            if (!buf.contains("charset=")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            CharsetPlain = buf.split("charset=")[1].replace("\"", "");
                            while (!buf.equals("")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                                if (buf.contains("Content-Transfer-Encoding")) {
                                    ContentTransferEncodingPlain = buf.replace("Content-Transfer-Encoding: ", "");
                                }
                            }
                            while (!buf.equals(".")) {
                                if (!buf.equals("")) {
                                    if (ContentTransferEncodingPlain.equals("quoted-printable")) {
                                        if (buf.charAt(buf.length() - 1) == '=') {
                                            String a = buf.substring(0, buf.length() - 1) + "\n";
                                            buf = buf.substring(0, buf.length() - 1) + "\n";
                                        } else {
                                            buf += "\n";
                                        }
                                    }
                                }
                                contentPlain += buf;
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                        } else if (buf.contains("text/html")) {
                            if (!buf.contains("charset=")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            CharsetHTML = buf.split("charset=")[1].replace("\"", "");
                            while (!buf.equals("")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                                if (buf.contains("Content-Transfer-Encoding")) {
                                    ContentTransferEncodingHTML = buf.replace("Content-Transfer-Encoding: ", "");
                                }
                            }
                            while (!buf.equals(".")) {
                                if (!buf.equals("")) {
                                    if (ContentTransferEncodingHTML.equals("quoted-printable")) {
                                        if (buf.charAt(buf.length() - 1) == '=') {
                                            String a = buf.substring(0, buf.length() - 1) + "\n";
                                            buf = buf.substring(0, buf.length() - 1) + "\n";
                                        } else {
                                            buf += "\n";
                                        }
                                    }
                                }
                                contentHTML += buf;
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                        }
                        cont = false;
                    }
                }
                if (buf.equals("")) {
                    if (!(multi_alt || multi_mix || multi_re || text)) {
                        none = true;
                    }
                    cont = false;
                }
            }

            if (!text) {
                if (multi_mix) {
                    while (!buf.contains(boundary[0])) {
                        buf = pop_in.readLine();
                        System.out.println(buf);
                    }
                    buf = pop_in.readLine();
                    System.out.println(buf);
                    if (buf.contains("multipart/related")) {
                        if (!buf.contains("boundary")) {
                            buf = pop_in.readLine();
                            System.out.println(buf);
                        }
                        boundary[1] = buf.split("boundary=\"|\"|boundary=")[1];
                        buf = pop_in.readLine();
                        System.out.println(buf);
                        while (!buf.contains(boundary[1])) {
                            buf = pop_in.readLine();
                            System.out.println(buf);
                        }
                        buf = pop_in.readLine();
                        System.out.println(buf);
                        if (buf.contains("multipart/alternative")) {
                            if (!buf.contains("boundary")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            boundary[2] = buf.split("boundary=\"|\"|boundary=")[1];
                        }
                    } else if (buf.contains("multipart/alternative")) {
                        if (!buf.contains("boundary")) {
                            buf = pop_in.readLine();
                            System.out.println(buf);
                        }
                        boundary[2] = buf.split("boundary=\"|\"|boundary=")[1];
                    }
                } else if (multi_re) {
                    while (!buf.contains(boundary[1])) {
                        buf = pop_in.readLine();
                        System.out.println(buf);
                    }
                    buf = pop_in.readLine();
                    System.out.println(buf);
                    if (!buf.contains("boundary")) {
                        buf = pop_in.readLine();
                        System.out.println(buf);
                    }
                    boundary[2] = buf.split("boundary=\"|\"|boundary=")[1];
                }

                cont = true;

                if (boundary[2] != null) {
                    while (!buf.contains(boundary[2] + "--")) {
                        buf = pop_in.readLine();
                        System.out.println(buf);
                        if (buf.contains("text/plain")) {
                            if (!buf.contains("charset=")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            CharsetPlain = buf.split("charset=")[1].replace("\"", "");
                            while (!buf.equals("")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                                if (buf.contains("Content-Transfer-Encoding")) {
                                    ContentTransferEncodingPlain = buf.replace("Content-Transfer-Encoding: ", "");
                                }
                            }
                            while (!buf.contains(boundary[2])) {
                                if (!buf.equals("")) {
                                    if (ContentTransferEncodingPlain.equals("quoted-printable")) {
                                        if (buf.charAt(buf.length() - 1) == '=') {
                                            String a = buf.substring(0, buf.length() - 1) + "\n";
                                            buf = buf.substring(0, buf.length() - 1) + "\n";
                                        } else {
                                            buf += "\n";
                                        }
                                    }
                                }
                                contentPlain += buf;
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                        } else if (buf.contains("text/html")) {
                            if (!buf.contains("charset=")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            CharsetHTML = buf.split("charset=")[1].replace("\"", "");
                            while (!buf.equals("")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                                if (buf.contains("Content-Transfer-Encoding")) {
                                    ContentTransferEncodingHTML = buf.replace("Content-Transfer-Encoding: ", "");
                                }
                            }
                            while (!buf.contains(boundary[2])) {
                                if (!buf.equals("")) {
                                    if (ContentTransferEncodingHTML.equals("quoted-printable")) {
                                        if (buf.charAt(buf.length() - 1) == '=') {
                                            String a = buf.substring(0, buf.length() - 1) + "\n";
                                            buf = buf.substring(0, buf.length() - 1) + "\n";
                                        } else {
                                            buf += "\n";
                                        }
                                    }
                                }
                                contentHTML += buf;
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            if (ContentTransferEncodingPlain.equals("quoted-printable")) {
                                buf += ".";
                            }
                        }
                    }
                }
                if (boundary[1] != null) {
                    while (!buf.contains(boundary[1] + "--")) {
                        buf = pop_in.readLine();
                        System.out.println(buf);
                        if (buf.contains("image") || buf.contains("video") || buf.contains("application")) {
                            while (!buf.contains("Content-ID")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            related_id.add(buf.split("<|>")[1]);
                            while (!buf.equals("")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            tempForFile = "";
                            while (!buf.contains(boundary[1])) {
                                tempForFile += buf;
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            related.add(tempForFile);
                        }
                    }
                }

                if (boundary[0] != null) {
                    while (!buf.contains(boundary[0] + "--")) {
                        buf = pop_in.readLine();
                        System.out.println(buf);
                        if (buf.contains("image") || buf.contains("video") || buf.contains("application")) {
                            while (!buf.contains("filename")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            mixed_name.add(buf.split("\"")[1]);
                            while (!buf.equals("")) {
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            tempForFile = "";
                            while (!buf.contains(boundary[0])) {
                                tempForFile += buf;
                                buf = pop_in.readLine();
                                System.out.println(buf);
                            }
                            mixed.add(tempForFile);
                        }
                    }
                }

                while (cont) {
                    buf = pop_in.readLine();
                    System.out.println(buf);
                    if (buf.equals(".")) {
                        cont = false;
                    }
                }
            }
        }

        if (multi_mix){
            window.attach.setText("此檔案有附件");
        }
        else{
            window.attach.setText("");
        }

        if (action == 4){
            if (multi_mix){
                new MailContent(window, contentHTML, Date, ContentType, Subject, From, ContentTransferEncodingHTML, mixed, mixed_name, related, related_id, CharsetHTML).downloadAllFile();
                JOptionPane.showMessageDialog(null, "下載完成", "下載檔案", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null, "該信件沒有檔案", "下載檔案", JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (action == 5){
            String inputValue = JOptionPane.showInputDialog("請輸入收件人(以\",\"分隔)");
            if (inputValue != null){
                new Mail(username, inputValue, contentPlain, "Fw: " + window.subject.getText(), 2).start();
            }
        }
        else if (!contentHTML.equals("")){
            new MailContent(window, contentHTML, Date, ContentType, Subject, From, ContentTransferEncodingHTML, mixed, mixed_name, related, related_id, CharsetHTML).print(0);
        }
        else if (!contentPlain.equals("")){
            new MailContent(window, contentPlain, Date, ContentType, Subject, From, ContentTransferEncodingPlain, mixed, mixed_name, related, related_id, CharsetPlain).print(1);
        }
    }

    public void getSingleLine(String cmd) throws IOException{
        pop_out.print(cmd + "\r\n");
        pop_out.flush();
        System.out.println(cmd);
        String res = pop_in.readLine();
        System.out.println(res);
        if(!res.substring(0, 3).equals("+OK")){
            pop.close();
            throw new RuntimeException(res);
        }
    }

    public void authorization() throws IOException{
        String pop_server = "mail.nutn.edu.tw";

        pop = new Socket(pop_server, POP_PORT);
        pop_in = new BufferedReader(new InputStreamReader(pop.getInputStream()));
        pop_out = new PrintWriter(pop.getOutputStream());
        String res = pop_in.readLine();
        System.out.println(res);

        if (!res.substring(0, 3).equals("+OK")){
            pop.close();
            throw new RuntimeException(res);
        }

        pop_out.print("USER " + username + "\r\n");
        pop_out.flush();
        System.out.println("USER " + username);
        res = pop_in.readLine();
        System.out.println(res);
        if(!res.substring(0, 3).equals("+OK")){
            pop.close();
            JOptionPane.showMessageDialog(null, "登入錯誤", "登入錯誤", JOptionPane.ERROR_MESSAGE);
            window.pwUI.setLogout();
            throw new RuntimeException(res);
        }
        else{
            pop_out.print("PASS " + password + "\r\n");
            pop_out.flush();
            System.out.println("PASS " + password);
            res = pop_in.readLine();
            System.out.println(res);
            if(!res.substring(0, 3).equals("+OK")){
                pop.close();
                JOptionPane.showMessageDialog(null, "登入錯誤", "登入錯誤", JOptionPane.ERROR_MESSAGE);
                window.pwUI.setLogout();
                throw new RuntimeException(res);
            }
        }
        window.log.setText("登出");
    }

    public void update() throws IOException{
        pop_out.print("QUIT\r\n");
        pop_out.flush();
        pop.close();
    }

    public void mainproc() throws IOException {
        authorization();
        transaction();
        update();
    }

    public void run(){
        try {
            mainproc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
