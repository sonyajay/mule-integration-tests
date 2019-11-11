/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import static org.mule.runtime.core.api.config.MuleProperties.MULE_FLOW_TRACE;
import static org.mule.tck.junit4.rule.VerboseExceptions.setVerboseExceptions;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.VerboseExceptions;
import org.mule.test.AbstractIntegrationTestCase;

public class SubTypesModuleTestCase extends AbstractIntegrationTestCase {

  // Just to ensure the previous value is set after the test
  @ClassRule
  public static VerboseExceptions verboseExceptions = new VerboseExceptions(false);

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/exceptions/subtypes-module-config.xml";
  }

  @Rule
  public SystemProperty logFlowStack = new SystemProperty(MULE_FLOW_TRACE, Boolean.toString(false));

  @Test
  public void run() throws Exception {
    runSuccesses(true, "testFlow");
  }

  //@Test
  //public void run2() throws Exception {
  //  runSuccesses(true, "testFlow2");
  //}

  private void runSuccesses(boolean verboseExceptions, String flowName) throws Exception {
    setVerboseExceptions(verboseExceptions);
    flowRunner(flowName).run();
  }

}
