/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pdftools;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
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
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.element.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author avizzeo
 */
@ManagedBean
@SessionScoped
public class PdfBean {
    private String phrase;
    
    private String path = "";

    private String pdfName;
    
    private String fusionnedPdfName;
    
    private boolean fusion;
    
    private String pdfContent;
    
    private boolean alert;
    
    private boolean alertSup;
    
    private boolean alertConverImg;
    
    private int nbPageBeforeRemoving;
    
    private int nbPageAfterRemoving;

    private String imgName;
    
    private String extension;
    
    private List<String> pdfFiles;
    
    private File[] listOfFiles;
    
    private File folder;
    
    private String newPdfName;
    
    private List<Integer> pagesToDelete;
    
    private String pages;
    
    private String alertMessageSuccessCreation;
    
    private String alertMessageSuccessConvertImg;

    private String alertMessageSuccessPageRemoving;
    
    public static final String DEST = "/home/avizzeo/Documents/Code/LEARN_JAVA/PDFTools/newPdf/";
    public static final String IMGDEST = "/home/avizzeo/Documents/Code/LEARN_JAVA/PDFTools/images/";
    
    /**
     * Creates a new instance of HelloBean
     */
    public PdfBean() {
        this.phrase     = "PDF MANAGER";  
        this.alert = false;
        this.alertSup = false;
        this.alertConverImg = false;
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
            new PdfBean().mergePdf();
        } catch (IOException ex) {
            Logger.getLogger(PdfBean.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void deletePage() throws IOException {
        String[] pages;
        pages = this.pages.split(",");
        
        this.pagesToDelete = new ArrayList<Integer>();
        
        for(String page : pages){
            this.pagesToDelete.add(Integer.parseInt(page));
        }
        
        PdfDocument srcDoc = new PdfDocument(new PdfReader(this.DEST +  this.pdfName + this.extension), new PdfWriter(this.DEST + this.newPdfName + this.extension));

         //Listing the number of existing pages       
        this.nbPageBeforeRemoving = srcDoc.getNumberOfPages(); 
       
        for(int i = 0; i <  this.pagesToDelete.size(); i++){
            srcDoc.removePage(this.pagesToDelete.get(i)-i);
        }

        this.nbPageAfterRemoving = srcDoc.getNumberOfPages(); 
        
        this.alertSup = true;
        
        this.alertMessageSuccessPageRemoving = "Le pdf '" + this.pdfName + "' a " + this.nbPageBeforeRemoving + " pages avant suppresion.<br> " + this.pagesToDelete.size() + " pages ont été supprimées.<br> Le nouveau fichier pdf '" + this.newPdfName + "' comporte après suppresion " + this.nbPageAfterRemoving + " pages.";
        
        //Closing the document  
        srcDoc.close(); 
    }
    
    public void createImageFromPdf() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(this.DEST + this.pdfName + this.extension), new PdfWriter(this.IMGDEST + this.imgName + this.extension));
        
        PdfDictionary pageDict = pdfDoc.getFirstPage().getPdfObject();    
        PdfDictionary resources = pageDict.getAsDictionary(PdfName.Resources);
        PdfDictionary xObjects = resources.getAsDictionary(PdfName.XObject);
        PdfName imgRef = xObjects.keySet().iterator().next();
        PdfStream stream = xObjects.getAsStream(imgRef);
        Image img = convertToBlackAndWhitePng(new PdfImageXObject(stream));
 
        // Replace the original image with the grayscale image
        xObjects.put(imgRef, img.getXObject().getPdfObject());
        
        this.alertConverImg = true;
        this.alertMessageSuccessConvertImg = "Conversion de l'image => '" + this.imgName + "' éffecutée";
        pdfDoc.close();
    }
    
    private static Image convertToBlackAndWhitePng(PdfImageXObject image) throws IOException {
        BufferedImage bi = image.getBufferedImage();
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
        newBi.getGraphics().drawImage(bi, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", baos);
        return new Image(ImageDataFactory.create(baos.toByteArray()));
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
    
    public boolean isAlertSup() {
        return alertSup;
    }

    public void setAlertSup(boolean alertSup) {
        this.alertSup = alertSup;
    }
    
    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
    
    public boolean isAlertConverImg() {
        return alertConverImg;
    }

    public void setAlertConverImg(boolean alertConverImg) {
        this.alertConverImg = alertConverImg;
    }
    
    public String getAlertMessageSuccessConvertImg() {
        return alertMessageSuccessConvertImg;
    }

    public void setAlertMessageSuccessConvertImg(String alertMessageSuccessConvertImg) {
        this.alertMessageSuccessConvertImg = alertMessageSuccessConvertImg;
    }
    
    public String getNewPdfName() {
        return newPdfName;
    }

    public void setNewPdfName(String newPdfName) {
        this.newPdfName = newPdfName;
    }
    
    public String getAlertMessageSuccessPageRemoving() {
        return alertMessageSuccessPageRemoving;
    }

    public void setAlertMessageSuccessPageRemoving(String alertMessageSuccessPageRemoving) {
        this.alertMessageSuccessPageRemoving = alertMessageSuccessPageRemoving;
    }
    
    public int getNbPageBeforeRemoving() {
        return nbPageBeforeRemoving;
    }

    public void setNbPageBeforeRemoving(int nbPageBeforeRemoving) {
        this.nbPageBeforeRemoving = nbPageBeforeRemoving;
    }

    public int getNbPageAfterRemoving() {
        return nbPageAfterRemoving;
    }

    public void setNbPageAfterRemoving(int nbPageAfterRemoving) {
        this.nbPageAfterRemoving = nbPageAfterRemoving;
    }
    
    public List<Integer> getPagesToDelete() {
        return pagesToDelete;
    }

    public void setPagesToDelete(List<Integer> pagesToDelete) {
        this.pagesToDelete = pagesToDelete;
    }
    
    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
}
