package main

import (
	"bytes"
	"crypto"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"math"
	"regexp"
	"strconv"
	"time"

	//	"time"
	//   "strconv"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type SmartContract struct {
}
type stateDate struct {
	ID       string  `json:"iD"`
	Wallet_r string  `json:"wallet_r"`
	TemPk_r  string  `json:"temPk_r"`
	Wallet_e string  `json:"wallet_e"`
	TemPk_e  string  `json:"temppk_e"`
	Nonce    float64 `json:"nonce"`
	TemNonce float64 `json:"temNonce"`
	Dp       int64   `json:"dp"`
	State    string  `json:"state"`
	Amount   float64 `json:"amount"`
	Cost     float64 `json:"cost"`
	FB_e     float64 `json:"fB_e"`
	FB_r     float64 `json:"fB_r"`
	Data     string  `json:"data"`
	TS       int64   `json:"t"`
}

type actionData struct {
	ID          string  `json:"ID"`
	actionNonce float64 `json:"actionNonce"`
	cost        float64 `json:"cost"`
	fB_r        float64 `json:"fB_r"`
	fB_e        float64 `json:"fB_e"`
	data        string  `json:"data"`
	hash        string  `json:"hash"`
	sign        string  `json:"sign"`
}

// ===================================================================================
// Main
// ===================================================================================
func main() {
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}

func (s *SmartContract) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (s *SmartContract) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)
	// Handle different functions
	if function == "channelOpen" { //create identity
		return s.channelOpen(stub, args)
	}
	if function == "disputeCreate" {
		return s.disputeCreate(stub, args)
	}
	if function == "disputeResolve" {
		return s.disputeResolve(stub, args)
	}
	if function == "disputeRefute" {
		return s.disputeRefute(stub, args)
	}
	if function == "temporaryState" {
		return s.temporaryState(stub, args)
	}
	if function == "unilateralClose" {
		return s.unilateralClose(stub, args)
	}
	if function == "cooperateclose" {
		return s.cooperateclose(stub, args)
	}
	if function == "stateQuery" {
		return s.stateQuery(stub, args)
	}
	if function == "depositsWithdraw" {
		return s.depositsWithdraw(stub, args)
	}
	if function == "deleteChannel" {
		return s.deleteChannel(stub, args)
	}

	return shim.Error("Received unknown function invocation")
}

