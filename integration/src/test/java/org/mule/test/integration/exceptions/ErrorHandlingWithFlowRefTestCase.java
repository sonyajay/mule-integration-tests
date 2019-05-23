/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.test.AbstractIntegrationTestCase;

public class ErrorHandlingWithFlowRefTestCase extends AbstractIntegrationTestCase {

  private static int executions;

  @Override
  public void doSetUp() throws Exception {
    super.doSetUp();
    executions = 0;
  }

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/exceptions/error-handler-flow-ref.xml";
  }

  @Test
  public void errorHandlerExecutedOnce() throws Exception {
    flowRunner("error-handler-with-flow-ref").run();
    assertThat(executions, is(1));
  }

  public static class TestProcessorCounter implements Processor {

    @Override
    public CoreEvent process(CoreEvent event) throws MuleException {
      executions++;
      return event;
    }

  }
}
