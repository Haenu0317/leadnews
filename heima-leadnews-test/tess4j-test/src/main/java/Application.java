import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Application {
    public static void main(String[] args) throws TesseractException {
        ITesseract iTesseract = new Tesseract();
        iTesseract.setDatapath("E:\\Code_Project\\heima-leadnews\\heima-leadnews-test\\tess4j-test\\src\\main\\java\\tessdata");
        iTesseract.setLanguage("chi_sim");
        File file = new File("E:\\Code_Project\\heima-leadnews\\heima-leadnews-test\\tess4j-test\\src\\main\\resources\\test.jpeg");
        String result = iTesseract.doOCR(file);
        System.out.println(result.replace("\\r|\\n", "-"));
    }
}
