package dlineage.postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
import gudusoft.gsqlparser.dlineage.dataflow.model.Option;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Relationship;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.metadata.Table;
import gudusoft.gsqlparser.util.CollectionUtil;
import junit.framework.TestCase;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testProcedure extends TestCase {
    public void test1() {
        File file = new File(common.gspCommon.BASE_SQL_DIR_PRIVATE +"dataflow/postgresql/I61CZ0.json");
        EDbVendor vendor = TGSqlParser.getDBVendorByName("postgresql");
        Option option = new Option();
        option.setVendor(vendor);
        option.setSimpleOutput(true);
        option.setShowCallRelation(true);
        option.setShowConstantTable(true);

        DataFlowAnalyzer dataFlowAnalyzer = new DataFlowAnalyzer(file, option);
        dataFlowAnalyzer.setShowConstantTable(true);
        dataFlowAnalyzer.setShowCallRelation(true);

        dataFlowAnalyzer.generateDataFlow();
        dataflow flow = dataFlowAnalyzer.getDataFlow();
        Dataflow dataFlow = DataFlowAnalyzer.getSqlflowJSONModel(vendor, flow, false);
        assertTrue(dataFlow.getDbobjs().getServers().get(0).getDatabases().get(0).getSchemas().get(1).getProcedures().get(0).getName().equalsIgnoreCase("fsa048"));

        List<Relationship> fsa048Relationships = Arrays.stream(dataFlow.getRelationships())
                .filter(s -> s.getTarget().getParentName().contains("report.trs_fsa048_report_v002")).collect(Collectors.toList());
        assertTrue(fsa048Relationships.size() == 3);

        List<Relationship> a305Relationships = fsa048Relationships.stream().filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().toLowerCase().contains("tft_scheme_details_a305".toLowerCase())))
                .collect(Collectors.toList());
        assertTrue(!CollectionUtil.isEmpty(a305Relationships) && a305Relationships.size()==1);

        List<Relationship> v012Relationships = fsa048Relationships.stream().filter(r -> Arrays.stream(r.getSources()).anyMatch(s -> s.getParentName().toLowerCase().contains("TFT_ACCT_EOD_POSN_V012".toLowerCase())))
                .collect(Collectors.toList());
        assertTrue(!CollectionUtil.isEmpty(v012Relationships) && v012Relationships.size()==3);
        
    }
}
