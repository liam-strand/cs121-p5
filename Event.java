/* Event.java
 * 
 * By: Liam Strand
 * On: December 2022
 * 
 * An inteface for a loggable event.
 */

import java.util.*;

public interface Event {
    public List<String> toStringList();
    public void replayAndCheck(MBTA mbta);
}
