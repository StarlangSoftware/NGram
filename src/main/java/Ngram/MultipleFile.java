package Ngram;

import Util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MultipleFile {

    private BufferedReader br;
    private int index;
    private final String[] fileNameList;

    public MultipleFile(String... fileNameList){
        index = 0;
        this.fileNameList = fileNameList;
        InputStream inputStream = FileUtils.getInputStream(fileNameList[index]);
        if (inputStream != null){
            br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }
    }

    public void close(){
        try {
            br.close();
        } catch (IOException ignored) {
        }
    }

    public String readLine(){
        String tmpLine;
        try {
            tmpLine = br.readLine();
            if (tmpLine != null){
                return tmpLine;
            } else {
                br.close();
                index++;
                InputStream inputStream = FileUtils.getInputStream(fileNameList[index]);
                if (inputStream != null){
                    br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    return br.readLine();
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public BufferedReader getBufferedReader(){
        return br;
    }

}
