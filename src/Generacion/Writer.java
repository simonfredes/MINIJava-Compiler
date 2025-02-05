package Generacion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    private FileWriter writer;
    public Writer(String archivo) {

        try {
            writer = new FileWriter(archivo);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void write(String s) {
        try {
            writer.write(s);
            writer.write("\n");
        }catch
        (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void closeArchivo(){
        try {
            writer.close();
        }catch
        (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
