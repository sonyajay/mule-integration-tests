/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.source;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import javax.inject.Inject;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.mule.functional.api.component.TestConnectorQueueHandler;
import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.component.location.ConfigurationComponentLocator;
import org.mule.runtime.api.component.location.Location;
import org.mule.runtime.api.source.SchedulerMessageSource;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.test.AbstractIntegrationTestCase;

public class SourceTestCase extends AbstractIntegrationTestCase {

  @Rule
  public DynamicPort httpPort1 = new DynamicPort("httpPort1");

  @Inject
  private ConfigurationComponentLocator componentLocator;

  private TestConnectorQueueHandler queueHandler;

  @Override
  protected String getConfigFile() {
    return "org/mule/test/source/source-config.xml";
  }

  @Before
  public void createQueueHandler() {
    queueHandler = new TestConnectorQueueHandler(registry);
  }

  @Test
  public void simpleTest() {
    Optional<Component> source = componentLocator.find(Location.builder().globalName("simpleFlow").addSourcePart().build());
    SchedulerMessageSource schedulerMessageSource = (SchedulerMessageSource) source.get();
    schedulerMessageSource.trigger();
    assertThat(queueHandler.read("simpleFlowQueue", 10000000).getMessage().getPayload().getValue(), Is.is("hole"));
  }

  @Ignore("Fix copy of component model to make it work")
  @Test
  public void simpleTestReuse() {
    Optional<Component> source = componentLocator.find(Location.builder().globalName("simpleFlowReuse").addSourcePart().build());
    SchedulerMessageSource schedulerMessageSource = (SchedulerMessageSource) source.get();
    schedulerMessageSource.trigger();
    assertThat(queueHandler.read("simpleFlowReuseQueue", RECEIVE_TIMEOUT).getMessage().getPayload().getValue(), Is.is("hole"));
  }

  @Test
  public void flowWithError() {
    Optional<Component> source =
        componentLocator.find(Location.builder().globalName("sourceWithFailureFlow").addSourcePart().build());
    SchedulerMessageSource schedulerMessageSource = (SchedulerMessageSource) source.get();
    schedulerMessageSource.trigger();
    assertThat(queueHandler.read("sourceWithFailureFlowQueue", 100), IsNull.nullValue());
  }

  @Test
  public void flowWithErrorAndTry() {
    Optional<Component> source =
        componentLocator.find(Location.builder().globalName("sourceWithFailureFlowAndTry").addSourcePart().build());
    SchedulerMessageSource schedulerMessageSource = (SchedulerMessageSource) source.get();
    schedulerMessageSource.trigger();
    assertThat(queueHandler.read("sourceWithFailureFlowAndTryQueue", RECEIVE_TIMEOUT).getMessage().getPayload().getValue(),
               Is.is("hole"));
  }

  @Test
  public void flowWithErrorAndTryNotHandled() {
    Optional<Component> source =
        componentLocator.find(Location.builder().globalName("sourceWithFailureFlowAndTryNotHandled").addSourcePart().build());
    SchedulerMessageSource schedulerMessageSource = (SchedulerMessageSource) source.get();
    schedulerMessageSource.trigger();
    assertThat(queueHandler.read("sourceWithFailureFlowAndTryNotHandledQueue", 100), IsNull.nullValue());
  }

  @Test
  public void flowWithHttpListener() throws Exception {
    CoreEvent result = flowRunner("httpClientFlow").withVariable("port", httpPort1.getNumber()).run();
    assertThat(result.getMessage().getPayload().getValue(), Is.is("hole"));
  }
}
