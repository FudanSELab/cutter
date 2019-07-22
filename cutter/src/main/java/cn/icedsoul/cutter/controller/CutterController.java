package cn.icedsoul.cutter.controller;

import cn.icedsoul.cutter.domain.bo.*;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.MethodRepository;
import cn.icedsoul.cutter.repository.SqlRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

import static cn.icedsoul.cutter.util.Common.isNullString;

/**
 * @author icedsoul
 */
@RestController
@Log
@Api(value = "controller of cutter")
public class CutterController {

    @Autowired
    HandleDataService handleDataService;
    @Autowired
    WeightCalculationService weightCalculationService;
    @Autowired
    TableCutService tableCutService;
    @Autowired
    SharingDegreeService sharingDegreeService;
    @Autowired
    SplitCostService splitCostService;

    //测试完后才能移除
    @Autowired
    CloseToRepository closeToRepository;
    @Autowired
    TableRepository tableRepository;
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    SqlRepository sqlRepository;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/data")
    @ApiOperation(value = "Read dat file", notes = "Add nodes and relations")
    public void handleData(@RequestParam("file") String file){
        if(!isNullString(file)){
            handleDataService.handleData(file);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/weight")
    @ApiOperation(value = "Add weight", notes = "Add weight")
    public void addWeight(){
//        weightCalculationService.addWeight();
        weightCalculationService.addSimilarWeight();
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/adjustWeightAndCut")
    @ApiOperation(value = "Cut table3", notes = "Adjust the weight of tables that have high sharing degree and then cut table")
    public SplitResult cutTable3(@RequestParam("k") int k){
        Map<Integer, List<Table>> proposal = tableCutService.cutTable3(k);
        //calculate split cost
        SplitCost cost = calculateSplitCost(proposal);
        SplitGranularity splitGranularity = new SplitGranularity(tableCutService.getCurServiceNum(), tableCutService.getMaxServiceNum());
        SplitResult result = new SplitResult(proposal, splitGranularity, cost, tableCutService.getCostProportion());

        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/extractAndCut")
    @ApiOperation(value = "Cut table2", notes = "Extract tables that sharing degree are high and then cut table")
    public SplitResult cutTable2(@RequestParam("k") int k){
        Map<Integer, List<Table>> proposal = tableCutService.cutTable2(k);
        System.out.println("=====FINAL RESULT:=====");
        for(int key: proposal.keySet()){
            System.out.println("第"+ key + "组：" + proposal.get(key).stream().map(r -> r.getTableName()).collect(Collectors.toList()));
        }
        System.out.println("========================");

        //calculate split cost
        SplitCost cost = calculateSplitCost(proposal);
        SplitGranularity splitGranularity = new SplitGranularity(tableCutService.getCurServiceNum(), tableCutService.getMaxServiceNum());
        SplitResult result = new SplitResult(proposal, splitGranularity, cost, tableCutService.getCostProportion());
        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/cutAndExtract")
    @ApiOperation(value = "Cut table", notes = "Cut tables and then extract tables that sharing degree are high")
    public SplitResult cutTable(@RequestParam("k") int k){
        Map<Integer, List<Table>> cutClusters = tableCutService.cutTable(k);
        List<Set<ShareTable>> sharingClusters = sharingDegreeService.shareCalculate();

        Map<Integer, List<Table>> proposal  = new HashMap<>();
        int i = 1;
        Set<String> usedTables = new HashSet<>();
        System.out.println("=====FINAL RESULT:=====");
        for(Set<ShareTable> set: sharingClusters){
            List<Table> tempTables = new ArrayList<>();
            for(ShareTable t: set){
                tempTables.add(t.getTable());
                usedTables.add(t.getTable().getTableName());
            }
            System.out.println("第"+ i + "组：" + tempTables.stream().map(t -> t.getTableName()).collect(Collectors.toList()));
            proposal.put(i, tempTables);
            i++;
        }
        for(int key: cutClusters.keySet()){
            List<Table> cluster = cutClusters.get(key);
            List<Table> tempTables = new ArrayList<>();
            for(Table s: cluster){
                if(!usedTables.contains(s.getTableName())){
                    tempTables.add(s);
                }
            }
            if(!tempTables.isEmpty()){
                System.out.println("第"+ i + "组：" + tempTables.stream().map(t -> t.getTableName()).collect(Collectors.toList()));
                proposal.put(i, tempTables);
                i++;
            }
        }

        //calculate split cost
        SplitCost cost = calculateSplitCost(proposal);
        SplitGranularity splitGranularity = new SplitGranularity(tableCutService.getCurServiceNum(), tableCutService.getMaxServiceNum());
        SplitResult result = new SplitResult(proposal, splitGranularity, cost, tableCutService.getCostProportion());
        return result;
    }


    /////////////////////////////web page用到的接口////////////////////////////////////////////
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/realCut")
    @ApiOperation(value = "Real Cut", notes = "Real Cut")
    //根据外面传过来的共享度高的表做切分
    public SplitResult realCut(@RequestBody List<List<Table>> sharingTableGroups){
        int k = 0;
        Map<Integer, List<Table>> proposal = tableCutService.realCut(k, sharingTableGroups);
        SplitCost cost = calculateSplitCost(proposal);
        SplitGranularity splitGranularity = new SplitGranularity(tableCutService.getCurServiceNum(), tableCutService.getMaxServiceNum());
        SplitResult result = new SplitResult(proposal, splitGranularity, cost, tableCutService.getCostProportion());
        return result;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/addCostProportion")
    @ApiOperation(value = "Add Cost Proportion", notes = "Add Cost Proportion")
    //拆分开销占比增加0.1
    public SplitResult addCostProportion(){
        Map<Integer, List<Table>> proposal = tableCutService.addCostProportion();
        return composeSplitResult(proposal);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/reduceCostProportion")
    @ApiOperation(value = "Reduce Cost Proportion", notes = "Reduce Cost Proportion")
    //拆分开销占比减小0.1
    public SplitResult reduceCostProportion(){
        Map<Integer, List<Table>> proposal = tableCutService.reduceCostProportion();
        return composeSplitResult(proposal);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/addService")
    @ApiOperation(value = "Add Service", notes = "Add Service")
    //增加服务数量，即划分粒度变小
    public SplitResult addService(@RequestBody int lastServiceNum){
        Map<Integer, List<Table>> proposal = tableCutService.addService(lastServiceNum);
        return composeSplitResult(proposal);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/reduceService")
    @ApiOperation(value = "Reduce Service", notes = "Reduce Service")
    //增加服务数量，即划分粒度变大
    public SplitResult reduceService(@RequestBody int lastServiceNum){
        Map<Integer, List<Table>> proposal = tableCutService.reduceService(lastServiceNum);
        return composeSplitResult(proposal);
    }

    private SplitResult composeSplitResult(Map<Integer, List<Table>> proposal){
        SplitCost cost = calculateSplitCost(proposal);
        SplitGranularity splitGranularity = new SplitGranularity(tableCutService.getCurServiceNum(), tableCutService.getMaxServiceNum());
        SplitResult result = new SplitResult(proposal, splitGranularity, cost, tableCutService.getCostProportion());
        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/splitCost")
    @ApiOperation(value = "split cost", notes = "split cost")
    public SplitCost splitCost(@RequestBody List<List<Long>> idList){
        return splitCostService.getSplitCost(idList);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/splitDetail")
    @ApiOperation(value = "split detail", notes = "split detail")
    //总体的代码拆分指导，包含了不拆分的代码归属
    //要在调用了splitCost之后使用
    public SplitDetail splitDetail(){
        return splitCostService.getCodeSplitDetail();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/splitDetailTree")
    @ApiOperation(value = "split detail tree", notes = "split detail tree")
    //总体的代码拆分指导，树形结构，包含了不拆分的代码归属
    //要在调用了splitCost之后使用!!!!
    public Map<Integer, List<SplitNode>> splitDetailTree(){
        return splitCostService.getCodeSplitDetailTree();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/noTableTree")
    @ApiOperation(value = "return all methods and classes that not related to a table", notes = "no table tree")
    //没有和任何数据表相关联的类和方法
    //要在调用了splitCost之后使用!!!!
    public List<SplitNode> getNoTableTree(){
        return splitCostService.getNoTableTree();
    }


    @CrossOrigin(origins = "*")
    @GetMapping(value = "/share")
    @ApiOperation(value = "share", notes = "share")
    public List<List<Table>> calShare(){
        List<Set<ShareTable>> shareTables = sharingDegreeService.shareCalculate();
        //将ShareTable转化为Table
        List<List<Table>> result = shareTableToTable(shareTables);
        return result;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/allTables")
    @ApiOperation(value = "Get All Tables", notes = "Get All Tables")
    public List<Table> allTables(){
        List<Table> tableList = (List)tableRepository.findAll();
        return tableList;
    }

    //将ShareTable转化为Table
    private List<List<Table>> shareTableToTable(List<Set<ShareTable>> shareTables){
        List<List<Table>> result = new ArrayList<>();
        for(Set<ShareTable> sts: shareTables){
            List<Table> list = new ArrayList<>();
            for(ShareTable st: sts){
                list.add(st.getTable());
            }
            result.add(list);
        }
        return result;
    }

    //calculate split cost
    private SplitCost calculateSplitCost(Map<Integer, List<Table>> result){
        List<List<Long>> idList = new ArrayList<>();
        for(int groupNum: result.keySet()){
            List<Table> tables = result.get(groupNum);
            List<Long> ids = tables.stream().map(t -> t.getId()).collect(Collectors.toList());
            idList.add(ids);
        }
        return splitCostService.getSplitCost(idList);
    }

}
