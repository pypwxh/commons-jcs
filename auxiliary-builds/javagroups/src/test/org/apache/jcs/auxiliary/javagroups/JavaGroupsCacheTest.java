package org.apache.jcs.auxiliary.javagroups;

import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.jcs.access.CacheAccess;
import org.javagroups.log.Tracer;
import org.javagroups.log.Trace;

import java.util.Properties;
import java.io.IOException;

import junit.framework.TestCase;

public class JavaGroupsCacheTest extends TestCase
{
    public JavaGroupsCacheTest( String testName )
    {
        super( testName );
    }

    public void testStuff() throws Exception
    {
        // Create and configure two managers for the same channel

        CompositeCacheManager manager1 = new CompositeCacheManager();

        manager1.configure( getProperties() );

        CompositeCacheManager manager2 = new CompositeCacheManager();

        manager2.configure( getProperties() );

        // Get two the same region in each of the managers

        CacheAccess one = new CacheAccess( manager1.getCache( "testCache" ) );
        CacheAccess two = new CacheAccess( manager2.getCache( "testCache" ) );

        // Put some items to one of the mangers

        one.put( "1", "one" );
        one.put( "2", "two" );
        one.put( "3", "three" );
        one.put( "4", "four" );
        one.put( "5", "five" );

        // Wait for it to propogate -- FIXME: This is time sensitive and thus
        //                                    a bad idea for a unit test.

        Thread.sleep( 1000 );

        // Assert that the values were correctly propogated

        assertEquals( "one",   two.get( "1" ) );
        assertEquals( "two",   two.get( "2" ) );
        assertEquals( "three", two.get( "3" ) );
        assertEquals( "four",  two.get( "4" ) );
        assertEquals( "five",  two.get( "5" ) );

        // Free caches

        manager1.freeCache( "testCache" );
        manager2.freeCache( "testCache" );
    }

    private Properties getProperties() throws IOException
    {
        Properties props = new Properties();

        props.load( getClass().getResourceAsStream(
            "/org/apache/jcs/auxiliary/javagroups/JavaGroupsCacheTest.ccf" ) );

        return props;
    }
}
