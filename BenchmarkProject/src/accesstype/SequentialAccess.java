package accesstype;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import filehandling.FileSystem;

public class SequentialAccess extends Access {

    //private ByteBuffer buf;
    private static final int BUFFER_FILL_SIZE = 4 * 1024 * 1024; // 4 MB for the buffer filler
    private static final int GB_TO_BYTE = 1024 * 1024;    // for conversion of the file size from GB to bytes
    private File file;
    private FileSystem fs;
    private int cntTests;

    public SequentialAccess(int numTests) {
        super(numTests);
        fs = new FileSystem();               //use a FileSystem object in order to construct the path of the file
        file = new File(fs.getFilePath());   //open a new temp file with the specified path
    }

    public void read(int fileSize, int bufferSize) {
        long fSize = GB_TO_BYTE;     //convert the file size from GB to bytes
        fSize = fileSize * fSize;
        ByteBuffer buf = ByteBuffer.allocate(bufferSize);
        FileChannel inChannel;
        FileInputStream in;

        try {
            in = new FileInputStream(file);        //get file input stream for reading from the file
            inChannel = in.getChannel();           //get the associated channel of the file
            while (inChannel.read(buf) != -1)       //while there still are bytes to be read from file, continue
                buf.clear();                       //clear the buffer so the next input can be stored correctly
            inChannel.close();
            in.close();
            System.out.println("haha");
            if (super.shouldDeleteFile(++cntTests)) {
                System.out.println("haha222");
                fs.deleteFile(file);               //delete the temp file after benchmark finishes
                cntTests = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int fileSize) {         //overloaded method for writing the temp file used for reading
        write(fileSize, BUFFER_FILL_SIZE);    //will write the file with a specified buffer size and file size
    }

    public void write(int fileSize, int bufferSize) {
        BufferedOutputStream out;
        long crntWrittenSize = 0;
        long fSize = GB_TO_BYTE;      //convert the file size from GB to bytes
        fSize = fileSize * fSize;
        byte[] buf = new byte[bufferSize];

        try {
            out = new BufferedOutputStream(new FileOutputStream(file));    //get buffered file output stream for writing in the file
            while (crntWrittenSize < fSize) {          //continue writing until the file size was reached
                fillBuffer(buf);                     //add random bytes into a buffer of the given size
                out.write(buf);                     //write these bytes through the channel into the file
                crntWrittenSize += bufferSize;       //update the total amount of bytes written so far in the file
            }
            out.close();
            if (super.shouldDeleteFile(++cntTests)) {
                System.out.println("File has been deleted(Code: \" haha222\")");
                fs.deleteFile(file);               //delete the temp file after benchmark finishes
                cntTests = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillBuffer(byte[] bytes) {
        Random rand = new Random();
        rand.nextBytes(bytes);
    }

}
