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

import org.olanto.senseos.SenseOS;
import java.rmi.*;
import org.olanto.idxvli.server.*;
import static org.olanto.util.Messages.*;

/** Test
-server  -Xmx8000m -Djava.rmi.server.codebase="file:///c:/MTMX/prog/myCAT/dist/myTMX.jar"  -Djava.security.policy="c:/MYTMX/rmi.policy"
 */
public class TestClientServices {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://localhost/VLI");

            System.out.println("access to serveur");

            if (r instanceof IndexService_MyCat) {
                IndexService_MyCat is = ((IndexService_MyCat) r);
                String s = is.getInformation();

                System.out.println("chaîne renvoyée = " + s);
                System.out.println("corpus source = " + is.getROOT_CORPUS_SOURCE());
                System.out.println("corpus txt = " + is.getROOT_CORPUS_TXT());

                System.out.println("docname = " + is.getDocName(100));
 
 



                msg("end ...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
