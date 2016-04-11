package qlobbe;

/*
 * Java
 */

import java.lang.Math;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
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

public class  SiteLinkComponent extends SearchComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteLinkComponent.class);

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		return;	
	}

	/*
	 *   Undistributed case
	 */
    @Override
    public void process(ResponseBuilder rb) {

    	if (rb.req == null || rb.req.getParams() == null || rb.req.getParams().getBool(ShardParams.IS_SHARD, false) || !rb.req.getParams().getBool("siteLink", false)) {
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

    	if (rb.req == null || rb.req.getParams() == null || rb.stage != rb.STAGE_GET_FIELDS || !rb.req.getParams().getBool("siteLink", false)) {
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

            // process grouped response

            String groupField = req.getParams().get("group.field",null);
            NamedList grouped = (NamedList)((SimpleOrderedMap)rsp.getValues().get("grouped")).get(groupField);
            int groupeIdx     = rsp.getValues().indexOf("grouped", 0);            
            ArrayList groups  = (ArrayList)grouped.get("groups");        

            // create a brand new response

            SimpleOrderedMap response = new SimpleOrderedMap();
            Boolean first = false;

            // init links array with the first doc of the first group if exist

            if (groups.size() == 0)
                return;

            int nbLinks = ((String)((SolrDocument)((SolrDocumentList)((NamedList)groups.get(0)).get("doclist")).get(0)).getFieldValue("link_diaspora")).length();
            int[] links = new int[nbLinks]; 

            // process all the groups

            for(Object group : groups) {

                Object doclist = ((NamedList)group).get("doclist");
                SolrDocumentList docs = (SolrDocumentList)doclist;
                SolrDocument doc = (SolrDocument)docs.get(0);

                int[] tmp = Arrays.asList(((String)doc.getFieldValue("link_diaspora")).split("")).stream().mapToInt(Integer::parseInt).toArray();

                if (!first) {
                    links = tmp;
                    first = true;
                } else {
                    int cpt = 0;

                    for(int link : links) {
                        links[cpt] = link + tmp[cpt];
                        cpt ++;
                    }
                }
            }

            // build a new link string

            String rst  = "";

            for (int link : links) {
                rst = rst + String.valueOf(link);
            }   

            response.add("site",req.getParams().get("siteName"));
            response.add("time",req.getParams().get("time"));
            response.add("nb_pages",(Integer)grouped.get("matches"));
            response.add("link_diasporas",rst);

            rsp.getValues().setName(groupeIdx,"response");  
            rsp.getValues().setVal(groupeIdx,response);            

        } catch (Exception e) {        
            rsp.setException(e);
        }



    }  

    @Override
    public String getDescription() {
        return "";
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