/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pdftools;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.utils.PdfMerger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author avizzeo
 */
@ManagedBean
@SessionScoped
public class HelloBean {
    private String phrase;
    
    private String path = "";

    private String pdfName;
    
    private String fusionnedPdfName;
    
    private boolean fusion;
    
    private String pdfContent;
    
    private boolean alert;
    
    private String extension;
    
    private List<String> pdfFiles;
    
    private File[] listOfFiles;
    
    private File folder;
    
    public static final String DEST = "/home/avizzeo/Documents/Code/LEARN_JAVA/PDFTools/newPdf/";
    
    /**
     * Creates a new instance of HelloBean
     */
    public HelloBean() {
//        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.phrase     = "PDF MANAGER";  
        this.alert = false;
        this.fusion = false;
        this.extension = ".pdf";
        pdfFiles = new ArrayList<String>();
        folder = new File(DEST);
        listOfFiles = folder.listFiles();
        
    }
    
    public List<String> getPdfFiles() {
        for (int i = 0; i < listOfFiles.length; i++) {
            pdfFiles.add(listOfFiles[i].getName());
        }
        return pdfFiles;
    }
    
    public String createPdf(String pathDirectory) throws IOException{
        File file = new File(pathDirectory + this.pdfName + this.extension);
        
        file.getParentFile().mkdirs();
        
        // Initialize PDF writer
        FileOutputStream fos = new FileOutputStream(file);
        PdfWriter writer = new PdfWriter(fos);
        
        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        
        // Initialize document
        Document document = new Document(pdf);
        
        // Add paragraph to the document
        document.add(new Paragraph(this.pdfContent));
        
        document.close();
        
        this.alert = true;
        
        this.alertMessageSuccessCreation = "PDF '" + this.pdfName + "' créé avec succès";
        
        return "index.xhtml";
    }
    
    public static void main(String[] args) {
        try {
            new HelloBean().mergePdf();
        } catch (IOException ex) {
            Logger.getLogger(HelloBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void mergePdf() throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        manipulatePdf(DEST + this.pdfName + this.extension);
        this.alert = true;
        this.alertMessageSuccessCreation = "Fusion éffectuée avec succès => '" + this.pdfName + "' généré après fusion";
    }
 
    protected void manipulatePdf(String dest) throws IOException {
        getPdfFiles();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        
        PdfMerger merger = new PdfMerger(pdfDoc);
        
        for(String pdf : this.pdfFiles){
            PdfDocument srcDoc = new PdfDocument(new PdfReader(DEST + pdf));
            System.out.println(pdf);
            int numberOfPages = srcDoc.getNumberOfPages();
            
            merger.setCloseSourceDocuments(true)
                .merge(srcDoc, 1, numberOfPages);
        }

        PdfOutline rootOutline = pdfDoc.getOutlines(false);
        
        this.fusionnedPdfName = DEST + this.pdfName + ".pdf";
        
        this.fusion = true;
        
        pdfDoc.close();
    }
    
    public void extractPdf() throws IOException {

    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public File[] getListOfFiles() {
        return listOfFiles;
    }

    public void setListOfFiles(File[] listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    public void setPdfFiles(List<String> pdfFiles) {
        this.pdfFiles = pdfFiles;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    private String alertMessageSuccessCreation;

    public String getAlertMessageSuccessCreation() {
        return alertMessageSuccessCreation;
    }

    public void setAlertMessageSuccessCreation(String alertMessageSuccessCreation) {
        this.alertMessageSuccessCreation = alertMessageSuccessCreation;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public String getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(String pdfContent) {
        this.pdfContent = pdfContent;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }
    
    public String getFusionnedPdfName() {
        return fusionnedPdfName;
    }

    public void setFusionnedPdfName(String fusionnedPdfName) {
        this.fusionnedPdfName = fusionnedPdfName;
    }
    
     public boolean isFusion() {
        return fusion;
    }

    public void setFusion(boolean fusion) {
        this.fusion = fusion;
    }
}
