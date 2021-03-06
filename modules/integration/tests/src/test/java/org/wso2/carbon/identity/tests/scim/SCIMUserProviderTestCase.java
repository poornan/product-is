/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.identity.tests.scim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.identity.scim.SCIMConfigAdminClient;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.identity.scim.common.stub.config.SCIMProviderDTO;
import org.wso2.carbon.identity.tests.ISIntegrationTest;

public class SCIMUserProviderTestCase extends ISIntegrationTest {
    private static final Log log = LogFactory.getLog(SCIMUserProviderTestCase.class);
    private SCIMConfigAdminClient scimConfigAdminClient;
    public static final String providerId = "testProvider";
    public static final int providerUserId = 2;
    private String scim_Provider_url;

    @BeforeClass(alwaysRun = true)
    public void testInit() throws Exception {
        super.init(2);
        userInfo = UserListCsvReader.getUserInfo(providerUserId);
        scimConfigAdminClient =
                new SCIMConfigAdminClient(isServer.getBackEndUrl(),
                        isServer.getSessionCookie());
        scim_Provider_url = "https://" + isServer.getBackEndUrl() + "/wso2/scim/";

    }

    @Test(description = "Add user service provider", priority = 1)
    public void testAddUserProvider() throws Exception {
        boolean providerAvailable = false;
        scimConfigAdminClient.addUserProvider(userInfo.getUserName() + "@carbon.super", providerId,
                userInfo.getUserName(), userInfo.getPassword(),
                scim_Provider_url + "Users", scim_Provider_url + "Groups");
        Thread.sleep(5000);
        SCIMProviderDTO[] scimProviders = scimConfigAdminClient.listUserProviders(userInfo.getUserName() +
                "@carbon.super", providerId);
        for (SCIMProviderDTO scimProvider : scimProviders) {
            if (scimProvider.getProviderId().equals(providerId)) {
                providerAvailable = true;
            }
        }
        Assert.assertTrue(providerAvailable, "Provider adding failed");
    }

    @Test(description = "delete user service provider", dependsOnMethods = {"testAddUserProvider"}, priority = 2)
    public void testDeleteUserProvider() {
        boolean providerDeleted = false;
        try {
            scimConfigAdminClient.deleteUserProvider(userInfo.getUserName() + "@carbon.super", providerId);
            providerDeleted = true;
        }  catch (Exception e) {
            log.error("Provider [" + providerId + "] delete failed.", e);
        }
        Assert.assertTrue(providerDeleted, "Provider [" + providerId + "] delete failed");
    }
}
