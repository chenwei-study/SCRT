package main

import (
	"crypto"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"strconv"
	"time"
)

type SmartContract struct {
}

type deviceInfo struct {
	Walle         string  `json:"walle"`
	Role          string  `json:"role"`
	PublicKey     string  `json:"publicKey"`
	Funds         float64 `json:"funds"`
	Errnumber     int     `json:"err"`
	Tradingnumber int     `json:"tradingnumber"`
}

func main() {
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}

func (d *SmartContract) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
	//后期加入写入管理员账号
}

func (d *SmartContract) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)
	// Handle different functions
	if function == "deviceRegister" { //create identity
		return d.deviceRegister(stub, args)
	}
	if function == "deviceLogin" { //create identity
		return d.deviceLogin(stub, args)
	}
	if function == "deviceQuery" {
		return d.deviceQuery(stub, args)
	}
	if function == "transfertoAB" {
		return d.transfertoAB(stub, args)
	}
	if function == "deleteDevice" { //create identity
		return d.deleteDevice(stub, args)
	}
	if function == "deviceUpdate" { //create identity
		return d.deviceUpdate(stub, args)
	}
	if function == "transferAsset" {
		return d.transferAsset(stub, args)
	}
	if function == "returnPublicKey" {
		return d.returnPublicKey(stub, args)
	}
	if function == "doublesignature" {
		return d.doublesignature(stub, args)
	}
	if function == "tradingNum" {
		return d.tradingNum(stub, args)
	}
	if function == "depositsWithdraw" {
		return d.depositsWithdraw(stub, args)
	}

	return shim.Error("Received unknown function invocation")
}

func (d *SmartContract) deviceLogin(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	sign := args[1]

	if len(args[0]) <= 0 || len(args[1]) <= 0 {
		return shim.Error("Parameter cannot be empty")
	}

	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfo deviceInfo
	err = json.Unmarshal(value, &DBdeviceInfo) //反序列化
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	flag := VerySign(deviceWalle, DBdeviceInfo.PublicKey, sign)
	if flag == true {
		return shim.Success([]byte("succeed"))
	} else {
		return shim.Error("Sign error")
	}

}

func (d *SmartContract) deviceRegister(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	deviceWalle := args[0]
	deviceRole := args[1]
	devicePublicKey := args[2]
	deviceSign := args[3]
	if len(args[0]) <= 0 || len(args[1]) <= 0 || len(args[2]) <= 0 || len(args[3]) <= 0 {
		return shim.Error("Parameter cannot be empty")
	}
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value != nil {
		return shim.Error("device already exists")
	}

	var data string
	data += deviceWalle + deviceRole + devicePublicKey
	flag := VerySign(data, devicePublicKey, deviceSign)
	//tS2:=time.Now().UnixNano()
	//timecose:=strconv.FormatInt((tS2-tS1),10)
	//test:=strconv.FormatInt((tS2),10)
	//returnmessage :=test+"verfitime:"+ timecose
	if flag == true {
		tS2 := time.Now().UnixNano()
		deviceInfo := &deviceInfo{deviceWalle, deviceRole, devicePublicKey, 0, 0, 0}
		deviceInfoJSONasBytes, err := json.Marshal(deviceInfo)
		if err != nil {
			return shim.Error(err.Error())
		}

		err = stub.PutState(deviceWalle, deviceInfoJSONasBytes)
		//tS3:=time.Now().UnixNano()
		//savecose:=strconv.FormatInt((tS3-tS2),10)
		if err != nil {
			return shim.Error(err.Error())
		}
		tS3 := time.Now().UnixNano()
		//var myList []string
		//myList = append(myList, timecose)
		//myList = append(myList, savecose)
		//myList = append(myList, "timecost")
		// 数据格式化为json
		//jsonText, _ := json.Marshal(myList)
		time1 := strconv.FormatInt((tS2 - tS1), 10)
		time2 := strconv.FormatInt((tS3 - tS2), 10)
		return shim.Success([]byte("succeed-" + time1 + "-" + time2))
	} else {
		return shim.Error("Sign error")
	}
}

func (d *SmartContract) deviceQuery(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("device not found")
	}
	return shim.Success(value)
}

