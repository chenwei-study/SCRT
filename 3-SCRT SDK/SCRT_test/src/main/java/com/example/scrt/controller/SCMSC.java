package com.example.scrt.controller;


import com.example.scrt.entity.ReturnMessage;
import com.example.scrt.service.block.BlockService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/api", method = RequestMethod.POST)
public class SCMSC {
    @Autowired
    private BlockService blockServer;

    @CrossOrigin
    @GetMapping(value = "channelOpen",produces = "application/json;charset=utf-8")
    public ReturnMessage channelOpen(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String channelID = jsonObject.getString("ID");
        String wallet_r = jsonObject.getString("wallet_r");
        String wallet_e = jsonObject.getString("wallet_e");
        String amount = jsonObject.getString("amount");
        String dp = jsonObject.getString("dp");
        String temPk_r = jsonObject.getString("temPk_r");
        String temPk_e = jsonObject.getString("temPk_e");
        String sign_r = jsonObject.getString("sign_r");
        String sign_e = jsonObject.getString("sign_e");
        HashMap map = new HashMap();
        map.put("0",channelID);
        map.put("1",wallet_r);
        map.put("2",wallet_e);
        map.put("3",amount);
        map.put("4",dp);
        map.put("5",temPk_r);
        map.put("6",temPk_e);
        map.put("7",sign_r);
        map.put("8",sign_e);
        try {
            return blockServer.SCMSCchannelOpen(map, peer_name,"channelOpen");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }


    @CrossOrigin
    @GetMapping(value = "disputeCreate",produces = "application/json;charset=utf-8")
    public ReturnMessage disputeCreate(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("AC1ID");
        String AC1Noce = jsonObject.getString("AC1Noce");
        String AC1cost = jsonObject.getString("AC1cost");
        String AC1fb_r = jsonObject.getString("AC1fb_r");
        String AC1fb_e = jsonObject.getString("AC1fb_e");
        String AC1Data = jsonObject.getString("AC1Data");
        String AC1Hash = jsonObject.getString("AC1Hash");
        String AC1Sign = jsonObject.getString("AC1Sign");


        String AC2ID = jsonObject.getString("AC2ID");
        String AC2Noce = jsonObject.getString("AC2Noce");
        String AC2cost = jsonObject.getString("AC2cost");
        String AC2fb_r = jsonObject.getString("AC2fb_r");
        String AC2fb_e = jsonObject.getString("AC2fb_e");
        String AC2Data = jsonObject.getString("AC2Data");
        String AC2Hash = jsonObject.getString("AC2Hash");
        String AC2Sign = jsonObject.getString("AC2Sign");


        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",AC1Noce);
        map.put("2",AC1cost);
        map.put("3",AC1fb_r);
        map.put("4",AC1fb_e);
        map.put("5",AC1Data);
        map.put("6",AC1Hash);
        map.put("7",AC1Sign);


        map.put("8",AC2ID);
        map.put("9",AC2Noce);
        map.put("10",AC2cost);
        map.put("11",AC2fb_r);
        map.put("12",AC2fb_e);
        map.put("13",AC2Data);
        map.put("14",AC2Hash);
        map.put("15",AC2Sign);
        map.put("16","");
        try {
            return blockServer. SCMSCdisputeCreateandunilateralClose(map, peer_name,"disputeCreate");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }


    //设备信息删除
    @CrossOrigin
    @GetMapping(value = "disputeResolve",produces = "application/json;charset=utf-8")
    public ReturnMessage disputeResolve(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("AC1ID");
        String AC1Noce = jsonObject.getString("AC1Noce");
        String AC1cost = jsonObject.getString("AC1cost");
        String AC1fb_r = jsonObject.getString("AC1fb_r");
        String AC1fb_e = jsonObject.getString("AC1fb_e");
        String AC1Data = jsonObject.getString("AC1Data");
        String AC1Hash = jsonObject.getString("AC1Hash");
        String AC1Sign = jsonObject.getString("AC1Sign");
        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",AC1Noce);
        map.put("2",AC1cost);
        map.put("3",AC1fb_r);
        map.put("4",AC1fb_e);
        map.put("5",AC1Data);
        map.put("6",AC1Hash);
        map.put("7",AC1Sign);
        map.put("8","");
        try {
            return blockServer.SCMSCdisputeResolve(map, peer_name,"disputeResolve");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }


    @CrossOrigin
    @GetMapping(value = "disputeRefute",produces = "application/json;charset=utf-8")
    public ReturnMessage disputeRefute(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("AC1ID");
        String AC1Noce = jsonObject.getString("AC1Noce");
        String AC1cost = jsonObject.getString("AC1cost");
        String AC1fb_r = jsonObject.getString("AC1fb_r");
        String AC1fb_e = jsonObject.getString("AC1fb_e");
        String AC1Data = jsonObject.getString("AC1Data");
        String AC1Hash = jsonObject.getString("AC1Hash");
        String AC1Sign = jsonObject.getString("AC1Sign");

        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",AC1Noce);
        map.put("2",AC1cost);
        map.put("3",AC1fb_r);
        map.put("4",AC1fb_e);
        map.put("5",AC1Data);
        map.put("6",AC1Hash);
        map.put("7",AC1Sign);
        map.put("8","");
        try {
            return blockServer.SCMSCdisputeResolve(map, peer_name,"disputeRefute");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }




    @CrossOrigin
    @GetMapping(value = "temporaryState",produces = "application/json;charset=utf-8")
    public ReturnMessage temporaryState(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("AC1ID");
        String AC1Noce = jsonObject.getString("AC1Noce");
        String AC1cost = jsonObject.getString("AC1cost");
        String AC1fb_r = jsonObject.getString("AC1fb_r");
        String AC1fb_e = jsonObject.getString("AC1fb_e");
        String AC1Data = jsonObject.getString("AC1Data");
        String AC1Hash = jsonObject.getString("AC1Hash");
        String AC1Sign = jsonObject.getString("AC1Sign");


        String Identity = jsonObject.getString("Identity");

        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",AC1Noce);
        map.put("2",AC1cost);
        map.put("3",AC1fb_r);
        map.put("4",AC1fb_e);
        map.put("5",AC1Data);
        map.put("6",AC1Hash);
        map.put("7",AC1Sign);
        map.put("8",Identity);
        try {
            return blockServer.SCMSCdisputeResolve(map, peer_name,"temporaryState");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }



    @CrossOrigin
    @GetMapping(value = "unilateralClose",produces = "application/json;charset=utf-8")
    public ReturnMessage unilateralClose(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
            JSONObject jsonObject = new JSONObject(requesyBody);
            ReturnMessage returnMessage=new ReturnMessage();
            //String peer_name = jsonObject.getString("peer_name");
            String peer_name ="peer0";
            String AC1ID = jsonObject.getString("AC1ID");
            String AC1Noce = jsonObject.getString("AC1Noce");
            String AC1cost = jsonObject.getString("AC1cost");
            String AC1fb_r = jsonObject.getString("AC1fb_r");
            String AC1fb_e = jsonObject.getString("AC1fb_e");
            String AC1Data = jsonObject.getString("AC1Data");
            String AC1Hash = jsonObject.getString("AC1Hash");
            String AC1Sign = jsonObject.getString("AC1Sign");


            String AC2ID = jsonObject.getString("AC2ID");
            String AC2Noce = jsonObject.getString("AC2Noce");
            String AC2cost = jsonObject.getString("AC2cost");
            String AC2fb_r = jsonObject.getString("AC2fb_r");
            String AC2fb_e = jsonObject.getString("AC2fb_e");
            String AC2Data = jsonObject.getString("AC2Data");
            String AC2Hash = jsonObject.getString("AC2Hash");
            String AC2Sign = jsonObject.getString("AC2Sign");


             String Identity = jsonObject.getString("Identity");

            HashMap map = new HashMap();
            map.put("0",AC1ID);
            map.put("1",AC1Noce);
            map.put("2",AC1cost);
            map.put("3",AC1fb_r);
            map.put("4",AC1fb_e);
            map.put("5",AC1Data);
            map.put("6",AC1Hash);
            map.put("7",AC1Sign);


            map.put("8",AC2ID);
            map.put("9",AC2Noce);
            map.put("10",AC2cost);
            map.put("11",AC2fb_r);
            map.put("12",AC2fb_e);
            map.put("13",AC2Data);
            map.put("14",AC2Hash);
            map.put("15",AC2Sign);


            map.put("16",Identity);
            try {
                return blockServer.SCMSCdisputeCreateandunilateralClose(map, peer_name,"unilateralClose");
            }catch (Exception e){
                returnMessage.setMessage(e.toString());
                returnMessage.setStatus(0);
                return returnMessage;
            }
    }
    @CrossOrigin
    @GetMapping(value = "cooperateclose",produces = "application/json;charset=utf-8")
    public ReturnMessage cooperateclose(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("ID");
        String AC1fb_r = jsonObject.getString("fb_r");
        String AC1fb_e = jsonObject.getString("fb_e");
        String AC1Sign = jsonObject.getString("Sign_r");
        String AC1Sign_r = jsonObject.getString("Sign_e");
        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",AC1fb_r);
        map.put("2",AC1fb_e);
        map.put("3",AC1Sign);
        map.put("4",AC1Sign_r);
        map.put("5","");
        map.put("6","");
        map.put("7","");
        map.put("8","");
        try {
            return blockServer.SCMSCdisputeResolve(map, peer_name,"cooperateclose");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }

    @CrossOrigin
    @GetMapping(value = "delate",produces = "application/json;charset=utf-8")
    public ReturnMessage delate(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("ID");
        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1","");
        map.put("2","");
        map.put("3","");
        map.put("4","");
        map.put("5","");
        map.put("6","");
        map.put("7","");
        map.put("8","");
        try {
            return blockServer.SCMSCdisputeResolve(map, peer_name,"deleteChannel");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }
    @CrossOrigin
    @GetMapping(value = "depositsWithdraw",produces = "application/json;charset=utf-8")
    public ReturnMessage depositsWithdraw(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        ReturnMessage returnMessage=new ReturnMessage();
        //String peer_name = jsonObject.getString("peer_name");
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("ID");
        String AC1fb_r = jsonObject.getString("Role");
        String AC1Sign = jsonObject.getString("Sign");
        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",AC1fb_r);
        map.put("2",AC1Sign);
        map.put("3","");
        map.put("4","");
        map.put("5","");
        map.put("6","");
        map.put("7","");
        map.put("8","");
        try {
            return blockServer.SCMSCdisputeResolve(map, peer_name,"depositsWithdraw");
        }catch (Exception e){
            returnMessage.setMessage(e.toString());
            returnMessage.setStatus(0);
            return returnMessage;
        }
    }




    @CrossOrigin
    @GetMapping(value = "stateQuery",produces = "application/json;charset=utf-8")
    public String stateQuery(@RequestBody(required = false) String requesyBody) throws JSONException {     //查询合约
        JSONObject jsonObject = new JSONObject(requesyBody);
        String peer_name ="peer0";
        String AC1ID = jsonObject.getString("ID");
        String wallet_r = jsonObject.getString("wallet_r");
        String wallet_e = jsonObject.getString("wallet_e");
        String state = jsonObject.getString("state");
        HashMap map = new HashMap();
        map.put("0",AC1ID);
        map.put("1",wallet_r);
        map.put("2",wallet_e);
        map.put("3",state);
        try {
            JSONObject re=blockServer.SCMSCquery(map, peer_name,"stateQuery");
            return re.toString();
        }catch (Exception e){
            JSONObject rejson = new JSONObject();
            rejson.put("status", 1);
            rejson.put("message", "没有该匹配项");
            return rejson.toString();
        }
    }




}




