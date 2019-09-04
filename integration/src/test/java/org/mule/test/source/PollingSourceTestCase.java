/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.test.AbstractIntegrationTestCase;
import org.mule.test.infrastructure.client.ftp.FTPTestClient;
import org.mule.test.infrastructure.process.rules.FtpServer;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

public class PollingSourceTestCase extends AbstractIntegrationTestCase {

  private static final String DEFAULT_FTP_HOST = "localhost";
  private static final String FTP_SERVER_BASE_DIR = "target/ftpserver";
  private static final String WORKING_DIR_SYSTEM_PROPERTY = "workingDir";
  private static final String WORKING_DIR = "base";
  protected static final File BASE_DIR = new File(FTP_SERVER_BASE_DIR, WORKING_DIR);
  private static final String FTP_USER = "anonymous";
  private static final String FTP_PASSWORD = "password";

  private FTPTestClient ftpClient;

  @Rule
  public SystemProperty workingDirSystemProperty = new SystemProperty(WORKING_DIR_SYSTEM_PROPERTY, WORKING_DIR);

  @Rule
  public FtpServer ftpServer = new FtpServer("ftpPort", BASE_DIR);

  @Override
  protected String getConfigFile() {
    return "org/mule/source/polling-source-test-case.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    ftpServer.start();
    ftpClient = new FTPTestClient(DEFAULT_FTP_HOST, ftpServer.getPort(), FTP_USER, FTP_PASSWORD);

    if (!ftpClient.testConnection()) {
      throw new IOException("could not connect to ftp server");
    }
    ftpClient.changeWorkingDirectory(WORKING_DIR);
  }

  @Test
  public void pollingSourceListensFileMultipleTimes() throws Exception {
    flowRunner("write-file").run();
    ((Flow) getFlowConstruct("listen-file")).start();
    flowRunner("checkTimesListened").run();
  }

  @Override
  protected void doTearDown() throws Exception {
    super.doTearDownAfterMuleContextDispose();

    if (ftpClient != null) {
      try {
        if (ftpClient.isConnected()) {
          ftpClient.disconnect();
        }
      } finally {
        ftpServer.stop();
      }
    }
  }
}
