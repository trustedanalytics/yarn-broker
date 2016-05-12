[![Build Status](https://travis-ci.org/trustedanalytics/yarn-broker.svg?branch=master)](https://travis-ci.org/trustedanalytics/yarn-broker)
[![Dependency Status](https://www.versioneye.com/user/projects/5729c2daa0ca350034be6849/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5729c2daa0ca350034be6849)

# yarn-broker

Cloud foundry broker for YARN.

# How to use it?
To use yarn-broker, you need to build it from sources configure, deploy, create instance and bind it to your app. Follow steps described below.

## Build
Run command for compile and package.:
```
mvn clean package
```

## Kerberos configuration
Broker automatically bind to an existing kerberos provide service. This will provide default kerberos configuration, for REALM and KDC host. Before deploy check:

- if kerberos service does not exists in your space, you can create it with command:
```
cf cups kerberos-service -p '{ "kdc": "kdc-host", "kpassword": "kerberos-password", "krealm": "kerberos-realm", "kuser": "kerberos-user" }'
```

- if kerberos-service exists in your space, you can update it with command:
```
cf uups kerberos-service -p '{ "kdc": "kdc-host", "kpassword": "kerberos-password", "krealm": "kerberos-realm", "kuser": "kerberos-user" }'
```

## Deploy
Push broker binary code to cloud foundry (use cf client).:
```
cf push yarn-broker -p target/yarn-broker-*.jar -m 512M -i 1 --no-start
```

## Configure

For strict separation of config from code (twelve-factor principle), configuration must be placed in environment variables.

Broker configuration params list (environment properties):

* obligatory :
  * USER_PASSWORD - password to interact with service broker
* optional :
  * BASE_GUID - base id for catalog plan creation (uuid)
  * HADOOP_PROVIDED_ZIP - list of yarn configuration parameters exposed by service (json format, default: {})
  * CF_CATALOG_SERVICENAME - service name in cloud foundry catalog (default: yarn)
  * CF_CATALOG_SERVICEID - service id in cloud foundry catalog (default: yarn)
  * YARNBRK_SPACE: - (default: /yarnbrk_space)

## Injection of Yarn configuration
YARN configuration must be set via HADOOP_PROVIDED_ZIP environment variable. Description of this process is this same as in HDFS broker case ( https://github.com/trustedanalytics/hdfs-broker/ ).

## Zookeeper configuration
Broker instance should be bind with zookeeper broker instance to get zookeeper configuration.
```
cf bs <app> <zookeeper-instance>
```

## Start service broker application

Use cf client :
```
cf start yarn-broker
```
## Create new service instance

Use cf client :
```
cf create-service-broker yarn-broker <user> <password> https://yarn-broker.<platform_domain>
cf enable-service-access yarn
cf cs yarn shared yarn-instance
```

## Binding broker instance

Broker instance can be bind with cf client :
```
cf bs <app> yarn-instance
```
or by configuration in app's manifest.yml :
```yaml
  services:
    - yarn-instance
```

To check if broker instance is bound, use cf client :
```
cf env <app>
```
and look for :
```yaml
  "yarn": [
   {
    "credentials": {
     "HADOOP_CONFIG_KEY": {
      <yarn_configuration_json>
     },
     "HADOOP_CONFIG_ZIP": {
      "description": "This is the encoded zip file of hadoop-configuration",
      "encoded_zip": "<base64 of configuration>"
     }
     "kerberos": {
      "kdc": "ip-10-10-9-198.us-west-2.compute.internal",
      "krealm": "US-WEST-2.COMPUTE.INTERNAL"
     }
    },
    "label": "yarn",
    "name": "yarn-instance",
    "plan": "shared",
    "tags": []
   }
  ]
```
in VCAP_SERVICES.
