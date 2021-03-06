/* 
 * TableWriterOrderByMin Copyright 2012, NOAA.
 * See the LICENSE.txt file in this file's directory.
 */
package gov.noaa.pfel.erddap.dataset;

import com.cohort.array.PrimitiveArray;
import com.cohort.util.SimpleException;
import com.cohort.util.String2;

import gov.noaa.pfel.coastwatch.pointdata.Table;
import gov.noaa.pfel.erddap.util.EDStatic;

import java.util.BitSet;

/**
 * TableWriterOrderByMin provides a way to sort the response table's rows,
 * and just keep the row where the value of the last sort variable is highest.
 * For example, you could use orderBy(\"stationID,time\") to just get the rows
 * of data with each station's minimum time value.
 *
 * <p>This doesn't do anything to missing values and doesn't asssume they are
 * stored as NaN or fake missing values.
 *
 * <p>Unlike TableWriterAllWithMetadata, this doesn't keep track of min,max for actual_range
 * or update metadata at end. It is assumed that this is like a filter,
 * and that a subsequent TableWriter will handle that if needed.
 *
 * @author Bob Simons (bob.simons@noaa.gov) 2009-05-13
 */
public class TableWriterOrderByMin extends TableWriterAll {


    //set by constructor
    protected TableWriter otherTableWriter;
    public String orderBy[];

    /**
     * The constructor.
     *
     * @param tDir a private cache directory for storing the intermediate files,
     *    usually cacheDirectory(datasetID)
     * @param tFileNameNoExt is the fileName without dir or extension (used as basis for temp files).
     *     A random number will be added to it for safety.
     * @param tOtherTableWriter the tableWriter that will receive the unique rows
     *   found by this tableWriter.
     * @param tOrderByCsv the names of the columns to sort by (most to least important)
     */
    public TableWriterOrderByMin(String tDir, String tFileNameNoExt, 
            TableWriter tOtherTableWriter, String tOrderByCsv) {

        super(tDir, tFileNameNoExt); 
        otherTableWriter = tOtherTableWriter;
        if (tOrderByCsv == null || tOrderByCsv.trim().length() == 0)
            throw new SimpleException("Query error: " +
                "No column names were specified for 'orderByMin'.");
        orderBy = String2.split(tOrderByCsv, ',');
    }


    /**
     * This adds the current contents of table (a chunk of data) to the OutputStream.
     * This calls ensureCompatible each time it is called.
     * If this is the first time this is called, this does first time things
     *   (e.g., call OutputStreamSource.outputStream() and write file header).
     * The number of columns, the column names, and the types of columns 
     *   must be the same each time this is called.
     *
     * @param table with destinationValues
     * @throws Throwable if trouble
     */
    public void writeSome(Table table) throws Throwable {
        if (table.nRows() == 0) 
            return;

        //to save time and disk space, this just does a partial job 
        //  (remove non-min rows from this partial table)
        //  and leaves perfect job to finish()
        table.orderByMin(orderBy);

        //ensure the table's structure is the same as before
        //and write to dataOutputStreams
        super.writeSome(table);
    }

    
    /**
     * This finishes orderByMin and writes results to otherTableWriter
     *
     * @throws Throwable if trouble (e.g., EDStatic.THERE_IS_NO_DATA if there is no data)
     */
    public void finish() throws Throwable {
        super.finish();

        Table cumulativeTable = cumulativeTable();
        releaseResources();
        cumulativeTable.orderByMin(orderBy);
        otherTableWriter.writeAllAndFinish(cumulativeTable);

        //clean up
        otherTableWriter = null;
    }

    /**
     * If caller has the entire table, use this instead of repeated writeSome() + finish().
     * This overwrites the superclass method.
     *
     * @throws Throwable if trouble (e.g., EDStatic.THERE_IS_NO_DATA if there is no data)
     */
    public void writeAllAndFinish(Table tCumulativeTable) throws Throwable {
        tCumulativeTable.orderByMin(orderBy);
        otherTableWriter.writeAllAndFinish(tCumulativeTable);
        otherTableWriter = null;
    }



}



