package kernelSearch;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Start {
    public static void main(String[] args) {
        String pathmps = args[0];
        String pathlog = args[1];
        String pathConfig = args[2];
        
        Properties p = new Properties();
        try {
        	p.loadFromXML(new FileInputStream(new File(pathConfig)));
        }catch(Exception e) {
        	e.printStackTrace();
        }        
        p.setProperty("instance", pathmps);
        p.setProperty("log", pathlog);

        KernelSearch ks = new KernelSearch(p);
        ks.start();
    }
}

