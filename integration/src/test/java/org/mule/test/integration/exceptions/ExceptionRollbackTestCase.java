/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.DefaultMuleException;
import org.mule.api.transaction.Transaction;
import org.mule.config.i18n.CoreMessages;
import org.mule.exception.DefaultSystemExceptionStrategy;
import org.mule.routing.filters.WildcardFilter;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.testmodels.mule.TestTransaction;
import org.mule.transaction.TransactionCoordination;

import java.io.FileNotFoundException;

public class ExceptionRollbackTestCase extends AbstractMuleTestCase
{
    private DefaultSystemExceptionStrategy strategy;
    private Transaction tx;

    @Override
    protected void doSetUp() throws Exception
    {
        strategy = new DefaultSystemExceptionStrategy(muleContext);
        strategy.setCommitTxFilter(new WildcardFilter("java.io.*"));
        strategy.setRollbackTxFilter(new WildcardFilter("org.mule.*, javax.*"));

        initialiseObject(strategy);
        tx = new TestTransaction(muleContext);
        TransactionCoordination.getInstance().bindTransaction(tx);
    }

    @Override
    protected void doTearDown() throws Exception
    {
        TransactionCoordination.getInstance().unbindTransaction(tx);
    }

    public void testCommit() throws Exception
    {
        strategy.handleException(new FileNotFoundException());
        assertFalse(tx.isRollbackOnly());
        //There is nothing to actually commit the transaction since we are not running in a real tx
        //assertTrue(tx.isCommitted());
    }

    public void testRollback() throws Exception
    {
        strategy.handleException(new DefaultMuleException(CoreMessages.agentsRunning()));
        assertTrue(tx.isRollbackOnly());
        //There is nothing to actually commit the transaction since we are not running in a real tx
        assertFalse(tx.isCommitted());
    }

    public void testRollbackByDefault() throws Exception
    {
        strategy.handleException(new IllegalAccessException());
        assertTrue(tx.isRollbackOnly());
        //There is nothing to actually commit the transaction since we are not running in a real tx
        assertFalse(tx.isCommitted());
    }
}
