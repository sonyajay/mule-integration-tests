/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.lifecycle;

import static java.util.Arrays.asList;
import static org.mule.runtime.config.api.LazyComponentInitializer.LAZY_COMPONENT_INITIALIZER_SERVICE_KEY;
import static org.mule.runtime.config.api.SpringXmlConfigurationBuilderFactory.createConfigurationBuilder;
import static org.mule.runtime.core.api.config.MuleDeploymentProperties.MULE_LAZY_INIT_DEPLOYMENT_PROPERTY;
import org.mule.runtime.config.api.LazyComponentInitializer;
import org.mule.runtime.core.api.config.ConfigurationBuilder;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.test.AbstractIntegrationTestCase;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.qameta.allure.Issue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;

@Issue("MULE-14827")
@RunnerDelegateTo(Parameterized.class)
public class SmartConnectorUsesAppConfigTestCase extends AbstractIntegrationTestCase {

  @Inject
  @Named(value = LAZY_COMPONENT_INITIALIZER_SERVICE_KEY)
  private LazyComponentInitializer lazyComponentInitializer;

  @Rule
  public DynamicPort listenPort = new DynamicPort("port");

  @Rule
  public SystemProperty path = new SystemProperty("path", "path");

  @Parameterized.Parameter(0)
  public String type;

  @Parameterized.Parameter(1)
  public boolean lazyInit;

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
            {"NOT LAZY INIT", false},
            {"LAZY INIT", true}
    });
  }

  @Override
  public boolean enableLazyInit() {
    return lazyInit;
  }

  @Override
  protected ConfigurationBuilder getBuilder() throws Exception {
    if(lazyInit) {
      final ConfigurationBuilder configurationBuilder = createConfigurationBuilder(getConfigFiles(), true);
      configureSpringXmlConfigurationBuilder(configurationBuilder);
      return configurationBuilder;
    }
    return super.getBuilder();
  }


  @Override
  protected String[] getConfigFiles() {
    return new String[] {"org/mule/test/integration/lifecycle/smart-connector-uses-app-config.xml"};
  }

  @Test
  public void request() throws Exception {
    final String storeAuthCodeFlow = "store-auth-code";
    final String scConfig = "scConfig";
    final String listenerConfig = "listenerConfig";
    final String requestFlow = "request";
    final String oauthTokenListenerFlow = "oauth-token";

    List<String> expectedLocations = asList(listenerConfig, scConfig, storeAuthCodeFlow, requestFlow, oauthTokenListenerFlow);

    if(lazyInit) {
      lazyComponentInitializer.initializeComponents(l -> expectedLocations.contains(l.getLocation()));
    }

    // Do the OAuth dance
    flowRunner(storeAuthCodeFlow).run();

    // Use the SC to do a request authenticated with OAuth
    flowRunner(requestFlow).run();

  }
}