func (d *SmartContract) deviceUpdate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfo deviceInfo
	err = json.Unmarshal(value, &DBdeviceInfo) //反序列化
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	newdeviceWalle := args[1]
	newdeviceRole := args[2]
	newdevicePublicKey := args[3]
	deviceSign := args[4]
	var data string
	data += deviceWalle + newdeviceWalle + newdeviceRole + newdevicePublicKey
	flag := VerySign(data, DBdeviceInfo.PublicKey, deviceSign)
	if newdeviceWalle != "" || newdevicePublicKey != "" {
		if newdeviceWalle == "" {
			return shim.Error("Walle can not be null")
		}
		if newdevicePublicKey == "" {
			return shim.Error("PublicKey can not be null")
		}
		if newdeviceWalle == DBdeviceInfo.Walle {
			return shim.Error("newDeviceWalle can not equal to oldDeviceWalle")
		}

		if newdeviceWalle != DBdeviceInfo.Walle {
			value, err := stub.GetState(newdeviceWalle)
			if err != nil {
				return shim.Error(err.Error())
			}
			if value != nil {
				return shim.Error("device already exists")
			}
		}
		DBdeviceInfo.Walle = newdeviceWalle
	}
	if newdeviceRole != "" {
		DBdeviceInfo.Role = newdeviceRole
	}

	if flag == true {
		deviceInfoJSONasBytes, err := json.Marshal(DBdeviceInfo)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(DBdeviceInfo.Walle, deviceInfoJSONasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}

		return shim.Success([]byte("succeed"))
	} else {
		return shim.Error("Sign error")
	}

}

//同时删除资源信息
func (d *SmartContract) deleteDevice(stub shim.ChaincodeStubInterface, args []string) pb.Response {
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
	return shim.Success([]byte("device delete succeed"))
}

func (d *SmartContract) transferAsset(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	if len(args) <= 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}
	deviceWalle := args[0]
	peeraWalle := args[1]
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("device not found")
	}
	peerValue, err := stub.GetState(peeraWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if peerValue == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfo deviceInfo
	err = json.Unmarshal(value, &DBdeviceInfo) //反序列化
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	amount := args[2]
	sign := args[3]
	amoutfloat, err := strconv.ParseFloat(args[2], 64)

	if DBdeviceInfo.Funds < amoutfloat {
		return shim.Error("funds not enough")
	}
	var data string
	data += deviceWalle + peeraWalle + amount
	flag := VerySign(data, DBdeviceInfo.PublicKey, sign)
	if flag == true {
		tS2 := time.Now().UnixNano()
		DBdeviceInfo.Funds = DBdeviceInfo.Funds - amoutfloat
		deviceInfoJSONasBytes, err := json.Marshal(DBdeviceInfo)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(deviceWalle, deviceInfoJSONasBytes)
		if err != nil {
			return shim.Error(err.Error())
		}
		var peerDeviceInfo deviceInfo
		err = json.Unmarshal(peerValue, &peerDeviceInfo) //反序列化
		if err != nil {
			return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
		}
		peerDeviceInfo.Funds = peerDeviceInfo.Funds + amoutfloat
		peerDeviceInfoJSONasBytes, err := json.Marshal(peerDeviceInfo)
		if err != nil {
			return shim.Error(err.Error())
		}
		err = stub.PutState(peeraWalle, peerDeviceInfoJSONasBytes)
		if err != nil {
			DBdeviceInfo.Funds = DBdeviceInfo.Funds + amoutfloat
			deviceInfoJSONasBytes, err := json.Marshal(DBdeviceInfo)
			if err != nil {
				return shim.Error(err.Error())
			}
			err = stub.PutState(deviceWalle, deviceInfoJSONasBytes)
			if err != nil {
				return shim.Error("Failed to cancel transfer")
			}
			return shim.Error("fail to trans")
		}
		tS3 := time.Now().UnixNano()
		peerFunds := strconv.FormatFloat(peerDeviceInfo.Funds, 'f', 2, 64)
		localFunds := strconv.FormatFloat(DBdeviceInfo.Funds, 'f', 2, 64)
		time1 := strconv.FormatInt((tS2 - tS1), 10)
		time2 := strconv.FormatInt((tS3 - tS2), 10)
		return shim.Success([]byte(peeraWalle + ":" + peerFunds + ";" + deviceWalle + ":" + localFunds + "succeed-" + time1 + "-" + time2))
	} else {
		return shim.Error("Sign error")
	}
}

func (d *SmartContract) tradingNum(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	flge := args[1]
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	var ResourceInfo deviceInfo
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("device not found")
	}
	err = json.Unmarshal(value, &ResourceInfo) //反序列化
	if flge == "0" {
		ResourceInfo.Errnumber = ResourceInfo.Errnumber + 1
	}
	if flge == "1" {
		ResourceInfo.Tradingnumber = ResourceInfo.Tradingnumber + 1
	}
	ResourceInfoJSONasBytes, err := json.Marshal(ResourceInfo)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(deviceWalle, ResourceInfoJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte("succeed"))
}

func (d *SmartContract) returnPublicKey(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfo deviceInfo
	err = json.Unmarshal(value, &DBdeviceInfo) //反序列化
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	return shim.Success([]byte(DBdeviceInfo.PublicKey))
}

