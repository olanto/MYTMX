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

/**
 *
 * @author simple
 */
public class Hascode64 {

    public static void main(String[] args) {

        String w = "this is a String";
        System.out.println(w + " = " + h64(w));
        w = "CHAPTER 1";
        System.out.println(w + " = " + h64(w));
       System.out.println(w + " = " + s64(w));
        w = "1";
        System.out.println(w + " = " + h64(w));
       System.out.println(w + " = " + s64(w));
        w = "2";
        System.out.println(w + " = " + h64(w));
       System.out.println(w + " = " + s64(w));

    }

    public static final String s64(String s) {
        String resHex=Long.toHexString(h64(s));
        resHex=("0000000000000000" + resHex).substring(resHex.length());
        return resHex;
    }

    public static final long h64(String s) {
        int hdoc = s.hashCode();
        String s1 = s;
        if (s.length() > 1) {
            s1 = s.substring(1) + s.substring(0, 1);
        }
        int clue = s1.hashCode();
        return (long) ((long) hdoc << 32) | (((long) (clue) << 32) >>> 32); // haute + basse
    }
}
