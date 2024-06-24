# pimonitoringservice

This project simply monitors the messages and channels of SAP PI. In case of issues, it alerts the developers via email.

## Build profiles

### netweaver
#### mvn clean install -Pnetweaver
The artifact generated is to be deployed in SAP NetWeaver. It requires the oms-service-libs dependency. Email is send via the configured JNDI resource.

### tomcat
#### mvn clean install -Ptomcat
The artifact generated can be deployed in any tomcat or standalone servers. It bundles all the required dependencies. Email is configured in application.properties

## Tasks
#### DailyMonitoringTask
This monitors the messages of the previous day and alerts if there are any failures. Runs once a day in the morning.

#### PeriodicMonitoringTask
This runs periodically and monitors the messages and channels. If there are continous errors for last one hour(it checks at 10 minutes interval) for the same message and channel, it raises an alert. Runs thrice on a business day.

