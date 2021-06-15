import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MailContent {
    String ContentType;
    String From;
    String ContentTransferEncoding;
    String Subject;
    String Date;
    String Content;
    String Charset;
    String Email;
    MailBoxInterface window;
    ArrayList<String> Related;
    ArrayList<String> Related_id;
    ArrayList<String> Mixed;
    ArrayList<String> Mixed_name;


    MailContent(MailBoxInterface win, String content, String date, String contentType, String Sub, String from, String contentTransferEncoding, ArrayList<String> mixed, ArrayList<String> mixed_name, ArrayList<String> related, ArrayList<String> related_id, String charset){
        window = win;
        Content = content;
        Date = date;
        ContentType = contentType;
        Subject = Sub;
        From = from;
        ContentTransferEncoding = contentTransferEncoding;
        Related = related;
        Related_id = related_id;
        Mixed = mixed;
        Mixed_name = mixed_name;
        Charset = charset;
    }

    private void parseMail(int num) throws UnsupportedEncodingException {
        String[] split;

        if (Subject.contains("utf-8") || Subject.contains("UTF-8")){
            if (Subject.contains("?Q")){
                split = Subject.split("=?UTF-8\\?Q\\?|=?utf-8\\?Q\\?|\\?=");
                Subject = "";
                for (int i = 1; i < split.length; i += 2){
                    try {
                        Subject += new QuotedPrintableCodec().decode(split[i]);
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (Subject.contains("?B")){
                split = Subject.split("=?UTF-8\\?B\\?|=?utf-8\\?B\\?|\\?=");
                Subject = "";
                for (int i = 1; i < split.length; i += 2){
                    Subject += decode(split[i]);
                }
            }
        }
        else if (Subject.contains("big5") || Subject.contains("BIG5")){
            if (Subject.contains("?Q")){
                split = Subject.split("=?BIG5\\?Q\\?|=?big5\\?Q\\?|\\?=");
                Subject = "";
                for (int i = 1; i < split.length; i += 2){
                    try {
                        Subject += new String(new QuotedPrintableCodec().decode(split[i]).getBytes(StandardCharsets.UTF_8), "Big5");
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (Subject.contains("?B")){
                split = Subject.split("=?BIG5\\?B\\?|=?big5\\?B\\?|\\?=");
                Subject = "";
                for (int i = 1; i < split.length; i += 2){
                    Subject += new String(new Base64().decode(split[i]), "Big5");
                }

            }
        }
        else{
            Subject = Subject.replace("Subject: ", "");
        }

        if (From.contains("<")){
            Email = From.split("<|>")[1];
        }

        if (From.contains("utf-8") || From.contains("UTF-8")){
            if (From.contains("?Q")){
                split = From.split("=?UTF-8\\?Q\\?|=?utf-8\\?Q\\?|\\?=");
                From = "";
                for (int i = 1; i < split.length; i += 2){
                    try {
                        From += new QuotedPrintableCodec().decode(split[i]);
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (From.contains("?B")){
                split = From.split("=?UTF-8\\?B\\?|=?utf-8\\?B\\?|\\?=");
                From = "";
                for (int i = 1; i < split.length; i += 2){
                    From += split[i];
                }
                From = decode(From);
            }
            From += "   " + Email;
        }
        else if (From.contains("big5") || From.contains("BIG5")){
            if (From.contains("?Q")){
                split = From.split("=?BIG5\\?Q\\?|=?big5\\?Q\\?|\\?=");
                From = "";
                for (int i = 1; i < split.length; i += 2){
                    try {
                        From += new String(new QuotedPrintableCodec().decode(split[i]).getBytes(StandardCharsets.UTF_8), "Big5");
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (From.contains("?B")){
                split = From.split("=?BIG5\\?B\\?|=?big5\\?B\\?|\\?=");
                From = "";
                for (int i = 1; i < split.length; i += 2){
                    From += new String(new Base64().decode(split[i]), "Big5");
                }
            }
            From += "   " + Email;
        }
        else{
            From = From.replace("From: ", "");
        }

        Date = Date.replace("le-Original-Date: ", "");

        if (ContentTransferEncoding.equalsIgnoreCase("base64")){
            Content = new String(new Base64().decode(Content));
        }
        else if ((ContentTransferEncoding.equalsIgnoreCase("quoted-printable"))){
            String a = "<div style=\"color:red\">Mail is encoding by quoted-printable Not Base64!!<div/><br/>";
            split = Content.split("\n");
            Content = "";
            for (int i = 0; i < split.length; i++){
                if (Charset.equalsIgnoreCase("utf-8")){
                    try {
                        Content += new QuotedPrintableCodec().decode(split[i]);
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
                else if (Charset.equalsIgnoreCase("Big5")){
                    try {
                        Content += new String(new QuotedPrintableCodec().decode(split[i], "Big5"));
                        if (num == 1){
                            Content += "<br/>";
                        }
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        String url = null;
        for (int i = 0; i < Related.size(); i++){
            try {
                url = saveImageToCache(Related.get(i), Related_id.get(i));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Content = Content.replace("cid:" + Related_id.get(i), url);
        }
        if (Content.contains("</body>")){
            Content = Content.split("<body.*?>|</body>")[1];
        }

        String temp;


    }

    public void print(int num) throws UnsupportedEncodingException {
        System.out.println(Subject);
        parseMail(num);
        window.editorPane1.setText(Content);
        window.subject.setText(Subject);
        window.from.setText(From);
        window.date.setText(Date);
    }

    private String decode(String input) {
        return new String(new Base64().decode(input));
    }

    public void downloadAllFile(){
        String temp;
        String[] split;
        for (int i = 0; i < Mixed.size(); i++){
            if (Mixed_name.get(i).contains("utf-8") || Mixed_name.get(i).contains("UTF-8")){
                if (Mixed_name.get(i).contains("?Q")){
                    split = Mixed_name.get(i).split("=?UTF-8\\?Q\\?|=?utf-8\\?Q\\?|\\?=");
                    Mixed_name.set(i, "");
                    temp = "";
                    for (int j = 1; j < split.length; j += 2){
                        try {
                            temp += new QuotedPrintableCodec().decode(split[j]);
                        } catch (DecoderException e) {
                            e.printStackTrace();
                        }
                    }
                    Mixed_name.set(i, temp);
                }
                else if (Mixed_name.get(i).contains("?B")){
                    split = Mixed_name.get(i).split("=?UTF-8\\?B\\?|=?utf-8\\?B\\?|\\?=");
                    Mixed_name.set(i, "");
                    temp = "";
                    for (int j = 1; j < split.length; j += 2){
                        temp += split[j];
                    }
                    Mixed_name.set(i, decode(temp));
                }
            }
            else if (Mixed_name.get(i).contains("big5") || Mixed_name.get(i).contains("BIG5")){
                if (Mixed_name.get(i).contains("?Q")){
                    split = Mixed_name.get(i).split("=?BIG5\\?Q\\?|=?big5\\?Q\\?|\\?=");
                    Mixed_name.set(i, "");
                    temp = "";
                    for (int j = 1; j < split.length; j += 2){
                        try {
                            try {
                                temp += new String(new QuotedPrintableCodec().decode(split[j]).getBytes(StandardCharsets.UTF_8), "Big5");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } catch (DecoderException e) {
                            e.printStackTrace();
                        }
                    }
                    Mixed_name.set(i, temp);
                }
                else if (Mixed_name.get(i).contains("?B")){
                    split = Mixed_name.get(i).split("=?BIG5\\?B\\?|=?big5\\?B\\?|\\?=");
                    Mixed_name.set(i, "");
                    temp = "";
                    for (int j = 1; j < split.length; j += 2){
                        try {
                            temp += new String(new Base64().decode(split[j]), "Big5");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    Mixed_name.set(i, temp);
                }
            }
            downloadFile(Mixed.get(i), Mixed_name.get(i));
        }
    }

    private void downloadFile(String content, String name){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(name);
            byte[] decoded = java.util.Base64.getDecoder().decode(content);
            fos.write(decoded);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String saveImageToCache(String input, String name) throws MalformedURLException {
        JEditorPane pane = this.window.editorPane1;
        byte[] imgBytes = new Base64().decode(input);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(imgBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Dictionary cache = (Dictionary) pane.getDocument().getProperty("imageCache");
        if (cache == null) {
            cache = new Hashtable();
            pane.getDocument().putProperty("imageCache", cache);
        }
        String url = "http:\\buffered/" + name;
        cache.put(new URL(url), img);
        return url;
    }
}
