package cn.icedsoul.cutter.controller;

import cn.icedsoul.cutter.service.api.TableCutService;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CutterController {
    @Autowired
    WeightCalculationService weightCalculationService;
    @Autowired
    TableCutService tableCutService;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/addweight", method ={RequestMethod.GET})
    public void addweight(){
        weightCalculationService.addWeight();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/cuttable", method ={RequestMethod.GET})
    public void cutTable(){
        tableCutService.cutTable();
    }

}
