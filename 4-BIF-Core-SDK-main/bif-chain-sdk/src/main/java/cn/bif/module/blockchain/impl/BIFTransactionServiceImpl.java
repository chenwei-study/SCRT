/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * © COPYRIGHT 2021 Corporation CAICT All rights reserved.
 * http://www.caict.ac.cn
 */
package cn.bif.module.blockchain.impl;

import cn.bif.api.BIFSDK;
import cn.bif.common.*;
import cn.bif.exception.SDKException;
import cn.bif.exception.SdkError;
import cn.bif.utils.http.HttpUtils;
import cn.bif.model.request.*;
import cn.bif.model.request.operation.*;
import cn.bif.model.response.*;
import cn.bif.model.response.result.*;
import cn.bif.module.account.impl.BIFAccountServiceImpl;
import cn.bif.module.blockchain.BIFBlockService;
import cn.bif.module.blockchain.BIFTransactionService;
import cn.bif.module.contract.impl.BIFContractServiceImpl;
import cn.bif.module.encryption.key.PrivateKeyManager;
import cn.bif.module.encryption.key.PublicKeyManager;
import cn.bif.utils.hash.HashUtil;
import cn.bif.utils.hash.model.HashType;
import cn.bif.utils.hex.HexFormat;

import cn.bif.protobuf.Chain;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BIFTransactionServiceImpl implements BIFTransactionService {

    /**
     * @Method BIFSerializable
     * @Params [bifTransactionSerializeRequest]
     * @Return BIFTransactionSerializeResponse
     */
    @Override
    public BIFTransactionSerializeResponse BIFSerializable(BIFTransactionSerializeRequest bifTransactionSerializeRequest) {
        BIFTransactionSerializeResponse bifTransactionSerializeResponse = new BIFTransactionSerializeResponse();
        BIFTransactionSerializeResult transactionSerializeResult = new BIFTransactionSerializeResult();
        try {
            if (Tools.isEmpty(bifTransactionSerializeRequest)) {
                throw new SDKException(SdkError.REQUEST_NULL_ERROR);
            }
            // check sourceAddress
            String sourceAddress = bifTransactionSerializeRequest.getSourceAddress();
            if (!PublicKeyManager.isAddressValid(sourceAddress)) {
                throw new SDKException(SdkError.INVALID_SOURCEADDRESS_ERROR);
            }
            // check nonce
            Long nonce = bifTransactionSerializeRequest.getNonce();
            if (Tools.isEmpty(nonce) || nonce < Constant.INIT_ONE) {
                throw new SDKException(SdkError.INVALID_NONCE_ERROR);
            }
            // check gasPrice
            Long gasPrice = bifTransactionSerializeRequest.getGasPrice();
            if (Tools.isEmpty(gasPrice) || gasPrice < Constant.INIT_ZERO) {
                throw new SDKException(SdkError.INVALID_GASPRICE_ERROR);
            }

            // check feeLimit
            Long feeLimit = bifTransactionSerializeRequest.getFeeLimit();
            if (Tools.isEmpty(feeLimit) || feeLimit < Constant.INIT_ZERO) {
                throw new SDKException(SdkError.INVALID_FEELIMIT_ERROR);
            }

            Long ceilLedgerSeq = bifTransactionSerializeRequest.getCeilLedgerSeq();
            if (!Tools.isEmpty(ceilLedgerSeq) && ceilLedgerSeq <= Constant.INIT_ZERO) {
                throw new SDKException(SdkError.INVALID_CEILLEDGERSEQ_ERROR);
            }
            // check metadata
            String metadata = bifTransactionSerializeRequest.getMetadata();
            // build transaction
            Chain.Transaction.Builder transaction = Chain.Transaction.newBuilder();
            // add note
            if (!Tools.isEmpty(metadata)) {
                transaction.setMetadata(ByteString.copyFromUtf8(metadata));
            }
            // check operation
            BIFBaseOperation operation = bifTransactionSerializeRequest.getOperation();
            if (Tools.isEmpty(operation)) {
                throw new SDKException(SdkError.OPERATIONS_EMPTY_ERROR);
            }
            buildOperations(operation, sourceAddress, transaction);
            // add other information
            transaction.setSourceAddress(sourceAddress);
            transaction.setNonce(nonce);
            transaction.setFeeLimit(feeLimit);
            transaction.setGasPrice(gasPrice);
            if (!Tools.isEmpty(BIFSDK.getSdk().getChainId())) {
                transaction.setChainId(BIFSDK.getSdk().getChainId());
            }

            if (!Tools.isEmpty(ceilLedgerSeq)) {
                // get blockNumber
                BIFBlockService blockService = new BIFBlockServiceImpl();
                BIFBlockGetNumberResponse blockGetNumberResponse = blockService.getBlockNumber();
                Integer errorCode = blockGetNumberResponse.getErrorCode();
                if (!Tools.isEmpty(errorCode) && errorCode != Constant.SUCCESS) {
                    String errorDesc = blockGetNumberResponse.getErrorDesc();
                    throw new SDKException(errorCode, errorDesc);
                } else if (Tools.isEmpty(errorCode)) {
                    throw new SDKException(SdkError.CONNECTNETWORK_ERROR);
                }
                // check ceilLedgerSeq
                Long blockNumber = blockGetNumberResponse.getResult().getHeader().getBlockNumber();
                transaction.setCeilLedgerSeq(ceilLedgerSeq + blockNumber);
            }
            byte[] transactionBlobBytes = transaction.build().toByteArray();
            String transactionBlob = HexFormat.byteToHex(transactionBlobBytes);
            transactionSerializeResult.setTransactionBlob(transactionBlob);
            transactionSerializeResult.setHash(HashUtil.GenerateHashHex(transactionBlobBytes, HashType.SHA256));
            bifTransactionSerializeResponse.buildResponse(SdkError.SUCCESS, transactionSerializeResult);
        } catch (SDKException apiException) {
            Integer errorCode = apiException.getErrorCode();
            String errorDesc = apiException.getErrorDesc();
            bifTransactionSerializeResponse.buildResponse(errorCode, errorDesc, transactionSerializeResult);
        } catch (Exception e) {
            bifTransactionSerializeResponse.buildResponse(SdkError.SYSTEM_ERROR.getCode(), e.getMessage(), transactionSerializeResult);
        }

        return bifTransactionSerializeResponse;
    }



    /**
     * @Method submit
     * @Params [transactionSubmitRequest]
     * @Return TransactionSubmitResponse
     */
    @Override
    public BIFTransactionSubmitResponse BIFSubmit(BIFTransactionSubmitRequest bifTransactionSubmitRequest) {
        BIFTransactionSubmitResponse bifTransactionSubmitResponse = new BIFTransactionSubmitResponse();
        BIFTransactionSubmitResult transactionSubmitResult = new BIFTransactionSubmitResult();
        try {
            if (Tools.isEmpty(General.getInstance().getUrl())) {
                throw new SDKException(SdkError.URL_EMPTY_ERROR);
            }
            if (Tools.isEmpty(bifTransactionSubmitRequest)) {
                throw new SDKException(SdkError.REQUEST_NULL_ERROR);
            }
            String blob = bifTransactionSubmitRequest.getTransactionBlob();
            if (Tools.isEmpty(blob)) {
                throw new SDKException(SdkError.INVALID_SERIALIZATION_ERROR);
            }
            Chain.Transaction.parseFrom(HexFormat.hexToByte(blob));
            String signData = bifTransactionSubmitRequest.getSignData();
            if (Tools.isEmpty(signData)) {
                throw new SDKException(SdkError.SIGNDATA_NULL_ERROR);
            }
            String publicKey = bifTransactionSubmitRequest.getPublicKey();
            if (Tools.isEmpty(publicKey)) {
                throw new SDKException(SdkError.PUBLICKEY_NULL_ERROR);
            }
            // serializable transaction request
            Map<String, Object> transactionItemsRequest = new HashMap<>();
            List<Object> transactionItems = new ArrayList<>();
            Map<String, Object> transactionItem = new HashMap<>();
            transactionItem.put("transaction_blob", blob);
            // build sign
            List<Object> signatureItems = new ArrayList<>();
            Map<String, Object> signatureItem = new HashMap<>();
            signatureItem.put("sign_data", signData);
            signatureItem.put("public_key", publicKey);
            signatureItems.add(signatureItem);
            transactionItem.put("signatures", signatureItems);
            transactionItems.add(transactionItem);
            transactionItemsRequest.put("items", transactionItems);
            // submit
            String submitUrl = General.getInstance().transactionSubmitUrl();
            String transactionRequest = JsonUtils.toJSONString(transactionItemsRequest);
            String result = HttpUtils.httpPost(submitUrl, transactionRequest);
            BIFTransactionSubmitHttpResponse transactionSubmitHttpResponse = JsonUtils.toJavaObject(result, BIFTransactionSubmitHttpResponse.class);
            Integer successCount = transactionSubmitHttpResponse.getSuccessCount();
            BIFTransactionSubmitHttpResult[] httpResults = transactionSubmitHttpResponse.getResults();
            if (!Tools.isEmpty(httpResults)) {
                transactionSubmitResult.setHash(httpResults[0].getHash());
                if (!Tools.isEmpty(successCount) && 0 == successCount) {
                    Integer errorCode = httpResults[0].getErrorCode();
                    String errorDesc = httpResults[0].getErrorDesc();
                    throw new SDKException(errorCode, errorDesc);
                }
            } else {
                throw new SDKException(SdkError.INVALID_SERIALIZATION_ERROR);
            }

            bifTransactionSubmitResponse.buildResponse(SdkError.SUCCESS, transactionSubmitResult);
        } catch (SDKException apiException) {
            Integer errorCode = apiException.getErrorCode();
            String errorDesc = apiException.getErrorDesc();
            bifTransactionSubmitResponse.buildResponse(errorCode, errorDesc, transactionSubmitResult);
        } catch (InvalidProtocolBufferException | IllegalArgumentException e) {
            bifTransactionSubmitResponse.buildResponse(SdkError.INVALID_SERIALIZATION_ERROR, transactionSubmitResult);
        } catch (NoSuchAlgorithmException | KeyManagementException | NoSuchProviderException | IOException e) {
            bifTransactionSubmitResponse.buildResponse(SdkError.CONNECTN_BLOCKCHAIN_ERROR, transactionSubmitResult);
        } catch (Exception e) {
            bifTransactionSubmitResponse.buildResponse(SdkError.SYSTEM_ERROR.getCode(), e.getMessage(), transactionSubmitResult);
        }
        return bifTransactionSubmitResponse;
    }

    /**
     * 广播交易
     *
     * @return
     */
    @Override
    public String radioTransaction(String senderAddress, Long feeLimit, Long gasPrice, BIFBaseOperation operation,
                                   Long ceilLedgerSeq, String remarks, String senderPrivateKey) {
        BIFAccountServiceImpl accountService = new BIFAccountServiceImpl();
        // 一、获取交易发起的账号nonce值
        BIFAccountGetNonceRequest getNonceRequest = new BIFAccountGetNonceRequest();
        getNonceRequest.setAddress(senderAddress);
        // 调用getBIFNonce接口
        BIFAccountGetNonceResponse nonceResponse = accountService.getNonce(getNonceRequest);
        if (nonceResponse.getErrorCode() != Constant.SUCCESS) {
            throw new SDKException(nonceResponse.getErrorCode(), nonceResponse.getErrorDesc());
        }
        Long nonce = nonceResponse.getResult().getNonce();

        // 二、构建操作、序列化交易
        // 初始化请求参数
        BIFTransactionSerializeRequest serializeRequest = new BIFTransactionSerializeRequest();
        serializeRequest.setSourceAddress(senderAddress);
        serializeRequest.setNonce(nonce + 1);
        serializeRequest.setFeeLimit(feeLimit);
        serializeRequest.setGasPrice(gasPrice);
        serializeRequest.setOperation(operation);
        serializeRequest.setCeilLedgerSeq(ceilLedgerSeq);

        // 调用BIFSerializable接口
        serializeRequest.setMetadata(remarks);
        BIFTransactionSerializeResponse serializeResponse = BIFSerializable(serializeRequest);
        if (serializeResponse.getErrorCode() != Constant.SUCCESS) {
            throw new SDKException(serializeResponse.getErrorCode(), serializeResponse.getErrorDesc());
        }
        String transactionBlob = serializeResponse.getResult().getTransactionBlob();

        // 三、签名
        byte[] signBytes = PrivateKeyManager.sign(HexFormat.hexToByte(transactionBlob), senderPrivateKey);
        String publicKey = PrivateKeyManager.getEncPublicKey(senderPrivateKey);

        //四、提交交易
        BIFTransactionSubmitRequest submitRequest = new BIFTransactionSubmitRequest();
        submitRequest.setTransactionBlob(transactionBlob);
        submitRequest.setPublicKey(publicKey);
        submitRequest.setSignData(HexFormat.byteToHex(signBytes));
        // 调用bifSubmit接口
        BIFTransactionSubmitResponse transactionSubmitResponse = BIFSubmit(submitRequest);
        if (transactionSubmitResponse.getErrorCode() != Constant.SUCCESS) {
            throw new SDKException(transactionSubmitResponse.getErrorCode(), transactionSubmitResponse.getErrorDesc());
        }
        return transactionSubmitResponse.getResult().getHash();
    }

    /**
     * @Method getInfo
     * @Params [transactionGetInfoRequest]
     * @Return TransactionGetInfoResponse
     */
    @Override
    public BIFTransactionGetInfoResponse getTransactionInfo(BIFTransactionGetInfoRequest transactionGetInfoRequest) {
        BIFTransactionGetInfoResponse transactionGetInfoResponse = new BIFTransactionGetInfoResponse();
        BIFTransactionGetInfoResult transactionGetInfoResult = new BIFTransactionGetInfoResult();
        try {
            if (Tools.isEmpty(General.getInstance().getUrl())) {
                throw new SDKException(SdkError.URL_EMPTY_ERROR);
            }
            if (Tools.isEmpty(transactionGetInfoRequest)) {
                throw new SDKException(SdkError.REQUEST_NULL_ERROR);
            }
            String hash = transactionGetInfoRequest.getHash();
            if (Tools.isEmpty(hash) || hash.length() != Constant.HASH_HEX_LENGTH) {
                throw new SDKException(SdkError.INVALID_HASH_ERROR);
            }
            transactionGetInfoResponse = getTransactionInfo(hash);
        } catch (SDKException apiException) {
            Integer errorCode = apiException.getErrorCode();
            String errorDesc = apiException.getErrorDesc();
            transactionGetInfoResponse.buildResponse(errorCode, errorDesc, transactionGetInfoResult);
        } catch (NoSuchAlgorithmException | KeyManagementException | NoSuchProviderException | IOException e) {
            transactionGetInfoResponse.buildResponse(SdkError.CONNECTNETWORK_ERROR, transactionGetInfoResult);
        } catch (Exception e) {
            transactionGetInfoResponse.buildResponse(SdkError.SYSTEM_ERROR.getCode(), e.getMessage(), transactionGetInfoResult);
        }
        return transactionGetInfoResponse;
    }

    @Override
    public BIFTransactionGasSendResponse gasSend(BIFTransactionGasSendRequest request) {
        BIFTransactionGasSendResponse response = new BIFTransactionGasSendResponse();
        BIFTransactionGasSendResult result = new BIFTransactionGasSendResult();
        try {
            if (Tools.isEmpty(request)) {
                throw new SDKException(SdkError.REQUEST_NULL_ERROR);
            }
            String senderAddress = request.getSenderAddress();
            if (!PublicKeyManager.isAddressValid(senderAddress)) {
                throw new SDKException(SdkError.INVALID_ADDRESS_ERROR);
            }
            String privateKey = request.getPrivateKey();
            if (Tools.isEmpty(privateKey)) {
                throw new SDKException(SdkError.PRIVATEKEY_NULL_ERROR);
            }
            Long ceilLedgerSeq = request.getCeilLedgerSeq();
            String remarks = request.getRemarks();

            BIFGasSendOperation operation = new BIFGasSendOperation();
            String destAddress = request.getDestAddress();
            if (!PublicKeyManager.isAddressValid(destAddress)) {
                throw new SDKException(SdkError.INVALID_DESTADDRESS_ERROR);
            }
            Long amount = request.getAmount();
            if (Tools.isEmpty(amount) || amount < Constant.INIT_ZERO) {
                throw new SDKException(SdkError.INVALID_GAS_AMOUNT_ERROR);
            }
            operation.setDestAddress(destAddress);
            operation.setAmount(amount);

            // 广播交易
            String hash = radioTransaction(senderAddress, Constant.FEE_LIMIT, Constant.GAS_PRICE, operation, ceilLedgerSeq,
                    remarks, privateKey);
            result.setHash(hash);
            response.buildResponse(SdkError.SUCCESS, result);
        } catch (SDKException apiException) {
            Integer errorCode = apiException.getErrorCode();
            String errorDesc = apiException.getErrorDesc();
            response.buildResponse(errorCode, errorDesc, result);
        } catch (Exception e) {
            response.buildResponse(SdkError.SYSTEM_ERROR.getCode(), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public BIFTransactionPrivateContractCallResponse privateContractCall(BIFTransactionPrivateContractCallRequest request) {
        BIFTransactionPrivateContractCallResponse response = new BIFTransactionPrivateContractCallResponse();
        BIFTransactionPrivateContractCallResult result = new BIFTransactionPrivateContractCallResult();
        try {
            if (Tools.isEmpty(request)) {
                throw new SDKException(SdkError.REQUEST_NULL_ERROR);
            }
            String senderAddress = request.getSenderAddress();
            if (!PublicKeyManager.isAddressValid(senderAddress)) {
                throw new SDKException(SdkError.INVALID_ADDRESS_ERROR);
            }
            String privateKey = request.getPrivateKey();
            if (Tools.isEmpty(privateKey)) {
                throw new SDKException(SdkError.PRIVATEKEY_NULL_ERROR);
            }

            BIFPrivateContractCallOperation operation = new BIFPrivateContractCallOperation();
            String destAddress = request.getDestAddress();
            Integer type = request.getType();
            if (!Tools.isEmpty(type) && type < Constant.INIT_ZERO) {
                throw new SDKException(SdkError.INVALID_CONTRACT_TYPE_ERROR);
            }
            String input = request.getInput();
            String from = request.getFrom();
            String[] to = request.getTo();
            operation.setdestAddress(destAddress);
            operation.setType(type);
            operation.setInput(input);
            operation.setFrom(from);
            operation.setTo(to);

            Long ceilLedgerSeq = request.getCeilLedgerSeq();
            String remarks = request.getRemarks();
            // 广播交易
            String hash = radioTransaction(senderAddress, Constant.FEE_LIMIT, Constant.GAS_PRICE, operation, ceilLedgerSeq, remarks, privateKey);
            result.setHash(hash);
            response.buildResponse(SdkError.SUCCESS, result);
        } catch (SDKException apiException) {
            Integer errorCode = apiException.getErrorCode();
            String errorDesc = apiException.getErrorDesc();
            response.buildResponse(errorCode, errorDesc, result);
        } catch (Exception e) {
            response.buildResponse(SdkError.SYSTEM_ERROR.getCode(), e.getMessage(), result);
        }
        return response;
    }

    @Override
    public BIFTransactionPrivateContractCreateResponse privateContractCreate(BIFTransactionPrivateContractCreateRequest request) {
        BIFTransactionPrivateContractCreateResponse response = new BIFTransactionPrivateContractCreateResponse();
        BIFTransactionPrivateContractCreateResult result = new BIFTransactionPrivateContractCreateResult();
        try {
            if (Tools.isEmpty(request)) {
                throw new SDKException(SdkError.REQUEST_NULL_ERROR);
            }
            String senderAddress = request.getSenderAddress();
            if (!PublicKeyManager.isAddressValid(senderAddress)) {
                throw new SDKException(SdkError.INVALID_ADDRESS_ERROR);
            }
            String privateKey = request.getPrivateKey();
            if (Tools.isEmpty(privateKey)) {
                throw new SDKException(SdkError.PRIVATEKEY_NULL_ERROR);
            }

            BIFPrivateContractCreateOperation operation = new BIFPrivateContractCreateOperation();
            Integer type = request.getType();
            if (!Tools.isEmpty(type) && type < Constant.INIT_ZERO) {
                throw new SDKException(SdkError.INVALID_CONTRACT_TYPE_ERROR);
            }
            String payload = request.getPayload();
            if (Tools.isEmpty(payload)) {
                throw new SDKException(SdkError.PAYLOAD_EMPTY_ERROR);
            }
            String initInput = request.getInitInput();
            String from = request.getFrom();
            String[] to = request.getTo();
            operation.setTo(to);
            operation.setType(type);
            operation.setPayload(payload);
            operation.setInitInput(initInput);
            operation.setFrom(from);

            Long ceilLedgerSeq = request.getCeilLedgerSeq();
            String remarks = request.getRemarks();
            // 广播交易
            String hash = radioTransaction(senderAddress, Constant.FEE_LIMIT, Constant.GAS_PRICE, operation, ceilLedgerSeq, remarks, privateKey);
            result.setHash(hash);
            response.buildResponse(SdkError.SUCCESS, result);
        } catch (SDKException apiException) {
            Integer errorCode = apiException.getErrorCode();
            String errorDesc = apiException.getErrorDesc();
            response.buildResponse(errorCode, errorDesc, result);
        } catch (Exception e) {
            response.buildResponse(SdkError.SYSTEM_ERROR.getCode(), e.getMessage(), result);
        }
        return response;
    }

    /**
     * @Method buildOperations
     * @Params [operationBase, transaction]
     * @Return void
     */
    private void buildOperations(BIFBaseOperation operationBase, String transSourceAddress, Chain.Transaction.Builder transaction) throws SDKException {
        Chain.Operation operation;
        OperationType operationType = operationBase.getOperationType();
        switch (operationType) {
            case ACCOUNT_ACTIVATE:
                operation = BIFAccountServiceImpl.activate((BIFAccountActivateOperation) operationBase, transSourceAddress);
                break;
            case ACCOUNT_SET_METADATA:
                operation = BIFAccountServiceImpl.accountSetMetadata((BIFAccountSetMetadataOperation) operationBase);
                break;
            case ACCOUNT_SET_PRIVILEGE:
                operation =
                        BIFAccountServiceImpl.accountSetPrivilege((BIFAccountSetPrivilegeOperation) operationBase);
                break;
            case GAS_SEND:
                operation = BIFGasServiceImpl.send((BIFGasSendOperation) operationBase, transSourceAddress);
                break;
            case CONTRACT_CREATE:
                operation = BIFContractServiceImpl.create((BIFContractCreateOperation) operationBase);
                break;
            case CONTRACT_INVOKE:
                operation = BIFContractServiceImpl.invokeByGas((BIFContractInvokeOperation) operationBase, transSourceAddress);
                break;
            case PRIVATE_CONTRACT_CREATE:
                operation = BIFContractServiceImpl.createPrivateContract((BIFPrivateContractCreateOperation) operationBase);
                break;
            case PRIVATE_CONTRACT_CALL:
                operation = BIFContractServiceImpl.callPrivateContract((BIFPrivateContractCallOperation) operationBase);
                break;
            default:
                throw new SDKException(SdkError.OPERATIONS_ONE_ERROR);
        }
        if (Tools.isEmpty(operation)) {
            throw new SDKException(SdkError.OPERATIONS_ONE_ERROR);
        }
        transaction.addOperations(operation);
    }

    public static BIFTransactionGetInfoResponse getTransactionInfo(String hash) throws Exception {
        if (Tools.isEmpty(General.getInstance().getUrl())) {
            throw new SDKException(SdkError.URL_EMPTY_ERROR);
        }
        String getInfoUrl = General.getInstance().transactionGetInfoUrl(hash);
        String result = HttpUtils.httpGet(getInfoUrl);
        return JsonUtils.toJavaObject(result, BIFTransactionGetInfoResponse.class);
    }
}

