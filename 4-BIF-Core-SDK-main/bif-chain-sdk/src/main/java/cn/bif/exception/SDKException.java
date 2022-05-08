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
package cn.bif.exception;

public class SDKException extends RuntimeException {
    private static final long serialVersionUID = 429654902433634386L;
    private Integer errorCode;
    private String errorDesc;

    public SDKException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = SdkError.SYSTEM_ERROR.getCode();
        this.errorDesc = message;
    }

    public SDKException(Throwable cause) {
        super(cause);
        this.errorCode = SdkError.SYSTEM_ERROR.getCode();
        this.errorDesc = SdkError.SYSTEM_ERROR.getDescription();
    }

    public SDKException(SdkError errEnum) {
        this(errEnum.getCode(), errEnum.getDescription());
    }

    public SDKException(SdkError errEnum, String message) {
        this(errEnum.getCode(), message);
    }

    public SDKException(Integer errCode, String message) {
        super(message);
        this.errorCode = errCode;
        this.errorDesc = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }
}
