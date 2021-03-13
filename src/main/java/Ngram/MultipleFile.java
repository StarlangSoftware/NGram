package Ngram;

import Util.FileUtils;

import java.io.*;

public class MultipleFile {

    private BufferedReader br;
    private int index;
    private String[] fileNameList;

    public MultipleFile(String... fileNameList){
        index = 0;
        this.fileNameList = fileNameList;
        InputStream inputStream = FileUtils.getInputStream(fileNameList[index]);
        if (inputStream != null){
            br = new BufferedReader(new InputStreamReader(inputStream));
        }
    }

    public void close(){
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    return br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedReader getBufferedReader(){
        return br;
    }

}
