package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.bo.*;
import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Sql;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.repository.MethodCallRepository;
import cn.icedsoul.cutter.repository.SqlRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.SharingDegreeService;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.icedsoul.cutter.util.Common.*;

@Service
@Log
public class SharingDegreeServiceImpl implements SharingDegreeService {

    @Autowired
    TableRepository tableRepository;

    @Autowired
    MethodCallRepository methodCallRepository;

    @Autowired
    SqlRepository sqlRepository;

    @Autowired
    WeightCalculationService weightCalculationService;

    Map<String, Double> ssdMap = new HashMap();
    Map<String, Double> msdMap = new HashMap();

    @Override
    public void calculateSharingDegree() {
        //总场景数量
        int scenarioNum = methodCallRepository.countScenarioNum();
        //总模块数量
        int moduleNum = methodCallRepository.countModuleNum();
        //得到所有table
        Iterator<Table> tableIterator = tableRepository.findAll().iterator();
        while(tableIterator.hasNext()){
            Table table = tableIterator.next();
            int scenarioNumOfTable = tableRepository.countScenarioNumByTableId(table.getId());
            double ssd = (double)scenarioNumOfTable/(double)scenarioNum;
            tableRepository.setSSDByTableId(table.getId(), ssd);
            ssdMap.put(table.getTableName(), ssd);

            int moduleNumOfTable = tableRepository.countModuleNumByTableId(table.getId());
            double msd = (double)moduleNumOfTable/(double)moduleNum;
            tableRepository.setMSDByTableId(table.getId(), msd);
            msdMap.put(table.getTableName(), msd);
        }
        System.out.println("-----共享度-----");
        System.out.println("tableName\tssd\tmsd");
        for(String s:ssdMap.keySet()){
            System.out.println(s+" | "+ssdMap.get(s)+" | "+msdMap.get(s));
        }
    }

