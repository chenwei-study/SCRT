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
package cn.bif.model.response.result.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 *
 */
public class BIFGasSendInfo {

    @JsonProperty(value = "dest_address")
    private String destAddress;

    @JsonProperty(value = "amount")
    private Long amount;

    @JsonProperty(value = "input")
    private String input;

    /**
     *
     * @Method getDestAddress
     * @Params []
     * @Return java.lang.String
     *
     */
    public String getDestAddress() {
        return destAddress;
    }

    /**
     *
     * @Method setDestAddress
     * @Params [destAddress]
     * @Return void
     *
     */
    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    /**
     *
     * @Method getAmount
     * @Params []
     * @Return java.lang.Long
     *
     */
    public Long getAmount() {
        return amount;
    }

    /**
     *
     * @Method setAmount
     * @Params [amount]
     * @Return void
     *
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     *
     * @Method getInput
     * @Params []
     * @Return java.lang.String
     *
     */
    public String getInput() {
        return input;
    }

    /**
     *
     * @Method setInput
     * @Params [input]
     * @Return void
     *
     */
    public void setInput(String input) {
        this.input = input;
    }
}
