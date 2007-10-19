/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.client;

import org.mule.MuleException;
import org.mule.config.ConfigurationBuilder;
import org.mule.extras.client.MuleClient;
import org.mule.extras.client.RemoteDispatcher;
import org.mule.impl.endpoint.EndpointURIEndpointBuilder;
import org.mule.impl.internal.admin.MuleAdminAgent;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.transformers.simple.ByteArrayToString;
import org.mule.umo.UMOExceptionPayload;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.MalformedEndpointException;
import org.mule.umo.endpoint.UMOEndpointBuilder;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.transformer.TransformerException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RemoteExceptionTestCase extends FunctionalTestCase
{

    protected String getConfigResources()
    {
        return "";
    }

    protected ConfigurationBuilder getBuilder() throws Exception
    {
        QuickConfigurationBuilder builder = new QuickConfigurationBuilder();
        // Test component 1. Will be used to create a transformer exceotion
        UMOEndpointBuilder endpointBuilder = new EndpointURIEndpointBuilder("vm://test.queue.1", managementContext);
        endpointBuilder.addTransformer(new ByteArrayToString())
        UMOImmutableEndpoint ep = managementContext.getRegistry().lookupEndpointFactory().getInboundEndpoint(
            endpointBuilder, managementContext);

        builder.registerComponent(FunctionalTestComponent.class.getName(), "testComponent1", ep, null, null);

        // Test component 2. Will be used to create an exception thrown from within
        // the component
        UMOImmutableEndpoint ep2 = managementContext.getRegistry().lookupEndpointFactory().getInboundEndpoint(
            "vm://test.queue.2", managementContext);
        Map props = new HashMap();
        props.put("throwException", "true");
        builder.registerComponent(FunctionalTestComponent.class.getName(), "testComponent2", ep2, null, props);

        MuleAdminAgent agent = new MuleAdminAgent();
        agent.setServerUri("http://localhost:5555");
        builder.getManagementContext().getRegistry().registerAgent(agent);
        return builder;
    }

    public void testClientTransformerException() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("http://localhost:5555");
        UMOMessage result = dispatcher.sendRemote("vm://test.queue.1", new Date(), null);
        assertNotNull(result);
        UMOExceptionPayload exceptionPayload = result.getExceptionPayload();
        assertNotNull(exceptionPayload);
        assertTrue(exceptionPayload.getRootException() instanceof TransformerException);
    }

    public void testClientMalformedEndpointException() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("http://localhost:5555");
        UMOMessage result = dispatcher.sendRemote("test.queue.2", new Date(), null);
        assertNotNull(result);
        UMOExceptionPayload exceptionPayload = result.getExceptionPayload();
        assertNotNull(exceptionPayload);
        assertTrue(exceptionPayload.getRootException() instanceof MalformedEndpointException);
    }

    public void testClientComponentException() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("http://localhost:5555");
        UMOMessage result = dispatcher.sendRemote("vm://test.queue.2", new Date(), null);
        assertNotNull(result);
        UMOExceptionPayload exceptionPayload = result.getExceptionPayload();
        assertNotNull(exceptionPayload);
        assertTrue(exceptionPayload.getRootException() instanceof MuleException);
        assertEquals("Functional Test Component Exception", exceptionPayload.getRootException().getMessage());
    }

}
