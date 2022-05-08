package com.example.scrt.controller;


import com.example.scrt.entity.ReturnMessage;
import com.example.scrt.service.block.BlockService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping(value = "/api", method = RequestMethod.POST)
public class DMSC {
    @Autowired
    private BlockService blockServer;
//    arguments[0] = (String) map.get("deviceWalle");
//    arguments[1] = (String) map.get("deviceRole");
//    arguments[2] = (String) map.get("devicePublicKey");
//    arguments[3] = (String) map.get("deviceSign");
//    arguments[4] = (String) map.get("newdeviceSign");
        //设备注册
        @CrossOrigin
        @GetMapping(value = "deviceLogin",produces = "application/json;charset=utf-8")
        public ReturnMessage deviceLogin(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
            JSONObject jsonObject = new JSONObject(requesyBody);
            ReturnMessage returnMessage=new ReturnMessage();
            String peer_name ="peer0";
            String deviceWalle = jsonObject.getString("deviceWalle");
            String deviceSign = jsonObject.getString("deviceSign");
            HashMap map = new HashMap();
            map.put("0",deviceWalle);
            map.put("1",deviceSign);
            map.put("2","");
            map.put("3","");
            map.put("4","");
            try {
                return blockServer.DMSCinvoke(map, peer_name,"deviceLogin");
            }catch (Exception e){
                returnMessage.setMessage(e.toString());
                returnMessage.setStatus(0);
                return returnMessage;
            }
        }
            //设备注册
    @CrossOrigin
    @GetMapping(value = "deviceRegister",produces = "application/json;charset=utf-8")
    public ReturnMessage deviceRegister(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        ReturnMessage returnMessage=new ReturnMessage();
        try {
        JSONObject jsonObject = new JSONObject(requesyBody);
        //String peer_name = jsonObject.getString("peer_name");
        Random random = new Random();
        String peer_name ="peer0";
        String deviceWalle = jsonObject.getString("deviceWalle");
        String deviceRole = jsonObject.getString("deviceRole");
        String devicePublicKey = jsonObject.getString("devicePublicKey");
        String deviceSign = jsonObject.getString("deviceSign");
        HashMap map = new HashMap();
        map.put("0",deviceWalle);
        map.put("1",deviceRole);
        map.put("2",devicePublicKey);
        map.put("3",deviceSign);
        map.put("4","");
            try {
                return blockServer.DMSCinvoke(map, peer_name,"deviceRegister");
            }catch (Exception e){
                returnMessage.setMessage(e.toString());
                returnMessage.setStatus(0);
                return returnMessage;
            }
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }


    }

    //设备信息更新
    @CrossOrigin
    @GetMapping(value = "deviceUpdate",produces = "application/json;charset=utf-8")
    public ReturnMessage deviceUpdate(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        Random random = new Random();
        String peer_name ="peer0";

        String deviceWalle = jsonObject.getString("oldDeviceWalle");
        String neWdeviceWalle = jsonObject.getString("newDeviceWalle");
        String newDeviceRole = jsonObject.getString("newDeviceRole");
        String newDevicePublicKey = jsonObject.getString("newDevicePublicKey");
        String deviceSign = jsonObject.getString("deviceSign");

        HashMap map = new HashMap();
        map.put("0",deviceWalle);
        map.put("1",neWdeviceWalle);
        map.put("2",newDeviceRole);
        map.put("3",newDevicePublicKey);
        map.put("4",deviceSign);
        try {
            return blockServer.DMSCinvoke(map, peer_name,"deviceUpdate");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }
    //设备信息查询
    @CrossOrigin
    @GetMapping(value = "deviceQuery",produces = "application/json;charset=utf-8")
    public String deviceQuery(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        String peer_name ="peer0";
        String deviceWalle = jsonObject.getString("deviceWalle");
        HashMap map = new HashMap();
        map.put("1",deviceWalle);
        try {
            JSONObject re=blockServer.DMSCquery(map, peer_name,"deviceQuery");
            System.out.println(re);
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
    @GetMapping(value = "deviceDelete",produces = "application/json;charset=utf-8")
    public ReturnMessage deleteauthority(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        String peer_name = jsonObject.getString("peer_name");
        String deviceWalle = jsonObject.getString("deviceWalle");
        HashMap map = new HashMap();
        map.put("0",deviceWalle);
        map.put("1","");
        map.put("2","");
        map.put("3","");
        map.put("4","");
        try {
            return blockServer.DMSCinvoke(map, peer_name,"deleteDevice");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }



    //设备间信息转账
    @CrossOrigin
    @GetMapping(value = "transferAsset",produces = "application/json;charset=utf-8")
    public ReturnMessage transferAsset(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        Random random = new Random();
        String peer_name ="peer1";
        String deviceWalle = jsonObject.getString("deviceWalle");
        String fund = jsonObject.getString("fund");
        String peeraWalle = jsonObject.getString("peeraWalle");
        String sign = jsonObject.getString("sign");
        HashMap map = new HashMap();
        map.put("0",deviceWalle);
        map.put("1",peeraWalle);
        map.put("2",fund);
        map.put("3",sign);
        map.put("4","");
        try {
            return blockServer.DMSCinvoke(map, peer_name,"transferAsset");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }


    //管理员初始化金额
    @CrossOrigin
    @GetMapping(value = "manage",produces = "application/json;charset=utf-8")
    public ReturnMessage manage(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        Random random = new Random();
        String peer_name ="peer1";
        String deviceWalle = jsonObject.getString("deviceWalle");
        String deviceRole = jsonObject.getString("fund");
        HashMap map = new HashMap();
        map.put("0",deviceWalle);
        map.put("1",deviceRole);
        map.put("2","");
        map.put("3","");
        map.put("4","");
        try {
            return blockServer.DMSCinvoke(map, peer_name,"transfertoAB");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }
}




