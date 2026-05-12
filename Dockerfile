FROM eclipse-temurin:21-jre-ubi9-minimal

COPY --chown=1001:0 build/wlp /opt/ol/wlp/

COPY --chown=1001:0 build/wlp/usr/shared/resources/postgresql-42.7.7.jar /opt/ol/wlp/usr/shared/resources/
COPY --chown=1001:0 src/main/liberty/config/server.xml /opt/ol/wlp/usr/servers/defaultServer/
COPY --chown=1001:0 build/libs/*.war /opt/ol/wlp/usr/servers/defaultServer/apps/

USER 1001
CMD ["/opt/ol/wlp/bin/server", "run", "defaultServer"]