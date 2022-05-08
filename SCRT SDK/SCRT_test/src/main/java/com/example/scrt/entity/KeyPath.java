package com.example.scrt.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyPath {

    @Value("${block.peerOrganizations.org1.user.admin.msp.keystore.keystorepath}")
    private  String   keyFolderPath ;
    @Value("${block.peerOrganizations.org1.user.admin.msp.keystore.keyFileName}")
    private    String  keyFileName;
    @Value("${block.peerOrganizations.org1.user.admin.msp.cacerts.certFoldePath}")
    private  String  certFoldePath;
    @Value("${block.peerOrganizations.org1.user.admin.msp.cacerts.certFoldeName}")
    private  String certFileName;


    @Value("${block.orderOrganizations.tlsca}")
    private  String tlsOrderFilePathurces;


    @Value("${block.txfilePath}")
    private  String txfilePath;


    @Value("${block.peerOrganizations.org1.peers.peer0.msp.tlscacerts}")
    private  String tlsPeer0FilePath;

    @Value("${block.peerOrganizations.org1.peers.peer1.msp.tlscacerts}")
    private  String tlsPeer1FilePath;

    @Value("${block.peerOrganizations.org1.peers.peer2.msp.tlscacerts}")
    private  String tlsPeer2FilePath;

    @Value("${block.peerOrganizations.org1.peers.peer3.msp.tlscacerts}")
    private  String tlsPeer3FilePath;

    @Value("${block.peerOrganizations.org1.peers.peer4.msp.tlscacerts}")
    private  String tlsPeer4FilePath;

    @Value("${block.peerOrganizations.org1.peers.peer5.msp.tlscacerts}")
    private  String tlsPeer5FilePath;

    public String getTlsPeer0FilePath() {
        return tlsPeer0FilePath;
    }

    public void setTlsPeer0FilePath(String tlsPeer0FilePath) {
        this.tlsPeer0FilePath = tlsPeer0FilePath;
    }

    public String getTlsPeer1FilePath() {
        return tlsPeer1FilePath;
    }

    public void setTlsPeer1FilePath(String tlsPeer1FilePath) {
        this.tlsPeer1FilePath = tlsPeer1FilePath;
    }

    public String getTlsPeer2FilePath() {
        return tlsPeer2FilePath;
    }

    public void setTlsPeer2FilePath(String tlsPeer2FilePath) {
        this.tlsPeer2FilePath = tlsPeer2FilePath;
    }

    public String getTlsPeer3FilePath() {
        return tlsPeer3FilePath;
    }

    public void setTlsPeer3FilePath(String tlsPeer3FilePath) {
        this.tlsPeer3FilePath = tlsPeer3FilePath;
    }

    public String getTlsPeer4FilePath() {
        return tlsPeer4FilePath;
    }

    public void setTlsPeer4FilePath(String tlsPeer4FilePath) {
        this.tlsPeer4FilePath = tlsPeer4FilePath;
    }

    public String getTlsPeer5FilePath() {
        return tlsPeer5FilePath;
    }

    public void setTlsPeer5FilePath(String tlsPeer5FilePath) {
        this.tlsPeer5FilePath = tlsPeer5FilePath;
    }

    public String getKeyFolderPath() {
        return keyFolderPath;
    }

    public void setKeyFolderPath(String keyFolderPath) {
        this.keyFolderPath = keyFolderPath;
    }

    public String getKeyFileName() {
        return keyFileName;
    }

    public void setKeyFileName(String keyFileName) {
        this.keyFileName = keyFileName;
    }

    public String getCertFoldePath() {
        return certFoldePath;
    }

    public void setCertFoldePath(String certFoldePath) {
        this.certFoldePath = certFoldePath;
    }

    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }

    public String getTlsOrderFilePathurces() {
        return tlsOrderFilePathurces;
    }

    public void setTlsOrderFilePathurces(String tlsOrderFilePathurces) {
        this.tlsOrderFilePathurces = tlsOrderFilePathurces;
    }

    public String getTxfilePath() {
        return txfilePath;
    }

    public void setTxfilePath(String txfilePath) {
        this.txfilePath = txfilePath;
    }
}
