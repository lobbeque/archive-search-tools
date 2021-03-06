package qlobbe;

/*
 * Java
 */

import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat; 
import java.lang.Integer;

/*
 * Joda Time
 */

import org.joda.time.DateTime;

/*
 * Solr
 */

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/*
 * Sl4j
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArchiveSearchHandler extends RequestHandlerBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveSearchHandler.class);

    @Override
    public void init(NamedList args) {
        super.init(args);
    }

    private Date getDate(Date t, int n, char op) {
        DateTime d = new DateTime(t);
        
        switch (op) {
            case '+':  d = d.plusDays(n);
                break;
            case '-':  d = d.minusDays(n);
                break;
        }
        return d.toDate();
    }

    @Override
    public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {

        try {

            SolrCore core = req.getCore();

            String coreName = core.getName();

            ModifiableSolrParams params = new ModifiableSolrParams(req.getParams());

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            DateFormat dfSolr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");     

            System.out.println("==== handleRequest");   

            /*
             * Add specifique params for siteLink component
             */            

            if (req.getParams().getBool("siteLink", true) && req.getParams().get("siteName") != null && req.getParams().get("time") != null) {

                Date t = df.parse(req.getParams().get("time"));                
                params.add("group","true");
                params.add("group.field","url");  
                params.add("group.sort","last_modified desc");                
                params.add("group.limit","1");
                params.add("rows","100000");   
                params.add(CommonParams.FQ, "site:" + (String)req.getParams().get("siteName"));                
                params.add(CommonParams.FQ, "last_modified:[ * TO " + dfSolr.format(t) + " ]");
                params.add(CommonParams.FL, "link_diaspora");                                            

            }

            /*
             * Add specifique params for timePicker component
             */

            if (req.getParams().getBool("timePicker", false) && req.getParams().get("time") != null && req.getParams().get("timeRange") != null) {
                   
                Date t = df.parse(req.getParams().get("time"));
                int tRange = req.getParams().getInt("timeRange");
                Date tMax = getDate(t,tRange,'+');
                Date tMin = getDate(t,tRange,'-');
                params.add("group","true");
                params.add("group.field","url"); 
                params.add("rows","100000");                   
                params.add("group.limit","1000");                
                params.add(CommonParams.FQ, "date:[ " + dfSolr.format(tMin) + " TO " + dfSolr.format(tMax) + " ]");

            }

            /*
             * Add specifique params for contextMap
             */

            if (req.getParams().getBool("contextMap",false)) {

                params.add("fl","first_modified,last_modified,link_out_social,link_out_url,link_out_corpus");
                params.add("sort","first_modified asc");

                System.out.println("==== add params");

            }

            req.setParams(params);           

            core.getRequestHandler("").handleRequest(req, rsp);

        } catch (Exception e) {        
            rsp.setException(e);
        }
    }

    @Override
    public Category getCategory() {
        return Category.QUERYHANDLER;
    }

    @Override
    public String getDescription() {
        return "RequestHandler for web archive management";
    }

    @Override
    public URL[] getDocs() {
        return null;
    }

    @Override
    public String getName() {
        return "qlobbe";
    }

    @Override
    public String getSource() {
        return "Quentin Lobbé";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public NamedList getStatistics() {
        return new SimpleOrderedMap<Integer>();
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void handleRequestBody(SolrQueryRequest arg0, SolrQueryResponse arg1)
            throws Exception {
        throw new Error(); // unused
    }

}
