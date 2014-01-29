/*
 * #%L
 * GraphWalker SOAP
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.soap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nils Olsson
 */
public class SoapServiceTest {

    private Endpoint endpoint;

    @Before
    public void setup() {
        endpoint = Endpoint.publish("http://localhost:8080/graphwalker", new BaseSoapService());
        Assert.assertTrue(endpoint.isPublished());
        Assert.assertEquals("http://schemas.xmlsoap.org/wsdl/soap/http", endpoint.getBinding().getBindingID());
    }

    @After
    public void teardown() {
        endpoint.stop();
        Assert.assertFalse(endpoint.isPublished());
    }

    private SoapService getService() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/graphwalker?wsdl");
        String namespaceURI = "http://soap.graphwalker.org/";
        String servicePart = "BaseSoapServiceService";
        String portName = "BaseSoapServicePort";
        QName serviceQN = new QName(namespaceURI, servicePart);
        QName portQN = new QName(namespaceURI, portName);
        return Service.create(url, serviceQN).getPort(portQN, SoapService.class);
    }

    @Test
    public void ping() throws MalformedURLException {
        Assert.assertTrue(getService().ping());
    }
}
