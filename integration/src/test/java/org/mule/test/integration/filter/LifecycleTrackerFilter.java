/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.filter;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.api.routing.filter.Filter;

import java.util.ArrayList;
import java.util.List;

public class LifecycleTrackerFilter implements Filter, Lifecycle, MuleContextAware
{

    private final List<String> tracker = new ArrayList<String>();

    public List<String> getTracker()
    {
        return tracker;
    }

    public void setProperty(final String value)
    {
        tracker.add("setProperty");
    }

    public void setMuleContext(final MuleContext context)
    {
        tracker.add("setMuleContext");
    }


    public void initialise() throws InitialisationException
    {
        tracker.add("initialise");
    }

    public void start() throws MuleException
    {
        tracker.add("start");
    }

    public void stop() throws MuleException
    {
        tracker.add("stop");
    }

    public void dispose()
    {
        tracker.add("dispose");
    }

    public boolean accept(MuleMessage message)
    {
        // TODO Auto-generated method stub
        return false;
    }

}

