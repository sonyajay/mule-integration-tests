/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.tooling;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.rules.ExpectedException.none;
import static org.mule.runtime.module.tooling.TestExtensionDeclarationUtils.configurationDeclaration;
import static org.mule.runtime.module.tooling.TestExtensionDeclarationUtils.connectionDeclaration;

import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.app.declaration.api.fluent.ArtifactDeclarer;
import org.mule.runtime.deployment.model.api.DeploymentInitException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DslConnectionElementDeclarationTestingTestCase extends DeclarationSessionTestCase {

  private static final String CONFIG_CONNECTION_MISSING_REQUIRED_PARAM_NAME = "CONFIG_CONNECTION_MISSING_REQUIRED_PARAM_NAME";

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected void declareArtifact(ArtifactDeclarer artifactDeclarer) {
    super.declareArtifact(artifactDeclarer);
    artifactDeclarer.withGlobalElement(configurationDeclaration(CONFIG_CONNECTION_MISSING_REQUIRED_PARAM_NAME,
                                                                connectionDeclaration(CLIENT_NAME, CLIENT_NAME, null)));
  }

  @Test
  public void testConnectionMissingRequiredParameterOnConnectionProvider() {
    expectedException.expect(MuleRuntimeException.class);
    expectedException.expectCause(instanceOf(DeploymentInitException.class));
    expectedException.expectMessage("Parameter 'acting-parameter' is required but was not found");
    session.testConnection(CONFIG_CONNECTION_MISSING_REQUIRED_PARAM_NAME);
  }

}
