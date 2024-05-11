/*
 * Tencent is pleased to support the open source community by making Polaris available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tencent.polaris.common.registry;

import com.tencent.polaris.api.core.ConsumerAPI;
import com.tencent.polaris.api.core.ProviderAPI;
import com.tencent.polaris.circuitbreak.api.CircuitBreakAPI;
import com.tencent.polaris.circuitbreak.factory.CircuitBreakAPIFactory;
import com.tencent.polaris.client.api.SDKContext;
import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.api.DiscoveryAPIFactory;
import com.tencent.polaris.factory.api.RouterAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import com.tencent.polaris.ratelimit.api.core.LimitAPI;
import com.tencent.polaris.ratelimit.factory.LimitAPIFactory;
import com.tencent.polaris.router.api.core.RouterAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class PolarisClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolarisClient.class);

    private final SDKContext sdkContext;

    private final ConsumerAPI consumerAPI;

    private final ProviderAPI providerAPI;

    private final LimitAPI limitAPI;

    private final RouterAPI routerAPI;

    private final CircuitBreakAPI circuitBreakAPI;

    public PolarisClient(String registryAddress, String configAddress) {
        ConfigurationImpl configuration = (ConfigurationImpl) ConfigAPIFactory.defaultConfig();
        configuration.setDefault();
        configuration.getGlobal().getServerConnector()
                .setAddresses(Collections.singletonList(registryAddress));
        configuration.getConfigFile().getServerConnector()
                .setAddresses(Collections.singletonList(configAddress));
        configuration.getConsumer().getLocalCache().setPersistEnable(true);
        sdkContext = SDKContext.initContextByConfig(configuration);
        consumerAPI = DiscoveryAPIFactory.createConsumerAPIByContext(sdkContext);
        providerAPI = DiscoveryAPIFactory.createProviderAPIByContext(sdkContext);
        limitAPI = LimitAPIFactory.createLimitAPIByContext(sdkContext);
        routerAPI = RouterAPIFactory.createRouterAPIByContext(sdkContext);
        circuitBreakAPI = CircuitBreakAPIFactory.createCircuitBreakAPIByContext(sdkContext);
    }

    public void destroy() {
        sdkContext.close();
    }

    public  ConsumerAPI getConsumerAPI() {
        return consumerAPI;
    }

    public  ProviderAPI getProviderAPI() {
        return providerAPI;
    }

    public  LimitAPI getLimitAPI() {
        return limitAPI;
    }

    public  RouterAPI getRouterAPI() {
        return routerAPI;
    }

    public  CircuitBreakAPI getCircuitBreakAPI() {
        return circuitBreakAPI;
    }

}