    @Override
    public List<Set<ShareTable>> shareCalculate() {

        int scenarioNum = methodCallRepository.countScenarioNum();
        int moduleNum = methodCallRepository.countModuleNum();

        int requestNumber = requests.keySet().size();
        int traceTypeNumber = 0;
        int traceNumber = 0;

        int cRequestNumber = 0;
        int cTraceTypeNumber = 0;
        int cTraceNumber = 0;

        for (Request request: requests.values()) {
            if(request.isContainTable()){
                cRequestNumber++;
            }
            traceTypeNumber += request.getTraces().size();
            for(TraceType traceType: request.getTraces()) {
                if(traceType.isContainTable()) {
                    cTraceTypeNumber++;
                    cTraceNumber += traceType.getTraceIds().size();
                }
                traceNumber += traceType.getTraceIds().size();
            }
        }
        int sqlNumber = 0;
        Iterable<Sql> sqls = sqlRepository.findAll();
        for (Sql sql : sqls){
            if(sql.getTables().size() > 1){
                sqlNumber++;
            }
        }
        Iterable<Table> tables = tableRepository.findAll();
        int finalTraceNumber = traceNumber;
        int finalTraceTypeNumber = traceTypeNumber;
        int finalCRequestNumber = cRequestNumber;
        int finalCTraceTypeNumber = cTraceTypeNumber;
        int finalCTraceNumber = cTraceNumber;

        log.info("共有 " + sqlNumber + "个 SQL, " + traceNumber + " 条Trace, " + traceTypeNumber + " 种 TraceType" + requestNumber + " 种 Request" +
                scenarioNum + " 个 Scenario, " + moduleNum + "个 Module");

        List<ShareTable> shareTables = new ArrayList<>();
        int finalSqlNumber = sqlNumber;
        tables.forEach(table -> {
            int appearSql = 0;
            for(Long sqlId : table.getAppearSql()){
                Sql sql = sqlRepository.findById(sqlId).get();
                if(sql.getTables().size() > 1){
                    appearSql += 1;
                }
            }
            double sqlShare = round((double) appearSql / (double) finalSqlNumber);
            double traceShare = round((double) table.getAppearTrace().size() / (double) finalTraceNumber);
            double scenarioShare = round((double) table.getAppearScenario().size() / (double) scenarioNum);
            double moduleShare = round((double) table.getAppearModule().size() / (double) moduleNum);

            Set<Method> appearRequest = new HashSet<>();
            Set<TraceType> appearTraceType = new HashSet<>();
            for(Long traceId: table.getAppearTrace()) {
                for (Request request : requests.values()) {
                    if (request.containTrace(traceId)) {
                        appearRequest.add(request.getEntry());
                        for(TraceType traceType : request.getTraces()){
                            if(traceType.containTrace(traceId)){
                                appearTraceType.add(traceType);
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            int appearRequestNum = appearRequest.size();
            int appearTraceTypeNum = appearTraceType.size();

            double requestShare =round((double) appearRequestNum / (double) requestNumber);
            double traceTypeShare = round((double) appearTraceTypeNum / (double) finalTraceTypeNumber);

            double cTraceShare = round((double) table.getAppearTrace().size() / (double) finalCTraceNumber);
            double cRequestShare =round((double) appearRequestNum / (double) finalCRequestNumber);
            double cTraceTypeShare = round((double) appearTraceTypeNum / (double) finalCTraceTypeNumber);

            ShareTable shareTable = new ShareTable(table, sqlShare, traceShare, cTraceShare, scenarioShare, moduleShare, requestShare, traceTypeShare, cRequestShare, cTraceTypeShare);
            shareTables.add(shareTable);
            log.info(table.getTableName() + " 出现在 " + table.getAppearSql().size() + "个 SQL， " + table.getAppearTrace().size() + "个 Trace, "
            + appearRequestNum + "个 Request, " + appearTraceTypeNum + "个 RequestNum， " + table.getAppearScenario().size() + " 个Scenario， "
            + table.getAppearModule().size() + "个 Module");
        });
        Collections.sort(shareTables);
        for(ShareTable shareTable : shareTables){
            log.info(shareTable.getTable().getTableName() + " " + (shareTable.getSqlShare() * 0.2 + shareTable.getCTraceTypeShare() * 0.8  + shareTable.getScenarioShare()));
            log.info(shareTable.getTable().getTableName() + " " + shareTable.getSqlShare() + " " + shareTable.getCTraceTypeShare() + " " + shareTable.getScenarioShare());
        }

        //确定共享表数量
        int number = shareTableCount(shareTables.size());
        List<Set<ShareTable>> group = new ArrayList<>();
        for(int i = 0; i < number; i++){
            Set<ShareTable> set = new HashSet<>();
            set.add(shareTables.get(i));
            group.add(set);
        }

        double[][] sqlSimilar, traceSimilar, scenarioSimilar;

        //计算、填写相似度矩阵
        sqlSimilar = new double[shareTables.size()][shareTables.size()];
        traceSimilar = new double[shareTables.size()][shareTables.size()];
        scenarioSimilar = new double[shareTables.size()][shareTables.size()];

        for(int i = 0; i < shareTables.size(); i++){
            ShareTable table1 = shareTables.get(i);
            for(int j = i + 1; j < shareTables.size(); j++){
                ShareTable table2 = shareTables.get(j);
                TwoWayRelation sqlTwoWayRelation = weightCalculationService.calculateSqlSimilar(table1.getTable(), table2.getTable());
                sqlSimilar[i][j] = sqlTwoWayRelation.getAToB();
                sqlSimilar[j][i] = sqlTwoWayRelation.getBToA();
                TwoWayRelation traceTwoWayRelation = weightCalculationService.calculateTraceSimilar(table1.getTable(), table2.getTable());
                traceSimilar[i][j] = traceTwoWayRelation.getAToB();
                traceSimilar[j][i] = traceTwoWayRelation.getBToA();
                TwoWayRelation scenarioTwoWayRelation = weightCalculationService.calculateScenarioSimilar(table1.getTable(), table2.getTable());
                scenarioSimilar[i][j] = scenarioTwoWayRelation.getAToB();
                scenarioSimilar[j][i] = scenarioTwoWayRelation.getBToA();
                if(i < number && j < number) {
                    log.info(table1.getTable().getTableName() + "  " + table2.getTable().getTableName() + " " + sqlTwoWayRelation.getAToB() + " " + sqlTwoWayRelation.getBToA() + " " +
                            traceTwoWayRelation.getAToB() + " " + traceTwoWayRelation.getBToA() + " " +
                                    scenarioTwoWayRelation.getAToB() + " " + scenarioTwoWayRelation.getBToA());
                    if (similar(sqlTwoWayRelation, traceTwoWayRelation, scenarioTwoWayRelation)) {
                        Set<ShareTable> set1 = new HashSet<>();
                        Set<ShareTable> set2 = new HashSet<>();
                        for (Set<ShareTable> set : group) {
                            if (set.contains(table1)) {
                                set1 = set;
                            }
                            if (set.contains(table2)) {
                                set2 = set;
                            }
                        }
                        group.remove(set1);
                        group.remove(set2);
                        set1.addAll(set2);
                        group.add(set1);
                    }
                }
            }
        }

        for (Set<ShareTable> set : group){
            log.info("Group:");
            for(ShareTable shareTable : set){
                log.info(shareTable.getTable().getTableName());
            }
        }

//        for(int i = 0; i < sqlSimilar.length ; i++){
//            for(int j = 0; j < sqlSimilar.length ; j++){
//                System.out.print(String.format("%.2f",sqlSimilar[i][j]).toString() + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//        for(int i = 0; i < sqlSimilar.length ; i++){
//            for(int j = 0; j < sqlSimilar.length ; j++){
//                System.out.print(String.format("%.2f",traceSimilar[i][j]).toString() + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//        for(int i = 0; i < sqlSimilar.length ; i++){
//            for(int j = 0; j < sqlSimilar.length ; j++){
//                System.out.print(String.format("%.2f",scenarioSimilar[i][j]).toString() + " ");
//            }
//            System.out.println();
//        }

        for(Set<ShareTable> set : group){
            Set<ShareTable> relyOnShareTable = new HashSet<>();
            for(ShareTable shareTable : set) {
                for (int i = number; i < shareTables.size(); i++) {
                    log.info(shareTable.getTable().getTableName() + " " + shareTables.get(i).getTable().getTableName());
                    if(relyOn(shareTables.indexOf(shareTable), i, sqlSimilar, traceSimilar, scenarioSimilar, set, shareTables)){
                        log.info("adddddddddddddd");
                        relyOnShareTable.add(shareTables.get(i));
                    }
                }
            }
            if(relyOnShareTable.size() > 0) {
                set.addAll(relyOnShareTable);
            }
        }

        for (Set<ShareTable> set : group){
            log.info("Group:");
            for(ShareTable shareTable : set){
                log.info(shareTable.getTable().getTableName());
            }
        }

        return group;
    }

    private int shareTableCount(int tableCount){
        if(tableCount > 150){
            return (int) Math.ceil(tableCount * 0.08);
        }
        //拟合曲线
        return (int) Math.ceil(tableCount * (-2.648790319632655 * Math.pow(10, -12) * Math.pow(tableCount, 6) + 1.2157745273003864 * Math.pow(10, -9) * Math.pow(tableCount, 5) - 2.1024376321144118 * Math.pow(10, -7) * Math.pow(tableCount, 4)+ 0.000016781858324772667 * Math.pow(tableCount, 3) - 0.0005827489400527467 * Math.pow(tableCount, 2)+ 0.003528621097963147 * tableCount+ 0.3081903336705743));
    }

    /**
     * 判断a，b两表是否相似
     * @param sqlTwoWayRelation
     * @param traceTwoWayRelation
     * @return
     */
    private boolean similar(TwoWayRelation sqlTwoWayRelation, TwoWayRelation traceTwoWayRelation, TwoWayRelation scenarioTwoWayRelation){
        boolean isSimilar = false;
//      SQL级别相似
        if(sqlTwoWayRelation.getAToB() + sqlTwoWayRelation.getBToA() > 1.4){
            isSimilar = true;
        }
//      Trace级别相似
        if(traceTwoWayRelation.getAToB() + traceTwoWayRelation.getBToA() > 1.6){
            isSimilar = true;
        }
//      Scenario级别相似
        if(scenarioTwoWayRelation.getAToB() + scenarioTwoWayRelation.getBToA() >= 2.0){
            isSimilar = true;
        }
//      整体相似
        if(sqlTwoWayRelation.getAToB() + sqlTwoWayRelation.getBToA() + traceTwoWayRelation.getAToB() + traceTwoWayRelation.getBToA() > 2.8
                && scenarioTwoWayRelation.getBToA() + scenarioTwoWayRelation.getAToB() > 1.5){
            isSimilar = true;
        }
        return isSimilar;
    }

    /**
     * 判断表b是否反向依赖表a，ab为表在sharetables中编号。
     * @param a
     * @param b
     * @return
     */
    private boolean relyOn(int a, int b, double[][] sqlSimilar, double[][] traceSimilar, double[][] scenarioSimilar, Set<ShareTable> set, List<ShareTable> shareTables){
//        System.out.println("[Notice]:" + a + " " + b);
//        return relyOnSingle(sqlSimilar[b][a], a, b, traceSimilar, scenarioSimilar, set, shareTables) ||
//                relyOnSingle(traceSimilar[b][a], a, b, sqlSimilar, scenarioSimilar, set, shareTables) ||
//                relyOnSingle(scenarioSimilar[b][a], a, b, sqlSimilar, traceSimilar, set, shareTables);
        if(sqlSimilar[b][a] > 0 || traceSimilar[b][a] > 0 || scenarioSimilar[b][a] > 0){
            double s = 0;
            for(int i = 0; i < sqlSimilar.length; i++){
                if(i != a && !set.contains(shareTables.get(i))){
                    s += sqlSimilar[b][i] + traceSimilar[b][i] + scenarioSimilar[b][i];
                }
            }

            return s == 0;
        }
        return false;
    }

//    private boolean relyOnSingle(Double t, int a, int b, double[][] similarA, double[][] similarB, Set<ShareTable> set, List<ShareTable> shareTables){
//        if(t == 1){
//            double s = 0;
//            for(int i = 0; i < similarA.length; i++){
//                if(i != a && !set.contains(shareTables.get(i))) {
//                    s += similarA[b][i];
//                    s += similarB[b][i];
//                }
//            }
//            return s == 0;
//        }
//        return false;
//    }


}
