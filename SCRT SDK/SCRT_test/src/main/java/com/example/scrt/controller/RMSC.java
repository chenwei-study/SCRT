package com.example.scrt.controller;


import com.example.scrt.entity.ListPageUtil;
import com.example.scrt.entity.ReturnMessage;
import com.example.scrt.service.block.BlockService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(value = "/api", method = RequestMethod.POST)
public class RMSC {
    @Autowired
    private BlockService blockServer;

    @CrossOrigin
    @GetMapping(value = "resourcePublish",produces = "application/json;charset=utf-8")
    public ReturnMessage resourcePublish(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String Walle = jsonObject.getString("Walle");
        String AppName = jsonObject.getString("AppName");
        String Cost = jsonObject.getString("Cost");
        String CurrentCpu = jsonObject.getString("CurrentCpu");
        String CurrentMemory = jsonObject.getString("CurrentMemory");
        String Deposit = jsonObject.getString("Deposit");
        String Bandwidth = jsonObject.getString("Bandwidth");
        String deviceSign = jsonObject.getString("DeviceSign");
        String address = jsonObject.getString("Address");
        String challengeDuration = jsonObject.getString("ChallengeDuration");
        String ip = jsonObject.getString("Ip");
        String Port = jsonObject.getString("Port");
        String Mac = jsonObject.getString("Mac");
        HashMap map = new HashMap();
        map.put("0",Walle);
        map.put("1",deviceSign);
        map.put("2",AppName);
        map.put("3",Cost);
        map.put("4",CurrentCpu);
        map.put("5",CurrentMemory);
        map.put("6",Deposit);
        map.put("7",Bandwidth);
        map.put("8",address);
        map.put("9",challengeDuration);
        map.put("10",ip);
        map.put("11",Port);
        map.put("12",Mac);
        try {
            return blockServer.RMSCinvoke(map, peer_name,"resourcePublish");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }


    @CrossOrigin
    @GetMapping(value = "resourceQuery",produces = "application/json;charset=utf-8")
    public String deviceQuery(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        String peer_name ="peer0";
        String deviceWalle = jsonObject.getString("Walle");
        String AppName = jsonObject.getString("AppName");
        String Cost = jsonObject.getString("Cost");
        String address = jsonObject.getString("Address");
        HashMap map = new HashMap();
        map.put("0",deviceWalle);
        map.put("1",AppName);
        map.put("2",Cost);
        map.put("3",address);
        try {

            JSONObject re=blockServer.RMSCquery(map, peer_name,"resourceQuery");
            return re.toString();
        }catch (Exception e){
            JSONObject rejson = new JSONObject();
            rejson.put("status", 1);
            rejson.put("message", "没有该匹配项");
            return rejson.toString();
        }
    }


    //设备信息删除
    @CrossOrigin
    @GetMapping(value = "resourceDelete",produces = "application/json;charset=utf-8")
    public ReturnMessage resourceDelete(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        String peer_name = jsonObject.getString("peer_name");
        String deviceWalle = jsonObject.getString("deviceWalle");
        HashMap map = new HashMap();
        map.put("deviceWalle",deviceWalle);
        map.put("deviceRole","");
        map.put("devicePublicKey","");
        map.put("deviceSign","");
        try {
            return blockServer.DMSCinvoke(map, peer_name,"deleteDevice");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }




}




