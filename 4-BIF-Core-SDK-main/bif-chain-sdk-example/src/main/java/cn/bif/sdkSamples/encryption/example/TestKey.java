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
package cn.bif.sdkSamples.encryption.example;

import cn.bif.common.JsonUtils;
import cn.bif.model.crypto.KeyPairEntity;
import cn.bif.module.encryption.key.PrivateKeyManager;
import cn.bif.module.encryption.key.PublicKeyManager;
import cn.bif.module.encryption.model.KeyType;
import cn.bif.utils.hex.HexFormat;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TestKey {

    @Test
    public void getKey() {
        KeyPairEntity keypair = KeyPairEntity.getBidAndKeyPair();
        String encAddress = keypair.getEncAddress();
        String encPublicKey = keypair.getEncPublicKey();
        String encPrivateKey = keypair.getEncPrivateKey();
        byte[] rawPublicKey = keypair.getRawPublicKey();
        byte[] rawPrivateKey = keypair.getRawPrivateKey();
        System.out.println(JsonUtils.toJSONString(keypair));
        KeyPairEntity keyPairBySM2 = KeyPairEntity.getBidAndKeyPairBySM2();
        String encAddress1 = keyPairBySM2.getEncAddress();
        String encPrivateKey1 = keyPairBySM2.getEncPrivateKey();
        String encPublicKey1 = keyPairBySM2.getEncPublicKey();
        byte[] rawPrivateKey1 = keyPairBySM2.getRawPrivateKey();
        byte[] rawPublicKey1 = keyPairBySM2.getRawPublicKey();
        System.out.println(JsonUtils.toJSONString(keyPairBySM2));
    }

    @Test
    public void test_ED25519() {
        try {
            PrivateKeyManager priKey = new PrivateKeyManager(KeyType.ED25519);
            System.out.println("Key1 private key: " + priKey.getEncPrivateKey());
            byte[] rawPrivateKey = priKey.getRawPrivateKey();
            System.out.println("Key1 public key: " + priKey.getEncPublicKey());
            byte[] rawPublicKey = priKey.getRawPublicKey();
            System.out.println("Key1 address: " + priKey.getEncAddress());

            System.out.println("Key1 static public key: " + PrivateKeyManager.getEncPublicKey(priKey.getEncPrivateKey()));
            String encPublicKey = PrivateKeyManager.getEncPublicKey(rawPublicKey, KeyType.ED25519);
            String encPrivateKey = PrivateKeyManager.getEncPrivateKey(rawPrivateKey, KeyType.ED25519);
            System.out.println("Key1 static address: " + PrivateKeyManager.getEncAddress(priKey.getEncPublicKey()));

           // PrivateKeyManager priKey2 = new PrivateKeyManager(priKey.getEncPrivateKey());
            PrivateKeyManager priKey2 = new PrivateKeyManager("priSPKsSkkYSR5J9kp7BAN7iyztz62xQEN9FT3XxqyTLfu84ox");
            System.out.println("Key1 static public key: " + PrivateKeyManager.getEncPublicKey(priKey.getEncPrivateKey()));
            System.out.println("Key2 private key: " + priKey2.getEncPrivateKey());
            System.out.println("Key2 public key: " + priKey2.getEncPublicKey());
            System.out.println("Key2 address: " + priKey2.getEncAddress());

            PublicKeyManager publicKey = new PublicKeyManager(priKey.getEncPublicKey());
            System.out.println(publicKey.getEncAddress());
            System.out.println(PublicKeyManager.isAddressValid(publicKey.getEncAddress() + "@"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:efLrFZCn3wqSrozTG9MkxXbriRmwUHs5"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:efn4h4pGfrmitGNWsFpF3QbMZzASz45M"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:efoVR5seiFsrvwHZ43KykjTx7o2em7rV"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:efSHUw3MrSqCS4uDzTE2X5vqKaqi7zTP"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:efUgGzb6zA4aKGADtD7dM6Cdo45dSymv"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:ef4YCXNTHuPCZhUCPPkYA6SbD3HDjimS"));
            String src = "0a2e61303032643833343562383964633334613537353734656234393736333566663132356133373939666537376236100122b90108012ab4010a2e61303032663836366337663431356537313934613932363131386363353565346365393939656232396231363461123a123866756e6374696f6e206d61696e28696e707574537472297b0a202f2ae8bf99e698afe59088e7baa6e585a5e58fa3e587bde695b02a2f207d1a06080a1a020807223e0a0568656c6c6f1235e8bf99e698afe5889be5bbbae8b4a6e58fb7e79a84e8bf87e7a88be4b8ade8aebee7bdaee79a84e4b880e4b8aa6d65746164617461";
            byte[] sign = priKey.sign(HexFormat.hexStringToBytes(src));
            byte[] sign_static = PrivateKeyManager.sign(HexFormat.hexStringToBytes(src), priKey.getEncPrivateKey());
            System.out.println("signature: " + HexFormat.byteToHex(sign));
            System.out.println("static signature: " + HexFormat.byteToHex(sign_static));
            System.out.println("verify: " + PublicKeyManager.verify(HexFormat.hexStringToBytes(src), sign, priKey.getEncPublicKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_SM2() {
        try {
            PrivateKeyManager priKey = new PrivateKeyManager(KeyType.SM2);
            System.out.println("Key1 private key: " + priKey.getEncPrivateKey());
            System.out.println("Key1 public key: " + priKey.getEncPublicKey());
            System.out.println("Key1 address: " + priKey.getEncAddress());
            System.out.println("Key1 static public key: " + PrivateKeyManager.getEncPublicKey(priKey.getEncPrivateKey()));
            System.out.println("Key1 static address: " + PrivateKeyManager.getEncAddress(priKey.getEncPublicKey()));

            PrivateKeyManager priKey2 = new PrivateKeyManager(priKey.getEncPrivateKey());
            System.out.println("Key1 static public key: " + PrivateKeyManager.getEncPublicKey(priKey.getEncPrivateKey()));
            System.out.println("Key2 private key: " + priKey2.getEncPrivateKey());
            System.out.println("Key2 public key: " + priKey2.getEncPublicKey());
            System.out.println("Key2 address: " + priKey2.getEncAddress());

            PublicKeyManager publicKey = new PublicKeyManager(priKey.getEncPublicKey());
            System.out.println(publicKey.getEncAddress());
            System.out.println(PublicKeyManager.isAddressValid(publicKey.getEncAddress() + "@"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:zfLrFZCn3wqSrozTG9MkxXbriRmwUHs5"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:zfn4h4pGfrmitGNWsFpF3QbMZzASz45M"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:zfoVR5seiFsrvwHZ43KykjTx7o2em7rV"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:zfSHUw3MrSqCS4uDzTE2X5vqKaqi7zTP"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:zfUgGzb6zA4aKGADtD7dM6Cdo45dSymv"));
            System.out.println(PublicKeyManager.isAddressValid("did:bid:zf4YCXNTHuPCZhUCPPkYA6SbD3HDjimS"));

            String encAddress = PublicKeyManager.getEncAddress(priKey.getEncPublicKey());
            System.out.println(encAddress);

            String src = "test";
            byte[] sign = priKey.sign(HexFormat.hexStringToBytes(src));
            System.out.println("signature: " + HexFormat.byteToHex(sign).toLowerCase());
            System.out.println("verify: " + PublicKeyManager.verify(HexFormat.hexStringToBytes(src), sign, priKey.getEncPublicKey()));

            byte[] sign_static = PrivateKeyManager.sign(HexFormat.hexStringToBytes(src), priKey.getEncPrivateKey());
            System.out.println("static signature: " + HexFormat.byteToHex(sign_static).toLowerCase());
            System.out.println("static verify: " + PublicKeyManager.verify(HexFormat.hexStringToBytes(src), sign, priKey2.getEncPublicKey()));

            boolean verify = publicKey.verify(HexFormat.hexStringToBytes(src), sign);
            System.out.println(verify);
            boolean verify1 = PublicKeyManager.verify(HexFormat.hexStringToBytes(src), sign, priKey.getEncPublicKey());
            System.out.println(verify1);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
