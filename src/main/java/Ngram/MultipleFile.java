package Ngram;

import Util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MultipleFile {

    private BufferedReader br;
    private int index;
    private final String[] fileNameList;

    /**
     * Constructor for {@link MultipleFile} class. Initializes the buffer reader with the first input file
     * from the fileNameList. MultipleFile supports simple multipart file system, where a text file is divided
     * into multiple files.
     * @param fileNameList A list of files given as dynamic parameters.
     */
    public MultipleFile(String... fileNameList){
        index = 0;
        this.fileNameList = fileNameList;
        InputStream inputStream = FileUtils.getInputStream(fileNameList[index]);
        if (inputStream != null){
            br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }
    }

    /**
     * Closes the buffer reader.
     */
    public void close(){
        try {
            br.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Reads a single line from the current file. If the end of file is reached for the current file,
     * next file is opened and a single line from that file is read. If all files are read, the method
     * returns null.
     * @return Read line from the current file.
     */
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

    /**
     * Accessor for the buffered reader
     * @return Buffered reader
     */
    public BufferedReader getBufferedReader(){
        return br;
    }

}
