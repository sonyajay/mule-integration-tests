/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.mule.functional.api.flow.FlowRunner;
import org.mule.runtime.core.api.exception.MessagingException;
import org.mule.test.AbstractIntegrationTestCase;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class ExceptionMappingTestCase extends AbstractIntegrationTestCase {

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/exceptions/exception-mapping-config.xml";
  }

  @Test
  public void transformationError() throws Exception {
    MessagingException messagingException = new FlowRunner(registry, "transformationErrorFlow")
        .withPayload(new InputStream() {

          @Override
          public int read() throws IOException {
            throw new IOException();
          }
        }).runExpectingException();
    assertThat(messagingException.getEvent().getError().get().getErrorType().getIdentifier(), is("TRANSFORMATION"));
  }

  @Test
  public void expressionError() throws Exception {
    MessagingException messagingException = new FlowRunner(registry, "expressionErrorFlow").runExpectingException();
    assertThat(messagingException.getEvent().getError().get().getErrorType().getIdentifier(), is("EXPRESSION"));
  }

}
