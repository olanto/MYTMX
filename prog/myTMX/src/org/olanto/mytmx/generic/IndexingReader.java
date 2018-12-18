/**
 * ********
 * Copyright © 2010-2014 Olanto Foundation Geneva
 *
 * This file is part of myCAT.
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
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.mytmx.generic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import org.olanto.idxvli.*;
import org.olanto.util.Timer;

/**
 * index le corpus (sans mode serveur)
 */
public class IndexingReader {      // is an application, not an applet !

    static IdxStructure id;
    static Timer t1 = new Timer("global time");
    
        public static void indexThisDir(IdxStructure id, String path, int limit, String txt_encoding) {
        File f = new File(path);
        System.out.println("path:"+f);
        if (f.isFile()) {
            
            if (path.endsWith(".mono")) {
                indexThis(id,f.getAbsolutePath(),limit,txt_encoding);
            }
        } else {
            String[] lf = f.list();
            int ilf = Array.getLength(lf);
            for (int i = 0; i < ilf; i++) {
                indexThisDir(id,path + "/" + lf[i],limit,txt_encoding);
            }
        }
    }

    public static void indexThis(IdxStructure id, String fileso, int limit, String txt_encoding) {
        System.out.println("------------- index corpus: " + fileso );
        int totread = 0;
        try {
            InputStreamReader isrso = new InputStreamReader(new FileInputStream(fileso), txt_encoding);
            BufferedReader so = new BufferedReader(isrso);
            String wso = so.readLine();
            while (wso != null && totread < limit) {
                String[] part = wso.split("\t");
                        totread++;
                        String docnameso = part[0];
                        if (totread % 10000 == 0) {
                            System.out.println(totread+" - "+id.lastRecordedDoc);
                         }
                        id.indexThisContent(part[0], part[1]);
                        
                wso = so.readLine();
            }
            so.close();
            System.out.println("   read entries: " + totread);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