func (s *SmartContract) channelOpen(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	channelID := args[0]
	wallet_r := args[1]
	wallet_e := args[2]
	amount := args[3]
	dp := args[4]
	temPk_r := args[5]
	temPk_e := args[6]
	sign_r := args[7]
	sign_e := args[8]

	value, _ := stub.GetState(channelID)

	if value != nil {
		return shim.Error("channelID Already exists")
	}
	var data string
	data += wallet_r + wallet_e + amount + dp + temPk_r + temPk_e
	HashFlag := VeryHash(data, channelID)

	if HashFlag == true {
		chaincodeArgs := toChaincodeArgs2("doublesignature", channelID, sign_r, sign_e, wallet_r, wallet_e, amount)
		response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
		if response.Status != shim.OK {
			return shim.Error("DMSC:" + response.Message)
		}
		timestamp := time.Now().Unix()
		tS2 := time.Now().UnixNano()
		dpfloat, err := strconv.ParseInt(dp, 10, 64)
		amountfloat, err := strconv.ParseFloat(amount, 64)
		statedata := &stateDate{channelID, wallet_r, temPk_r, wallet_e, temPk_e, 0, 0, dpfloat, "O", amountfloat, 0, amountfloat, amountfloat, "", timestamp}
		resourceJSONasBytes, err := json.Marshal(statedata)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(channelID, resourceJSONasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
		tS3 := time.Now().UnixNano()
		time1 := strconv.FormatInt((tS2 - tS1), 10)
		time2 := strconv.FormatInt((tS3 - tS2), 10)
		return shim.Success([]byte("succeed-" + time1 + "-" + time2))
	}
	return shim.Error("Hash error")

}

func (s *SmartContract) disputeCreate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	if len(args) < 16 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	noce, _ := strconv.ParseFloat(args[1], 64)
	cost, _ := strconv.ParseFloat(args[2], 64)
	fb_r, _ := strconv.ParseFloat(args[3], 64)
	fb_e, _ := strconv.ParseFloat(args[4], 64)

	noce2, _ := strconv.ParseFloat(args[9], 64)
	cost2, _ := strconv.ParseFloat(args[10], 64)
	fb_r2, _ := strconv.ParseFloat(args[11], 64)
	fb_e2, _ := strconv.ParseFloat(args[12], 64)

	bool, _ := regexp.MatchString(`^(([1-9]{1}\d*)|(0{1}))(\.\d{0,1})?$`, args[1])
	if bool != true {
		return shim.Error(args[1] + "error")
	}
	bool, _ = regexp.MatchString(`^(([1-9]{1}\d*)|(0{1}))(\.\d{0,1})?$`, args[9])
	if bool != true {
		return shim.Error(args[9] + "error")
	}

	actiondata := &actionData{args[0], noce, cost, fb_r, fb_e, args[5], args[6], args[7]}
	actiondata2 := &actionData{args[8], noce2, cost2, fb_r2, fb_e2, args[13], args[14], args[15]}

	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	var channel stateDate
	if value == nil {
		return shim.Error("channel not found")
	}
	err = json.Unmarshal(value, &channel) //反序列化
	if channel.State != "O" {
		return shim.Error("channel not in the state O")
	}

	var a = 0.0
	var b = 0.0
	var c = 0

	if (actiondata.actionNonce-math.Floor(actiondata.actionNonce) > 0.6000001) || (actiondata2.actionNonce-math.Floor(actiondata2.actionNonce) > 0.6000001) {
		return shim.Error("Nonce of actiondatas error:>0.6")
	}

	if actiondata.actionNonce >= actiondata2.actionNonce {
		cha := strconv.FormatFloat(actiondata2.actionNonce-actiondata.actionNonce, 'f', 2, 64)
		if (cha == "0.10") || (cha == "0.50") {
			a = actiondata.actionNonce
			b = actiondata.actionNonce - actiondata2.actionNonce
			c = 1
		} else {
			cha := strconv.FormatFloat(actiondata.actionNonce-actiondata2.actionNonce, 'f', 2, 64)
			return shim.Error("Nonce of actiondatas error:" + cha)
		}
	}
	if actiondata.actionNonce < actiondata2.actionNonce {
		cha := strconv.FormatFloat(actiondata2.actionNonce-actiondata.actionNonce, 'f', 2, 64)
		if (cha == "0.10") || (cha == "0.50") {
			a = actiondata2.actionNonce
			b = actiondata2.actionNonce - actiondata.actionNonce
			c = 2
		} else {

			return shim.Error("Nonce of  actiondatas  error:" + cha)
		}
	}
	if (a < channel.Nonce) || (a < channel.TemNonce) {
		return shim.Error("Nonce error")
	}

	if (actiondata.ID != actiondata2.ID) || (actiondata.cost != actiondata2.cost) {
		return shim.Error("ID or cost not equal")
	}

	if b == 0.1 {
		if actiondata.cost != actiondata2.cost {
			return shim.Error("cost error")
		}
	}

	if math.Floor(a) != 0.4 {
		if (actiondata.fB_r != actiondata2.fB_r) || (actiondata.fB_e != actiondata2.fB_e) {
			return shim.Error("fB error")
		}

	} else {
		if c == 1 {
			if (actiondata.fB_e-actiondata2.fB_e != cost) || (actiondata2.fB_r-actiondata.fB_r != cost) {
				return shim.Error("fB error")
			}
		}
		if c == 2 {
			if (actiondata2.fB_e-actiondata.fB_e != cost) || (actiondata.fB_r-actiondata2.fB_r != cost) {
				return shim.Error("fB error")
			}
		}
	}

	if actiondata.fB_r+actiondata.fB_e != 2*channel.Amount {
		return shim.Error("Actiondata fB error")
	}
	if actiondata2.fB_r+actiondata2.fB_e != 2*channel.Amount {
		return shim.Error("Actiondata2 fB error")
	}

	var data string
	data += args[0] + args[1] + args[2] + args[3] + args[4] + args[5]
	HashFlag := VeryHash(data, args[6])
	if HashFlag != true {
		if c == 1 {
			return shim.Error("hash1 error")
		} else {
			return shim.Error("hash2 error")
		}
	}
	var data2 string
	data2 += args[8] + args[9] + args[10] + args[11] + args[12] + args[13]
	HashFlag2 := VeryHash(data2, args[14])
	if HashFlag2 != true {
		if c == 1 {
			return shim.Error("hash1 error")
		} else {
			return shim.Error("hash2 error")
		}
	}
	cha := strconv.FormatFloat(actiondata.actionNonce-math.Floor(actiondata.actionNonce), 'f', 2, 64)
	if cha == "0.10" || cha == "0.30" || cha == "0.50" {
		flag := VerySign(actiondata.hash, channel.TemPk_r, actiondata.sign)
		flag2 := VerySign(actiondata2.hash, channel.TemPk_e, actiondata2.sign)
		if flag != true || flag2 != true {
			return shim.Error("sign error")
		}
	} else {
		flag := VerySign(actiondata.hash, channel.TemPk_e, actiondata.sign)
		flag2 := VerySign(actiondata2.hash, channel.TemPk_r, actiondata2.sign)
		if flag != true || flag2 != true {
			return shim.Error("sign error")
		}
	}

	tS2 := time.Now().UnixNano()
	channel.State = "TD"
	channel.TS = time.Now().Unix()
	channel.Nonce = a
	channel.Cost = actiondata.cost
	if c == 1 {
		channel.Data = actiondata.data
		channel.FB_r = actiondata.fB_r
		channel.FB_e = actiondata.fB_e
	} else {
		channel.Data = actiondata2.data
		channel.FB_r = actiondata2.fB_r
		channel.FB_e = actiondata2.fB_e
	}
	resourceJSONasBytes, err := json.Marshal(channel)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(channel.ID, resourceJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	tS3 := time.Now().UnixNano()
	time1 := strconv.FormatInt((tS2 - tS1), 10)
	time2 := strconv.FormatInt((tS3 - tS2), 10)
	return shim.Success([]byte("succeed-" + time1 + "-" + time2))

}

func (s *SmartContract) disputeResolve(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	noce, _ := strconv.ParseFloat(args[1], 64)
	cost, _ := strconv.ParseFloat(args[2], 64)
	fb_r, _ := strconv.ParseFloat(args[3], 64)
	fb_e, _ := strconv.ParseFloat(args[3], 64)

	actiondata := &actionData{args[0], noce, cost, fb_r, fb_e, args[5], args[6], args[7]}

	bool, _ := regexp.MatchString(`^(([1-9]{1}\d*)|(0{1}))(\.\d{0,1})?$`, args[1])
	if bool != true {
		return shim.Error(args[1] + "error")
	}

	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	var channel stateDate
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("channel not found")
	}
	err = json.Unmarshal(value, &channel) //反序列化
	if channel.State != "TD" {
		return shim.Error("state errr")
	}

	if time.Now().Unix()-channel.TS >= channel.Dp {
		return shim.Error("TS errr: TS out")
	}

	cha := strconv.FormatFloat(actiondata.actionNonce-channel.Nonce, 'f', 2, 64)
	if cha != "0.10" {
		return shim.Error("nonce errr")
	}
	cha = strconv.FormatFloat(actiondata.actionNonce-math.Floor(actiondata.actionNonce), 'f', 2, 64)
	if cha == "0.40" {
		if (actiondata.fB_r-channel.FB_r != channel.Cost) || (actiondata.fB_e-channel.FB_e != channel.Cost) {
			return shim.Error("fB errr")
		}
	} else {
		if (actiondata.fB_r-channel.FB_r != 0) || (actiondata.fB_e-channel.FB_e != 0) {
			return shim.Error("fB errr")
		}
	}

	var data2 string
	data2 += args[0] + args[1] + args[2] + args[3] + args[4] + actiondata.data

	HashFlag := VeryHash(data2, actiondata.hash)

	if HashFlag != true {
		return shim.Error("hash error")
	}

	cha = strconv.FormatFloat(actiondata.actionNonce-math.Floor(actiondata.actionNonce), 'f', 2, 64)
	if cha == "0.10" || cha == "0.30" || cha == "0.50" {
		flag := VerySign(actiondata.hash, channel.TemPk_r, actiondata.sign)
		if flag != true {
			return shim.Error("sig error")
		}
	} else {
		flag2 := VerySign(actiondata.hash, channel.TemPk_e, actiondata.sign)
		if flag2 != true {
			return shim.Error("sig error")
		}
	}
	tS2 := time.Now().UnixNano()
	channel.State = "O"
	channel.TS = time.Now().Unix()
	channel.Nonce = actiondata.actionNonce
	channel.Data = actiondata.data
	channel.FB_r = actiondata.fB_r
	channel.FB_e = actiondata.fB_e

	resourceJSONasBytes, err := json.Marshal(channel)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(channel.ID, resourceJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	tS3 := time.Now().UnixNano()
	time1 := strconv.FormatInt((tS2 - tS1), 10)
	time2 := strconv.FormatInt((tS3 - tS2), 10)
	return shim.Success([]byte("succeed-" + time1 + "-" + time2))

}

func (s *SmartContract) disputeRefute(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	noce, _ := strconv.ParseFloat(args[1], 64)
	cost, _ := strconv.ParseFloat(args[2], 64)
	fb_r, _ := strconv.ParseFloat(args[3], 64)
	fb_e, _ := strconv.ParseFloat(args[4], 64)
	actiondata := &actionData{args[0], noce, cost, fb_r, fb_e, args[5], args[6], args[7]}
	bool, _ := regexp.MatchString(`^(([1-9]{1}\d*)|(0{1}))(\.\d{0,1})?$`, args[1])
	if bool != true {
		return shim.Error(args[1] + "error")
	}
	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	var channel stateDate
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("device not found")
	}
	err = json.Unmarshal(value, &channel) //反序列化

	if (channel.State != "TD") && (channel.State != "CD") {
		return shim.Error("state error")
	}

	if time.Now().Unix()-channel.TS >= channel.Dp {

		return shim.Error("tS error")
	}

	if actiondata.actionNonce <= channel.Nonce {
		return shim.Error("nonce error")
	}

	var data2 string
	data2 += args[0] + args[1] + args[2] + args[3] + args[4] + actiondata.data

	HashFlag := VeryHash(data2, actiondata.hash)

	if HashFlag != true {
		return shim.Error("hash error")
	}
	tS2 := time.Now().UnixNano()
	cha := strconv.FormatFloat(channel.Nonce-math.Floor(channel.Nonce), 'f', 2, 64)
	if cha == "0.10" || cha == "0.30" || cha == "0.50" {
		cha = strconv.FormatFloat(actiondata.actionNonce-math.Floor(actiondata.actionNonce), 'f', 2, 64)
		if cha == "0.20" || cha == "0.40" || cha == "0.60" {
			return shim.Error("noce  error")
		}
		flag := VerySign(actiondata.hash, channel.TemPk_r, actiondata.sign)
		if flag != true {
			return shim.Error("sig  error")
		}
		stringamount := strconv.FormatFloat(channel.FB_e+channel.FB_r, 'f', 2, 64)
		chaincodeArgs := toChaincodeArgs2("depositsWithdraw", channel.Wallet_r, channel.Wallet_e, "0", stringamount, "0", "1")
		response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
		if response.Status != shim.OK {
			return shim.Error(response.Message)
		}
	} else {
		cha = strconv.FormatFloat(actiondata.actionNonce-math.Floor(actiondata.actionNonce), 'f', 2, 64)
		if cha == "0.10" || cha == "0.30" || cha == "0.50" {
			return shim.Error("noce  error")
		}
		flag := VerySign(actiondata.hash, channel.TemPk_e, actiondata.sign)
		if flag != true {
			return shim.Error("sig error")
		}
		stringamount := strconv.FormatFloat(channel.FB_e+channel.FB_r, 'f', 2, 64)
		chaincodeArgs := toChaincodeArgs2("depositsWithdraw", channel.Wallet_r, channel.Wallet_e, stringamount, "0", "1", "0")
		response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
		if response.Status != shim.OK {
			return shim.Error(response.Message)
		}
	}

	channel.State = "C"
	channel.TS = time.Now().Unix()
	channel.Nonce = actiondata.actionNonce
	channel.Cost = actiondata.cost
	channel.Data = actiondata.data
	channel.FB_r = 0
	channel.FB_e = 0
	resourceJSONasBytes, err := json.Marshal(channel)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(channel.ID, resourceJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	tS3 := time.Now().UnixNano()
	time1 := strconv.FormatInt((tS2 - tS1), 10)
	time2 := strconv.FormatInt((tS3 - tS2), 10)
	return shim.Success([]byte("succeed-" + time1 + "-" + time2))

}

func (s *SmartContract) cooperateclose(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	ID := args[0]
	sign_r := args[3]
	sign_e := args[4]
	value, err := stub.GetState(ID)
	if err != nil {
		return shim.Error(err.Error())
	}
	var channel stateDate
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("channel not found")
	}
	err = json.Unmarshal(value, &channel) //反序列化
	if channel.State != "O" {
		return shim.Error("state error")
	}
	funds_r, _ := strconv.ParseFloat(args[1], 64)
	funds_e, _ := strconv.ParseFloat(args[2], 64)
	if funds_r+funds_e > 2*channel.Amount {
		return shim.Error("funds value error")
	}
	var data2 string
	data2 += args[0] + args[1] + args[2]
	flag := VerySign(data2, channel.TemPk_r, sign_r)
	flag2 := VerySign(data2, channel.TemPk_e, sign_e)
	if flag != true || flag2 != true {
		return shim.Error("sig error")
	}
	tS2 := time.Now().UnixNano()
	Funds := ""
	chaincodeArgs := toChaincodeArgs2("depositsWithdraw", channel.Wallet_r, channel.Wallet_e, args[1], args[2], "1", "1")
	response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
	if response.Status != shim.OK {
		return shim.Error("DMSC:" + response.Message)
	} else {
		Funds = string(response.Payload)
	}

	channel.State = "C"
	channel.FB_r = 0
	channel.FB_e = 0
	channel.TS = time.Now().Unix()
	resourceJSONasBytes, err := json.Marshal(channel)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(channel.ID, resourceJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	tS3 := time.Now().UnixNano()
	time1 := strconv.FormatInt((tS2 - tS1), 10)
	time2 := strconv.FormatInt((tS3 - tS2), 10)
	return shim.Success([]byte("succeed-" + time1 + "-" + time2 + "--" + Funds))

	//
	//response:= stub.InvokeChaincode("DMSC",chaincodeArgs,"mychannel")
	//if response.Status!=shim.OK{
	//	return shim.Error("DMSC:"+response.Message)
	//}else {
	//	Funds_r = string(response.Payload)
	//}
	//chaincodeArgs2:= toChaincodeArgs2("tradingNum",channel.Wallet_r,"1")
	//response2:= stub.InvokeChaincode("DMSC",chaincodeArgs2,"mychannel")
	//if response2.Status!=shim.OK{
	//	return shim.Error("DMSC:"+response2.Message)
	//}
	//
	//
	//
	//chaincodeArgs_e:= toChaincodeArgs2("transfertoAB",channel.Wallet_e,args[2])
	//response_e:= stub.InvokeChaincode("DMSC",chaincodeArgs_e,"mychannel")
	//if response_e.Status!=shim.OK{
	//	return shim.Error("DMSC:"+response_e.Message)
	//}else {
	//	Funds_e = string(response_e.Payload)
	//}
	//chaincodeArgs3:= toChaincodeArgs2("tradingNum",channel.Wallet_e,"1")
	//response3:= stub.InvokeChaincode("DMSC",chaincodeArgs3,"mychannel")
	//if response3.Status!=shim.OK{
	//	return shim.Error("DMSC:"+response3.Message)
	//}
	//channel.State="C"
	//channel.FB_r=0
	//channel.FB_e=0
	//channel.TS=time.Now().Unix()
	//resourceJSONasBytes, err := json.Marshal(channel)
	//if err != nil {
	//	return shim.Error(err.Error())
	//}
	//err = stub.PutState(channel.ID, resourceJSONasBytes)
	//if err != nil {
	//	return shim.Error(err.Error())
	//}
	//return shim.Success([]byte("succeed:"+Funds_r+"-"+Funds_e))
}

func (s *SmartContract) unilateralClose(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	noce, _ := strconv.ParseFloat(args[1], 64)
	cost, _ := strconv.ParseFloat(args[2], 64)
	fb_r, _ := strconv.ParseFloat(args[3], 64)
	fb_e, _ := strconv.ParseFloat(args[4], 64)

	noce2, _ := strconv.ParseFloat(args[9], 64)
	cost2, _ := strconv.ParseFloat(args[10], 64)
	fb_r2, _ := strconv.ParseFloat(args[11], 64)
	fb_e2, _ := strconv.ParseFloat(args[12], 64)

	bool, _ := regexp.MatchString(`^(([1-9]{1}\d*)|(0{1}))(\.\d{0,1})?$`, args[1])
	if bool != true {
		return shim.Error(args[1] + "error")
	}
	bool, _ = regexp.MatchString(`^(([1-9]{1}\d*)|(0{1}))(\.\d{0,1})?$`, args[9])
	if bool != true {
		return shim.Error(args[9] + "error")
	}
	var actiondata = new(actionData)
	var actiondata2 = new(actionData)
	var action string
	var action2 string

	if noce < noce2 {
		actiondata = &actionData{args[0], noce, cost, fb_r, fb_e, args[5], args[6], args[7]}
		actiondata2 = &actionData{args[8], noce2, cost2, fb_r2, fb_e2, args[13], args[14], args[15]}
		action += args[0] + args[1] + args[2] + args[3] + args[4] + args[5]
		action2 += args[8] + args[9] + args[10] + args[11] + args[12] + args[13]
	} else if noce > noce2 {
		actiondata2 = &actionData{args[0], noce, cost, fb_r, fb_e, args[5], args[6], args[7]}
		actiondata = &actionData{args[8], noce2, cost2, fb_r2, fb_e2, args[13], args[14], args[15]}
		action2 += args[0] + args[1] + args[2] + args[3] + args[4] + args[5]
		action += args[8] + args[9] + args[10] + args[11] + args[12] + args[13]
	} else {
		return shim.Error("noce  error")
	}

	if (actiondata.ID != actiondata2.ID) || (args[3] != args[11]) || (args[4] != args[12]) {
		return shim.Error("FB  error")
	}

	if actiondata2.actionNonce-actiondata.actionNonce > 0.1001 {
		return shim.Error("noce  error")
	}

	if (actiondata.actionNonce-math.Floor(actiondata.actionNonce) > 0.6000001) || (actiondata2.actionNonce-math.Floor(actiondata2.actionNonce) > 0.6000001) {
		return shim.Error("Nonce of actiondatas error:>0.6")
	}
	if (actiondata.actionNonce-math.Floor(actiondata.actionNonce) < 0.4999999) || (actiondata2.actionNonce-math.Floor(actiondata2.actionNonce) < 0.499999999) {
		return shim.Error("Nonce of actiondatas error:<0.5")
	}
	Identity := args[16]
	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	var channel stateDate
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("channel not found")
	}
	err = json.Unmarshal(value, &channel) //反序列化
	if channel.State != "O" {
		return shim.Error("state erroe")
	}

	if (actiondata2.actionNonce < channel.Nonce) || (actiondata2.actionNonce < channel.TemNonce) {
		return shim.Error("nonce error")
	}

	if actiondata.fB_r+actiondata.fB_e != 2*channel.Amount {
		return shim.Error("Actiondata fB error")
	}
	if actiondata2.fB_r+actiondata2.fB_e != 2*channel.Amount {
		return shim.Error("Actiondata2 fB error")
	}

	flag := VerySign(args[0], channel.TemPk_r, Identity)
	flag2 := VerySign(args[0], channel.TemPk_e, Identity)
	if flag == true {
		HashFlag := VeryHash(action, actiondata.hash)
		if HashFlag != true {
			return shim.Error("hash error")
		}
		flag := VerySign(actiondata.hash, channel.TemPk_r, actiondata.sign)
		if flag != true {
			return shim.Error("Sig error")
		}
		HashFlag2 := VeryHash(action2, actiondata2.hash)
		if HashFlag2 != true {
			return shim.Error("hash error")
		}
		flag2 := VerySign(actiondata2.hash, channel.TemPk_e, actiondata2.sign)
		if flag2 != true {
			return shim.Error("Sig error")
		}
		tS2 := time.Now().UnixNano()
		channel.State = "CD"
		channel.TS = time.Now().Unix()
		channel.Nonce = actiondata.actionNonce
		channel.Cost = actiondata.cost
		channel.Data = actiondata.data
		channel.FB_r = actiondata.fB_r
		channel.FB_e = actiondata.fB_e
		resourceJSONasBytes, err := json.Marshal(channel)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(channel.ID, resourceJSONasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
		tS3 := time.Now().UnixNano()
		time1 := strconv.FormatInt((tS2 - tS1), 10)
		time2 := strconv.FormatInt((tS3 - tS2), 10)
		return shim.Success([]byte("succeed-" + time1 + "-" + time2 + "--"))

	}

	if flag2 == true {

		HashFlag := VeryHash(action, actiondata.hash)

		if HashFlag != true {
			return shim.Error("hash error")
		}
		flag := VerySign(actiondata.hash, channel.TemPk_r, actiondata.sign)
		if flag != true {
			return shim.Error("Sig error")
		}

		HashFlag2 := VeryHash(action2, actiondata2.hash)

		if HashFlag2 != true {
			return shim.Error("hash error")
		}
		flag2 := VerySign(actiondata2.hash, channel.TemPk_e, actiondata2.sign)
		if flag2 != true {
			return shim.Error("Sig error")
		}
		tS2 := time.Now().UnixNano()
		channel.State = "CD"
		channel.TS = time.Now().Unix()
		channel.Nonce = actiondata2.actionNonce
		channel.Cost = actiondata.cost

		channel.Data = actiondata2.data
		channel.FB_r = actiondata2.fB_r
		channel.FB_e = actiondata2.fB_e
		resourceJSONasBytes, err := json.Marshal(channel)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(channel.ID, resourceJSONasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
		tS3 := time.Now().UnixNano()
		time1 := strconv.FormatInt((tS2 - tS1), 10)
		time2 := strconv.FormatInt((tS3 - tS2), 10)
		return shim.Success([]byte("succeed-" + time1 + "-" + time2 + "--"))
	}
	return shim.Error("Identity Sig error")

}

func (s *SmartContract) deleteChannel(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("Identifier not found")
	}
	err = stub.DelState(args[0])
	if err != nil {
		return shim.Error("Failed to delete , key is: " + args[0])
	}
	return shim.Success([]byte("succeed"))
}

func (s *SmartContract) stateQuery(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	channelID := args[0]
	wallet_r := args[1]
	wallet_e := args[2]
	state := args[3]
	var buffer bytes.Buffer
	buffer.WriteString("{\"selector\":{")
	if len(args[0]) <= 0 {
		buffer.WriteString("\"iD\":{\"$regex\": \"\"},")
	} else {
		buffer.WriteString("\"iD\":{\"$regex\":\"")
		buffer.WriteString(string(channelID))
		buffer.WriteString("\"},")
	}
	if len(args[1]) <= 0 {
		buffer.WriteString("\"wallet_r\":{\"$regex\": \"\"},")
	} else {
		buffer.WriteString("\"wallet_r\":{\"$regex\":\"")
		buffer.WriteString(string(wallet_r))
		buffer.WriteString("\"},")
	}
	if len(args[2]) <= 0 {
		buffer.WriteString("\"wallet_e\":{\"$gt\": null},")
	} else {
		buffer.WriteString("\"wallet_r\":{\"$regex\":\"")
		buffer.WriteString(string(wallet_e))
		buffer.WriteString("\"},")
	}
	if len(args[3]) <= 0 {
		buffer.WriteString("\"state\":{\"$regex\": \"\"}")
	} else {
		buffer.WriteString("\"state\":{\"$regex\":\"")
		buffer.WriteString(string(state))
		buffer.WriteString("\"}")
	}
	buffer.WriteString("}}")
	//return shim.Success([]byte(buffer.String()))
	queryResults, err := getQueryResultForQueryString(stub, buffer.String())
	if err != nil {
		return shim.Error(err.Error() + buffer.String())
	}
	return shim.Success(queryResults)
}

func (s *SmartContract) temporaryState(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	noce, _ := strconv.ParseFloat(args[1], 64)
	cost, _ := strconv.ParseFloat(args[2], 64)
	fb_r, _ := strconv.ParseFloat(args[3], 64)
	fb_e, _ := strconv.ParseFloat(args[3], 64)
	var actiondata = new(actionData)
	Identity := args[8]
	actiondata = &actionData{args[0], noce, cost, fb_r, fb_e, args[5], args[6], args[7]}
	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	var channel stateDate
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("device not found")
	}
	err = json.Unmarshal(value, &channel) //反序列化
	if channel.State != "O" {
		return shim.Error("state error")
	}
	if actiondata.actionNonce <= channel.TemNonce {
		return shim.Error("Nonce error:<= channel.TemNonce")
	}

	var action string
	action += args[0] + args[1] + args[2] + args[3] + args[4] + args[5]
	HashFlag := VeryHash(action, actiondata.hash)
	if HashFlag != true {
		return shim.Error("hash error")
	}
	cha := strconv.FormatFloat(actiondata.actionNonce-math.Floor(actiondata.actionNonce), 'f', 2, 64)
	if cha == "0.10" || cha == "0.30" || cha == "0.50" {
		flag := VerySign(actiondata.hash, channel.TemPk_r, actiondata.sign)
		if flag != true {
			return shim.Error("Sig error")
		}
		flag2 := VerySign(args[0], channel.TemPk_e, Identity)
		if flag2 != true {
			return shim.Error("Sig error")
		}
	} else {
		flag := VerySign(actiondata.hash, channel.TemPk_e, actiondata.sign)
		if flag != true {
			return shim.Error("Sig error")
		}
		flag2 := VerySign(args[0], channel.TemPk_r, Identity)
		if flag2 != true {
			return shim.Error("Sig error")
		}
	}
	tS2 := time.Now().UnixNano()
	channel.TemNonce = actiondata.actionNonce
	resourceJSONasBytes, err := json.Marshal(channel)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(channel.ID, resourceJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	tS3 := time.Now().UnixNano()
	time1 := strconv.FormatInt((tS2 - tS1), 10)
	time2 := strconv.FormatInt((tS3 - tS2), 10)
	return shim.Success([]byte("succeed-" + time1 + "-" + time2 + "--"))
}

func (s *SmartContract) depositsWithdraw(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	ID := args[0]
	Role := args[1]
	S := args[2]
	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("channel not found")
	}
	var channel stateDate
	err = json.Unmarshal(value, &channel) //反序列化
	tS2 := time.Now().Unix()
	if Role == "r" {
		flag := VerySign(ID, channel.TemPk_r, S)
		if flag != true {
			return shim.Error("Sig error")
		}
	} else if Role == "e" {
		flag := VerySign(ID, channel.TemPk_e, S)
		if flag != true {
			return shim.Error("Sig error")
		}
	} else {
		return shim.Error("Role error")
	}

	tradingNum_r := "1"
	tradingNum_e := "1"
	if tS2-channel.TS > channel.Dp {
		tS4 := time.Now().UnixNano()
		if channel.State == "TD" {
			cha := strconv.FormatFloat(channel.Nonce-math.Floor(channel.Nonce), 'f', 2, 64)
			if cha == "0.20" || cha == "0.40" || cha == "0.60" {
				tradingNum_r = "0"
				tradingNum_e = "1"
			} else {
				tradingNum_r = "1"
				tradingNum_e = "0"
			}
		} else if channel.State == "CD" {
			tradingNum_r = "1"
			tradingNum_e = "1"
		} else {
			return shim.Error("state error")
		}

		funds := strconv.FormatFloat(channel.FB_r, 'f', 2, 64)
		funde := strconv.FormatFloat(channel.FB_e, 'f', 2, 64)
		chaincodeArgs := toChaincodeArgs2("depositsWithdraw", channel.Wallet_r, channel.Wallet_e, funds, funde, tradingNum_r, tradingNum_e)
		response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
		if response.Status != shim.OK {
			return shim.Error(response.Message)
		}
		channel.FB_e = 0
		channel.FB_r = 0
		channel.State = "C"
		resourceJSONasBytes, err := json.Marshal(channel)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(channel.ID, resourceJSONasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
		tS3 := time.Now().UnixNano()
		time1 := strconv.FormatInt((tS4 - tS1), 10)
		time2 := strconv.FormatInt((tS3 - tS4), 10)
		return shim.Success([]byte("succeed-" + time1 + "-" + time2 + "--"))

	} else {
		if channel.State == "CD" {
			tS4 := time.Now().UnixNano()
			if (channel.Nonce-math.Floor(channel.Nonce) == 0.2) || (channel.Nonce-math.Floor(channel.Nonce) == 0.4) || (channel.Nonce-math.Floor(channel.Nonce) == 0.6) {
				if Role == "r" {
					funds := strconv.FormatFloat(channel.FB_r, 'f', 2, 64)
					chaincodeArgs := toChaincodeArgs2("transfertoAB", channel.Wallet_r, funds)
					response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
					if response.Status != shim.OK {
						return shim.Error("transfertoAB error")
					}
					channel.FB_r = 0
				} else {
					return shim.Error("Role error")
				}
			} else {
				if Role == "e" {
					funds := strconv.FormatFloat(channel.FB_e, 'f', 2, 64)
					chaincodeArgs := toChaincodeArgs2("transfertoAB", channel.Wallet_e, funds)
					response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
					if response.Status != shim.OK {
						return shim.Error("transfertoAB error")
					}
					channel.FB_e = 0
				} else {
					return shim.Error("Role error")
				}
			}
			resourceJSONasBytes, err := json.Marshal(channel)
			if err != nil {
				return shim.Error(err.Error())
			}
			err = stub.PutState(channel.ID, resourceJSONasBytes)
			if err != nil {
				return shim.Error(err.Error())
			}

			tS3 := time.Now().UnixNano()
			time1 := strconv.FormatInt((tS4 - tS1), 10)
			time2 := strconv.FormatInt((tS3 - tS4), 10)
			return shim.Success([]byte("succeed-" + time1 + "-" + time2 + "--"))
		} else {
			return shim.Error("state error")
		}
	}

	return shim.Error(err.Error())
}

func toChaincodeArgs2(args ...string) [][]byte {
	bargs := make([][]byte, len(args))
	for i, arg := range args {
		bargs[i] = []byte(arg)
	}
	return bargs
}

//验证Hassh签名
func VeryHash(data string, hash string) bool {
	myhash := crypto.SHA256
	hashInstance := myhash.New()
	hashInstance.Write([]byte(data))
	hashed := hashInstance.Sum(nil)
	HEXhashcode := hex.EncodeToString(hashed[:]) //将数组转换成切片，转换成16进制，返回字符串
	if HEXhashcode == hash {
		return true
	}
	return false
}

//验证RSA签名
func VerySign(data string, publicKey string, sign string) bool {
	signature, _ := base64.StdEncoding.DecodeString(string(sign))
	key, _ := base64.StdEncoding.DecodeString(publicKey)
	pubKey, _ := x509.ParsePKIXPublicKey(key)
	myhash := crypto.SHA256
	hashInstance := myhash.New()
	hashInstance.Write([]byte(data))
	hashed := hashInstance.Sum(nil)
	error := rsa.VerifyPKCS1v15(pubKey.(*rsa.PublicKey), myhash, hashed, signature)
	if error == nil {
		return true
	}
	return false
}

func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) (*bytes.Buffer, error) {
	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	return &buffer, nil
}

func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {
	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	buffer, err := constructQueryResponseFromIterator(resultsIterator)
	if err != nil {
		return nil, err
	}

	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

	return buffer.Bytes(), nil
}
