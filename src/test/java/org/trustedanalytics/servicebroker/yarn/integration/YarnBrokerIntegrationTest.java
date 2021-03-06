/**
 * Copyright (c) 2015 Intel Corporation
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

package org.trustedanalytics.servicebroker.yarn.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.DeleteServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trustedanalytics.servicebroker.yarn.config.Application;
import org.trustedanalytics.servicebroker.yarn.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.yarn.integration.config.kerberos.KerberosLocalConfiguration;
import org.trustedanalytics.servicebroker.yarn.integration.config.store.ZkLocalConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, YarnBrokerIntegrationTest.class,
    KerberosLocalConfiguration.class, ZkLocalConfiguration.class})
@IntegrationTest("server.port=0")
@ActiveProfiles("integration-test")
public class YarnBrokerIntegrationTest {

  @Autowired
  private ExternalConfiguration conf;

  @Autowired
  private ServiceInstanceService instanceService;

  @Autowired
  private ServiceInstanceBindingService instanceBindingService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCreateServiceInstance_success_shouldReturnCreatedInstance() throws Exception {
    CreateServiceInstanceRequest request = getCreateInstanceRequest("instanceId");
    instanceService.createServiceInstance(request);
    ServiceInstance instance = instanceService.getServiceInstance(request.getServiceInstanceId());
    assertThat(instance.getServiceInstanceId(), equalTo("instanceId"));
  }

  @Test
  public void testDeleteServiceInstance_success_shouldReturnRemovedInstance() throws Exception {
    ServiceInstance instance =
        instanceService.createServiceInstance(getCreateInstanceRequest("instanceId3"));
    ServiceInstance removedInstance =
        instanceService.deleteServiceInstance(new DeleteServiceInstanceRequest(instance
            .getServiceInstanceId(), instance.getServiceDefinitionId(), instance.getPlanId()));
    assertThat(instance.getServiceInstanceId(), equalTo(removedInstance.getServiceInstanceId()));
  }

  @Test
  public void testCreateInstanceBinding_success_shouldReturnBinding() throws Exception {
    CreateServiceInstanceRequest request = getCreateInstanceRequest("instanceId4");
    instanceService.createServiceInstance(request);
    CreateServiceInstanceBindingRequest bindingRequest =
        new CreateServiceInstanceBindingRequest(getServiceInstance("instanceId4")
            .getServiceDefinitionId(), "fake-bare-plan", "appGuid").withBindingId("bindingId")
            .withServiceInstanceId("instanceId4");
    ServiceInstanceBinding binding = instanceBindingService.createServiceInstanceBinding(bindingRequest);
    assertThat(binding.getServiceInstanceId(), equalTo("instanceId4"));
  }

  private ServiceInstance getServiceInstance(String id) {
    return new ServiceInstance(new CreateServiceInstanceRequest(id, "fake-bare-plan",
        "f0487d90-fde6-4da1-a933-03f38776115d", "spaceGuid"));
  }

  private CreateServiceInstanceRequest getCreateInstanceRequest(String serviceId) {
    return new CreateServiceInstanceRequest("serviceDefinitionId", "fake-bare-plan",
        "f0487d90-fde6-4da1-a933-03f38776115d", "spaceGuid").withServiceInstanceId(serviceId);
  }
}
