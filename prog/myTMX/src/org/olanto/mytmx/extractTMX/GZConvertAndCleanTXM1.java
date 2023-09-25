/**
 * ********
 * Copyright © 2010-208 Olanto Foundation Geneva
 *
 * This file is part of myTMX.
 *
 * myCAT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myTMX. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.mytmx.extractTMX;

import java.io.*;
import java.lang.reflect.Array;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.olanto.util.StringManipulation;

/**
 *
 * @author jg
 */
public class GZConvertAndCleanTXM1 {

    static String sourceTMX, targetMFLF, lang2, corpusName, langSO, langTA;
    static long totentities;
    static OutputStreamWriter outmflfsmallbebore;
    static StringManipulation stringManip = new StringManipulation();
    static int limit = Integer.MAX_VALUE;
    static String pivotLang = "en";
    static final boolean processIt = false;
    static String drive = "F:";
    static String langList = "bg ca cs da de el es et fi fr ga hu it lt lv mt nl no pl pt ro sk sl sv ar ru zh eo";
    /*   
     */

    public static void main(String[] args) {
        HtmlEntitiesMap.init();

        sourceTMX = drive + "/MYTMX/TMX/EN-Centered/";
        targetMFLF = drive + "/MYTMX/BLCK/EN-Centered/";
        scanSOTA("BOOKS");
       scanSOTA("CAPES");
       scanSOTA("ECB");
       scanSOTA("EMEA");
       scanSOTA("EUBKS");
       scanSOTA("EUPARL");
       scanSOTA("JRCACQ");
       scanSOTA("MULTUN");
       scanSOTA("NEWCOM");
       scanSOTA("OPENST");
       scanSOTA("QED");
       scanSOTA("SCIELO");
       scanSOTA("TATOEB");
       scanSOTA("TED");
       scanSOTA("UNPC");
       scanSOTA("WIKTRA");
        
       
    }

    public static void scanSOTA(String collection) {
        String[] langs = langList.split(" ");
        for (int i = 0; i < langs.length; i++) {
            if (langs[i].compareTo(pivotLang) < 0) {
                langSO = langs[i];
                langTA = pivotLang;
            } else {
                langSO = pivotLang;
                langTA = langs[i];
            }


            lang2 = langSO + "-" + langTA;

            processdir(sourceTMX + collection + "/", lang2, collection);
        }
    }

    public static void processdir(String path, String sota, String collection) {
        File f = new File(path);
        //System.out.println("path:"+f);
        if (f.isFile()) {
            //System.out.println("path:"+f);
            if (path.endsWith(".tmx.gz") && path.contains(sota)) {
                process(f.getName().substring(0, f.getName().length() - 7), collection);
            }
        } else {
            String[] lf = f.list();
            int ilf = Array.getLength(lf);
            for (int i = 0; i < ilf; i++) {
                processdir(path + "/" + lf[i], sota, collection);
            }
        }
    }

    static void process(String corpusName, String collection) {

        System.out.println("------------- entry TMX: " + corpusName);
        System.out.println("------------- collection: " + collection);
        String inputFName = sourceTMX + collection + "/" + corpusName + ".tmx.gz";
        System.out.println("------------- inputFName: " + inputFName);
        System.out.println("------------- so-ta    : " + langSO + "-" + langTA);
        String outputFName = targetMFLF + collection + "." + corpusName + ".mflf.gz";
        System.out.println("------------- outputFName: " + outputFName);

        if (processIt) {
            int totTMXEntry = 0;
            totentities = 0;
            try {
//            InputStreamReader isrtmx = new InputStreamReader(new FileInputStream(inputFName), "UTF-8");
//            BufferedReader tmx = new BufferedReader(isrtmx);
//            OutputStreamWriter outmflf = new OutputStreamWriter(new FileOutputStream(outputFName), "UTF-8");

                InputStream fileStream = new FileInputStream(inputFName);
                InputStream gzipStream = new GZIPInputStream(fileStream);
                Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
                BufferedReader tmx = new BufferedReader(decoder);

                GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(outputFName)));
                OutputStreamWriter outmflf = new OutputStreamWriter(zip, "UTF-8");




                String w = tmx.readLine();
                while (w != null && totTMXEntry < limit) {
                    if (w.contains("<tuv xml:lang=\"" + langSO + "\">")) {
                        String wso = extractSentence(w);
                        w = tmx.readLine();
                        if (w.contains("<tuv xml:lang=\"" + langTA + "\">")) {
                            totTMXEntry++;
                            String wta = extractSentence(w);
                            outmflf.append(Hascode64.s64(wso) + "\t" + wso + "\t" + Hascode64.s64(wta) + "\t" + wta + "\n");
                        }
                    }
                    w = tmx.readLine();
                }
                tmx.close();
                outmflf.close();
                System.out.println("   read entries: " + totTMXEntry);
                System.out.println("   totentities substitution: " + totentities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static String extractSentence(String w) {
        String decoded = null;
        String entity = null;
        int start = w.indexOf("<seg>");
        int end = w.indexOf("</seg>");
        String res = w.substring(start + 5, end);
        //System.out.println("  initial " + res);
        try {
            while (res.contains("&amp;")) {
                start = res.indexOf("&amp;");
                end = res.indexOf(";", start + 5);
                if (end == -1) {
                    //System.out.println("  no " + res);
                    decoded = "&";
                    end = start + 4;
                } else {
                    entity = res.substring(start, end + 1);
                    decoded = HtmlEntitiesMap.getTranslation(entity);
                }

                if (decoded == null) {
                    //System.out.println("Warning this :" + entity + " is not decoded ");
                    decoded = "&";
                    end = start + 4;
                }
                //System.out.println(entity + " -> " + decoded + " in " + res);
                if (end + 1 == res.length()) { //at the end
                    res = res.substring(0, start) + decoded;
                } else { // in the middle
                    res = res.substring(0, start) + decoded + res.substring(end + 1, res.length() - 1);
                }
                totentities++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("debug1:" + entity + " -> " + decoded + " in " + res);
            System.out.println("debug2:" + res.length() + " -> " + start + " in " + end);
        }
        //System.out.println("  new " + res);
        return res;
    }
}
