package org.apache.jcs.auxiliary.remote.server;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.jcs.auxiliary.MockCacheEventLogger;
import org.apache.jcs.auxiliary.remote.MockRemoteCacheListener;
import org.apache.jcs.auxiliary.remote.behavior.IRemoteCacheAttributes;
import org.apache.jcs.auxiliary.remote.server.behavior.IRemoteCacheServerAttributes;
import org.apache.jcs.engine.CacheElement;
import org.apache.jcs.engine.behavior.ICacheElement;
import org.apache.jcs.utils.timing.SleepUtil;

/**
 * Since the server does not know that it is a server, it is easy to unit test. The factory does all
 * the rmi work.
 * <p>
 * @author Aaron Smuts
 */
public class RemoteCacheServerUnitTest
    extends TestCase
{
    /**
     * Add a listener. Pass the id of 0, verify that the server sets a new listener id. Do another
     * and verify that the second gets an id of 2.
     * <p>
     * @throws Exception
     */
    public void testAddListenerToCache_LOCALtype()
        throws Exception
    {
        // SETUP
        String expectedIp1 = "adfasdf";
        String expectedIp2 = "adsfadsafaf";

        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockRemoteCacheListener mockListener1 = new MockRemoteCacheListener();
        mockListener1.remoteType = IRemoteCacheAttributes.LOCAL;
        mockListener1.localAddress = expectedIp1;
        MockRemoteCacheListener mockListener2 = new MockRemoteCacheListener();
        mockListener1.remoteType = IRemoteCacheAttributes.LOCAL;
        mockListener2.localAddress = expectedIp2;

        String cacheName = "testAddListener";

        // DO WORK
        server.addCacheListener( cacheName, mockListener1 );
        server.addCacheListener( cacheName, mockListener2 );

        // VERIFY
        assertEquals( "Wrong listener id.", 1, mockListener1.getListenerId() );
        assertEquals( "Wrong listener id.", 2, mockListener2.getListenerId() );
        assertEquals( "Wrong ip.", expectedIp1, server.getExtraInfoForRequesterId( 1 ) );
        assertEquals( "Wrong ip.", expectedIp2, server.getExtraInfoForRequesterId( 2 ) );
    }

    /**
     * Add a listener. Pass the id of 0, verify that the server sets a new listener id. Do another
     * and verify that the second gets an id of 2.
     * <p>
     * @throws Exception
     */
    public void testAddListenerToCache_CLUSTERtype()
        throws Exception
    {
        // SETUP
        String expectedIp1 = "adfasdf";
        String expectedIp2 = "adsfadsafaf";

        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockRemoteCacheListener mockListener1 = new MockRemoteCacheListener();
        mockListener1.remoteType = IRemoteCacheAttributes.CLUSTER;
        mockListener1.localAddress = expectedIp1;
        MockRemoteCacheListener mockListener2 = new MockRemoteCacheListener();
        mockListener1.remoteType = IRemoteCacheAttributes.CLUSTER;
        mockListener2.localAddress = expectedIp2;

        String cacheName = "testAddListener";

        // DO WORK
        server.addCacheListener( cacheName, mockListener1 );
        server.addCacheListener( cacheName, mockListener2 );

        // VERIFY
        assertEquals( "Wrong listener id.", 1, mockListener1.getListenerId() );
        assertEquals( "Wrong listener id.", 2, mockListener2.getListenerId() );
        assertEquals( "Wrong ip.", expectedIp1, server.getExtraInfoForRequesterId( 1 ) );
        assertEquals( "Wrong ip.", expectedIp2, server.getExtraInfoForRequesterId( 2 ) );
    }
    
    /**
     * Add a listener. Pass the id of 0, verify that the server sets a new listener id. Do another
     * and verify that the second gets an id of 2.
     * <p>
     * @throws Exception
     */
    public void testAddListener_ToAll()
        throws Exception
    {
        // SETUP
        String expectedIp1 = "adfasdf";
        String expectedIp2 = "adsfadsafaf";
        
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockRemoteCacheListener mockListener1 = new MockRemoteCacheListener();
        mockListener1.localAddress = expectedIp1;
        MockRemoteCacheListener mockListener2 = new MockRemoteCacheListener();
        mockListener2.localAddress = expectedIp2;

        // DO WORK
        // don't specify the cache name
        server.addCacheListener( mockListener1 );
        server.addCacheListener( mockListener2 );

        // VERIFY
        assertEquals( "Wrong listener id.", 1, mockListener1.getListenerId() );
        assertEquals( "Wrong listener id.", 2, mockListener2.getListenerId() );
        assertEquals( "Wrong ip.", expectedIp1, server.getExtraInfoForRequesterId( 1 ) );
        assertEquals( "Wrong ip.", expectedIp2, server.getExtraInfoForRequesterId( 2 ) );        
    }

    /**
     * Add a listener. Pass the id of 0, verify that the server sets a new listener id. Do another
     * and verify that the second gets an id of 2. Call remove Listener and verify that it is
     * removed.
     * <p>
     * @throws Exception
     */
    public void testAddListener_ToAllThenRemove()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockRemoteCacheListener mockListener1 = new MockRemoteCacheListener();
        MockRemoteCacheListener mockListener2 = new MockRemoteCacheListener();

        String cacheName = "testAddListenerToAllThenRemove";

        // DO WORK
        server.addCacheListener( cacheName, mockListener1 );
        server.addCacheListener( cacheName, mockListener2 );

        // VERIFY
        assertEquals( "Wrong number of listeners.", 2, server.getCacheListeners( cacheName ).eventQMap.size() );
        assertEquals( "Wrong listener id.", 1, mockListener1.getListenerId() );
        assertEquals( "Wrong listener id.", 2, mockListener2.getListenerId() );

        // DO WORK
        server.removeCacheListener( cacheName, mockListener1.getListenerId() );
        assertEquals( "Wrong number of listeners.", 1, server.getCacheListeners( cacheName ).eventQMap.size() );
    }

    /**
     * Add a listener. Pass the id of 0, verify that the server sets a new listener id. Do another
     * and verify that the second gets an id of 2. Call remove Listener and verify that it is
     * removed.
     * <p>
     * @throws Exception
     */
    public void testAddListener_ToAllThenRemove_clusterType()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockRemoteCacheListener mockListener1 = new MockRemoteCacheListener();
        mockListener1.remoteType = IRemoteCacheServerAttributes.CLUSTER;
        MockRemoteCacheListener mockListener2 = new MockRemoteCacheListener();
        mockListener2.remoteType = IRemoteCacheServerAttributes.CLUSTER;

        String cacheName = "testAddListenerToAllThenRemove";

        // DO WORK
        server.addCacheListener( cacheName, mockListener1 );
        server.addCacheListener( cacheName, mockListener2 );

        // VERIFY
        assertEquals( "Wrong number of listeners.", 0, server.getCacheListeners( cacheName ).eventQMap.size() );
        assertEquals( "Wrong number of listeners.", 2, server.getClusterListeners( cacheName ).eventQMap.size() );
        assertEquals( "Wrong listener id.", 1, mockListener1.getListenerId() );
        assertEquals( "Wrong listener id.", 2, mockListener2.getListenerId() );

        // DO WORK
        server.removeCacheListener( cacheName, mockListener1.getListenerId() );
        assertEquals( "Wrong number of listeners.", 1, server.getClusterListeners( cacheName ).eventQMap.size() );
        assertNull( "Should be no entry in the ip map.", server.getExtraInfoForRequesterId( 1 ) );
    }

    /**
     * Register a listener and then verify that it is called when we put using a different listener
     * id.
     * @throws Exception
     */
    public void testSimpleRegisterListenerAndPut()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );

        MockRemoteCacheListener mockListener = new MockRemoteCacheListener();
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        String cacheName = "testSimpleRegisterListenerAndPut";
        server.addCacheListener( cacheName, mockListener );

        // DO WORK
        List inputItems = new LinkedList();
        int numToPut = 10;

        for ( int i = 0; i < numToPut; i++ )
        {
            ICacheElement element = new CacheElement( cacheName, String.valueOf( i ), Long.valueOf( i ) );
            inputItems.add( element );
            server.update( element, 9999 );
        }

        Thread.sleep( 100 );
        Thread.yield();
        Thread.sleep( 100 );

        // VERIFY
        assertEquals( "Wrong number of items put to listener.", numToPut, mockListener.putItems.size() );
        for ( int i = 0; i < numToPut; i++ )
        {
            assertEquals( "Wrong item.", inputItems.get( i ), mockListener.putItems.get( i ) );
        }
    }

    /**
     * Register a listener and then verify that it is called when we put using a different listener
     * id. The updates should come from a cluster listener and local cluster consistency should be
     * true.
     * <p>
     * @throws Exception
     */
    public void testSimpleRegisterListenerAndPut_FromClusterWithLCC()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setLocalClusterConsistency( true );
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        // this is to get the listener id for inserts.
        MockRemoteCacheListener clusterListener = new MockRemoteCacheListener();
        clusterListener.remoteType = IRemoteCacheAttributes.CLUSTER;

        // this should get the updates
        MockRemoteCacheListener localListener = new MockRemoteCacheListener();
        localListener.remoteType = IRemoteCacheAttributes.LOCAL;

        String cacheName = "testSimpleRegisterListenerAndPut_FromClusterWithLCC";
        server.addCacheListener( cacheName, clusterListener );
        server.addCacheListener( cacheName, localListener );

        // DO WORK
        List inputItems = new LinkedList();
        int numToPut = 10;

        for ( int i = 0; i < numToPut; i++ )
        {
            ICacheElement element = new CacheElement( cacheName, String.valueOf( i ), Long.valueOf( i ) );
            inputItems.add( element );
            // update using the cluster listener id
            server.update( element, clusterListener.getListenerId() );
        }

        SleepUtil.sleepAtLeast( 200 );
        Thread.yield();
        SleepUtil.sleepAtLeast( 200 );

        // VERIFY
        assertEquals( "Wrong number of items put to listener.", numToPut, localListener.putItems.size() );
        for ( int i = 0; i < numToPut; i++ )
        {
            assertEquals( "Wrong item.", inputItems.get( i ), localListener.putItems.get( i ) );
        }
    }

    /**
     * Register a listener and then verify that it is called when we put using a different listener
     * id.
     * @throws Exception
     */
    public void testSimpleRegisterListenerAndRemove()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );

        MockRemoteCacheListener mockListener = new MockRemoteCacheListener();
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        String cacheName = "testSimpleRegisterListenerAndPut";
        server.addCacheListener( cacheName, mockListener );

        // DO WORK
        int numToPut = 10;

        for ( int i = 0; i < numToPut; i++ )
        {
            // use a junk listener id
            server.remove( cacheName, String.valueOf( i ), 9999 );
        }

        Thread.sleep( 100 );
        Thread.yield();
        Thread.sleep( 100 );

        // VERIFY
        assertEquals( "Wrong number of items removed from listener.", numToPut, mockListener.removedKeys.size() );
        for ( int i = 0; i < numToPut; i++ )
        {
            assertEquals( "Wrong key.", String.valueOf( i ), mockListener.removedKeys.get( i ) );
        }
    }

    /**
     * Verify event log calls.
     * <p>
     * @throws Exception
     */
    public void testUpdate_simple()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockCacheEventLogger cacheEventLogger = new MockCacheEventLogger();
        server.setCacheEventLogger( cacheEventLogger );

        ICacheElement item = new CacheElement( "region", "key", "value" );

        // DO WORK
        server.update( item );

        // VERIFY
        assertEquals( "Start should have been called.", 1, cacheEventLogger.startICacheEventCalls );
        assertEquals( "End should have been called.", 1, cacheEventLogger.endICacheEventCalls );
    }

    /**
     * Verify event log calls.
     * <p>
     * @throws Exception
     */
    public void testGet_simple()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockCacheEventLogger cacheEventLogger = new MockCacheEventLogger();
        server.setCacheEventLogger( cacheEventLogger );

        // DO WORK
        server.get( "region", "key" );

        // VERIFY
        assertEquals( "Start should have been called.", 1, cacheEventLogger.startICacheEventCalls );
        assertEquals( "End should have been called.", 1, cacheEventLogger.endICacheEventCalls );
    }

    /**
     * Verify event log calls.
     * <p>
     * @throws Exception
     */
    public void testGetMatching_simple()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockCacheEventLogger cacheEventLogger = new MockCacheEventLogger();
        server.setCacheEventLogger( cacheEventLogger );

        // DO WORK
        server.getMatching( "region", "pattern", 0 );

        // VERIFY
        assertEquals( "Start should have been called.", 1, cacheEventLogger.startICacheEventCalls );
        assertEquals( "End should have been called.", 1, cacheEventLogger.endICacheEventCalls );
    }
    
    /**
     * Verify event log calls.
     * <p>
     * @throws Exception
     */
    public void testGetMultiple_simple()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockCacheEventLogger cacheEventLogger = new MockCacheEventLogger();
        server.setCacheEventLogger( cacheEventLogger );

        // DO WORK
        server.getMultiple( "region", new HashSet() );

        // VERIFY
        assertEquals( "Start should have been called.", 1, cacheEventLogger.startICacheEventCalls );
        assertEquals( "End should have been called.", 1, cacheEventLogger.endICacheEventCalls );
    }
    
    /**
     * Verify event log calls.
     * <p>
     * @throws Exception
     */
    public void testRemove_simple()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockCacheEventLogger cacheEventLogger = new MockCacheEventLogger();
        server.setCacheEventLogger( cacheEventLogger );

        // DO WORK
        server.remove( "region", "key" );

        // VERIFY
        assertEquals( "Start should have been called.", 1, cacheEventLogger.startICacheEventCalls );
        assertEquals( "End should have been called.", 1, cacheEventLogger.endICacheEventCalls );
    }
    
    /**
     * Verify event log calls.
     * <p>
     * @throws Exception
     */
    public void testRemoveAll_simple()
        throws Exception
    {
        // SETUP
        IRemoteCacheServerAttributes rcsa = new RemoteCacheServerAttributes();
        rcsa.setConfigFileName( "/TestRemoteCacheServer.ccf" );
        RemoteCacheServer server = new RemoteCacheServer( rcsa );

        MockCacheEventLogger cacheEventLogger = new MockCacheEventLogger();
        server.setCacheEventLogger( cacheEventLogger );

        // DO WORK
        server.removeAll( "region" );

        // VERIFY
        assertEquals( "Start should have been called.", 1, cacheEventLogger.startICacheEventCalls );
        assertEquals( "End should have been called.", 1, cacheEventLogger.endICacheEventCalls );
    }    
}
