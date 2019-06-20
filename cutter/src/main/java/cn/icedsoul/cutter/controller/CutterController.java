package cn.icedsoul.cutter.controller;

import cn.icedsoul.cutter.domain.bo.ShareTable;
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

    //测试完后才能需要移除
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
    public Map<Integer, List<Table>> cutTable3(@RequestParam("k") int k){
        Map<Integer, List<Table>> result = tableCutService.cutTable3(k);

        //calculate split cost
        calculateSplitCost(result);

        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/extractAndCut")
    @ApiOperation(value = "Cut table2", notes = "Extract tables that sharing degree are high and then cut table")
    public Map<Integer, List<Table>> cutTable2(@RequestParam("k") int k){
        Map<Integer, List<Table>> result = tableCutService.cutTable2(k);
        System.out.println("=====FINAL RESULT:=====");
        for(int key: result.keySet()){
            System.out.println("第"+ key + "组：" + result.get(key).stream().map(r -> r.getTableName()).collect(Collectors.toList()));
        }
        System.out.println("========================");

        //calculate split cost
        calculateSplitCost(result);

        return result;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/cutAndExtract")
    @ApiOperation(value = "Cut table", notes = "Cut tables and then extract tables that sharing degree are high")
    public Map<Integer, List<Table>> cutTable(@RequestParam("k") int k){
        Map<Integer, List<Table>> cutClusters = tableCutService.cutTable(k);
        List<Set<ShareTable>> sharingClusters = sharingDegreeService.shareCalculate(12);

        Map<Integer, List<Table>> result  = new HashMap<>();
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
            result.put(i, tempTables);
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
                result.put(i, tempTables);
                i++;
            }
        }

        //calculate split cost
        calculateSplitCost(result);

        return result;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/splitCost")
    @ApiOperation(value = "split cost", notes = "split cost")
    public int[] splitCost(){
        List<List<String>> splitList = new ArrayList<>();
        splitList.add(Arrays.asList("sys_dict"));
        splitList.add(Arrays.asList("sys_user", "sys_office", "oa_test_audit", "cms_article", "cms_category", "cms_site", "cms_link",
                "sys_area", "sys_role", "sys_user_role", "sys_role_office", "sys_role_menu",
                "gen_table", "gen_scheme",
                "oa_notify", "sys_menu", "oa_notify_record", "sys_log", "cms_article_data", "cms_comment", "cms_guestbook" ));
//        splitList.add(Arrays.asList("sys_user", "sys_office", "oa_test_audit", "cms_article", "cms_category", "cms_site", "cms_link"));
//        splitList.add(Arrays.asList("sys_area", "sys_role", "sys_user_role", "sys_role_office", "sys_role_menu"));
//        splitList.add(Arrays.asList("gen_table", "gen_scheme", "sys_dict"));
//        splitList.add(Arrays.asList("oa_notify", "sys_menu", "oa_notify_record", "sys_log", "cms_article_data", "cms_comment", "cms_guestbook"));
        List<List<Long>> idList = new ArrayList<>();
        for(List<String> l:splitList){
            List<Long> ids = new ArrayList<>();
            for(String s:l){
                ids.add(tableRepository.findByDatabaseNameAndAndTableName("jeesite_mybatis",s).getId());
            }
            idList.add(ids);
        }
        System.out.println("---idList:---");
        System.out.println(idList);
        return splitCostService.getSplitCost(idList);
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "/share")
    @ApiOperation(value = "share", notes = "share")
    public List<Set<ShareTable>> calShare(@RequestParam("k") int k){
        return sharingDegreeService.shareCalculate(k);
    }

    //calculate split cost
    private void calculateSplitCost(Map<Integer, List<Table>> result){
        List<List<Long>> idList = new ArrayList<>();
        for(int groupNum: result.keySet()){
            List<Table> tables = result.get(groupNum);
            List<Long> ids = tables.stream().map(t -> t.getId()).collect(Collectors.toList());
            idList.add(ids);
        }
        splitCostService.getSplitCost(idList);
    }

}
