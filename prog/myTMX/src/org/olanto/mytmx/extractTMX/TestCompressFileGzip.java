/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.mytmx.extractTMX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TestCompressFileGzip {

    public static void main(String[] args) {

        String gzip_filepath = "U:\\TMX\\EN-Centered\\NewCom\\en-it.tmx.gz";
        String gzip_filepathout = "U:\\TMX\\EN-Centered\\NewCom\\en-it.tmx_copy.gz";
        String decopressed_filepath = "U:\\TMX\\EN-Centered\\NewCom\\en-it.tmx";

        TestCompressFileGzip gZipFile = new TestCompressFileGzip();

       // gZipFile.unGunzipFile(gzip_filepath, decopressed_filepath);
        
        
        gZipFile.processGZFile(gzip_filepath,gzip_filepathout);
    }

    public void processGZFile(String compressedFile,String gzip_filepathout) {


        try {

            InputStream fileStream = new FileInputStream(compressedFile);
            InputStream gzipStream = new GZIPInputStream(fileStream);
            Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
            BufferedReader in = new BufferedReader(decoder);
           
            GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(gzip_filepathout)));
            OutputStreamWriter out = new OutputStreamWriter(zip, "UTF-8");

            
            String w = in.readLine();

            while (w != null) {
                //System.out.println(w);
                 out.append(w+"\n");
               w = in.readLine();
            }

            fileStream.close();
            out.flush();
            zip.close();

            System.out.println("The file was decompressed successfully!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
  
    
       public void gzipFile(String source_filepath, String destinaton_zip_filepath) {
 
        byte[] buffer = new byte[1024];
 
        try {
 
            FileOutputStream fileOutputStream = new FileOutputStream(
                    destinaton_zip_filepath);
 
            GZIPOutputStream gzipOuputStream = new GZIPOutputStream(
                    fileOutputStream);
 
            FileInputStream fileInput = new FileInputStream(source_filepath);
 
            int bytes_read;
 
            while ((bytes_read = fileInput.read(buffer)) > 0) {
                gzipOuputStream.write(buffer, 0, bytes_read);
            }
 
            fileInput.close();
 
            gzipOuputStream.finish();
            gzipOuputStream.close();
 
            System.out.println("The file was compressed successfully!");
 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 
    
    
    public void unGunzipFile(String compressedFile, String decompressedFile) {

        byte[] buffer = new byte[1024];

        try {

            FileInputStream fileIn = new FileInputStream(compressedFile);

            GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

            FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);

            int bytes_read;

            while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {

                fileOutputStream.write(buffer, 0, bytes_read);
            }

            gZIPInputStream.close();
            fileOutputStream.close();

            System.out.println("The file was decompressed successfully!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}