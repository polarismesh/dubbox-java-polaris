/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.polaris.dubbo.metadata.report;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.ErrorTypeAwareLogger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportFactory;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MultiReportUtil {

    private static final ErrorTypeAwareLogger logger = LoggerFactory.getErrorTypeAwareLogger(MultiReportUtil.class);

    public static Optional<MetadataReport> buildAnother(ApplicationModel application, URL url) {
        ExtensionLoader<MetadataReportFactory> loader = application.getExtensionLoader(MetadataReportFactory.class);
        Map<String, MetadataReportFactory> factoryMap = loader.getSupportedExtensionInstances().stream()
                .collect(HashMap::new, (m, v) -> m.put(loader.getExtensionName(v), v), HashMap::putAll);

        boolean enableMulti = url.getParameter("report_multi", Boolean.class);
        if (!enableMulti) {
            return Optional.empty();
        }

        String anotherName = url.getParameter("another_protocol", "");
        if (StringUtils.isBlank(anotherName)) {
            return Optional.empty();
        }
        if (!factoryMap.containsKey(anotherName)) {
            return Optional.empty();
        }
        String anotherNamespace = url.getParameter("another_namespace", "");

        // copy 一个 URL 出来
        URL copyUrl = URL.valueOf(url.toFullString());
        copyUrl = copyUrl.setProtocol(anotherName);
        copyUrl = copyUrl.setAddress(url.getParameter("another_address", ""));
        copyUrl = copyUrl.addParameter("namespace", anotherNamespace);

        MetadataReportFactory factory = factoryMap.get(anotherName);

        logger.info(String.format("another metadata-report connect url : %s", copyUrl));

        return Optional.ofNullable(factory.getMetadataReport(copyUrl));
    }

}
