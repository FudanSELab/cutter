package cn.icedsoul.cutter.controller;

import cn.icedsoul.cutter.service.api.HandleDataService;
import cn.icedsoul.cutter.service.api.TableCutService;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * @author icedsoul
 */
@RestController
@Api(value = "controller of cutter")
public class CutterController {

    @Autowired
    HandleDataService handleDataService;

    @Autowired
    WeightCalculationService weightCalculationService;

    @Autowired
    TableCutService tableCutService;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/data")
    @ApiOperation(value = "Read dat file", notes = "Add nodes and relations")
    public void handleData(@PathParam("file")String file){
        handleDataService.handleData(file);
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
    public void cutTable(@PathParam("k") int k){
        tableCutService.cutTable(k);
    }

}
