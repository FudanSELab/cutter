package cn.icedsoul.cutter.controller;

import cn.icedsoul.cutter.domain.bo.ShareTable;
import cn.icedsoul.cutter.domain.bo.SplitCost;
import cn.icedsoul.cutter.domain.bo.SplitProposal;
import cn.icedsoul.cutter.domain.bo.SplitResult;
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
    @PostMapping(value = "/realCut")
    @ApiOperation(value = "Real Cut", notes = "Real Cut")
    //根据外面传过来的共享度高的表做切分
    public SplitResult realCut(@RequestParam("k") int k, @RequestParam("sharingClusters") List<List<Table>> sharingClusters){
        Map<Integer, List<Table>> proposal = tableCutService.realCut(k, sharingClusters);
        //calculate split cost
        SplitCost cost = calculateSplitCost(proposal);
        SplitResult result = new SplitResult(proposal, cost);

        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/adjustWeightAndCut")
    @ApiOperation(value = "Cut table3", notes = "Adjust the weight of tables that have high sharing degree and then cut table")
    public SplitResult cutTable3(@RequestParam("k") int k){
        Map<Integer, List<Table>> proposal = tableCutService.cutTable3(k);
        //calculate split cost
        SplitCost cost = calculateSplitCost(proposal);
        SplitResult result = new SplitResult(proposal, cost);

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

        SplitResult result = new SplitResult(proposal, cost);
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

        SplitResult result = new SplitResult(proposal, cost);
        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/splitCost")
    @ApiOperation(value = "split cost", notes = "split cost")
    public SplitCost splitCost(@RequestParam("idList") List<List<Long>> idList){
        return splitCostService.getSplitCost(idList);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/splitProposal")
    @ApiOperation(value = "split proposal", notes = "split proposal")
    //总体的代码拆分指导，包含了不拆分的代码归属
    //要在调用了splitCost之后使用
    public SplitProposal splitProposal(@RequestParam("idList") List<List<Long>> idList){
        return splitCostService.getCodeSplitProposal();
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "/share")
    @ApiOperation(value = "share", notes = "share")
    public List<List<Table>> calShare(){
        List<Set<ShareTable>> shareTables = sharingDegreeService.shareCalculate();
        //将ShareTable转化为Table
        List<List<Table>> result = shareTableToTable(shareTables);
        return result;
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

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/allTables")
    @ApiOperation(value = "Get All Tables", notes = "Get All Tables")
    public List<Table> allTables(){
        List<Table> tableList = (List)tableRepository.findAll();
        return tableList;
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
