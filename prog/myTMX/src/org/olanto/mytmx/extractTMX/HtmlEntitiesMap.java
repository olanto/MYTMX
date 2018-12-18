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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.olanto.senseos.SenseOS;

/**
 *
 * @author x
 */
public class HtmlEntitiesMap {

    public static HashMap<String, String> entitiesmap = new HashMap<>();

    public static void main(String[] args) {
        init();
        System.out.println("&amp;gt;  -> " + getTranslation("&amp;gt;"));
        System.out.println("size=" + size());
    }

    public static String getTranslation(String lang) {
        //!!! no check
        return entitiesmap.get(lang);
    }

    public static int size() {
        return entitiesmap.size();
    }

    public static void init()   {
        InputStreamReader isr = null;
        try {
            String fileName=SenseOS.getMYCAT_HOME("MYTMX")+"/config/entitieslist.txt";
            isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            System.out.println("Init: file name entities:" + fileName);
            BufferedReader entities = new BufferedReader(isr);
            String w = entities.readLine();
            while (w != null) {
                String[] part = w.split("\t");
                entitiesmap.put("&amp;"+part[0]+";", part[1]);
                w = entities.readLine();
            }
            isr.close();
                   System.out.println("size=" + size());


//            for (String key : entitiesmap.keySet()) {
//                System.out.println(key + ":" + entitiesmap.get(key));
//            }
       
        } catch (IOException ex) {
            Logger.getLogger(HtmlEntitiesMap.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(HtmlEntitiesMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
