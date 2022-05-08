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

public class BIFAccountActivateOperation extends BIFBaseOperation {
    private String destAddress;
    private Long initBalance;

    public BIFAccountActivateOperation() {
        operationType = OperationType.ACCOUNT_ACTIVATE;
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
     * @Method getDestAddress
     * @Params []
     * @Return java.lang.String
     */
    public String getDestAddress() {
        return destAddress;
    }

    /**
     * @Method setDestAddress
     * @Params [destAddress]
     * @Return void
     */
    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
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
}
