package simpledb;
import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends AbstractDbIterator {

	private final JoinPredicate join;
	private final DbIterator child1, child2;
	private Tuple tup1;
	
    /**
     * Constructor.  Accepts to children to join and the predicate
     * to join them on
     *
     * @param p The predicate to use to join the children
     * @param child1 Iterator for the left(outer) relation to join
     * @param child2 Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
        // Done
    	join = p;
    	this.child1 = child1;
    	this.child2 = child2;
    }

    /**
     * @see simpledb.TupleDesc#combine(TupleDesc, TupleDesc) for possible implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // Done
    	return TupleDesc.combine(child1.getTupleDesc(), child2.getTupleDesc());
    }

    public void open()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        // Done
    	child1.open();
    	child2.open();
    	tup1 = child1.hasNext()? child1.next():null;
    }

    public void close() {
        // Done
    	tup1 = null;
    	child1.close();
    	child2.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // Done
    	child1.rewind();
    	child2.rewind();
    	tup1 = child1.hasNext()? child1.next():null;
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no more tuples.
     * Logically, this is the next tuple in r1 cross r2 that satisfies the join
     * predicate.  There are many possible implementations; the simplest is a
     * nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of
     * Join are simply the concatenation of joining tuples from the left and
     * right relation. Therefore, if an equality predicate is used 
     * there will be two copies of the join attribute
     * in the results.  (Removing such duplicate columns can be done with an
     * additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     *
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException {
        // Done
    	while (null != tup1) {
    		while (child2.hasNext()) {
    			Tuple tup2 = child2.next();
    			
    			if (join.filter(tup1, tup2)) {
    				Tuple tup = new Tuple(getTupleDesc());
    				int idx = 0;
    				
    				for (int i=0; i<tup1.getTupleDesc().numFields(); i++) {
    					tup.setField(idx++, tup1.getField(i));
    				}
    				for (int i=0; i<tup2.getTupleDesc().numFields(); i++) {
    					tup.setField(idx++, tup2.getField(i));
    				}
    				return tup;
    			}
    		}
    		if (child1.hasNext()) {
    			tup1 = child1.next();
    			child2.rewind();
    		} else {
    			break;
    		}
    	}
        return null;
    }
}
