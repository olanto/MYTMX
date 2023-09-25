/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.mytmx.generic;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.olanto.idxvli.doc.PropertiesList;
import org.olanto.idxvli.server.IndexService_MyCat;
import org.olanto.idxvli.server.SetLanguageProperties;
import org.olanto.idxvli.util.SetOfBits;
import org.olanto.util.ExtensionUtils;
import static org.olanto.util.Messages.*;

/**
 *
 * @author simple
 */
public class SetLanguageProperties4TMX extends SetLanguageProperties {

    public static String getLangOfDoc(String name) {

        String lang = name.substring(name.length() - 2, name.length());
//        msg("language of "+name+" is "+lang);
        return lang;
    }

    public static String getNameOfDocForThisLang(String name, String Lang) {
        String res = name.substring(0, name.length() - 3) + "_" + Lang;
//        msg("target of "+name+" is "+res);
        return res;
    }
    static String rootTxt;
    static IndexService_MyCat is;
    static boolean verbose = false;

    public static void updateLanguageProperties(IndexService_MyCat ispar) {

        is = ispar;

        try {

            rootTxt = is.getROOT_CORPUS_TXT();
            int lastdoc = is.getSize(); // taille du corpus

            String[] setOfLang = is.getCorpusLanguages();  // langues du corpus

            ExtensionUtils extutil = new ExtensionUtils(setOfLang);

            for (int i = 0; i < setOfLang.length; i++) {
                msg("clear properties for: " + setOfLang[i]);
                is.clearThisProperty("SOURCE." + setOfLang[i]);
                is.clearThisProperty("TARGET." + setOfLang[i]);
            }

            for (int i = 0; i < lastdoc; i++) {
                String name = is.getDocName(i);
                String language = getLangOfDoc(name);
                if (!language.equals("XX")) {  // mono lingue _XX
                    is.setDocumentPropertie(i, "SOURCE." + language);
                    for (int j = 0; j < setOfLang.length; j++) {
                        setTargetLang(i, name, setOfLang[j]);
                    }
                } else {// multi lingue _XX_YY
                    language = extutil.getNExt(name.substring(0, name.length() - 4)); // supprime le .txt
                    if (verbose) {
                        msg("extension" + language + " for:" + name.substring(0, name.length() - 4));
                    }

                    if (language.length() == 3) { // extension simple _XX
                        msg("WARNING in glossaries, mono languages for:" + name);
                    } else if (language.length() > 3) { // glossaries _XX_YY
                        if (verbose) {
                            msg("WARNING in glossaries, multi languages for:" + name);
                        }
                        if (verbose) {
                            msg("gloss ext:" + language);
                        }
                        is.setDocumentPropertie(i, "SOURCE." + "XX"); // marque la langue des glossaires
                        for (int j = 0; j < language.length() / 3; j++) {
                            String langsota = language.substring(j * 3 + 1, j * 3 + 3); //extract langue
                            msg("maek ext:" + langsota);
                            is.setDocumentPropertie(i, "SOURCE." + langsota);
                            is.setDocumentPropertie(i, "TARGET." + langsota);
                        }

                    } else {
                        msg("WARNING in glossaries, no language for:" + name);
                    }
                }

                if ((i + 1) % 10000 == 0) {
                    System.out.print(".");
                }
                if ((i + 1) % 1000000 == 0) {
                    System.out.println();
                }
            }
            inventoryOf(setOfLang);
            msg("language properties are updated ...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTargetLang(int isource, String name, String Lang) {
        try {
            String nameTarget = getNameOfDocForThisLang(name, Lang);
            if (is.getDocId(nameTarget) > 0) {
                // existe un document target
                is.setDocumentPropertie(isource, "TARGET." + Lang);
                //msg("text for "+nameTarget);
            } else {
                //msg("no text for "+nameTarget);
                ;


            }
        } catch (RemoteException ex) {
            Logger.getLogger(SetLanguageProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void inventoryOf(String[] setOfLang) {
        msg("");
        msg("");
        msg("---------- ");
        try {
            PropertiesList prop;
//            msg("---- all properties:");
//            PropertiesList prop = is.getDictionnary();
//            showVector(prop.result);
            msg("---- all properties SOURCE:");
            prop = is.getDictionnary("SOURCE.");
            showVector(prop.result);
            msg("---- all properties TARGET:");
            prop = is.getDictionnary("TARGET.");
            showVector(prop.result);
            msg("#doc:" + is.getSize());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        msg("----- GLOSSARY ---------------------------");
        countSet("SOURCE." + "XX");
        msg("----- SOURCE ---------------------------");
        for (int i = 0; i < setOfLang.length; i++) {
            countSet("SOURCE." + setOfLang[i]);
        }
        msg("----- TARGET ---------------------------");
        for (int i = 0; i < setOfLang.length; i++) {
            countSet("TARGET." + setOfLang[i]);
        }
    }

    static void countSet(String setName) {
        try {
            int lastdoc = is.getSize();
            int count = 0;
            SetOfBits sob = is.satisfyThisProperty(setName);
            if (sob == null) {
                msg("no property for " + setName + " :" + count);
                return;
            }

            for (int i = 0; i < lastdoc; i++) {
                if (sob.get(i)) {
                    count++;
                }
            }
            msg("count for " + setName + " :" + count);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
