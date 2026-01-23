package utils;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class PDFUtil {

    public static String extractText(PDFTextStripperByArea stripper, PDPage page, Rectangle2D region)
            throws IOException {

        stripper.addRegion("squareRegion", region);
        stripper.extractRegions(page);
        return stripper.getTextForRegion("squareRegion").trim();
    }

}
