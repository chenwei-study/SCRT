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
	"strconv"
	"time"

	//	"time"
	//   "strconv"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type SmartContract struct {
}
type resourceInfo struct {
	Walle         string  `json:"walle"`
	AppName       string  `json:"appName"`
	Cost          float64 `json:"cost"`
	CurrentCpu    string  `json:"cpu"`
	CurrentMemory float64 `json:"memory"`
	Deposit       float64 `json:"deposit"`
	Bandwidth     float64 `json:"bandwidth"`
	Address       string  `json:"address"`
	CD            float64 `json:"cD"`
	Ip            string  `json:"ip"`
	Port          string  `json:"port"`
	Mac           string  `json:"mac"`
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
	if function == "resourcePublish" { //create identity
		return s.resourcePublish(stub, args)
	}
	if function == "resourceQuery" {
		return s.resourceQuery(stub, args)
	}
	return shim.Error("Received unknown function invocation")
}

func (s *SmartContract) resourcePublish(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	tS1 := time.Now().UnixNano()
	deviceWalle := args[0]
	sign := args[1]
	appname := args[2]
	cost := args[3]
	cpu := args[4]
	Memory := args[5]
	deposit := args[6]
	bandwidth := args[7]
	address := args[8]
	challengeDuration := args[9]
	ip := args[10]
	port := args[11]
	mac := args[12]
	chaincodeArgs := toChaincodeArgs2("returnPublicKey", deviceWalle)
	response := stub.InvokeChaincode("DMSC", chaincodeArgs, "mychannel")
	if response.Status != shim.OK {
		return shim.Error("query publickey error")
	}
	var publickey []byte
	publickey = response.Payload
	flag := VerySign(deviceWalle, string(publickey), sign)
	if flag == true {
		costfloat, err := strconv.ParseFloat(cost, 32)
		Memoryfloat, err := strconv.ParseFloat(Memory, 32)
		depositfloat, err := strconv.ParseFloat(deposit, 32)
		bandwidthfloat, err := strconv.ParseFloat(bandwidth, 32)
		challengeDurationfloat, err := strconv.ParseFloat(challengeDuration, 32)
		value, err := stub.GetState(deviceWalle)
		if err != nil {
			return shim.Error(err.Error())
		}
		if value != nil {
			tS2 := time.Now().UnixNano()
			var ResourceInfo resourceInfo
			err = json.Unmarshal(value, &ResourceInfo) //反序列化
			if err != nil {
				return shim.Error("{\"Error\":\"Failed to decode JSON of: " + string(value) + "\" to deviceInfo}")
			}
			ResourceInfo.AppName = appname
			ResourceInfo.Cost = costfloat
			ResourceInfo.CurrentCpu = cpu
			ResourceInfo.CurrentMemory = Memoryfloat
			ResourceInfo.Deposit = depositfloat
			ResourceInfo.Bandwidth = bandwidthfloat
			ResourceInfo.Address = address
			ResourceInfo.CD = challengeDurationfloat
			ResourceInfo.Ip = ip
			ResourceInfo.Port = port
			ResourceInfo.Mac = mac
			ResourceInfofoJSONasBytes, err := json.Marshal(ResourceInfo)
			if err != nil {
				return shim.Error(err.Error())
			}
			err = stub.PutState(deviceWalle, ResourceInfofoJSONasBytes)
			if err != nil {
				return shim.Error("Failed")
			}
			tS3 := time.Now().UnixNano()
			time1 := strconv.FormatInt((tS2 - tS1), 10)
			time2 := strconv.FormatInt((tS3 - tS2), 10)
			return shim.Success([]byte("succeed-" + time1 + "-" + time2))
		} else {
			tS2 := time.Now().UnixNano()
			resource := &resourceInfo{deviceWalle, appname, costfloat, cpu, Memoryfloat, depositfloat, bandwidthfloat, address, challengeDurationfloat, ip, port, mac}
			resourceJSONasBytes, err := json.Marshal(resource)
			if err != nil {
				return shim.Error(err.Error())
			}
			err = stub.PutState(deviceWalle, resourceJSONasBytes)
			if err != nil {
				return shim.Error(err.Error())
			}
			tS3 := time.Now().UnixNano()
			time1 := strconv.FormatInt((tS2 - tS1), 10)
			time2 := strconv.FormatInt((tS3 - tS2), 10)
			return shim.Success([]byte("succeed-" + time1 + "-" + time2))
		}
	} else {
		return shim.Error("Sign error")
	}
}

func (s *SmartContract) resourceQuery(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	deviceWalle := args[0]
	appname := args[1]
	cost := args[2]
	address := args[3]
	var buffer bytes.Buffer
	buffer.WriteString("{\"selector\":{")
	if len(args[0]) <= 0 {
		buffer.WriteString("\"walle\":{\"$gt\": null},")
	} else {
		buffer.WriteString("\"walle\":\"")
		buffer.WriteString(string(deviceWalle))
		buffer.WriteString("\",")
	}
	if len(args[1]) <= 0 {
		buffer.WriteString("\"appName\":{\"$regex\": \"\"},")
	} else {
		buffer.WriteString("\"appName\":{\"$regex\":\"")
		buffer.WriteString(string(appname))
		buffer.WriteString("\"},")
	}
	if len(args[2]) <= 0 {
		buffer.WriteString("\"cost\":{\"$gt\": null},")
	} else {
		buffer.WriteString("\"cost\":{\"$lte\":\"")
		buffer.WriteString(string(cost))
		buffer.WriteString("\"},")
	}
	if len(args[3]) <= 0 {
		buffer.WriteString("\"address\":{\"$regex\": \"\"}")
	} else {
		buffer.WriteString("\"address\":{\"$regex\":\"")
		buffer.WriteString(string(address))
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
