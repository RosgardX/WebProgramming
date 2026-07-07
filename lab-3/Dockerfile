FROM quay.io/wildfly/wildfly:latest

# 1. Кладём JDBC-драйвер Postgres внутрь WildFly
COPY psql_jdbc_driver/postgresql-42.6.0.jar /opt/jboss/wildfly/standalone/deployments/

# 2. Кладём собранный WAR
COPY build/libs/points-lab-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/

# 3. Админ-пользователь WildFly (для web-консоли, если нужно)
ARG ADMIN_NAME="admin"
ARG ADMIN_PASSWORD="admin"
RUN /opt/jboss/wildfly/bin/add-user.sh -u $ADMIN_NAME -p $ADMIN_PASSWORD -s -r ManagementRealm

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]