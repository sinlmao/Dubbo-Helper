/**
 * Copyright (c) 2019, Sinlmao (888@1st.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.sinlmao.commons.helper.dubbo;

import org.apache.dubbo.config.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * DubboHelper类
 *
 * @program: Sinlmao Commons Dubbo Helper
 * @description: DubboHelper类
 * @author: Sinlmao
 * @create: 2019-08-12 13:12
 */
public class DubboHelper {

    private final static Logger log = Logger.getLogger(DubboHelper.class);

    private static DubboHelper instance;

    private Map<String, Object> referenceServices = new HashMap<String, Object>();

    //应用配置
    private ApplicationConfig applicationConfig;
    //注册中心配置
    private RegistryConfig registryConfig;
    //服务提供者协议配置
    private ProtocolConfig protocolConfig;

    /**
     *
     * @param registryUrl
     * @param applicationName
     * @param protocolName
     * @param protocolPort
     * @param qosPort
     */
    private DubboHelper(String registryUrl, String applicationName, String protocolName, int protocolPort, int qosPort) {
        //解析registryUrl信息
        String registryProtocol = registryUrl.split("://")[0];
        String registryAddress = registryUrl.split("://")[1];

        // 当前应用配置
        applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);
        applicationConfig.setQosPort(qosPort);

        // 连接注册中心配置
        registryConfig = new RegistryConfig();
        registryConfig.setAddress(registryAddress);
        registryConfig.setProtocol(registryProtocol);

        // 服务提供者协议配置
        protocolConfig = new ProtocolConfig();
        protocolConfig.setName(protocolName);
        protocolConfig.setPort(protocolPort);
        protocolConfig.setThreads(200); //暂时默认200
    }

    /**
     * 注册服务
     *
     * @param interfaceClass
     * @param service
     * @param version
     */
    public static void registerService(Class<?> interfaceClass, Object service, String version) {
        instance().toRegisterService(interfaceClass, service, version);
    }

    /**
     * 注册服务【内部】
     *
     * @param interfaceClass
     * @param service
     * @param version
     */
    public void toRegisterService(Class<?> interfaceClass, Object service, String version) {

        //打印日志信息
        log.warn("MingBo DubboPlugin execute registering service [name=" + interfaceClass.getSimpleName() + ", port=" + protocolConfig.getPort() + "]...");

        // 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口

        // 服务提供者暴露服务配置
        ServiceConfig serviceConfig = new ServiceConfig(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()
        serviceConfig.setProtocol(protocolConfig); // 多个协议可以用setProtocols()
        serviceConfig.setInterface(interfaceClass);
        serviceConfig.setRef(service);
        //serviceConfig.setVersion("1.0.0");
        serviceConfig.setVersion(version);

        // 暴露及注册服务
        serviceConfig.export();
    }

    /**
     * 引用（调用）服务
     *
     * @param interfaceClass
     * @param version
     * @param <T>
     * @return 泛型动态对象
     */
    public static <T> T referenceService(Class<T> interfaceClass, String version) {
        // 引用远程服务
        Object object = instance().getReferenceService(interfaceClass, version);   // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
        return (T) object;
    }

    /**
     * 引用（调用）服务【内部】
     *
     * @param interfaceClass
     * @param version
     * @return 服务实体对象
     */
    private Object getReferenceService(Class<?> interfaceClass, String version) {

        // 检查是否存在程序缓存（内存对象）中
        if (referenceServices.containsKey(interfaceClass.getName())) {
            return referenceServices.get(interfaceClass.getName());
        }

        //打印日志信息
        log.warn("MingBo DubboPlugin execute referenceing service [name=" + interfaceClass.getSimpleName() + "]...");

        // 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接

        // 引用远程服务
        ReferenceConfig reference = new ReferenceConfig(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig); // 多个注册中心可以用setRegistries()
        reference.setInterface(interfaceClass);
        reference.setVersion(version);

        Object object = reference.get();    // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用

        referenceServices.put(interfaceClass.getName(), object);

        //打印日志信息
        log.warn("MingBo DubboPlugin execute reference service [name=" + interfaceClass.getSimpleName() + ", memory address=" + object.toString() + "]...");

        return object;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param registryUrl
     * @param applicationName
     * @param protocolName
     * @param protocolPort
     * @param qosPort
     * @return 实例对象
     */
    public static DubboHelper init(String registryUrl, String applicationName, String protocolName, int protocolPort, int qosPort) {
        if (instance == null) {
            instance = new DubboHelper(registryUrl, applicationName, protocolName, protocolPort, qosPort);
        }
        return instance;
    }

    /**
     * 获得一个实例
     * @return 实例对象
     */
    private static DubboHelper instance() {
        if (instance == null) {
            throw new NullPointerException("you must call init before call this method.");
        }
        return instance;
    }

}
