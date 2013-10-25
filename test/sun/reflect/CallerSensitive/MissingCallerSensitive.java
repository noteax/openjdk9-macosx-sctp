/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8010117
 * @summary Test CallerSensitiveFinder to find missing annotation
 * @compile -XDignore.symbol.file MissingCallerSensitive.java
 * @build CallerSensitiveFinder MethodFinder ClassFileReader
 * @run main/othervm MissingCallerSensitive
 */

import java.io.File;
import java.util.*;
public class MissingCallerSensitive {
    public static void main(String[] args) throws Exception {
        String testclasses = System.getProperty("test.classes", ".");
        List<File> classes = new ArrayList<File>();
        classes.add(new File(testclasses, "MissingCallerSensitive.class"));

        final String method = "sun/reflect/Reflection.getCallerClass";
        CallerSensitiveFinder csfinder = new CallerSensitiveFinder(method);
        List<String> errors = csfinder.run(classes);
        /*
         * Expected 1 method missing @CallerSenitive and 2 methods not in
         * the MethodHandleNatives CS list
         */
        if (errors.size() != 3) {
            throw new RuntimeException("Unexpected number of methods found: " + errors.size());
        }
        int count=0;
        for (String e : errors) {
            if (e.startsWith("MissingCallerSensitive#missingCallerSensitiveAnnotation ")) {
                count++;
            }
        }
        if (count != 2) {
            throw new RuntimeException("Error: expected 1 method missing annotation & missing in the list");
        }
    }

    @sun.reflect.CallerSensitive
    public ClassLoader getCallerLoader() {
        Class<?> c = sun.reflect.Reflection.getCallerClass();
        return c.getClassLoader();
    }

    public ClassLoader missingCallerSensitiveAnnotation() {
        Class<?> c = sun.reflect.Reflection.getCallerClass();
        return c.getClassLoader();
    }
}
