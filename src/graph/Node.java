package graph;

/**This interface has to define the behavior of a node.
 * Generally speaking a Node could be ANYTHING, the only requirement is that every node
 * has to be uniquely identified from a bunch of them.
 * In order to do that is necessary to have a proper implementation of the native methods Equals and HashCode.
 * Equals: public boolean equals(Object obj)
 * HashCode: public int hashCode()
 */
public interface Node {

    public String getName();
    
}