func (d *SmartContract) doublesignature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) < 6 {
		return shim.Error("Incorrect number of arguments. Expecting 5")
	}
	channelID := args[0]
	sign_r := args[1]
	sign_e := args[2]
	wallet_r := args[3]
	wallet_e := args[4]
	amount := args[5]
	if len(args[0]) <= 0 || len(args[1]) <= 0 || len(args[2]) <= 0 || len(args[3]) <= 0 || len(args[4]) <= 0 || len(args[5]) <= 0 {
		return shim.Error("Parameter cannot be empty")
	}

	value, err := stub.GetState(wallet_r)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfor deviceInfo
	err = json.Unmarshal(value, &DBdeviceInfor) //反序列化
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	value2, err := stub.GetState(wallet_e)
	if err != nil {
		return shim.Error(err.Error())
	}
	if value2 == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfoe deviceInfo
	err = json.Unmarshal(value2, &DBdeviceInfoe) //反序列化
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	amounta, err := strconv.ParseFloat(amount, 64)
	if (DBdeviceInfor.Funds >= amounta) && (DBdeviceInfoe.Funds >= amounta) {
		flag := VerySign(channelID, DBdeviceInfor.PublicKey, sign_r)
		if flag == true {
			flag := VerySign(channelID, DBdeviceInfoe.PublicKey, sign_e)
			if flag == true {
				DBdeviceInfor.Funds = DBdeviceInfor.Funds - amounta
				DBdeviceInfoe.Funds = DBdeviceInfoe.Funds - amounta
				deviceInfoJSONasBytes, err := json.Marshal(DBdeviceInfor)
				if err != nil {
					return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
				}
				deviceInfoJSONasBytes2, err := json.Marshal(DBdeviceInfoe)
				if err != nil {
					return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
				}
				err = stub.PutState(wallet_r, deviceInfoJSONasBytes)
				if err != nil {
					return shim.Error("Failed to cancel transfer")
				}
				err = stub.PutState(wallet_e, deviceInfoJSONasBytes2)
				if err != nil {
					return shim.Error("Failed to cancel transfer")
				}
				return shim.Success(nil)

			} else {
				return shim.Error("sign_e error")
			}

		} else {
			return shim.Error("sign_r error")
		}

	}
	return shim.Error("Insufficient funds to open channels")
}

func (d *SmartContract) transfertoAB(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	funds, _ := strconv.ParseFloat(args[1], 64)
	value, err := stub.GetState(deviceWalle)
	if err != nil {
		return shim.Error(err.Error())
	}
	var DBdeviceInfo deviceInfo
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
	}
	if value == nil {
		return shim.Error("device not found")
	}
	err = json.Unmarshal(value, &DBdeviceInfo) //反序列化
	DBdeviceInfo.Funds = DBdeviceInfo.Funds + funds
	deviceInfoJSONasBytes, err := json.Marshal(DBdeviceInfo)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(deviceWalle, deviceInfoJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	Funds := strconv.FormatFloat(DBdeviceInfo.Funds, 'f', 2, 64)
	return shim.Success([]byte(Funds))
}

func (d *SmartContract) depositsWithdraw(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWall_R := args[0]
	deviceWall_E := args[1]
	fundR, _ := strconv.ParseFloat(args[2], 64)
	fundE, _ := strconv.ParseFloat(args[3], 64)
	value_R, err := stub.GetState(deviceWall_R)
	if err != nil {
		return shim.Error(err.Error())
	}
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value_R) + "\" to deviceInfo}")
	}
	if value_R == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfo_R deviceInfo
	err = json.Unmarshal(value_R, &DBdeviceInfo_R) //反序列化
	if args[4] == "1" {
		DBdeviceInfo_R.Tradingnumber = DBdeviceInfo_R.Tradingnumber + 1
	} else {
		DBdeviceInfo_R.Errnumber = DBdeviceInfo_R.Errnumber + 1
	}

	DBdeviceInfo_R.Funds = DBdeviceInfo_R.Funds + fundR
	deviceInfoJSONasBytes, err := json.Marshal(DBdeviceInfo_R)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(deviceWall_R, deviceInfoJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	value_E, err := stub.GetState(deviceWall_E)
	if err != nil {
		return shim.Error(err.Error())
	}
	if err != nil {
		return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value_E) + "\" to deviceInfo}")
	}
	if value_E == nil {
		return shim.Error("device not found")
	}
	var DBdeviceInfo_E deviceInfo
	err = json.Unmarshal(value_E, &DBdeviceInfo_E) //反序列化
	if args[5] == "1" {
		DBdeviceInfo_E.Tradingnumber = DBdeviceInfo_E.Tradingnumber + 1
	} else {
		DBdeviceInfo_E.Errnumber = DBdeviceInfo_E.Errnumber + 1
	}
	DBdeviceInfo_E.Funds = DBdeviceInfo_E.Funds + fundE
	deviceInfoJSONasBytes_E, err := json.Marshal(DBdeviceInfo_E)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(deviceWall_E, deviceInfoJSONasBytes_E)
	if err != nil {
		return shim.Error(err.Error())
	}
	peerFunds := strconv.FormatFloat(DBdeviceInfo_R.Funds, 'f', 2, 64)
	localFunds := strconv.FormatFloat(DBdeviceInfo_E.Funds, 'f', 2, 64)
	return shim.Success([]byte(peerFunds + "-" + localFunds))
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
