package cn.icedsoul.cutter.controller;

import cn.icedsoul.cutter.service.api.HandleDataService;
import cn.icedsoul.cutter.service.api.TableCutService;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

import java.util.List;
import java.util.Map;

import static cn.icedsoul.cutter.util.Common.isNullString;

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

}
