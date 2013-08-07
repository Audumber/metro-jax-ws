/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;

/**
 * This class determines an instance of {@link Container} for the runtime.
 * It applies for both server and client runtimes(for e.g in Servlet could
 * be accessing a Web Service). Always call {@link #setInstance} when the
 * application's environment is initailized and a Container instance should
 * be associated with an application.
 *
 * A client that is invoking a web service may be running in a
 * container(for e.g servlet). T
 *
 * <p>
 * ContainerResolver uses a static field to keep the instance of the resolver object.
 * Typically appserver may set its custom container resolver using the static method
 * {@link #setInstance(ContainerResolver)}
 *
 * @author Jitendra Kotamraju
 */
public abstract class ContainerResolver {

    private static final ContainerResolver NONE = new ContainerResolver() {
        public Container getContainer() {
            return Container.NONE;
        }
    };

    private static volatile ContainerResolver theResolver = NONE;

    /**
     * Sets the custom container resolver which can be used to get client's
     * {@link Container}.
     *
     * @param resolver container resolver
     */
    public static void setInstance(ContainerResolver resolver) {
        if(resolver==null)
            resolver = NONE;
        theResolver = resolver;
    }

    /**
     * Returns the container resolver which can be used to get client's {@link Container}.
     *
     * @return container resolver instance
     */
    public static @NotNull ContainerResolver getInstance() {
        return theResolver;
    }

    /**
     * Returns the default container resolver which can be used to get {@link Container}.
     *
     * @return default container resolver
     */
    public static ContainerResolver getDefault() {
        return NONE;
    }

    /**
     * Returns the {@link Container} context in which client is running.
     *
     * @return container instance for the client
     */
    public abstract @NotNull Container getContainer();

}