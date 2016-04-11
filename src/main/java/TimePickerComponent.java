package qlobbe;

/*
 * Java
 */

import java.lang.Math;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat; 
import java.util.Collection;

/*
 * Joda Time
 */

import org.joda.time.DateTime;

/*
 * Sl4j
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Solr
 */

import org.apache.solr.common.params.ShardParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.common.SolrDocument;

public class  TimePickerComponent extends SearchComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimePickerComponent.class);

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		return;	
	}

	/*
	 *   Undistributed case
	 */
    @Override
    public void process(ResponseBuilder rb) {

    	if (rb.req == null || rb.req.getParams() == null || rb.req.getParams().getBool(ShardParams.IS_SHARD, false) || !rb.req.getParams().getBool("timePicker", false)) {
            return;
        }

		try {
			select(rb.req,rb.rsp,false);
		}

		catch (Exception e) {
			rb.rsp.setException(e);
		}
	}


    /*
	 *   Distributed case
	 */
    @Override
    public void finishStage(ResponseBuilder rb) {    

    	if (rb.req == null || rb.req.getParams() == null || rb.stage != rb.STAGE_GET_FIELDS || !rb.req.getParams().getBool("timePicker", false)) {
            return;
        }

		try {
			select(rb.req,rb.rsp,true);
		}

		catch (Exception e) {
			rb.rsp.setException(e);
		}
    }  

    private void select(SolrQueryRequest req, SolrQueryResponse rsp, Boolean isDistributed) throws IOException {

        try {

            /*
             * Take some data from the "grouped" field and put it into the new field "response"
             */

            // http://localhost:8800/solr/ediasporas_maroco/select?q=*:*&fq=site:yabiladi.com&timePicker=true&time=2011-11-15&timeRange=5

            // http://localhost:8800/solr/ediasporas_maroco/select?q=*:*&fq=site:bladi.net&timePicker=true&time=2011-05-20&timeMode=inf&rows=400

            String groupField = req.getParams().get("group.field",null);

            NamedList grouped = (NamedList)((SimpleOrderedMap)rsp.getValues().get("grouped")).get(groupField);

            ArrayList groups = (ArrayList)grouped.get("groups");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            DateTime time = new DateTime(df.parse(req.getParams().get("time")));

            /*
             * create a new Solr Response 
             */

            SolrDocumentList response = new SolrDocumentList();

            response.setNumFound(Long.valueOf((Integer)grouped.get("matches")));

            int groupeIdx = rsp.getValues().indexOf("grouped", 0);

            /*
             * process all the groups
             */

            for(Object group : groups) {

                Object doclist = ((NamedList)group).get("doclist");

                SolrDocumentList docs = (SolrDocumentList)doclist;  

                int minIdx = 0;

                long min = Long.MAX_VALUE;

                for(SolrDocument doc : docs) {

                    Collection<Object> dates = doc.getFieldValues("date");

                    int docIdx = docs.indexOf(doc);

                    for(Object date : dates) {
                        DateTime d = new DateTime((Date)date);
                        long diff = Math.abs(time.getMillis() - d.getMillis());
                        if (diff < min) {
                            min = diff;
                            minIdx = docIdx;
                        }        
                    }
                }

                response.add(docs.get(minIdx));
            }

            rsp.getValues().setVal(groupeIdx,response);

            rsp.getValues().setName(groupeIdx,"response");            

        } catch (Exception e) {        
            rsp.setException(e);
        }



    }  

    @Override
    public String getDescription() {
        return "Pick up the best version of a web page related to a givan date";
    }


    @Override
    public String getSource() {
        return "Quentin Lobbé";
    }


    @Override
    public String getVersion() {
        return "1.0.0";
    }    

}