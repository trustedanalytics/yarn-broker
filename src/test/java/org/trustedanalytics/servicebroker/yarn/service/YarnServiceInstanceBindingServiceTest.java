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

package org.trustedanalytics.servicebroker.yarn.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.servicebroker.yarn.config.ExternalConfiguration;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class YarnServiceInstanceBindingServiceTest {

  private static final String HADOOP_CONFIG_KEY_VALUE = "HADOOP_CONFIG_KEY";

  @Mock
  private ExternalConfiguration configuration;

  @Mock
  private ServiceInstanceBindingService instanceBindingService;

  private YarnServiceInstanceBindingService service;

  @Before
  public void init() {
    service =
        new YarnServiceInstanceBindingService(instanceBindingService, ImmutableMap.of(
            HADOOP_CONFIG_KEY_VALUE, "{}"), configuration);
  }

  @Test
  public void testCreateServiceInstance() throws Exception {
    CreateServiceInstanceBindingRequest request =
        new CreateServiceInstanceBindingRequest(getServiceInstance("serviceId")
            .getServiceDefinitionId(), "planId", "appGuid").withBindingId("bindingId")
            .withServiceInstanceId("serviceId");
    when(instanceBindingService.createServiceInstanceBinding(request)).thenReturn(
        getServiceInstanceBinding("id"));
    ServiceInstanceBinding instance = service.createServiceInstanceBinding(request);

    assertThat(instance.getCredentials().get(HADOOP_CONFIG_KEY_VALUE), equalTo("{}"));
  }

  private ServiceInstanceBinding getServiceInstanceBinding(String id) {
    return new ServiceInstanceBinding(id, "serviceId", Collections.emptyMap(), null, "guid");
  }

  private ServiceInstance getServiceInstance(String id) {
    return new ServiceInstance(new CreateServiceInstanceRequest(id, "planId", "organizationGuid",
        "spaceGuid"));
  }
}
