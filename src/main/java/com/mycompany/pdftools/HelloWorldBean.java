/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pdftools;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author avizzeo
 */
@Named(value = "helloWorldBean")
@Dependent
public class HelloWorldBean {
    public String dest = "";
    
    public void createPdf() throws IOException {
        
        File file = new File(this.dest);
        
        file.getParentFile().mkdirs();
        
        new HelloWorldBean(this.dest).pdfCreation(this.dest);
    }
    
    public void pdfCreation(String dest) throws IOException{
        //Initialize pdf writer
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        
        //Initialize pdf document
        PdfDocument pdf = new PdfDocument(writer);
        
        //Initialize document
        Document document = new Document(pdf);
        
        //Add paragraph
        document.add(new Paragraph("Hello World!"));
        
        document.close();
    }
    /**
     * Creates a new instance of HelloWorldBean
     */
    public HelloWorldBean(String path) {
        this.dest = path;
    }
    
}
