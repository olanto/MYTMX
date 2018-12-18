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
import org.olanto.util.StringManipulation;

/**
 *
 * @author jg
 */
public class ConvertAndCleanTXM {

    static String sourceTMX, targetMFLF, lang2, corpusName, langSO, langTA;
    static long totentities;
    static OutputStreamWriter outmflfsmallbebore;
    static StringManipulation stringManip = new StringManipulation();
    static int limit = Integer.MAX_VALUE;
    /*   
     */

    public static void main(String[] args) {
        HtmlEntitiesMap.init();
        String drive = "C:";
        langSO = "de";langTA = "en";

        lang2 = langSO + "-" + langTA;
        sourceTMX = drive + "/MYTMX/corpus/TMX/";
        targetMFLF = "C:" + "/MYTMX/corpus/BLCK/";

processdir(sourceTMX + lang2 + "/", lang2);
    }

    public static void processdir(String path, String sota) {
        File f = new File(path);
        if (f.isFile()) {
            //System.out.println("path:"+f);
            if (path.endsWith(".tmx") && path.contains(sota)) {
                process(f.getName().substring(0, f.getName().length() - 4));
            }
        } else {
            String[] lf = f.list();
            int ilf = Array.getLength(lf);
            for (int i = 0; i < ilf; i++) {
                processdir(path + "/" + lf[i], sota);
            }
        }
    }

    static void process(String corpusName) {

        System.out.println("------------- entry TMX: " + corpusName);
        System.out.println("------------- so-ta    : " + langSO + "-" + langTA);
        int totTMXEntry = 0;
        totentities=0;
        try {
            InputStreamReader isrtmx = new InputStreamReader(new FileInputStream(sourceTMX + lang2 + "/" + corpusName + ".tmx"), "UTF-8");
            BufferedReader tmx = new BufferedReader(isrtmx);
            OutputStreamWriter outmflf = new OutputStreamWriter(new FileOutputStream(targetMFLF + "/" + corpusName + ".mflf"), "UTF-8");
            String w = tmx.readLine();
            while (w != null && totTMXEntry < limit) {
                if (w.contains("<tuv xml:lang=\"" + langSO + "\">")) {
                    String wso = extractSentence(w);
                    w = tmx.readLine();
                    if (w.contains("<tuv xml:lang=\"" + langTA + "\">")) {
                        totTMXEntry++;
                        String wta = extractSentence(w);                        
                        outmflf.append(Hascode64.s64(wso)+ "\t" +wso + "\t" +Hascode64.s64(wta)+ "\t" + wta + "\n");
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
