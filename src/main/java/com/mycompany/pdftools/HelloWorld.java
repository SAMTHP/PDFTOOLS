package com.mycompany.pdftools;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Samir Founou
 */
public class HelloWorld {
    public static final String DEST = "/home/avizzeo/Documents/Code/LEARN_JAVA/PDFTools";
    
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new HelloWorld().createPdf(DEST);
    }
    
    public void createPdf(String dest) throws IOException{
        // Initialize PDF writer
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        
        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        
        // Initialize document
        Document document = new Document(pdf);
        
        // Add paragraph to the document
        document.add(new Paragraph("Hello World!"));
        
        document.close();
    }
}
