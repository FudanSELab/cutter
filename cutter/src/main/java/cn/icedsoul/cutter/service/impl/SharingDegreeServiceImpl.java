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
    public List<Set<ShareTable>> shareCalculate(int k) {

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
            log.info(shareTable.getTable().getTableName() + " " + (shareTable.getSqlShare() * 0.1 + shareTable.getCTraceTypeShare() * 0.5  + shareTable.getScenarioShare()));
        }
        int number = (int) Math.ceil( (double)k * 0.4);
        List<Set<ShareTable>> group = new ArrayList<>();
        for(int i = 0; i < number; i++){
            Set<ShareTable> set = new HashSet<>();
            set.add(shareTables.get(i));
            group.add(set);
        }
        for(int i = 0; i < number; i++){
            ShareTable table1 = shareTables.get(i);
            for(int j = i + 1; j < number; j++){
                ShareTable table2 = shareTables.get(j);
                if(similar(table1.getTable(), table2.getTable())){
                    Set<ShareTable> set1 = new HashSet<>();
                    Set<ShareTable> set2 = new HashSet<>();
                    for(Set<ShareTable> set : group){
                        if(set.contains(table1)){
                            set1 = set;
                        }
                        if(set.contains(table2)){
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

        for (Set<ShareTable> set : group){
            log.info("Group:");
            for(ShareTable shareTable : set){
                log.info(shareTable.getTable().getTableName());
            }
        }
        return group;
    }


    private boolean similar(Table a, Table b){
        TwoWayRelation sqlTwoWayRelation = weightCalculationService.calculateSqlSimilar(a, b);
        TwoWayRelation traceTwoWayRelation = weightCalculationService.calculateTraceSimilar(a, b);
        TwoWayRelation scenarioTwoWayRelation = weightCalculationService.calculateScenarioSimilar(a, b);
//        log.info(a.getTableName() + " " + b.getTableName() + ": " + sqlTwoWayRelation.getAToB() + " " + sqlTwoWayRelation.getBToA() + " " + aToBTraceSimilar + " " + bToATraceSimilar);
        //TODO 优化
        boolean isSimilar = (sqlTwoWayRelation.getAToB() >= 0.8 || sqlTwoWayRelation.getBToA() >= 0.8) &&
                (traceTwoWayRelation.getAToB() >= 0.8 || traceTwoWayRelation.getBToA() >= 0.8);
        return isSimilar;
    }


}
