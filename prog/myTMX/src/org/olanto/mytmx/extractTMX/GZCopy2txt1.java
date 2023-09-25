package org.olanto.mytmx.extractTMX;


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
import org.olanto.mytmx.extractTMX.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.zip.GZIPInputStream;
import org.olanto.util.StringManipulation;

/**
 *
 * @author jg
 */
public class GZCopy2txt1 {

    static String sourceTMX, targetMFLF, corpusName, pivotLang;
    static int limit = Integer.MAX_VALUE;
    /*   
     */

    public static void main(String[] args) {
        HtmlEntitiesMap.init();

        pivotLang = "EN";
        sourceTMX = "U:" + "/MYTMX/BLCK/EN-Centered/";
        targetMFLF = "U:" + "/MYTMX/text/";

        processdir(sourceTMX + "/", pivotLang);
    }

    public static void processdir(String path, String pivotLang) {
        File f = new File(path);
        if (f.isFile()) {
            //System.out.println("path:"+f);
            if (path.endsWith(".mflf.gz")) {
                process(f.getName(), pivotLang);
            }
        } else {
            String[] lf = f.list();
            int ilf = Array.getLength(lf);
            for (int i = 0; i < ilf; i++) {
                processdir(path + "/" + lf[i], pivotLang);
            }
        }
    }

    static void process(String corpusName, String pivotLang) {

        boolean reversePivot = false;

        System.out.println("------------- entry TMX: " + corpusName);

        String[] partname = corpusName.split("\\.");
        System.out.println("------------- corpus: " + partname[0]);
        String corpus = partname[0].substring(0, Math.min(9, partname[0].length()));
        System.out.println("------------- so-ta: " + partname[1]);
        System.out.println("------------- ext: " + partname[2]);
        System.out.println("------------- gz: " + partname[3]);

        String[] partlang = partname[1].split("-");
        String langSO = partlang[0].toUpperCase();
        String langTA = partlang[1].toUpperCase();
        System.out.println("------------- SO-TA    : " + langSO + "-" + langTA);

        if (langTA.equals(pivotLang)) {
            reversePivot = true;
            String tempo = langSO;
            langSO = langTA;
            langTA = tempo;
        }


        int totTMXEntry = 0;

        try {
//            InputStreamReader isrtmx = new InputStreamReader(new FileInputStream(sourceTMX + "/" + corpusName), "UTF-8");
//            BufferedReader tmx = new BufferedReader(isrtmx);
   
                InputStream fileStream = new FileInputStream(sourceTMX + "/" + corpusName);
                InputStream gzipStream = new GZIPInputStream(fileStream);
                Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
                BufferedReader tmx = new BufferedReader(decoder);

            
            
            OutputStreamWriter SOmflf = new OutputStreamWriter(new FileOutputStream(targetMFLF + "/" + langSO + "/" + corpus+"."+partname[1] + ".mono"), "UTF-8");
            OutputStreamWriter TAmflf = new OutputStreamWriter(new FileOutputStream(targetMFLF + "/" + langTA + "/" + corpus+"."+partname[1] + ".mono"), "UTF-8");

            String w = tmx.readLine();
            while (w != null && totTMXEntry < limit) {
                String[] part = w.split("\t");

                if (langSO.equals(pivotLang)) { // keep pivot id for both langages
                    if (reversePivot) {
                        SOmflf.append(corpus + "¦" + part[2] + "_" + langSO + "\t" + part[3] + "\n");
                        TAmflf.append(corpus + "¦" + part[2] + "_" + langTA + "\t" + part[1] + "\n");
                    } else {
                        SOmflf.append(corpus + "¦" + part[0] + "_" + langSO + "\t" + part[1] + "\n");
                        TAmflf.append(corpus + "¦" + part[0] + "_" + langTA + "\t" + part[3] + "\n");
                    }
                } else { // keep different id
                    SOmflf.append(corpus + "¦" + part[0] + "_" + langSO + "\t" + part[1] + "\n");
                    TAmflf.append(corpus + "¦" + part[2] + "_" + langTA + "\t" + part[3] + "\n");
                }
            

            w = tmx.readLine();
        }
        tmx.close();
        SOmflf.close();        TAmflf.close();
        System.out.println("   read entries: " + totTMXEntry);
    }
    catch (Exception e

    
        ) {
            e.printStackTrace();
    }
}

}
