/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.tls;

import static org.junit.Assert.assertThat;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;
import static org.hamcrest.Matchers.equalTo;

import org.mule.rules.BouncyCastleProviderCleaner;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.test.AbstractIntegrationTestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import org.junit.Rule;
import org.junit.Test;

import java.security.Security;
import java.util.HashSet;

public class TlsCustomProviderTestCase extends AbstractIntegrationTestCase {

  @Rule
  public DynamicPort portSsl = new DynamicPort("portSsl");

  @Rule
  public BouncyCastleProviderCleaner bouncyCastleProviderCleaner = new BouncyCastleProviderCleaner();

  @Override
  protected void doSetUpBeforeMuleContextCreation() throws Exception {
    ensureBouncyCastleProviders();
  }

  @Override
  protected String getConfigFile() {
    return "tls/tls-custom-provider-config.xml";
  }

  private static void ensureBouncyCastleProviders() {
    Security.insertProviderAt(new BouncyCastleProvider(), 1);
    Security.insertProviderAt(new BouncyCastleJsseProvider(), 2);
  }

  @Test
  public void customTlsGlobalContext() throws Exception {
    final CoreEvent res = flowRunner("testFlowGlobalContextClient")
        .withPayload("data")
        .keepStreamsOpen()
        .run();
    assertThat(res.getMessage(), hasPayload(equalTo("ok")));

  }
}
