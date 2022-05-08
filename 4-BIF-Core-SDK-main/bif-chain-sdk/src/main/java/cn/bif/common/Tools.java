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
package cn.bif.common;

import java.util.List;

public class Tools {
    /**
     * @Method isEmpty
     * @Params [cs]
     * @Return java.lang.Boolean
     */
    public static Boolean isEmpty(final CharSequence cs) {
        return null == cs || cs.length() == 0;
    }

    /**
     * @Method isEmpty
     * @Params [obj]
     * @Return java.lang.Boolean
     */
    public static Boolean isEmpty(final Object obj) {
        return null == obj || "".equals(obj);
    }

    /**
     * @Method isEmpty
     * @Params [objs]
     * @Return java.lang.Boolean
     */
    public static Boolean isEmpty(final Object[] objs) {
        return null == objs || objs.length == 0;
    }

    /**
     * @Method isEmpty
     * @Params [objectList]
     * @Return java.lang.Boolean
     */
    public static Boolean isEmpty(final List<Object> objectList) {
        return null == objectList || objectList.size() == 0;
    }

    /**
     * @Method isNULL
     * @Params [obj]
     * @Return java.lang.Boolean
     */
    public static Boolean isNULL(final Object obj) {
        return null == obj;
    }
}
