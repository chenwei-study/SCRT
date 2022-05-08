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
package cn.bif.model.request.operation;

import cn.bif.common.OperationType;

public class BIFContractCreateOperation extends BIFBaseOperation {
    private Long initBalance;
    private Integer type;
    private String payload;
    private String initInput;

    public BIFContractCreateOperation() {
        operationType = OperationType.CONTRACT_CREATE;
    }

    /**
     * @Method getOperationType
     * @Params []
     * @Return OperationType
     */
    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    /**
     * @Method getInitBalance
     * @Params []
     * @Return java.lang.Long
     */
    public Long getInitBalance() {
        return initBalance;
    }

    /**
     * @Method setInitBalance
     * @Params [initBalance]
     * @Return void
     */
    public void setInitBalance(Long initBalance) {
        this.initBalance = initBalance;
    }

    /**
     * @Method getType
     * @Params []
     * @Return java.lang.Integer
     */
    public Integer getType() {
        return type;
    }

    /**
     * @Method setType
     * @Params [type]
     * @Return void
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @Method getPayload
     * @Params []
     * @Return java.lang.String
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @Method setPayload
     * @Params [payload]
     * @Return void
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * @Method getInitInput
     * @Params []
     * @Return java.lang.String
     */
    public String getInitInput() {
        return initInput;
    }

    /**
     * @Method setInitInput
     * @Params [initInput]
     * @Return void
     */
    public void setInitInput(String initInput) {
        this.initInput = initInput;
    }
}
