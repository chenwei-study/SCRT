package com.example.scrt.service.block.Impl;

import com.example.scrt.block.FabricClient;
import com.example.scrt.block.UserContext;
import com.example.scrt.block.UserUtils;
import com.example.scrt.entity.KeyPath;
import com.example.scrt.entity.ReturnMessage;
import com.example.scrt.service.block.BlockService;
import com.example.scrt.block.FabricClient;
import com.example.scrt.block.UserContext;
import org.bouncycastle.crypto.CryptoException;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BlockServiceImpl implements BlockService {



    private static final Logger log = LoggerFactory.getLogger(BlockServiceImpl.class);
    @Autowired
    private KeyPath keyPath;

    //DMSCinvoke
    @Override
    public ReturnMessage DMSCinvoke(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        ReturnMessage returnMessage=new ReturnMessage();
        try {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        System.out.println(keyPath.getKeyFolderPath());
            System.out.println(keyPath.getKeyFileName());
            System.out.println(keyPath.getCertFoldePath());
            System.out.println(keyPath.getCertFileName());
        Enrollment enrollment =  UserUtils.getEnrollment(keyPath.getKeyFolderPath(),keyPath.getKeyFileName(),keyPath.getCertFoldePath(),keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        if(peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if(peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if(peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com","grpcs://peer2.org1.SCRT.com:8051",keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if(peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com","grpcs://peer3.org1.SCRT.com:8056",keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if(peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com","grpcs://peer4.org1.SCRT.com:9051",keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if(peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com","grpcs://peer5.org1.SCRT.com:9056",keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }

        String[] arguments = new String[5];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        arguments[4] = (String) map.get("4");
        Orderer order = fabricClient.getOrderer("orderer0.SCRT.com","grpcs://orderer0.SCRT.com:8050",keyPath.getTlsOrderFilePathurces());
        JSONObject rejson = new JSONObject();
        try {
            Map remap =  fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG,"DMSC",order,peers,method,arguments);
            int code = (int) remap.get("code");
            if (code==1){
                returnMessage.setStatus(1);
                returnMessage.setMessage(remap.get("info").toString());
            }
            if (code==500){
                returnMessage.setStatus(0);
                returnMessage.setMessage((String) remap.get("info"));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnMessage.setStatus(0);
            returnMessage.setMessage("system error");
        }
        }
        catch (Exception e){
            e.printStackTrace();
            returnMessage.setStatus(0);
            returnMessage.setMessage("system error");
        }
        return returnMessage;
    }

    //DMSCquery
    @Override
    public JSONObject DMSCquery(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment = UserUtils.getEnrollment(keyPath.getKeyFolderPath(), keyPath.getKeyFileName(), keyPath.getCertFoldePath(), keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        //   Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051",tlsPeer0FilePath);
        if (peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com", "grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if (peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if (peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com", "grpcs://peer2.org1.SCRT.com:8051", keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if (peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com", "grpcs://peer3.org1.SCRT.com:8056", keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if (peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com", "grpcs://peer4.org1.SCRT.com:9051", keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if (peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com", "grpcs://peer5.org1.SCRT.com:9056", keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[1];
        arguments[0] = (String) map.get("1");
        try {
            Map remap = fabricClient.queryChaincode(peers, "mychannel", TransactionRequest.Type.GO_LANG, "DMSC", method, arguments);
            System.out.println( remap.get("info"));
            int code = (int) remap.get("code");
            JSONObject rejson = new JSONObject();
            if (code == 1) {
                    String data2 = remap.get("info").toString();
                    String data = data2.replaceAll("\\u0000"," ");
                    if (data.equals("[]")) {
                        rejson.put("status", 1);
                        rejson.put("message", "没有该匹配项");
                        return rejson;
                }

                if(data.startsWith("[")){
                    JSONArray jsondata= new JSONArray(data);
                    rejson.put("data", jsondata);
                }else {
                    JSONObject jsondata = new JSONObject(data);
                    rejson.put("data", jsondata);
                }
                rejson.put("status", 1);
                rejson.put("message", "查询成功");

                return rejson;

            }
            if (code == 500) {
                rejson.put("status", 0);
                rejson.put("message", (String)remap.get("info"));
                return rejson;
            }
            rejson.put("status", 404);
            rejson.put("message", "错误");
            return rejson;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", 0);
            error.put("message", "错误");
            return error;
        }



    }



    //RMSCinvoke
    @Override
    public ReturnMessage RMSCinvoke(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment =  UserUtils.getEnrollment(keyPath.getKeyFolderPath(),keyPath.getKeyFileName(),keyPath.getCertFoldePath(),keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        if(peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if(peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if(peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com","grpcs://peer2.org1.SCRT.com:8051",keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if(peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com","grpcs://peer3.org1.SCRT.com:8056",keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if(peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com","grpcs://peer4.org1.SCRT.com:9051",keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if(peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com","grpcs://peer5.org1.SCRT.com:9056",keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[13];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        arguments[4] = (String) map.get("4");
        arguments[5] = (String) map.get("5");
        arguments[6] = (String) map.get("6");
        arguments[7] = (String) map.get("7");
        arguments[8] = (String) map.get("8");
        arguments[9] = (String) map.get("9");
        arguments[10] = (String) map.get("10");
        arguments[11] = (String) map.get("11");
        arguments[12] = (String) map.get("12");
        Orderer order = fabricClient.getOrderer("orderer0.SCRT.com","grpcs://orderer0.SCRT.com:8050",keyPath.getTlsOrderFilePathurces());
        ReturnMessage returnMessage=new ReturnMessage();
        JSONObject rejson = new JSONObject();
        try {
            Map remap =  fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG,"RMSC",order,peers,method,arguments);
            int code = (int) remap.get("code");
            if (code==1){
                returnMessage.setStatus(1);
                returnMessage.setMessage(remap.get("info").toString());
            }
            if (code==500){
                returnMessage.setStatus(0);
                returnMessage.setMessage((String) remap.get("info"));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnMessage.setStatus(0);
            returnMessage.setMessage("system error");
        }
        return returnMessage;
    }

    //RMSCquery
    @Override
    public JSONObject RMSCquery(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment = UserUtils.getEnrollment(keyPath.getKeyFolderPath(), keyPath.getKeyFileName(), keyPath.getCertFoldePath(), keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        //   Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051",tlsPeer0FilePath);
        if (peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com", "grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if (peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if (peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com", "grpcs://peer2.org1.SCRT.com:8051", keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if (peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com", "grpcs://peer3.org1.SCRT.com:8056", keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if (peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com", "grpcs://peer4.org1.SCRT.com:9051", keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if (peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com", "grpcs://peer5.org1.SCRT.com:9056", keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[4];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        try {
            Map remap = fabricClient.queryChaincode(peers, "mychannel", TransactionRequest.Type.GO_LANG, "RMSC", method, arguments);
            int code = (int) remap.get("code");
            JSONObject rejson = new JSONObject();
            if (code == 1) {
                String data2 = remap.get("info").toString();
                String data = data2.replaceAll("\\u0000"," ");
                if (data.equals("[]")) {
                    rejson.put("status", 1);
                    rejson.put("message", "没有该匹配项");
                    return rejson;
                }
                if(data.startsWith("[")){
                    JSONArray jsondata= new JSONArray(data);
                    rejson.put("data", jsondata);
                }else {
                    JSONObject jsondata = new JSONObject(data);
                    rejson.put("data", jsondata);
                }
                rejson.put("status", 1);
                rejson.put("message", "查询成功");

                return rejson;

            }
            if (code == 500) {
                rejson.put("status", 0);
                rejson.put("message", (String)remap.get("info"));
                return rejson;
            }
            rejson.put("status", 404);
            rejson.put("message", "错误");
            return rejson;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", 0);
            error.put("message", "错误");
            return error;
        }



    }



    //SCMSCchannelOpen
    @Override
    public ReturnMessage SCMSCchannelOpen(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment =  UserUtils.getEnrollment(keyPath.getKeyFolderPath(),keyPath.getKeyFileName(),keyPath.getCertFoldePath(),keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        if(peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if(peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if(peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com","grpcs://peer2.org1.SCRT.com:8051",keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if(peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com","grpcs://peer3.org1.SCRT.com:8056",keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if(peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com","grpcs://peer4.org1.SCRT.com:9051",keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if(peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com","grpcs://peer5.org1.SCRT.com:9056",keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[9];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        arguments[4] = (String) map.get("4");
        arguments[5] = (String) map.get("5");
        arguments[6] = (String) map.get("6");
        arguments[7] = (String) map.get("7");
        arguments[8] = (String) map.get("8");
        Orderer order = fabricClient.getOrderer("orderer0.SCRT.com","grpcs://orderer0.SCRT.com:8050",keyPath.getTlsOrderFilePathurces());
        ReturnMessage returnMessage=new ReturnMessage();
        JSONObject rejson = new JSONObject();
        try {
            Map remap =  fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG,"SCMSC",order,peers,method,arguments);
            int code = (int) remap.get("code");
            if (code==1){
                returnMessage.setStatus(1);
                returnMessage.setMessage(remap.get("info").toString());
            }
            if (code==500){
                returnMessage.setStatus(0);
                returnMessage.setMessage((String) remap.get("info"));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnMessage.setStatus(0);
            returnMessage.setMessage("system error");
        }
        return returnMessage;
    }



    //SCMSCdisputeCreate
    @Override
    public ReturnMessage SCMSCdisputeCreateandunilateralClose(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment =  UserUtils.getEnrollment(keyPath.getKeyFolderPath(),keyPath.getKeyFileName(),keyPath.getCertFoldePath(),keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        if(peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if(peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if(peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com","grpcs://peer2.org1.SCRT.com:8051",keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if(peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com","grpcs://peer3.org1.SCRT.com:8056",keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if(peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com","grpcs://peer4.org1.SCRT.com:9051",keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if(peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com","grpcs://peer5.org1.SCRT.com:9056",keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[17];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        arguments[4] = (String) map.get("4");
        arguments[5] = (String) map.get("5");
        arguments[6] = (String) map.get("6");
        arguments[7] = (String) map.get("7");
        arguments[8] = (String) map.get("8");
        arguments[9] = (String) map.get("9");
        arguments[10] = (String) map.get("10");
        arguments[11] = (String) map.get("11");
        arguments[12] = (String) map.get("12");
        arguments[13] = (String) map.get("13");
        arguments[14] = (String) map.get("14");
        arguments[15] = (String) map.get("15");
        arguments[16] = (String) map.get("16");

        Orderer order = fabricClient.getOrderer("orderer0.SCRT.com","grpcs://orderer0.SCRT.com:8050",keyPath.getTlsOrderFilePathurces());
        ReturnMessage returnMessage=new ReturnMessage();
        JSONObject rejson = new JSONObject();
        try {
            Map remap =  fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG,"SCMSC",order,peers,method,arguments);
            int code = (int) remap.get("code");
            if (code==1){
                returnMessage.setStatus(1);
                returnMessage.setMessage(remap.get("info").toString());
            }
            if (code==500){
                returnMessage.setStatus(0);
                returnMessage.setMessage((String) remap.get("info"));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnMessage.setStatus(0);
            returnMessage.setMessage("system error");
        }
        return returnMessage;
    }



    //SCMSCdisputeCreate
    @Override
    public ReturnMessage SCMSCdisputeResolve(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment =  UserUtils.getEnrollment(keyPath.getKeyFolderPath(),keyPath.getKeyFileName(),keyPath.getCertFoldePath(),keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        if(peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if(peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if(peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com","grpcs://peer2.org1.SCRT.com:8051",keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if(peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com","grpcs://peer3.org1.SCRT.com:8056",keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if(peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com","grpcs://peer4.org1.SCRT.com:9051",keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if(peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com","grpcs://peer5.org1.SCRT.com:9056",keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[9];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        arguments[4] = (String) map.get("4");
        arguments[5] = (String) map.get("5");
        arguments[6] = (String) map.get("6");
        arguments[7] = (String) map.get("7");
        arguments[8] = (String) map.get("8");
        Orderer order = fabricClient.getOrderer("orderer0.SCRT.com","grpcs://orderer0.SCRT.com:8050",keyPath.getTlsOrderFilePathurces());
        ReturnMessage returnMessage=new ReturnMessage();
        JSONObject rejson = new JSONObject();
        try {
            Map remap =  fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG,"SCMSC",order,peers,method,arguments);
            int code = (int) remap.get("code");
            if (code==1){
                returnMessage.setStatus(1);
                returnMessage.setMessage(remap.get("info").toString());
            }
            if (code==500){
                returnMessage.setStatus(0);
                returnMessage.setMessage((String) remap.get("info"));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnMessage.setStatus(0);
            returnMessage.setMessage("system error");
        }
        return returnMessage;
    }





    //DMSCquery
    @Override
    public JSONObject SCMSCquery(Map map, String peer_name,String method) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, JSONException {
        UserContext userContext = new UserContext();
        userContext.setAffiliation("Org1");
        userContext.setMspId("Org1MSP");
        userContext.setAccount("admin");
        userContext.setName("admin");
        Enrollment enrollment = UserUtils.getEnrollment(keyPath.getKeyFolderPath(), keyPath.getKeyFileName(), keyPath.getCertFoldePath(), keyPath.getCertFileName());
        userContext.setEnrollment(enrollment);
        FabricClient fabricClient = new FabricClient(userContext);
        List<Peer> peers = new ArrayList<>();
        //   Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com","grpcs://peer0.org1.SCRT.com:7051",tlsPeer0FilePath);
        if (peer_name.equals("peer0")) {
            Peer peer0 = fabricClient.getPeer("peer0.org1.SCRT.com", "grpcs://peer0.org1.SCRT.com:7051", keyPath.getTlsPeer0FilePath());
            peers.add(peer0);
        }
        if (peer_name.equals("peer1")) {
            Peer peer1 = fabricClient.getPeer("peer1.org1.SCRT.com", "grpcs://peer1.org1.SCRT.com:8051", keyPath.getTlsPeer1FilePath());
            peers.add(peer1);
        }
//        if (peer_name.equals("peer2")) {
//            Peer peer2 = fabricClient.getPeer("peer2.org1.SCRT.com", "grpcs://peer2.org1.SCRT.com:8051", keyPath.getTlsPeer2FilePath());
//            peers.add(peer2);
//        }
//        if (peer_name.equals("peer3")) {
//            Peer peer3 = fabricClient.getPeer("peer3.org1.SCRT.com", "grpcs://peer3.org1.SCRT.com:8056", keyPath.getTlsPeer3FilePath());
//
//            peers.add(peer3);
//        }
//        if (peer_name.equals("peer4")) {
//            Peer peer4 = fabricClient.getPeer("peer4.org1.SCRT.com", "grpcs://peer4.org1.SCRT.com:9051", keyPath.getTlsPeer4FilePath());
//
//            peers.add(peer4);
//        }
//        if (peer_name.equals("peer5")) {
//            Peer peer5 = fabricClient.getPeer("peer5.org1.SCRT.com", "grpcs://peer5.org1.SCRT.com:9056", keyPath.getTlsPeer5FilePath());
//            peers.add(peer5);
//        }
        String[] arguments = new String[4];
        arguments[0] = (String) map.get("0");
        arguments[1] = (String) map.get("1");
        arguments[2] = (String) map.get("2");
        arguments[3] = (String) map.get("3");
        try {
            Map remap = fabricClient.queryChaincode(peers, "mychannel", TransactionRequest.Type.GO_LANG, "SCMSC", method, arguments);
            int code = (int) remap.get("code");
            JSONObject rejson = new JSONObject();
            if (code == 1) {
                String data2 = remap.get("info").toString();
                String data = data2.replaceAll("\\u0000"," ");
                if (data.equals("[]")) {
                    rejson.put("status", 1);
                    rejson.put("message", "没有该匹配项");
                    return rejson;
                }

                if(data.startsWith("[")){
                    JSONArray jsondata= new JSONArray(data);
                    rejson.put("data", jsondata);
                }else {
                    JSONObject jsondata = new JSONObject(data);
                    rejson.put("data", jsondata);
                }
                rejson.put("status", 1);
                rejson.put("message", "查询成功");

                return rejson;

            }
            if (code == 500) {
                rejson.put("status", 0);
                rejson.put("message", (String)remap.get("info"));
                return rejson;
            }
            rejson.put("status", 404);
            rejson.put("message", "错误");
            return rejson;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", 0);
            error.put("message", "错误");
            return error;
        }



    }

}
