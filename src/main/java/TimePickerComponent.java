package qlobbe;

/*
 * Java
 */

import java.io.IOException;

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

    	LOGGER.warn("==== coucou");

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