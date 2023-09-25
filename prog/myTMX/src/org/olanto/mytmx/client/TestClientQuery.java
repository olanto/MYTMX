/**********
    Copyright © 2010-2012 Olanto Foundation Geneva

   This file is part of myCAT.

   myCAT is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.mytmx.client;

import org.olanto.conman.server.GetContentService;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.olanto.idxvli.doc.PropertiesList;
import org.olanto.idxvli.server.*;
import org.olanto.idxvli.util.SetOfBits;
import static org.olanto.util.Messages.*;
import org.olanto.util.Timer;

/** test une recherche
 *
 */
public class TestClientQuery {

    static IndexService_MyCat is;

    public static void main(String[] args) {
            is = GetContentService.getServiceMYCAT("rmi://localhost/VLI");
            try {
                showVector(is.getDictionnary().result);
                msg(is.getDocName(0));
                msg(is.getDocName(1));
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }


    //test("armement OR nucléaire");
    //test("weapon IN[\"SOURCE.EN\" ANDL \"TARGET.RU\"]");
            //      testmore("report IN[\"SOURCE.EN\" ANDL \"TARGET.FR\" ANDL \"COLLECTION.Council_docs¦Letters\"]");
            //test("(weapon AND nuclear)IN[\"SOURCE.EN\" ANDL \"TARGET.RU\"]");
            //test("NEAR(\"weapon\",\"nuclear\")IN[\"SOURCE.EN\" ANDL \"TARGET.RU\"]");
//            testnice("QUOTATION(\"bottom-up\") IN[\"SOURCE.EN\" ANDL \"TARGET.FR\"]");
//     is.indexdir(is.getROOT_CORPUS_TXT());
//     is.showFullIndex();
//            testnice("QUOTATION(\"bottom-up\") IN[\"SOURCE.EN\" ANDL \"TARGET.FR\"]");
            

            
    //        testnice("QUOTATION(\"report\") IN[\"SOURCE.EN\" ANDL \"TARGET.FR\" ANDL \"COLLECTION.Admin\" ORL \"COLLECTION.Council_docs¦Letters\"]");
    //        try {
    //            PropertiesList prop = is.getDictionnary("COLLECTION.");
    //
    //            msg("#prop:" + prop.result.length + " first:" + prop.result[0]);
    //
    //            String[] stopwords = is.getStopWords();
    //            msg("#stop:" + stopwords.length + " first:" + stopwords[1]);
    //
    //            String[] lang = is.getCorpusLanguages();
    //            msg("#lang:" + lang.length + " first:" + lang[0]);
    //
    //            msg("url:" + is.getOriginalUrl("EN/Admin¦2003¦TM_2003_667.txt"));
    //
    //
    //            // test ref
    //            String ex = "In such cases, all previous signatories will have to sign the new document and the old, cloned document must be cancelled. Alternatively, you may contact AIS Support to request the manual re-routing of the document back to the supervisor. However, depending on the case, this whole operation can prove to be somewhat cumbersome.";
    //            UploadedFile up = new UploadedFile(ex, "test.txt");
    //            REFResultNice ref = is.getReferences(up, 6, "EN", "FR", null);
    //            msg("ref:" + ref.htmlref);
    //
    //        } catch (RemoteException ex) {
    //            Logger.getLogger(TestClientQuery.class.getName()).log(Level.SEVERE, null, ex);
    //        }
            
            
           testnice("QUOTATION(\"consecutive days\") IN[\"SOURCE.EN\" ANDL \"TARGET.FR\"]");
           
        try {
            int iddocEN=is.getDocId("Books¦29c6803a32a4a00a_EN");
            msg (" content EN; "+ is.getDoc(iddocEN));
            msg (" SOURCE.EN:"+is.getDocumentPropertie(iddocEN,"SOURCE.EN"));
            msg (" TARGET.FR:"+is.getDocumentPropertie(iddocEN,"TARGET.FR"));
            msg (" content FR; "+ is.getDoc(is.getDocId("Books¦29c6803a32a4a00a_FR")));
            
            String[] setOfLang = is.getCorpusLanguages();  // langues du corpus
            
            inventoryOf(setOfLang);
            
        } catch (RemoteException ex) {
            Logger.getLogger(TestClientQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
           testnice("QUOTATION(\"katavasov\") IN[\"SOURCE.EN\" ANDL \"TARGET.EN\"]");
          
          
//           testnice("QUOTATION(\"for which an established\") IN[\"SOURCE.EN\" ANDL \"TARGET.FR\"]");
//           testnice("QUOTATION(\"for which an established basis\") IN[\"SOURCE.EN\" ANDL \"TARGET.FR\"]");
            
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
    
    static void testnice(String query) {
        try {
            Timer t1 = new Timer("------------- " + query);
            QLResultNice res = is.evalQLNice(query, 0, 1000);
            msg("time:" + res.duration);
            msg("nbres:" + res.result.length);
            for (int i = 0; i < res.result.length; i++) {
                msg(i + "  docid: " + res.result[i]);
                msg("  docname: " + res.docname[i]);
                msg (" content; "+ is.getDoc(res.result[i]));
                msg("");
            }
            t1.stop();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    static void testmore(String query) {
        try {
            Timer t1 = new Timer("------------- " + query);
            QLResultAndRank res = is.evalQLMore(query);
            msg("time:" + res.duration);
            msg("nbres:" + res.result.length);
            for (int i = 0; i < res.result.length; i++) {
                msg(i + "  docid: " + res.result[i]);
                msg("  docname: " + res.docName[i]);
                msg("");
            }
            t1.stop();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
