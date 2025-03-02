///*
// *  Copyright 2021-2025 Tian Jun
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *  http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package cn.odboy.infra.websocket.core;
//
//import io.undertow.server.DefaultByteBufferPool;
//import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
//import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.stereotype.Component;
//
///**
// * 解决启动io.undertow.websockets.jsr UT026010: Buffer pool was not
// * set on WebSocketDeploymentInfo, the default pool will be used的警告
// *
// * @author odboy
// * @date 2025-01-11
// */
//@Component
//public class WebSocketServerFactory implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
//    @Override
//    public void customize(UndertowServletWebServerFactory factory) {
//        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
//            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
//            webSocketDeploymentInfo.setBuffers(
//                    new DefaultByteBufferPool(false, 1024)
////                    new DefaultByteBufferPool(false, 1024,20,4)
//            );
//            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
//        });
//    }
//}
