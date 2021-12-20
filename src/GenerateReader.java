import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by root on 11.12.17.
 */
public class GenerateReader {
    private String data;
    private int nom = 0;
    private boolean inputState = true;

    public GenerateReader(String str){
        data = str;
    }

    public void read() {
        if(nom < data.length()-1)
            nom++;
        else
            inputState = false;
    }
    public char get(){
        return data.charAt(nom);
    }
    public boolean isInputState(){
        return inputState;
    }

}
