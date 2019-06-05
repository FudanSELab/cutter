package cn.icedsoul.cutter.controller;

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
        weightCalculationService.addWeight();
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/cut")
    @ApiOperation(value = "Cut table", notes = "Cut table to k parts")
    public Map<Integer, List<String>> cutTable(@RequestParam("k") int k){
        return tableCutService.cutTable(k);
    }

//    @CrossOrigin(origins = "*")
//    @GetMapping(value = "/communitydetection")
//    @ApiOperation(value = "Cut table with", notes = "Cut table with community detection algorithm")
//    public Map<Integer, List<String>> communityDetection(){
//        return tableCutService.communityDetection();
//    }


    @CrossOrigin(origins = "*")
    @GetMapping(value = "/calculateSharingDegree")
    @ApiOperation(value = "calculate sharing degree", notes = "calculate sharing degree")
    public void calculateSharingDegree(){
        sharingDegreeService.calculateSharingDegree();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/clearCloseTo")
    @ApiOperation(value = "clear CloseTo", notes = "clear CloseTo")
    public void clearCloseTo(){
        closeToRepository.deleteAll();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/splitCost")
    @ApiOperation(value = "split cost", notes = "split cost")
    public int[] splitCost(){
        List<List<String>> splitList = new ArrayList<>();
//        splitList.add(Arrays.asList("sys_dict"));
//        splitList.add(Arrays.asList("sys_user", "sys_office", "oa_test_audit", "cms_article", "cms_category", "cms_site", "cms_link",
//                "sys_area", "sys_role", "sys_user_role", "sys_role_office", "sys_role_menu",
//                "gen_table", "gen_scheme",
//                "oa_notify", "sys_menu", "oa_notify_record", "sys_log", "cms_article_data", "cms_comment", "cms_guestbook" ));
        splitList.add(Arrays.asList("sys_user", "sys_office", "oa_test_audit", "cms_article", "cms_category", "cms_site", "cms_link"));
        splitList.add(Arrays.asList("sys_area", "sys_role", "sys_user_role", "sys_role_office", "sys_role_menu"));
        splitList.add(Arrays.asList("gen_table", "gen_scheme", "sys_dict"));
        splitList.add(Arrays.asList("oa_notify", "sys_menu", "oa_notify_record", "sys_log", "cms_article_data", "cms_comment", "cms_guestbook"));
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
//        return null;
        return splitCostService.getSplitCost(idList);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/testSplit")
    @ApiOperation(value = "test split", notes = "test split")
    public void testSplit(@RequestParam("k") int k){

    }

}
