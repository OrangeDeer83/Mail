import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Pop {
    final int POP_PORT = 995;
    BufferedReader pop_in = null;
    PrintWriter pop_out = null;
    Socket pop = null;

    public void transaction() throws IOException{
        String buf = "";
        BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
        boolean cont = true;

        while (cont){
            System.out.println("Command:L)ist R)etrieve D)elete Q)ueit");
            buf = lineread.readLine();
            if (buf.equalsIgnoreCase("Q")){
                cont = false;
            }
            else if ( buf.equalsIgnoreCase("L")){
                getLines("LIST");
            }
            else if (buf.equalsIgnoreCase("R")){
                System.out.println("Number?:");
                buf = lineread.readLine();
                getLines("RETR " + buf);
            }
            else if (buf.equalsIgnoreCase("D")){
                System.out.println("Number?:");
                buf  = lineread.readLine();
                getSingleLine("DELE " + buf);
            }
        }
    }

    public void getLines(String cmd) throws IOException{
        boolean cont = true;
        String buf = null;
        pop_out.print(cmd + "\r\n");
        pop_out.flush();
        String res = pop_in.readLine();
        System.out.println(res);
        if (!res.substring(0, 3).equals("+OK")){
            pop.close();
            throw new RuntimeException(res);
        }
        while (cont){
            buf = pop_in.readLine();
            System.out.println(buf);
            if (buf.equals(".")){
                cont = false;
            }
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
        String buf = "";
        BufferedReader lineread = new BufferedReader(new InputStreamReader(System.in));
        boolean cont = true;
        String pop_server = null;
        String username = null;
        String password = null;

        while (cont){
            System.out.println("Please Enter the address of POP Server: ");
            pop_server = lineread.readLine();
            System.out.println("The address of POP Server: " + pop_server);
            System.out.println("Above is right?(y/n)");
            buf = lineread.readLine();
            if(buf.equalsIgnoreCase("y")){
                cont = false;
            }
        }

        pop = new Socket(pop_server, POP_PORT);
        pop_in = new BufferedReader(new InputStreamReader(pop.getInputStream()));
        pop_out = new PrintWriter(pop.getOutputStream());
        String res = pop_in.readLine();
        System.out.println(res);

        if (!res.substring(0, 3).equals("+OK")){
            pop.close();
            throw new RuntimeException(res);
        }

        cont = true;

        while (cont){
            System.out.println("Please Enter user name: ");
            username = lineread.readLine();
            System.out.println("Please Enter password: ");
            password = lineread.readLine();
            System.out.println("user name " + username);
            System.out.println("password " + password);
            System.out.println("Above is right?(y/n)");
            buf = lineread.readLine();
            if (buf.equalsIgnoreCase("y")){
                cont = false;
            }
        }

        getSingleLine("USER " + username);
        getSingleLine("PASS " + password);
    }

    public void update() throws IOException{
        getSingleLine("QUIT");
        pop.close();
    }

    public void mainproc(String[] args) throws IOException {
        if (args.length == 0){
            authorization();
            transaction();
            update();
        }
        else{
            System.out.println("usage:java Pop");
        }
    }

    public static void main(String[] args) throws IOException{
        Pop p = new Pop();
        p.mainproc(args);
    }
}
