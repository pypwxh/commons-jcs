package org.apache.jcs.utils.threadpool;

import junit.framework.TestCase;

/**
 * This test is experiemental. I'm trying to find out if the max size setting
 * will result in the removal of threads.
 * 
 * @author Aaron Smuts
 *  
 */
public class ThreadPoolUnitTest
    extends TestCase
{

    /**
     * Make sure that the max size setting takes effect before the idle
     * time is reached.
     * 
     * We just want to ensure that we can adjust the max size of an active pool.
     * 
     * http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/PooledExecutor.html#setMaximumPoolSize(int)
     * 
     * @throws Exception
     */
    public void testMaxReduction()
        throws Exception
    {
        //ThreadPoolManager.setPropsFileName( "thread_pool_test.properties" );
        ThreadPool pool = ThreadPoolManager.getInstance().getPool( "maxtest" );

        System.out.println( "pool = " + pool );

        pool.getPool().setMaximumPoolSize( 5 );

        System.out.println( "current size before execute = " + pool.getPool().getPoolSize() );

       
        // add 6
        for ( int i = 1; i < 30; i++ )
        {
            final ThreadPool myPool = pool;
            final int cnt = i;
            pool.execute( new Runnable()
            {

                public void run()
                {
                    try
                    {
                        //System.out.println( cnt );
                        System.out.println( "count = " + cnt + " before sleep current size = " + myPool.getPool().getPoolSize() );                        
                        Thread.sleep( 200 / cnt );
                        System.out.println( "count = " + cnt + " after sleep current size = " + myPool.getPool().getPoolSize() );                        
                    }
                    catch ( InterruptedException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } );

        }

        System.out.println( "current size = " + pool.getPool().getPoolSize() );
        
        pool.getPool().setMaximumPoolSize( 4 );

        //Thread.sleep( 200 );
        
        System.out.println( "current size after set size to 4= " + pool.getPool().getPoolSize() );

        Thread.sleep( 200 );
        
        System.out.println( "current size again after sleep = " + pool.getPool().getPoolSize() );

        assertEquals( "Pool size should have been reduced.", 4, pool.getPool().getPoolSize() );
    }

}
