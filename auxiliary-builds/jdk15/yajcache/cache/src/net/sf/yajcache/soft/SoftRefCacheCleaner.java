/*
 * SoftRefCacheCleaner.java
 *
 * $Revision$ $Date$
 */

package net.sf.yajcache.soft;

import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Hanson Char
 */
enum SoftRefCacheCleaner {
    inst;
    private static final boolean debug = true;
    private Log log = debug ? LogFactory.getLog(this.getClass()) : null;
    
    private volatile int countTryKeyClean;
    private volatile int countRemovedByOthers;
    private volatile int countKeyCleaned;
    private volatile int countDataRace;
    private volatile int countDataRaceAndRemovedByOthers;
    private volatile int countBye;
    
    <V> void cleanupKey(Map<String, KeyedSoftRef<V>> map, String key) {
        V val = null;
        // already garbage collected.  So try to clean up the key.
        if (debug)
            log.debug("Try to clean up the key");
        this.countTryKeyClean++;
        KeyedSoftRef<V> oldRef = map.remove(key);
        // If oldRef is null, the key has just been 
        // cleaned up by another thread.
        if (oldRef == null) {
            if (debug)
                log.debug("Key has just been removed by another thread.");
            this.countRemovedByOthers++;
            return;
        }
        // Check for race condition.
        V oldVal = oldRef.get();

        if (val == oldVal) {
            // not considered a race condition
            oldRef.clear();
            if (debug)
                log.debug("Key removed and Soft Reference cleared.");
            this.countKeyCleaned++;
            return;
        }
        // Race condition.
        do {
            if (debug)
                log.debug("Race condition occurred.  So put back the old stuff.");
            this.countDataRace++;
            // race condition occurred
            // put back the old stuff
            val = oldVal;
            oldRef = map.put(key, oldRef);

            if (oldRef == null) {
                // key has just been cleaned up by another thread.
                if (debug)
                    log.debug("Key has just been removed by another thread.");
                this.countDataRaceAndRemovedByOthers++;
                return;  
            }
            oldVal = oldRef.get();
        } while (oldVal != val);

        if (debug)
            log.debug("Bye.");
        this.countBye++;
        return;
    }

    public int getCountTryKeyClean() {
        return countTryKeyClean;
    }

    public int getCountRemovedByOthers() {
        return countRemovedByOthers;
    }

    public int getCountKeyCleaned() {
        return countKeyCleaned;
    }

    public int getCountDataRace() {
        return countDataRace;
    }
    public int getCountDataRaceAndRemovedByOthers() {
        return countDataRaceAndRemovedByOthers;
    }

    public int getCountBye() {
        return countBye;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}