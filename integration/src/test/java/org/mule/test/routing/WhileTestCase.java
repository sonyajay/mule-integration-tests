/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.routing;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.rules.ExpectedException.none;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.expression.ExpressionRuntimeException;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.test.AbstractIntegrationTestCase;


public class WhileTestCase extends AbstractIntegrationTestCase {

  @Rule
  public ExpectedError expected = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "while-config.xml";
  }

  @Test
  public void noExecution() throws Exception {
    flowRunner("basicNoExecution").run();
  }

  @Test
  public void oneExecution() throws Exception {
    flowRunner("basicOneExecution").run();
  }

  private int factorial(int n) {
    if (n == 0) return 1;
    return n * factorial(n - 1);
  }

  @Test
  public void manyExecutions() throws Exception {
    CoreEvent event = flowRunner("manyExecutions").withPayload(10).run();
    assertThat(event.getMessage().getPayload().getValue(), is(factorial(10)));
  }

  @Test
  public void volumeExecutions() throws Exception {
    flowRunner("volumeExecutions").run();
  }

  @Test
  public void errorExecution() throws Exception {
    expected.expectErrorType("APP", "SOME");
    flowRunner("whileWithError").run();
  }

  @Test
  public void errorExecutionWithErrorHandler() throws Exception {
    CoreEvent event = flowRunner("whileWithErrorHandler").run();
    assertThat(event.getMessage().getPayload().getValue(), is("OK"));
  }

  @Test
  public void errorExecutionWithErrorHandlerFlowRefFlow() throws Exception {
    CoreEvent event = flowRunner("whileWithErrorHandlerAndFlowRef").withPayload("errorFlow").run();
    assertThat(event.getMessage().getPayload().getValue(), is("OK"));
  }

  @Test
  public void errorExecutionWithErrorHandlerFlowRefSubFlow() throws Exception {
    CoreEvent event = flowRunner("whileWithErrorHandlerAndFlowRef").withPayload("errorSubFlow").run();
    assertThat(event.getMessage().getPayload().getValue(), is("OK"));
  }

  @Test
  public void errorExecutionWithErrorHandlerFlowRefFlowWithErrorWhile() throws Exception {
    CoreEvent event = flowRunner("whileWithErrorHandlerAndFlowRef").withPayload("whileWithError").run();
    assertThat(event.getMessage().getPayload().getValue(), is("OK"));
  }

  @Test
  public void errorExecutionWithInnerErrorHandler() throws Exception {
    CoreEvent event = flowRunner("whileWithInnerErrorHandler").run();
    assertThat(event.getMessage().getPayload().getValue(), is(false));
    assertThat(event.getVariables().get("count").getValue(), is(1));
  }

  @Test
  public void wrongExpression() throws Exception {
    expected.expectCause(instanceOf(ExpressionRuntimeException.class));
    expected.expectErrorType("MULE", "EXPRESSION");
    flowRunner("wrongExpression").run();
  }

  @Test
  @Ignore
  public void infiniteLoop() throws Exception {
    expected.expectCause(instanceOf(ExpressionRuntimeException.class));
    expected.expectErrorType("MULE", "EXPRESSION");
    flowRunner("infiniteLoop").run();
  }

  @Test
  public void nestedWhiles() throws Exception {
    CoreEvent event = flowRunner("nestedWhiles").run();
    assertThat(event.getMessage().getPayload().getValue(), is(30));
  }

  @Test
  public void whileWithInnerForEach() throws Exception {
    CoreEvent event = flowRunner("whileWithInnerForEach").run();
    assertThat(event.getMessage().getPayload().getValue(), is(60));
  }

  @Test
  public void variableScope() throws Exception {
    flowRunner("variableScope").run();
  }

  @Test
  public void variableScopeWithErrorScenario() throws Exception {
    flowRunner("variableScopeWithErrorScenario").run();
  }

}
