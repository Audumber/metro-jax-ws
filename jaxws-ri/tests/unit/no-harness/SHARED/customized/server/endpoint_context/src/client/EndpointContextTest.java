/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package server.endpoint_context.client;

import junit.framework.TestCase;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import testutil.PortAllocator;


/**
 * Tests Endpoint.setEndpointContext()
 *
 * @author Jitendra Kotamraju
 */
public class EndpointContextTest extends TestCase {

    public void testEndpointContext() throws Exception {
        int port = PortAllocator.getFreePort();

        String address1 = "http://localhost:"+port+"/foo";
        Endpoint endpoint1 = getEndpoint(new FooService());

        String address2 = "http://localhost:"+port+"/bar";
        Endpoint endpoint2 = getEndpoint(new BarService());

        EndpointContext ctxt = new MyEndpointContext(endpoint1, endpoint2);
        endpoint1.setEndpointContext(ctxt);
        endpoint2.setEndpointContext(ctxt);

        endpoint1.publish(address1);
        endpoint2.publish(address2);

        URL pubUrl = new URL(address1+"?wsdl");
        boolean patched = isPatched(pubUrl.openStream(), address1, address2);
        assertTrue(patched);

        pubUrl = new URL(address2+"?wsdl");
        patched = isPatched(pubUrl.openStream(), address1, address2);
        assertTrue(patched);

        endpoint1.stop();
        endpoint2.stop();
    }

    private Endpoint getEndpoint(Object impl) throws IOException {
        Endpoint endpoint = Endpoint.create(impl);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        File[] docs = {
                resource("EchoService.wsdl")
        };
        List<Source> metadata = new ArrayList<Source>();
        for(File doc : docs) {
            URL url = doc.toURL();
            metadata.add(new StreamSource(url.openStream(), url.toExternalForm()));
        }
        endpoint.setMetadata(metadata);
        return endpoint;
    }

    public static java.io.File resource(String path) {
        URL resource = Util.class.getResource("/" + path);
        if (resource != null) {
            try {
                return new File(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            String pwd = System.getenv("PWD") + "/../src/resources/";
            File f =  new File(pwd + path);
            if (f.exists()) {
                return f;
            } else {
                throw new RuntimeException("No resource found path: " + f.getAbsolutePath());
            }
        }
    }



    public boolean isPatched(InputStream in, String address1, String address2)
            throws IOException {
        boolean address1Patched = false;
        boolean address2Patched = false;

        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        String str;
        while ((str=rdr.readLine()) != null) {
            //System.out.println(str);
            if (str.indexOf(address1) != -1) {
                address1Patched = true;
            }
            if (str.indexOf(address2) != -1) {
                address2Patched = true;
            }
        }
        return address1Patched && address2Patched;
    }

    @WebServiceProvider(serviceName="EchoService", portName="fooPort",
            targetNamespace="http://echo.org/")
    public class FooService implements Provider<Source> {
        public Source invoke(Source source) {
            throw new WebServiceException("Not testing the invocation");
        }
    }

    @WebServiceProvider(serviceName="EchoService", portName="barPort",
            targetNamespace="http://echo.org/")
    public class BarService implements Provider<Source> {
        public Source invoke(Source source) {
            throw new WebServiceException("Not testing the invocation");
        }
    }

    private static class MyEndpointContext extends EndpointContext {
        final Set<Endpoint> set = new HashSet<Endpoint>();

        public MyEndpointContext(Endpoint endpoint1, Endpoint endpoint2) {
            set.add(endpoint1);
            set.add(endpoint2);
        }

        public Set<Endpoint> getEndpoints() {
            return set;
        }
    }

}

